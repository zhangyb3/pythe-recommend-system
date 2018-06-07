package com.pythe.mapper;

import com.pythe.pojo.TblEssayRecommendation;
import com.pythe.pojo.TblEssayRecommendationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblEssayRecommendationMapper {
    int countByExample(TblEssayRecommendationExample example);

    int deleteByExample(TblEssayRecommendationExample example);

    int deleteByPrimaryKey(Long essayid);

    int insert(TblEssayRecommendation record);

    int insertSelective(TblEssayRecommendation record);

    List<TblEssayRecommendation> selectByExampleWithBLOBs(TblEssayRecommendationExample example);

    List<TblEssayRecommendation> selectByExample(TblEssayRecommendationExample example);

    TblEssayRecommendation selectByPrimaryKey(Long essayid);

    int updateByExampleSelective(@Param("record") TblEssayRecommendation record, @Param("example") TblEssayRecommendationExample example);

    int updateByExampleWithBLOBs(@Param("record") TblEssayRecommendation record, @Param("example") TblEssayRecommendationExample example);

    int updateByExample(@Param("record") TblEssayRecommendation record, @Param("example") TblEssayRecommendationExample example);

    int updateByPrimaryKeySelective(TblEssayRecommendation record);

    int updateByPrimaryKeyWithBLOBs(TblEssayRecommendation record);

    int updateByPrimaryKey(TblEssayRecommendation record);
}