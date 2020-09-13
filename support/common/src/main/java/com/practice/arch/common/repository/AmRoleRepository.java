package com.practice.arch.common.repository;

import com.practice.arch.common.domain.AmRole;
import com.practice.arch.common.domain.AmRoleCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmRoleRepository {
    long countByExample(AmRoleCriteria example);

    int deleteByExample(AmRoleCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(AmRole record);

    int insertSelective(AmRole record);

    AmRole selectOneByExample(AmRoleCriteria example);

    List<AmRole> selectByExample(AmRoleCriteria example);

    AmRole selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AmRole record, @Param("example") AmRoleCriteria example);

    int updateByExample(@Param("record") AmRole record, @Param("example") AmRoleCriteria example);

    int updateByPrimaryKeySelective(AmRole record);

    int updateByPrimaryKey(AmRole record);

    int batchInsert(@Param("list") List<AmRole> list);

    int batchInsertSelective(@Param("list") List<AmRole> list, @Param("selective") AmRole.Column ... selective);
}