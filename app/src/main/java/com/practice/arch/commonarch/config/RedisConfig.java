/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.practice.arch.commonarch.component.GlobalExceptionHandler;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by byang059 on 5/24/20
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Autowired
    RedissonConnectionFactory redissonConnectionFactory;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    RedissonClient redissonClient;

    @Bean
    @Override
    public CacheManager cacheManager() {
        Map<String, CacheConfig> config = new HashMap<>();
        return new DefaultRedissonSpringCacheManager(redissonClient, config);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return globalExceptionHandler;
    }

    @Bean
    public RedisScript<List> rateLimiterScript() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    public static class DefaultRedissonSpringCacheManager extends RedissonSpringCacheManager {

        public DefaultRedissonSpringCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config) {
            super(redisson, config);
        }

        @Override
        protected CacheConfig createDefaultConfig() {
            //cache with ttl = 5 minutes and maxIdleTime = 12 minutes
            return new CacheConfig(5 * 60 * 1000, 0);
        }
    }
}
