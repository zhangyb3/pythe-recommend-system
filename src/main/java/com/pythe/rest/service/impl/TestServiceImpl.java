package com.pythe.rest.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.pythe.algorithms.PropGetKit;
import com.pythe.algorithms.RecommendKit;
import com.pythe.common.pojo.PytheResult;
import com.pythe.common.utils.DateUtils;
import com.pythe.common.utils.ExceptionUtil;
import com.pythe.common.utils.FactoryUtils;
import com.pythe.common.utils.HttpClientUtil;
import com.pythe.common.utils.JsonUtils;
import com.pythe.contentbasedrecommend.ContentBasedRecommender;
import com.pythe.mapper.NewsLogsMapper;
import com.pythe.mapper.NewsMapper;
import com.pythe.mapper.RecommendationsMapper;
import com.pythe.mapper.TblEssayRecommendationMapper;
import com.pythe.mapper.UsersMapper;
//import com.pythe.mapper.VNewsVisitStatisticsMapper;
import com.pythe.pojo.News;
import com.pythe.pojo.NewsExample;
import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.NewsLogsExample;
import com.pythe.pojo.NewsWithBLOBs;
import com.pythe.pojo.TblEssayRecommendation;
import com.pythe.pojo.TblEssayRecommendationExample;
import com.pythe.pojo.VNewsVisitStatistics;
import com.pythe.rest.service.TestService;

import com.pythe.hotrecommend.HotRecommender;
import com.pythe.pojo.NewsLogs;

	

@Service
public class TestServiceImpl implements TestService{
	
	@Autowired
	private NewsMapper newsMapper;
	
	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private RecommendationsMapper recommendationsMapper;
	
	@Autowired
	private TblEssayRecommendationMapper essayRecommendationMapper;
	
//	@Autowired
//	private VNewsVisitStatisticsMapper vNewsVisitStatisticsMapper;
	
	// 将每天生成的“热点新闻”ID，按照新闻的热点程度从高到低放入此List
	private static ArrayList<Long> topHotNewsList = new ArrayList<Long>();

	@Override
	public void executeInstantRecommendOnce() throws Exception {
		 
		PropGetKit.loadProperties("paraConfig");
		
		
		Date newsTime = new Date();
		NewsExample newsExample = new NewsExample();
		newsExample.createCriteria().andIdIsNotNull();
		NewsWithBLOBs newses = new NewsWithBLOBs();
		newses.setNewsTime(newsTime);
		newsMapper.updateByExampleSelective(newses, newsExample);
		
		NewsLogsExample newsLogsExample = new NewsLogsExample();
		newsLogsExample.createCriteria().andIdIsNotNull();
		NewsLogs newsLogs = new NewsLogs();
		newsLogs.setViewTime(newsTime);
		newsLogsMapper.updateByExampleSelective(newsLogs, newsLogsExample);
		
		
		
		List<Long> userList=new ArrayList<Long>();
		userList.add(1l);
		userList.add(2l);
		userList.add(3l);
		 
		
		topHotNewsList.clear();
		
		ArrayList<Long> hotNewsTobeRecommended = new ArrayList<Long>();
		
//		List<NewsLogs> newsLogsList = Newslogs.dao.find("select news_id,count(*) as visitNums from newslogs where view_time>"
//						+ RecommendKit.getInRecDate(-10) + " group by news_id order by visitNums desc");
		
		List<VNewsVisitStatistics> vnvses = newsLogsMapper.queryNewsVisitStatisticsAfterDate(RecommendKit.getInRecDate(-10));
		
//		for (NewsLogs newsLog:newsLogsList )
//		{
//			hotNewsTobeReccommended.add(newsLog.getNewsId());
//		}
		for (VNewsVisitStatistics vNewsVisitStatistics : vnvses) {
			hotNewsTobeRecommended.add(vNewsVisitStatistics.getNewsId());
		}
		for (Long news : hotNewsTobeRecommended)
		{
			topHotNewsList.add(news);
			System.out.println("=========================> thn : " + news);
		}
		
		
		ContentBasedRecommender contentBasedRecommender = new ContentBasedRecommender();
		contentBasedRecommender.recommend(userList);
		
		
		System.out.println("本次推荐结束于 " + new Date());
		
		
	}

	@Override
	public void generateMaterialKeyword() throws Exception {
		
		PropGetKit.loadProperties("paraConfig");
		
		ContentBasedRecommender contentBasedRecommender = new ContentBasedRecommender();
		contentBasedRecommender.genrateMaterialKeyword();
		
	}

	@Override
	public void generateMaterialKeywordFix() {
		
		TblEssayRecommendationExample essayRecommendationExample = new TblEssayRecommendationExample();
		essayRecommendationExample.createCriteria().andEssayidIsNotNull();
		List<TblEssayRecommendation> essayRecommendations = essayRecommendationMapper.selectByExampleWithBLOBs(essayRecommendationExample);
		for (TblEssayRecommendation essayRecommendation : essayRecommendations) {
			List<Long> essayIds = JsonUtils.jsonToList(essayRecommendation.getRecommendation(), Long.class);
			if(essayIds.contains(essayRecommendation.getEssayid()))
			{
				System.out.println(essayIds.size());
				essayIds.remove(essayRecommendation.getEssayid());
				System.out.println("!!! essay " + essayRecommendation.getEssayid());
				System.out.println(essayIds.size());
				essayRecommendation.setRecommendation(JsonUtils.objectToJson(essayIds));
				essayRecommendationMapper.updateByPrimaryKeySelective(essayRecommendation);
			}
		}
		System.out.println(" fix finish !!!!!!!!!!!!!!!!!");
	}



}
