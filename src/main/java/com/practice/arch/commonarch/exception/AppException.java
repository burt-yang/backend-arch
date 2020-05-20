/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.exception;

import com.practice.arch.commonarch.enums.ResultCode;
import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by byang059 on 2020/4/30
 */
@Getter
public class AppException extends RuntimeException {

    private ResultCode resultCode;

    public AppException(ResultCode resultCode) {
        super(resultCode.getDefaultMessage(), null, false, false);
        this.resultCode = resultCode;
    }

    public AppException(ResultCode resultCode, String messagePattern, Object... args) {
        super(MessageFormatter.arrayFormat(messagePattern, args).getMessage(), null, false, false);
        this.resultCode = resultCode;
    }

    public AppException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getDefaultMessage(), cause, false, false);
        this.resultCode = resultCode;
    }
}
