/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Created by byang059 on 5/26/20
 */
@Component
public class RedisRateLimiter {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisScript<List> redisScript;

    /**
     * @param id
     * @param replenishRate How many requests per second do you want a user to be allowed to do?
     * @param burstCapacity How much bursting do you want to allow?
     * @param requestedTokens How many tokens are requested per request?
     * @return
     */
    public boolean isAllowed(String id, int replenishRate, int burstCapacity, int requestedTokens) {
        List<String> keys = getKeys(id);
        // The arguments to the LUA script. time() returns unixtime in seconds.
        List<Long> result = redisTemplate.execute(redisScript, keys, replenishRate,
                burstCapacity, Instant.now().getEpochSecond(),
                requestedTokens);
        return result.get(0) == 1;
    }

    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
