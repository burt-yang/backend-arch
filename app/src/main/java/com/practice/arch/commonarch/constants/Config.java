/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.constants;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by byang059 on 2020/4/30
 */
public class Config {

    public static List<String> NOT_RESPONSE_FORMAT_URLS = Lists.newArrayList("/swagger", "/v2/api-docs", "/error");
    public static List<String> NOT_SECURITY_URLS = Lists.newArrayList("/login");

}
