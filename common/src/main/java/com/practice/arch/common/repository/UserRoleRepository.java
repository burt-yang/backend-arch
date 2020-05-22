package com.practice.arch.common.repository;

import com.practice.arch.common.domain.Userrole;
import com.practice.arch.common.domain.UserroleCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserroleRepository {
    long countByExample(UserroleCriteria example);

    int deleteByExample(UserroleCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userrole record);

    int insertSelective(Userrole record);

    Userrole selectOneByExample(UserroleCriteria example);

    List<Userrole> selectByExample(UserroleCriteria example);

    Userrole selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userrole record, @Param("example") UserroleCriteria example);

    int updateByExample(@Param("record") Userrole record, @Param("example") UserroleCriteria example);

    int updateByPrimaryKeySelective(Userrole record);

    int updateByPrimaryKey(Userrole record);
}