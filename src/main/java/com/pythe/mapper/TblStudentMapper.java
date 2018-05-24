package com.pythe.mapper;

import com.pythe.pojo.TblStudent;
import com.pythe.pojo.TblStudentExample;
import com.pythe.pojo.TblStudentWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblStudentMapper {
    int countByExample(TblStudentExample example);

    int deleteByExample(TblStudentExample example);

    int deleteByPrimaryKey(Long studentid);

    int insert(TblStudentWithBLOBs record);

    int insertSelective(TblStudentWithBLOBs record);

    List<TblStudentWithBLOBs> selectByExampleWithBLOBs(TblStudentExample example);

    List<TblStudent> selectByExample(TblStudentExample example);

    TblStudentWithBLOBs selectByPrimaryKey(Long studentid);

    int updateByExampleSelective(@Param("record") TblStudentWithBLOBs record, @Param("example") TblStudentExample example);

    int updateByExampleWithBLOBs(@Param("record") TblStudentWithBLOBs record, @Param("example") TblStudentExample example);

    int updateByExample(@Param("record") TblStudent record, @Param("example") TblStudentExample example);

    int updateByPrimaryKeySelective(TblStudentWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TblStudentWithBLOBs record);

    int updateByPrimaryKey(TblStudent record);
}