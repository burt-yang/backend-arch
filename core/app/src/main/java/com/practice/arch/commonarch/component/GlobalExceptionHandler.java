/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.component.response.ResponseWrapper;
import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import com.practice.arch.commonarch.exception.TokenExpiredException;
import com.practice.arch.commonarch.exception.TokenInvalidException;
import com.practice.arch.commonarch.exception.TokenNotFoundException;
import com.practice.arch.commonarch.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;

import static com.practice.arch.commonarch.enums.ResultCode.ACCESS_DENY;
import static com.practice.arch.commonarch.enums.ResultCode.ACCOUNT_Disabled;
import static com.practice.arch.commonarch.enums.ResultCode.ACCOUNT_EXPIRED;
import static com.practice.arch.commonarch.enums.ResultCode.ACCOUNT_LOCKED;
import static com.practice.arch.commonarch.enums.ResultCode.AUTHENTICATION_FAILURE;
import static com.practice.arch.commonarch.enums.ResultCode.BAD_CREDENTIALS;
import static com.practice.arch.commonarch.enums.ResultCode.CREDENTIALS_EXPIRED;
import static com.practice.arch.commonarch.enums.ResultCode.FRAMEWORK_ERROR;
import static com.practice.arch.commonarch.enums.ResultCode.PARAM_NOT_VALID;
import static com.practice.arch.commonarch.enums.ResultCode.SERVER_ERROR;
import static com.practice.arch.commonarch.enums.ResultCode.TOKEN_EXPIRED;
import static com.practice.arch.commonarch.enums.ResultCode.TOKEN_INVALID;
import static com.practice.arch.commonarch.enums.ResultCode.TOKEN_NOT_FOUND;
import static com.practice.arch.commonarch.enums.ResultCode.USER_NOT_FOUND;

/**
 * Created by byang059 on 2020/4/30
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler, CacheErrorHandler {

    /**
     * 自定义程序异常
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException e) {
        log.warn(e.getMessage());
        ResultCode resultCode = e.getResultCode();
        return resultCode.toResponseEntity(e.getMessage());
    }

    /**
     * 未知程序异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e) {
        log.error(e.getMessage(), e);
        return ResultCode.SERVER_ERROR.toResponseEntity();
    }

    /**
     * handle method args valid exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return PARAM_NOT_VALID.toResponseEntity(e.getMessage());
    }

    /**
     * handle param bind exception
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(e.getMessage(), e);
        return handleValidException(e.getBindingResult());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(e.getMessage(), e);
        return handleValidException(e.getBindingResult());
    }

    /**
     * 处理SpringMVC异常
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(e.getMessage(), e);
        ResponseWrapper responseBody = ResponseWrapper.failure(FRAMEWORK_ERROR, e.getMessage());
        return new ResponseEntity<>(responseBody, headers, status);
    }

    /**
     * 处理Token异常相关
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleSpringSecurityException(AuthenticationException e) {
        log.error(e.getMessage(), e);
        ResultCode resultCode = handleAuthenticationException(e);
        return resultCode.toResponseEntity();
    }

    /**
     * 处理Spring Security - 授权失败
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        log.error(e.getMessage(), e);
        log.error("Authentication failure for Uri: {}", requestURI);
        ResultCode resultCode = handleAuthenticationException(e);
        sendResponse(response, resultCode);
    }

    /**
     * 处理Spring Security - 授权成功，但无权访问
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        log.error(e.getMessage(), e);
        log.error("Access deny for Uri: {}", requestURI);
        sendResponse(response, ACCESS_DENY);
    }

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.error(e.getMessage(), e);
    }

    private ResultCode handleAuthenticationException(AuthenticationException e) {
        ResultCode resultCode;
        if (e instanceof TokenExpiredException) {
            resultCode = TOKEN_EXPIRED;
        } else if (e instanceof TokenNotFoundException || e instanceof AuthenticationCredentialsNotFoundException) {
            resultCode = TOKEN_NOT_FOUND;
        } else if (e instanceof TokenInvalidException) {
            resultCode = TOKEN_INVALID;
        } else if (e instanceof UsernameNotFoundException) {
            resultCode = USER_NOT_FOUND;
        } else if (e instanceof BadCredentialsException || e instanceof InsufficientAuthenticationException) {
            resultCode = BAD_CREDENTIALS;
        } else if (e instanceof LockedException) {
            resultCode = ACCOUNT_LOCKED;
        } else if (e instanceof AccountExpiredException) {
            resultCode = ACCOUNT_EXPIRED;
        } else if (e instanceof CredentialsExpiredException) {
            resultCode = CREDENTIALS_EXPIRED;
        } else if (e instanceof DisabledException) {
            resultCode = ACCOUNT_Disabled;
        } else if (e instanceof AuthenticationServiceException) {
            resultCode = SERVER_ERROR;
        } else {
            resultCode = AUTHENTICATION_FAILURE;
        }
        return resultCode;
    }

    private void sendResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(resultCode.getHttpStatus().value());
        response.getOutputStream().write(JsonUtil.bean2Json(ResponseWrapper.failure(resultCode)).getBytes());
    }

    private ResponseEntity<Object> handleValidException(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError error : fieldErrors) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage()).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        return PARAM_NOT_VALID.toResponseEntity(sb.toString());
    }
}
