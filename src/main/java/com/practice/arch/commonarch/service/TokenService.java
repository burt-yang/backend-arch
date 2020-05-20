/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service;

import com.practice.arch.commonarch.domain.dto.UserDTO;

/**
 * Created by byang059 on 5/15/20
 */
public interface TokenService {

    String createAccessToken(String refreshToken);

    String createAccessToken(String refreshToken, UserDTO userVo);

    String createRefreshToken(UserDTO userVo);

    UserDTO decodeAndVerify(String token);

}
