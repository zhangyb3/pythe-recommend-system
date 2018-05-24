package com.pythe.mapper;

import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.VEssayViewLog;
import com.pythe.pojo.VEssayViewLogExample;
import com.pythe.pojo.VNewsVisitStatistics;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VEssayViewLogMapper {
    int countByExample(VEssayViewLogExample example);

    int deleteByExample(VEssayViewLogExample example);

    int insert(VEssayViewLog record);

    int insertSelective(VEssayViewLog record);

    List<VEssayViewLog> selectByExample(VEssayViewLogExample example);

    int updateByExampleSelective(@Param("record") VEssayViewLog record, @Param("example") VEssayViewLogExample example);

    int updateByExample(@Param("record") VEssayViewLog record, @Param("example") VEssayViewLogExample example);

    
    //查询当天新闻访问纪录
    List<VEssayViewLog> queryEssayViewLogsTheDate(@Param("date") String date);

    //查询指定时间后新闻访问纪录统计
//    List<VNewsVisitStatistics> queryNewsVisitStatisticsAfterDate(@Param("date") String date);

}