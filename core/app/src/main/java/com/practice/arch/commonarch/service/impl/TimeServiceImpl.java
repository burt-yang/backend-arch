/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service.impl;

import com.practice.arch.common.domain.AmUser;
import com.practice.arch.common.domain.AmUserCriteria;
import com.practice.arch.common.repository.AmUserRepository;
import com.practice.arch.commonarch.component.redis.lock.DistributedLock;
import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import com.practice.arch.commonarch.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.hint.HintManager;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Created by byang059 on 5/11/20
 */
@Service
@Slf4j
public class TimeServiceImpl implements TimeService {
//    @Autowired
//    RedissonClient redisson;

    @Autowired
    AmUserRepository amUserRepository;

//    @DistributedLock(name = "mylock", waitTime = 0, leaseTime = 100)
    @Cacheable(value = "getTime")
    @Override
    public Long getTime(String a, int b) {
        log.info("get lock");
//        HintManager.getInstance().setMasterRouteOnly();
        final AmUser record = new AmUser().withEmail(UUID.randomUUID().toString()).withPassword("123");
        amUserRepository.insert(record);
        return Long.valueOf(record.getId());
    }

    @Override
    public Long testAspectj() {
        return getTime("a", 1);
    }

    public List<AmUser> get() {
        HintManager.getInstance().setMasterRouteOnly();
        return amUserRepository.selectByExample(new AmUserCriteria());
    }
}
