/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service.impl;

import com.practice.arch.commonarch.domain.po.UserPO;
import com.practice.arch.commonarch.domain.dto.UserDTO;
import com.practice.arch.commonarch.service.TokenService;
import com.practice.arch.commonarch.service.UserService;
import com.practice.arch.commonarch.utils.JsonUtil;
import com.practice.arch.commonarch.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Created by byang059 on 5/15/20
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    @Lazy
    UserService userService;

    @Autowired
    Key key;

    @Override
    public String createAccessToken(String refreshToken) {
        UserDTO userVo = decodeAndVerify(refreshToken);
        UserPO userPo = (UserPO) userService.loadUserByUsername(userVo.getEmail());
        return createTokenByUserVo(UserDTO.from(userPo), 1800);
    }

    @Override
    public String createAccessToken(String refreshToken, UserDTO user) {
        return createTokenByUserVo(user, 1800);
    }

    @Override
    public String createRefreshToken(UserDTO userVo) {
        return createTokenByUserVo(userVo, 3600);
    }

    @Override
    public UserDTO decodeAndVerify(String token) {
        String userJson = TokenUtil.verifyToken(key, token);
        return JsonUtil.json2Bean(userJson, UserDTO.class);
    }

    private String createTokenByUserVo(UserDTO userVo, int seconds) {
        String userJson = JsonUtil.bean2Json(userVo);
        long expiredTime = System.currentTimeMillis() + seconds * 1000;
        return TokenUtil.createToken(key, new Date(expiredTime), userJson);
    }
}
