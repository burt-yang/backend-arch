/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service.impl;

import com.practice.arch.commonarch.component.redis.lock.DistributedLock;
import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import com.practice.arch.commonarch.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Created by byang059 on 5/11/20
 */
@Service
@Slf4j
public class TimeServiceImpl implements TimeService {
    @Autowired
    RedissonClient redisson;

    @DistributedLock(name = "mylock", waitTime = 1, leaseTime = 100)
    @Override
    public Long getTime(String a, int b) {
        log.info("get lock");
        return 1l;
    }

    @Override
    public Long testAspectj() {
        return getTime("a", 1);
    }

    private boolean get() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
