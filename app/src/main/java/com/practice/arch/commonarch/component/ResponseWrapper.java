/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by byang059 on 2020/4/30
 */
@Setter
@Getter
@ToString
public class ResponseWrapper {
    private int code;
    private String message;
    private Object data;

    private ResponseWrapper(ResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getDefaultMessage();
        this.data = data;
    }

    private ResponseWrapper(ResultCode resultCode, String message, Object data) {
        this.code = resultCode.getCode();
        this.message = message;
        this.data = data;
    }

    public static ResponseWrapper success(Object data) {
        return new ResponseWrapper(ResultCode.SUCCESS, data);
    }

    public static ResponseWrapper failure(ResultCode resultCode) {
        return new ResponseWrapper(resultCode, "");
    }

    public static ResponseWrapper failure(ResultCode resultCode, String message) {
        return new ResponseWrapper(resultCode, message, "");
    }
}
