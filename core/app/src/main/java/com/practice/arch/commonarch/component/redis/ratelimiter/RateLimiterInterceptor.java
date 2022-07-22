/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.ratelimiter;

import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by byang059 on 5/27/20
 */
@Component
@ConditionalOnBean(RedissonAutoConfiguration.class)
public class RateLimiterInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisRateLimiter redisRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimiter annotation = handlerMethod.getMethodAnnotation(RateLimiter.class);
        if (annotation == null) {
            return true;
        }
        RateLimiterDTO limiterDTO = redisRateLimiter.isAllowed(request.getRequestURI(), annotation.replenishRate(), annotation.burstCapacity(), annotation.requestedTokens());
        setResponseHeaders(response, limiterDTO.getTokensLeft(), annotation);
        if (!limiterDTO.isAllowed()) {
            throw new AppException(ResultCode.RATE_LIMITER_ERROR);
        }
        return true;
    }

    public void setResponseHeaders(HttpServletResponse response, Long tokensLeft, RateLimiter annotation) {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(tokensLeft));
        response.setHeader("X-RateLimit-Replenish-Rate", String.valueOf(annotation.replenishRate()));
        response.setHeader("X-RateLimit-Burst-Capacity", String.valueOf(annotation.burstCapacity()));
        response.setHeader("X-RateLimit-Requested-Tokens", String.valueOf(annotation.requestedTokens()));
    }
}
