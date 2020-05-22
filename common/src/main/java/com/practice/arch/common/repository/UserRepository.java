package com.practice.arch.common.repository;

import com.practice.arch.common.domain.User;
import com.practice.arch.common.domain.UserCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserRepository {
    long countByExample(UserCriteria example);

    int deleteByExample(UserCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectOneByExample(UserCriteria example);

    List<User> selectByExample(UserCriteria example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserCriteria example);

    int updateByExample(@Param("record") User record, @Param("example") UserCriteria example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}