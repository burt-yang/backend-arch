package com.practice.arch.common.repository;

import com.practice.arch.common.domain.AmUser;
import com.practice.arch.common.domain.AmUserCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmUserRepository {
    long countByExample(AmUserCriteria example);

    int deleteByExample(AmUserCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(AmUser record);

    int insertSelective(AmUser record);

    AmUser selectOneByExample(AmUserCriteria example);

    List<AmUser> selectByExample(AmUserCriteria example);

    AmUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AmUser record, @Param("example") AmUserCriteria example);

    int updateByExample(@Param("record") AmUser record, @Param("example") AmUserCriteria example);

    int updateByPrimaryKeySelective(AmUser record);

    int updateByPrimaryKey(AmUser record);

    int batchInsert(@Param("list") List<AmUser> list);

    int batchInsertSelective(@Param("list") List<AmUser> list, @Param("selective") AmUser.Column ... selective);
}