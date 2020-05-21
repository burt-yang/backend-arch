/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.domain.dto;

import com.practice.arch.commonarch.domain.po.RolePO;
import com.practice.arch.commonarch.domain.po.UserPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by byang059 on 5/15/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private List<String> roles;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String test;

    public static UserDTO from(UserPO user) {
        return UserDTO.builder().email(user.getEmail()).roles(user.getRoles().stream().map(RolePO::getName).collect(Collectors.toList())).build();
    }
}
