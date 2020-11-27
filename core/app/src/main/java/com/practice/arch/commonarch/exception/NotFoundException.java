/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.exception;

import com.practice.arch.commonarch.enums.ResultCode;

/**
 * Created by byang059 on 9/16/20
 */
public class NotFoundException extends AppException{
    public NotFoundException(ResultCode resultCode) {
        super(resultCode);
    }
}
