/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.enums;

import com.practice.arch.commonarch.component.response.ResponseWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Created by byang059 on 2020/4/30
 */
@Getter
@AllArgsConstructor
@ToString
public enum ResultCode {

    SUCCESS(2000, OK, "OK"),

    FRAMEWORK_ERROR(4000, BAD_REQUEST, "Framework error"),
    PARAM_NOT_VALID(4001, BAD_REQUEST, "Param is not valid"),

    TOKEN_EXPIRED(4101, UNAUTHORIZED, "Token is expired"),
    TOKEN_INVALID(4102, UNAUTHORIZED, "Token is not valid"),
    TOKEN_NOT_FOUND(4103, UNAUTHORIZED, "Token not found"),
    BAD_CREDENTIALS(4104, UNAUTHORIZED, "Password is wrong"),
    ACCOUNT_LOCKED(4105, UNAUTHORIZED, "Account is lock"),
    ACCOUNT_EXPIRED(4106, UNAUTHORIZED, "Account is expired"),
    CREDENTIALS_EXPIRED(4107, UNAUTHORIZED, "Password is expired"),
    ACCOUNT_Disabled(4108, UNAUTHORIZED, "Account is lock"),
    AUTHENTICATION_FAILURE(4109, UNAUTHORIZED, "Authentication failure"),

    ACCESS_DENY(4300, FORBIDDEN, "Access deny"),

    USER_NOT_FOUND(4400, NOT_FOUND, "User not exist"),
    LIMITED_DISTRIBUTED_LOCK(4401, NOT_FOUND, "Operation resource is limited, please try again"),

    SERVER_ERROR(5000, INTERNAL_SERVER_ERROR, "internal error, please contact system admin"),
    RATE_LIMITER_ERROR(5200, BAD_GATEWAY, "request is too much, as server is busy, please try later");

    private Integer code;
    private HttpStatus httpStatus;
    private String defaultMessage;

    public ResponseEntity<Object> toResponseEntity(String message) {
        return ResponseEntity.status(getHttpStatus()).body(ResponseWrapper.failure(this, message));
    }

    public ResponseEntity<Object> toResponseEntity() {
        return ResponseEntity.status(getHttpStatus()).body(ResponseWrapper.failure(this));
    }
}
