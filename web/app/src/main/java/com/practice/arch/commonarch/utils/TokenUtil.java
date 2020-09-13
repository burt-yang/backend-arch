/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.utils;

import com.practice.arch.commonarch.exception.TokenExpiredException;
import com.practice.arch.commonarch.exception.TokenInvalidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

/**
 * Created by byang059 on 5/15/20
 */
public class TokenUtil {

    private TokenUtil() {
    }

    public static String verifyToken(Key key, String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("token is expired", e);
        } catch (Exception e) {
            throw new TokenInvalidException("token is invalid", e);
        }
    }

    public static String createToken(Key key, Date expireDate, String content) {
        return Jwts.builder()
                .setSubject(content)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }
}
