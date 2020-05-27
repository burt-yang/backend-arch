/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.controller;

import com.practice.arch.commonarch.component.RateLimiter;
import com.practice.arch.commonarch.domain.dto.UserDTO;
import com.practice.arch.commonarch.service.TimeService;
import com.practice.arch.commonarch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by byang059 on 5/9/20
 */
@RestController
@Slf4j
@Validated
public class UserController {

    @Autowired
    TimeService timeService;

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public UserDTO login(@RequestParam String userName, @RequestParam String password) {
        return userService.login(userName, password);
    }

    @RateLimiter(replenishRate = 1, burstCapacity = 100)
    @PostMapping("/time")
    public boolean getTime(@RequestBody Map<String, Object> param) {
        log.info(param.toString());
        timeService.getTime("ab", 2);
//        get();
        return false;
    }

    private boolean get() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
