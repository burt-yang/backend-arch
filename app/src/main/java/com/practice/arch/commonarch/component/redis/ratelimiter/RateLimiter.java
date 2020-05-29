package com.practice.arch.commonarch.component.redis.ratelimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by byang059 on 5/25/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    int replenishRate() default 2;

    int burstCapacity() default 200;

    int requestedTokens() default 1;
}
