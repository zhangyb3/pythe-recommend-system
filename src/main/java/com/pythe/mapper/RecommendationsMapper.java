package com.pythe.mapper;

import com.pythe.pojo.Recommendations;
import com.pythe.pojo.RecommendationsExample;
import com.pythe.pojo.VRecommendationStatistics;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RecommendationsMapper {
    int countByExample(RecommendationsExample example);

    int deleteByExample(RecommendationsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Recommendations record);

    int insertSelective(Recommendations record);

    List<Recommendations> selectByExample(RecommendationsExample example);

    Recommendations selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Recommendations record, @Param("example") RecommendationsExample example);

    int updateByExample(@Param("record") Recommendations record, @Param("example") RecommendationsExample example);

    int updateByPrimaryKeySelective(Recommendations record);

    int updateByPrimaryKey(Recommendations record);

    
    //查询指定日期后的指定用户推荐数据
    List<Recommendations> queryCertainUserRecentlyRecommendations(@Param("userId") Long userId, @Param("date") String date);

    
    //获得已经预备为当前用户推荐的新闻
    VRecommendationStatistics queryRecommendationStatisticsAfterDate(@Param("date") String date, @Param("userId") Long userId);

  
}