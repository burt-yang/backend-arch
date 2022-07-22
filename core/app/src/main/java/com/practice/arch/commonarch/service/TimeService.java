package com.practice.arch.commonarch.service;

import com.practice.arch.common.domain.AmUser;

import java.util.List;

/**
 * Created by byang059 on 5/11/20
 */
public interface TimeService {
    Long getTime(String a,int b);

    Long testAspectj();

    List<AmUser> get();
}
