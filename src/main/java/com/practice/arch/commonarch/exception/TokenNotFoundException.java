/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by byang059 on 5/17/20
 */
public class TokenNotFoundException extends AuthenticationException {

    public TokenNotFoundException(String msg) {
        super(msg);
    }
}
