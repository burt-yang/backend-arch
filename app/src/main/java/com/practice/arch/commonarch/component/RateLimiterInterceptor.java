/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by byang059 on 5/27/20
 */
@Component
public class RateLimiterInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisRateLimiter redisRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimiter methodAnnotation = handlerMethod.getMethodAnnotation(RateLimiter.class);
        if (methodAnnotation == null) {
            return true;
        }
        boolean allowed = redisRateLimiter.isAllowed(request.getRequestURI(), methodAnnotation.replenishRate(), methodAnnotation.burstCapacity(), methodAnnotation.requestedTokens());
        if (!allowed) {
            throw new AppException(ResultCode.RATE_LIMITER_ERROR);
        }
        return true;
    }
}
