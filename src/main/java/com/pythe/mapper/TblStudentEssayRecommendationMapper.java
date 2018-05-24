package com.pythe.mapper;

import com.pythe.pojo.Recommendations;
import com.pythe.pojo.TblStudentEssayRecommendation;
import com.pythe.pojo.TblStudentEssayRecommendationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TblStudentEssayRecommendationMapper {
    int countByExample(TblStudentEssayRecommendationExample example);

    int deleteByExample(TblStudentEssayRecommendationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TblStudentEssayRecommendation record);

    int insertSelective(TblStudentEssayRecommendation record);

    List<TblStudentEssayRecommendation> selectByExample(TblStudentEssayRecommendationExample example);

    TblStudentEssayRecommendation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TblStudentEssayRecommendation record, @Param("example") TblStudentEssayRecommendationExample example);

    int updateByExample(@Param("record") TblStudentEssayRecommendation record, @Param("example") TblStudentEssayRecommendationExample example);

    int updateByPrimaryKeySelective(TblStudentEssayRecommendation record);

    int updateByPrimaryKey(TblStudentEssayRecommendation record);

    
    //查询指定日期后的指定用户推荐数据
    List<TblStudentEssayRecommendation> queryCertainUserRecentlyRecommendations(@Param("userId") Long userId, @Param("date") String date);

    
}