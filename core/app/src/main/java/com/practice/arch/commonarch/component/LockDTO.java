/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Created by byang059 on 6/1/20
 */
@Data
@Builder
public class LockDTO {
    String name;
    long waitTime;
    long leaseTime;
    TimeUnit timeUnit;
}
