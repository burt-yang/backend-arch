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
    /**
     * @return How many requests per second do you want a user to be allowed to do?
     */
    int replenishRate() default 2;

    /**
     * @return How much bursting do you want to allow?
     */
    int burstCapacity() default 200;

    /**
     * @return How many tokens are requested per request?
     */
    int requestedTokens() default 1;


}
