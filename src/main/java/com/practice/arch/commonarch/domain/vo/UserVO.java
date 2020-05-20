/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.domain.vo;

import java.util.List;

/**
 * Created by byang059 on 5/18/20
 */
public class UserVO {
    private List<String> roles;
    private String email;
    private String accessToken;
    private String refreshToken;
}
