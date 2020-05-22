/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.service.impl;

import com.google.common.collect.Lists;
import com.practice.arch.commonarch.domain.dto.UserDTO;
import com.practice.arch.commonarch.domain.po.RolePO;
import com.practice.arch.commonarch.domain.po.UserPO;
import com.practice.arch.commonarch.service.TokenService;
import com.practice.arch.commonarch.service.UserService;
import com.practice.arch.generator.domain.User;
import com.practice.arch.generator.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Created by byang059 on 5/12/20
 */
@Service
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserPO userPo = new UserPO();
        userPo.setEmail("abc@163.com");
        userPo.setPassword("$2a$10$kvC0enKET/HfqX0RnbGFjOMDoLLaN5D0HepaaE3xCSpsQmCJobQvW");
        RolePO rolePo = new RolePO();
        rolePo.setName("ROLE_ADMIN");
        userPo.setRoles(Lists.newArrayList(rolePo));
        return userPo;
    }

    @Override
    public UserDTO login(@NotEmpty String userName, @NotEmpty String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, password);
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserDTO user = UserDTO.from((UserPO) authenticate.getPrincipal());
        String refreshToken = tokenService.createRefreshToken(user);
        String accessToken = tokenService.createAccessToken(refreshToken, user);
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        final User record = new User();
        record.setEmail("abc@163.com");
        record.setPassword("$2a$10$kvC0enKET/HfqX0RnbGFjOMDoLLaN5D0HepaaE3xCSpsQmCJobQvW");
        userRepository.insert(record);
        return user;
    }
}
