/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.practice.arch.commonarch.domain.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by byang059 on 5/20/20
 */
@Mapper
public interface UserRepository extends BaseMapper<UserPO> {
}
