package com.pythe.mapper;

import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.NewsLogsExample;
import com.pythe.pojo.VNewsVisitStatistics;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NewsLogsMapper {
    int countByExample(NewsLogsExample example);

    int deleteByExample(NewsLogsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(NewsLogs record);

    int insertSelective(NewsLogs record);

    List<NewsLogs> selectByExample(NewsLogsExample example);

    NewsLogs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") NewsLogs record, @Param("example") NewsLogsExample example);

    int updateByExample(@Param("record") NewsLogs record, @Param("example") NewsLogsExample example);

    int updateByPrimaryKeySelective(NewsLogs record);

    int updateByPrimaryKey(NewsLogs record);

    
    //查询当天新闻访问纪录
    List<NewsLogs> queryNewsLogsTheDate(@Param("date") String date);

    //查询指定时间后新闻访问纪录统计
    List<VNewsVisitStatistics> queryNewsVisitStatisticsAfterDate(@Param("date") String date);

}