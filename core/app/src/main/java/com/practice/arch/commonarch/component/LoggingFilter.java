package com.practice.arch.commonarch.component;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;

/**
 * 方法入参日志记录以及请求日志记录
 * Created by byang059 on 01/05/18.
 */
@Aspect
@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter implements Ordered {

    @Around("execution(* com.practice.arch.commonarch.controller..*(..)) || execution(* com.practice.arch.commonarch.service..*(..))")
    public Object logMethodArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        if (args != null && args.length > 0) {
            log.info("entering method: {}.{}, input args: {}", className, methodName, StringUtils.join(args, ", "));
        } else {
            log.info("entering method: {}.{}", className, methodName);
        }
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long timeConsuming = System.currentTimeMillis() - start;
        log.info("exit method: {}.{}, execute time: {}ms", className, methodName, timeConsuming);
        return proceed;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    /**
     * logging http request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrapperRequest, wrapperResponse);
        String requestBody = getContentAsString(wrapperRequest.getContentAsByteArray(), wrapperRequest.getCharacterEncoding());
        String responseBody = getContentAsString(wrapperResponse.getContentAsByteArray(), wrapperResponse.getCharacterEncoding());
        log.info("Request: Method: {}, URI: {}, Cookies: {}, Headers: {}, Body: {}; Response: Status: {}, Headers: {}, Body: {}",
                wrapperRequest.getMethod(), uri, getCookies(wrapperRequest), getRequestHeaders(wrapperRequest), requestBody, wrapperResponse.getStatus(), getResponseHeaders(wrapperResponse), responseBody);
        wrapperResponse.copyBodyToResponse();
    }

    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); ) {
            String headerName = names.nextElement();
            for (Enumeration<String> headerValues = request.getHeaders(headerName);
                 headerValues.hasMoreElements(); ) {
                String headerValue = headerValues.nextElement();
                sb.append(headerName).append(": ").append(headerValue).append(", ");
            }
        }
        appendSuffix(sb);
        return sb.toString();
    }


    private String getResponseHeaders(HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String headerName : response.getHeaderNames()) {
            Collection<String> headerValues = response.getHeaders(headerName);
            for (String headerValue : headerValues) {
                sb.append(headerName).append(": ").append(headerValue).append(", ");
            }
        }
        appendSuffix(sb);
        return sb.toString();
    }

    private String getCookies(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName()).append(": ").append(cookie.getValue()).append(", ");
            }
        }
        appendSuffix(sb);
        return sb.toString();
    }

    private String getContentAsString(byte[] content, String characterEncoding) {
        if (content.length == 0) {
            return "";
        }
        String payload;
        try {
            payload = new String(content, 0, content.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            payload = "Unknown";
        }
        return payload;
    }

    private void appendSuffix(StringBuilder sb) {
        if (sb.length() >= 3) {
            sb.replace(sb.length() - 2, sb.length(), "]");
        } else {
            sb.append("]");
        }
    }
}
