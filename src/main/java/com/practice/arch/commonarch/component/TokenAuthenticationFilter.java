/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.service.TokenService;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by byang059 on 5/17/20
 */
public class TokenAuthenticationFilter extends GenericFilterBean {

    private AuthenticationEntryPoint entryPoint;
    private AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint entryPoint) {
        this.authenticationManager = authenticationManager;
        this.entryPoint = entryPoint;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        MDC.put("uri", request.getRequestURI());
        String jwtToken = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(jwtToken)) {
            try {
                PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(jwtToken, "");
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (AuthenticationException e) {
                entryPoint.commence(request, (HttpServletResponse) servletResponse, e);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        MDC.remove("uri");
        MDC.remove("email");
    }
}
