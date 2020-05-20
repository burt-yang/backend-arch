package com.practice.arch.commonarch.service;

import com.practice.arch.commonarch.domain.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Created by byang059 on 5/12/20
 */

public interface UserService extends UserDetailsService {
    UserDTO login(@NotEmpty String userName, @NotEmpty String password);
}
