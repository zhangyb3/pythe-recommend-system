package com.pythe.mapper;

import com.pythe.pojo.TblEssay;
import com.pythe.pojo.TblEssayExample;
import com.pythe.pojo.TblEssayWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblEssayMapper {
    int countByExample(TblEssayExample example);

    int deleteByExample(TblEssayExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TblEssayWithBLOBs record);

    int insertSelective(TblEssayWithBLOBs record);

    List<TblEssayWithBLOBs> selectByExampleWithBLOBs(TblEssayExample example);

    List<TblEssay> selectByExample(TblEssayExample example);

    TblEssayWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TblEssayWithBLOBs record, @Param("example") TblEssayExample example);

    int updateByExampleWithBLOBs(@Param("record") TblEssayWithBLOBs record, @Param("example") TblEssayExample example);

    int updateByExample(@Param("record") TblEssay record, @Param("example") TblEssayExample example);

    int updateByPrimaryKeySelective(TblEssayWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TblEssayWithBLOBs record);

    int updateByPrimaryKey(TblEssay record);
}