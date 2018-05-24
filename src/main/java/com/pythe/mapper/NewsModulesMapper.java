package com.pythe.mapper;

import com.pythe.pojo.NewsModules;
import com.pythe.pojo.NewsModulesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NewsModulesMapper {
    int countByExample(NewsModulesExample example);

    int deleteByExample(NewsModulesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(NewsModules record);

    int insertSelective(NewsModules record);

    List<NewsModules> selectByExampleWithBLOBs(NewsModulesExample example);

    List<NewsModules> selectByExample(NewsModulesExample example);

    NewsModules selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") NewsModules record, @Param("example") NewsModulesExample example);

    int updateByExampleWithBLOBs(@Param("record") NewsModules record, @Param("example") NewsModulesExample example);

    int updateByExample(@Param("record") NewsModules record, @Param("example") NewsModulesExample example);

    int updateByPrimaryKeySelective(NewsModules record);

    int updateByPrimaryKeyWithBLOBs(NewsModules record);
}