/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.lock;

import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by byang059 on 5/29/20
 */
@Component
@Aspect
public class DistributedLockHandler {

    @Autowired
    RedissonClient redisson;

    @Around("@annotation(com.practice.arch.commonarch.component.redis.lock.DistributedLock)")
    public Object tryLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        String name = annotation.name();
        if (StringUtils.isEmpty(name)) {
            name = UUID.randomUUID().toString();
        }
        RLock lock = redisson.getLock(name);
        boolean isLocked = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
        if (!isLocked) {
            throw new AppException(ResultCode.LIMITED_DISTRIBUTED_LOCK);
        }
        Object proceed = joinPoint.proceed();
        if (lock.isLocked()) {
            lock.unlockAsync();
        }
        return proceed;
    }
}
