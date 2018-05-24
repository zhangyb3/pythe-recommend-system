package com.pythe.mapper;

import com.pythe.pojo.Users;
import com.pythe.pojo.UsersExample;
import com.pythe.pojo.VNewsVisitStatistics;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper {
    int countByExample(UsersExample example);

    int deleteByExample(UsersExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Users record);

    int insertSelective(Users record);

    List<Users> selectByExampleWithBLOBs(UsersExample example);

    List<Users> selectByExample(UsersExample example);

    Users selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByExampleWithBLOBs(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByExample(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKeyWithBLOBs(Users record);

    int updateByPrimaryKey(Users record);

    
    //查询指定时间后活跃用户
    List<Users> queryActiveUsersAfterDate(@Param("date") String date);

}