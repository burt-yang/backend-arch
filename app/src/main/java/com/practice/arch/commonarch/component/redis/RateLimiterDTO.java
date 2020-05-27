/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by byang059 on 5/27/20
 */
@Data
@AllArgsConstructor
public class RateLimiterDTO {
    private boolean isAllowed;
    private Long tokensLeft;
}
