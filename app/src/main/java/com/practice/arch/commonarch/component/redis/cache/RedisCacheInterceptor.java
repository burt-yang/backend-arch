/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.cache;

import com.practice.arch.commonarch.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationInvoker;

/**
 * Created by byang059 on 6/6/20
 */
public class RedisCacheInterceptor extends CacheInterceptor {

    @Autowired
    RedisService redisService;

    @Override
    protected Cache.ValueWrapper doGet(Cache cache, Object key) {
        Cache.ValueWrapper valueWrapper = super.doGet(cache, key);

        return valueWrapper;
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
    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return super.invokeOperation(invoker);
    }
}
