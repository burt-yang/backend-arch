package com.practice.arch.generator.repository;

import com.practice.arch.generator.domain.Role;
import com.practice.arch.generator.domain.RoleCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleRepository {
    long countByExample(RoleCriteria example);

    int deleteByExample(RoleCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByExample(RoleCriteria example);

    Role selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Role record, @Param("example") RoleCriteria example);

    int updateByExample(@Param("record") Role record, @Param("example") RoleCriteria example);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
}