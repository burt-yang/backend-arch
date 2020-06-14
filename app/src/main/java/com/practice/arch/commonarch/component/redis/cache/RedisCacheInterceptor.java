/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.cache;

import com.google.common.collect.Lists;
import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import com.practice.arch.commonarch.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * Created by byang059 on 6/10/20
 */
@Slf4j
public class RedisCacheInterceptor extends CacheInterceptor {

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    @Qualifier("readCountRateLimiter")
    RedisScript<String> redisScript;

    @Override
    protected Cache.ValueWrapper doGet(Cache cache, Object key) {
        Cache.ValueWrapper wrapper = super.doGet(cache, key);
        if (wrapper == null) {
            String name = Thread.currentThread().getName();

            String result = redisTemplate.execute(redisScript, Lists.newArrayList("Read_" + key.toString()), name, 2, 20);
            if (StringUtils.isEmpty(result)) {
                log.info("get read lock -> read from db");
            } else {
                log.info("wait for other thread read db, this thread will sleep");
                try {
                    Thread.sleep(2000);
                    //re-read from cache
                    Cache.ValueWrapper secondWrapper = super.doGet(cache, key);
                    if (secondWrapper == null){
                        log.info("second read from redis also empty");
                        throw new AppException(ResultCode.LIMITED_DISTRIBUTED_LOCK);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //cache is not hint
            //get lock -> read from db
            //not get lock, wait for seconds, if timeout, return empty, other return cache -> not read from db
        }
        return wrapper;
    }

    @Override
    protected void doPut(Cache cache, Object key, Object result) {
        super.doPut(cache, key, result);
    }

    @Override
    protected void doEvict(Cache cache, Object key, boolean immediate) {
        super.doEvict(cache, key, immediate);
    }

    @Override
    protected void doClear(Cache cache, boolean immediate) {
        super.doClear(cache, immediate);
    }

    @Override
    public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
        super.setCacheOperationSource(cacheOperationSource);
    }
}
