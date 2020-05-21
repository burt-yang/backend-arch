/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.component;

import com.practice.arch.commonarch.domain.dto.UserDTO;
import com.practice.arch.commonarch.exception.TokenExpiredException;
import com.practice.arch.commonarch.exception.TokenInvalidException;
import com.practice.arch.commonarch.exception.TokenNotFoundException;
import com.practice.arch.commonarch.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Created by byang059 on 5/18/20
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = (String) authentication.getPrincipal();
        if (StringUtils.isEmpty(jwtToken)) {
            throw new TokenNotFoundException("token not found");
        }
        UserDTO userDTO = tokenService.decodeAndVerify(jwtToken);
        String email = userDTO.getEmail();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        MDC.put("email", email);
        return new PreAuthenticatedAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
