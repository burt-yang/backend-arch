/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service.impl;

import com.practice.arch.commonarch.enums.ResultCode;
import com.practice.arch.commonarch.exception.AppException;
import com.practice.arch.commonarch.service.TimeService;
import org.springframework.stereotype.Service;

/**
 * Created by byang059 on 5/11/20
 */
@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public Long getTime(String a, int b) {
        get();
        throw new AppException(ResultCode.USER_NOT_FOUND);
    }

    private boolean get(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
