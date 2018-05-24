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
import com.pythe.contentbasedrecommend.ContentBasedRecommender;
import com.pythe.mapper.NewsLogsMapper;
import com.pythe.mapper.NewsMapper;
import com.pythe.mapper.RecommendationsMapper;
import com.pythe.mapper.TblStudentMapper;
import com.pythe.mapper.UsersMapper;
//import com.pythe.mapper.VNewsVisitStatisticsMapper;
import com.pythe.pojo.News;
import com.pythe.pojo.NewsExample;
import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.NewsLogsExample;
import com.pythe.pojo.NewsWithBLOBs;
import com.pythe.pojo.TblStudent;
import com.pythe.pojo.TblStudentExample;
import com.pythe.pojo.Users;
import com.pythe.pojo.VNewsVisitStatistics;
import com.pythe.rest.service.RecommendService;
import com.pythe.rest.service.TestService;

import com.pythe.hotrecommend.HotRecommender;
import com.pythe.pojo.NewsLogs;

	

@Service
public class RecommendServiceImpl implements RecommendService{
	
	@Autowired
	private NewsMapper newsMapper;
	
	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private RecommendationsMapper recommendationsMapper;
	
	@Autowired
	private TblStudentMapper studentMapper;

	
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
		contentBasedRecommender.recommendTest(userList);
		
		
		System.out.println("本次推荐结束于 " + new Date());
		
		
	}

	@Override
	public PytheResult recommendEssayEveryday(String parameters) throws Exception {
		
		PropGetKit.loadProperties("paraConfig");
		
		JSONObject p = JSONObject.parseObject(parameters);
		String recommendMode = p.getString("mode");
		
		TblStudentExample studentExample = new TblStudentExample();
		studentExample.createCriteria().andStudentidIsNotNull();
		List<TblStudent> students = studentMapper.selectByExample(studentExample);
		List<Long> studentIds=new ArrayList<Long>();
		for (TblStudent tblStudent : students) {
			studentIds.add(tblStudent.getStudentid());
		}
		
		//基于内容推荐
		if(recommendMode.equals("CB"))
		{
			ContentBasedRecommender contentBasedRecommender = new ContentBasedRecommender();
			contentBasedRecommender.recommend(studentIds);
		}
		
		return null;
	}



}
