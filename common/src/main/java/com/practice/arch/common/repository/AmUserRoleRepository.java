package com.practice.arch.common.repository;

import com.practice.arch.common.domain.AmUserRole;
import com.practice.arch.common.domain.AmUserRoleCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AmUserRoleRepository {
    long countByExample(AmUserRoleCriteria example);

    int deleteByExample(AmUserRoleCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(AmUserRole record);

    int insertSelective(AmUserRole record);

    AmUserRole selectOneByExample(AmUserRoleCriteria example);

    List<AmUserRole> selectByExample(AmUserRoleCriteria example);

    AmUserRole selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AmUserRole record, @Param("example") AmUserRoleCriteria example);

    int updateByExample(@Param("record") AmUserRole record, @Param("example") AmUserRoleCriteria example);

    int updateByPrimaryKeySelective(AmUserRole record);

    int updateByPrimaryKey(AmUserRole record);

    int batchInsert(@Param("list") List<AmUserRole> list);

    int batchInsertSelective(@Param("list") List<AmUserRole> list, @Param("selective") AmUserRole.Column ... selective);
}