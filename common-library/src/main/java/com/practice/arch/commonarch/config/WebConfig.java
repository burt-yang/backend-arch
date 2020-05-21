/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Created by byang059 on 5/19/20
 */
@Configuration
public class WebConfig {
    @Bean
    public MethodValidationPostProcessor getMethodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
