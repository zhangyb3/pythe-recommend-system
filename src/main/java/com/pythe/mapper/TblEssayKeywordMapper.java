package com.pythe.mapper;

import com.pythe.pojo.TblEssayKeyword;
import com.pythe.pojo.TblEssayKeywordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblEssayKeywordMapper {
    int countByExample(TblEssayKeywordExample example);

    int deleteByExample(TblEssayKeywordExample example);

    int deleteByPrimaryKey(Long essayId);

    int insert(TblEssayKeyword record);

    int insertSelective(TblEssayKeyword record);

    List<TblEssayKeyword> selectByExampleWithBLOBs(TblEssayKeywordExample example);

    List<TblEssayKeyword> selectByExample(TblEssayKeywordExample example);

    TblEssayKeyword selectByPrimaryKey(Long essayId);

    int updateByExampleSelective(@Param("record") TblEssayKeyword record, @Param("example") TblEssayKeywordExample example);

    int updateByExampleWithBLOBs(@Param("record") TblEssayKeyword record, @Param("example") TblEssayKeywordExample example);

    int updateByExample(@Param("record") TblEssayKeyword record, @Param("example") TblEssayKeywordExample example);

    int updateByPrimaryKeySelective(TblEssayKeyword record);

    int updateByPrimaryKeyWithBLOBs(TblEssayKeyword record);

    int updateByPrimaryKey(TblEssayKeyword record);
}