package com.pythe.mapper;

import com.pythe.pojo.TblStudentEssayPreference;
import com.pythe.pojo.TblStudentEssayPreferenceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblStudentEssayPreferenceMapper {
    int countByExample(TblStudentEssayPreferenceExample example);

    int deleteByExample(TblStudentEssayPreferenceExample example);

    int deleteByPrimaryKey(Long studentId);

    int insert(TblStudentEssayPreference record);

    int insertSelective(TblStudentEssayPreference record);

    List<TblStudentEssayPreference> selectByExampleWithBLOBs(TblStudentEssayPreferenceExample example);

    List<TblStudentEssayPreference> selectByExample(TblStudentEssayPreferenceExample example);

    TblStudentEssayPreference selectByPrimaryKey(Long studentId);

    int updateByExampleSelective(@Param("record") TblStudentEssayPreference record, @Param("example") TblStudentEssayPreferenceExample example);

    int updateByExampleWithBLOBs(@Param("record") TblStudentEssayPreference record, @Param("example") TblStudentEssayPreferenceExample example);

    int updateByExample(@Param("record") TblStudentEssayPreference record, @Param("example") TblStudentEssayPreferenceExample example);

    int updateByPrimaryKeySelective(TblStudentEssayPreference record);

    int updateByPrimaryKeyWithBLOBs(TblStudentEssayPreference record);

    int updateByPrimaryKey(TblStudentEssayPreference record);
}