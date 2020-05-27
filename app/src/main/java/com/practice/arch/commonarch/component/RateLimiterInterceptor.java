/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.domain.dto.RateLimiterDTO;
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
        int replenishRate = methodAnnotation.replenishRate();
        int burstCapacity = methodAnnotation.burstCapacity();
        int requestedTokens = methodAnnotation.requestedTokens();
        if (replenishRate == 0 || burstCapacity == 0 || requestedTokens == 0) {
            return true;
        }
        RateLimiterDTO limiterDTO = redisRateLimiter.isAllowed(request.getRequestURI(), replenishRate, burstCapacity, requestedTokens);
        setResponseHeaders(response, limiterDTO.getTokensLeft(), replenishRate, burstCapacity, requestedTokens);
        if (!limiterDTO.isAllowed()) {
            throw new AppException(ResultCode.RATE_LIMITER_ERROR);
        }
        return true;
    }

    public void setResponseHeaders(HttpServletResponse response, Long tokensLeft, int replenishRate,
                                   int burstCapacity,
                                   int requestedTokens) {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(tokensLeft));
        response.setHeader("X-RateLimit-Replenish-Rate", String.valueOf(replenishRate));
        response.setHeader("X-RateLimit-Burst-Capacity", String.valueOf(burstCapacity));
        response.setHeader("X-RateLimit-Requested-Tokens", String.valueOf(requestedTokens));
    }
}
