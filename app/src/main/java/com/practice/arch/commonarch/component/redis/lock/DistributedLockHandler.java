/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.lock;

import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Created by byang059 on 5/29/20
 */
@Component
@Aspect
@Slf4j
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
            log.error("distributed lock get failure, name: {}", name);
            throw new AppException(ResultCode.LIMITED_DISTRIBUTED_LOCK);
        }
        log.info("distributed lock got success, name: {}", name);
        Object proceed = joinPoint.proceed();
        if (lock.isLocked()) {
            unLock(lock, name);
        }
        return proceed;
    }

    private void unLock(RLock lock, String name) {
        RFuture<Void> result = lock.unlockAsync();
        result.onComplete((aVoid, throwable) -> {
            if (result.isSuccess()) {
                log.info("distributed lock release success, name: {}", name);
            } else {
                log.error("distributed lock release failure, name: {}", name);
            }
        });
    }
}
