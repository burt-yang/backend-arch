/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by byang059 on 5/29/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String name();

    long waitTime() default 1;

    long leaseTime() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
