/**
 * 
 */
package com.pythe.contentbasedrecommend;

import java.io.Console;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.app.keyword.Keyword;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Session;
import com.pythe.algorithms.PropGetKit;
import com.pythe.algorithms.RecommendAlgorithm;
import com.pythe.algorithms.RecommendKit;
import com.pythe.common.pojo.PytheResult;
import com.pythe.common.utils.JsonUtils;
import com.pythe.mapper.NewsLogsMapper;
import com.pythe.mapper.NewsMapper;
import com.pythe.mapper.RecommendationsMapper;
import com.pythe.mapper.TblEssayKeywordMapper;
import com.pythe.mapper.TblEssayMapper;
import com.pythe.mapper.TblEssayRecommendationMapper;
import com.pythe.mapper.TblStudentEssayPreferenceMapper;
import com.pythe.mapper.TblStudentEssayRecommendationMapper;
import com.pythe.mapper.TblStudentMapper;
import com.pythe.mapper.UsersMapper;
import com.pythe.mapper.VEssayViewLogMapper;
import com.pythe.pojo.News;
import com.pythe.pojo.NewsExample;
import com.pythe.pojo.NewsWithBLOBs;
import com.pythe.pojo.TblEssay;
import com.pythe.pojo.TblEssayExample;
import com.pythe.pojo.TblEssayKeyword;
import com.pythe.pojo.TblEssayRecommendation;
import com.pythe.pojo.TblEssayWithBLOBs;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年10月20日 基于内容的推荐 Content-Based
 * 
 *       思路：提取抓取进来的新闻的关键词列表（tf-idf），与每个用户的喜好关键词列表，做关键词相似度计算，取最相似的N个新闻推荐给用户。
 * 
 *       Procedure: 1、Every time that the recommendation is started up(according
 *       to quartz framework), the current day's coming in news will be
 *       processed by class TF-IDF's getTFIDF() method to obtain their key words
 *       list.And then the system go over every user and calculate the
 *       similarity between every news's key words list with user's preference
 *       list.After that, rank the news according to the similarities and
 *       recommend them to users.
 */
@Service
public class ContentBasedRecommender implements RecommendAlgorithm
{
	private RecommendKit recommendKit;
	
//	@Autowired
	private NewsMapper newsMapper;
	
//	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
//	@Autowired
	private UsersMapper usersMapper;
	
	private TblStudentMapper studentMapper;
	
	private TblEssayMapper essayMapper;
	
	private VEssayViewLogMapper essayViewLogMapper;
	
	private TblStudentEssayPreferenceMapper studentEssayPreferenceMapper;
	
	private TblStudentEssayRecommendationMapper studentEssayRecommendationMapper;
	
	private TblEssayRecommendationMapper essayRecommendationMapper;
	
//	@Autowired
	private RecommendationsMapper recommendationsMapper;
	
	private TblEssayKeywordMapper essayKeywordMapper;
	
	private static SqlSessionFactory sqlSessionFactory;
	
	private static SqlSession session;
	
	public ContentBasedRecommender() throws Exception {
		//配置文件的名称  
        String resource = "mybatis/configuration.xml";  
        //通过Mybatis包中的Resources对象很轻松的获取到配置文件  
        Reader reader = Resources.getResourceAsReader(resource);  
        //通过SqlSessionFactoryBuilder创建  
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        //获得session实例  
        session = sqlSessionFactory.openSession();
		this.newsMapper = session.getMapper(NewsMapper.class);
		this.newsLogsMapper = session.getMapper(NewsLogsMapper.class);
		this.usersMapper = session.getMapper(UsersMapper.class);
		this.recommendationsMapper = session.getMapper(RecommendationsMapper.class);
		
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		this.essayViewLogMapper = session.getMapper(VEssayViewLogMapper.class);
		this.studentEssayPreferenceMapper = session.getMapper(TblStudentEssayPreferenceMapper.class);
		this.studentEssayRecommendationMapper = session.getMapper(TblStudentEssayRecommendationMapper.class);
		this.essayKeywordMapper = session.getMapper(TblEssayKeywordMapper.class);
		this.essayRecommendationMapper = session.getMapper(TblEssayRecommendationMapper.class);
		
		recommendKit = new RecommendKit();
		
	}
	
	public static final Logger logger = Logger.getLogger(ContentBasedRecommender.class);

	// TFIDF提取关键词数
	private static final int KEY_WORDS_NUM = PropGetKit.getInt("TFIDFKeywordsNum");
//	private static final int KEY_WORDS_NUM = PropGetKit.TFIDF_KEYWORDS_NUM;

	// 推荐新闻数
	private static final int N = PropGetKit.getInt("CBRecNum");
//	private static final int N = PropGetKit.CB_REC_NUM;

	@Override
	public void recommendTest(List<Long> users)
	{
		//获得session实例  
//        SqlSession session = sqlSessionFactory.openSession();
		this.usersMapper = session.getMapper(UsersMapper.class);
		
		try
		{
			int count=0;
			System.out.println("CB start at "+ new Date());
			// 首先进行用户喜好关键词列表的衰减更新+用户当日历史浏览记录的更新
			UserPrefRefresher userPrefRefresher = new UserPrefRefresher();
			userPrefRefresher.refresh(users);System.out.println("==========================> user quantity : " + users.size());
			
			// 新闻及对应关键词列表的Map
			HashMap<Long, List<Keyword>> newsKeyWordsMap = new HashMap<Long, List<Keyword>>();
			HashMap<Long, Integer> newsModuleMap = new HashMap<Long, Integer>();
			// 用户喜好关键词列表
			HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = recommendKit
					.getUserPrefListMap(users);
//			List<News> newsList=News.dao.find("select id,title,content,module_id from news where news_time>"
//							+ RecommendKit.getInRecDate());
			
			for (Long userId : users) {
				System.out.println("!!!!!!! user " + userId + " : " + userPrefListMap.get(userId));;
			}
			List<NewsWithBLOBs> newsList = newsMapper.queryNewsAfterDate(RecommendKit.getInRecDate());
			for (NewsWithBLOBs news:newsList)
			{
				List<Keyword> keywords = TFIDF.getTFIDF(news.getTitle(), news.getContent(), KEY_WORDS_NUM);
				newsKeyWordsMap.put(news.getId(), keywords);
				System.out.println("news " + news.getId() + " , "  );
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
				}
				newsModuleMap.put(news.getId(), news.getModuleId());
			}

			for (Long userId : users)
			{
				Map<Long, Double> tempMatchMap = new HashMap<Long, Double>();
				Iterator<Long> ite = newsKeyWordsMap.keySet().iterator();
				while (ite.hasNext())
				{
					Long newsId = ite.next();
					int moduleId = newsModuleMap.get(newsId);
					if (null != userPrefListMap.get(userId).get(moduleId))
						tempMatchMap.put(newsId,
								getMatchValue(userPrefListMap.get(userId).get(moduleId), newsKeyWordsMap.get(newsId)));
					else
						continue;
				}
				System.err.println("============================> temp match : " + JSONObject.toJSONString(tempMatchMap));
				// 去除匹配值为0的项目
				removeZeroItem(tempMatchMap);
				if (!(tempMatchMap.toString().equals("{}")))
				{
					tempMatchMap = sortMapByValue(tempMatchMap);
					Set<Long> toBeRecommended=tempMatchMap.keySet();
					System.out.println("======================> to be recommended : " + toBeRecommended.size());
					for (Long rec : toBeRecommended) {
						System.out.println(rec);
					}
					
					//过滤掉已经推荐过的新闻
					recommendKit.filterReccedNews(toBeRecommended,userId);
					//过滤掉用户已经看过的新闻
					recommendKit.filterBrowsedNews(toBeRecommended, userId);
					//如果可推荐新闻数目超过了系统默认为CB算法设置的单日推荐上限数（N），则去掉一部分多余的可推荐新闻，剩下的N个新闻才进行推荐
					if(toBeRecommended.size()>N){
//						RecommendKit.removeOverNews(toBeRecommended,N);
					}
					System.out.println("fiter: " + toBeRecommended.size());
					recommendKit.insertRecommend(userId, toBeRecommended,RecommendAlgorithm.CB);
					count+=toBeRecommended.size();
				}
			}
			System.out.println("CB has contributed " + (count/users.size()) + " recommending news on average");
			System.out.println("CB finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 获得用户的关键词列表和新闻关键词列表的匹配程度
	 * 
	 * @return
	 */
	private double getMatchValue(CustomizedHashMap<String, Double> map, List<Keyword> list)
	{
		Set<String> keywordsSet = map.keySet();
		double matchValue = 0;
		for (Keyword keyword : list)
		{
			if (keywordsSet.contains(keyword.getName()))
			{
				matchValue += keyword.getScore() * map.get(keyword.getName());
			}
		}
		return matchValue;
	}

	private void removeZeroItem(Map<Long, Double> map)
	{
		HashSet<Long> toBeDeleteItemSet = new HashSet<Long>();
		Iterator<Long> ite = map.keySet().iterator();
		while (ite.hasNext())
		{
			Long newsId = ite.next();
			if (map.get(newsId) <= 0)
			{
				toBeDeleteItemSet.add(newsId);
			}
		}
		for (Long item : toBeDeleteItemSet)
		{
			map.remove(item);
		}
	}
	
	/**
	 * 使用 Map按value进行排序
	 * @param map
	 * @return
	 */
	public static Map<Long, Double> sortMapByValue(Map<Long, Double> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<Long, Double> sortedMap = new LinkedHashMap<Long, Double>();
		List<Map.Entry<Long, Double>> entryList = new ArrayList<Map.Entry<Long, Double>>(
				oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator());

		Iterator<Map.Entry<Long, Double>> iter = entryList.iterator();
		Map.Entry<Long, Double> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

	@Override
	public void recommend(List<Long> students) throws Exception {
		
		this.studentMapper = session.getMapper(TblStudentMapper.class);
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		
		try
		{
			int count=0;
			System.out.println("CB recommend start at "+ new Date());
			
			// 首先进行用户喜好关键词列表的衰减更新+用户当日历史浏览记录的更新
			UserPrefRefresher userPrefRefresher = new UserPrefRefresher();
			userPrefRefresher.refreshStudentPreference(students);
			System.out.println("==========================> student quantity : " + students.size());
			
			// 文章及对应关键词列表的Map
			HashMap<Long, List<Keyword>> essayKeyWordsMap = new HashMap<Long, List<Keyword>>();
			HashMap<Long, Integer> essayTypeMap = new HashMap<Long, Integer>();
			// 用户喜好关键词列表
			HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = recommendKit
					.getStudentEssayPrefListMap(students);

			
			for (Long studentId : students) {
				System.out.println("!!!!!!! student " + studentId + " : " + userPrefListMap.get(studentId));;
			}
			
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria().andIdIsNotNull();
			List<TblEssayWithBLOBs> essays = essayMapper.selectByExampleWithBLOBs(essayExample);
			for (TblEssayWithBLOBs essay:essays)
			{
				List<Keyword> keywords = TFIDF.getTFIDF(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM);
				essayKeyWordsMap.put(essay.getId(), keywords);
				System.out.println("news " + essay.getId() + " , "  );
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
				}
				essayTypeMap.put(essay.getId(), essay.getType());
			}

			for (Long userId : students)
			{
				Map<Long, Double> tempMatchMap = new HashMap<Long, Double>();
				Iterator<Long> ite = essayKeyWordsMap.keySet().iterator();
				while (ite.hasNext())
				{
					Long newsId = ite.next();
					int moduleId = essayTypeMap.get(newsId);
					if (null != userPrefListMap.get(userId).get(moduleId))
						tempMatchMap.put(newsId,
								getMatchValue(userPrefListMap.get(userId).get(moduleId), essayKeyWordsMap.get(newsId)));
					else
						continue;
				}
				System.err.println("============================> temp match : " + JSONObject.toJSONString(tempMatchMap));
				// 去除匹配值为0的项目
				removeZeroItem(tempMatchMap);
				if (!(tempMatchMap.toString().equals("{}")))
				{
					tempMatchMap = sortMapByValue(tempMatchMap);
					Set<Long> toBeRecommended=tempMatchMap.keySet();
					System.out.println("======================> to be recommended : " + toBeRecommended.size());
					for (Long rec : toBeRecommended) {
						System.out.println(rec);
					}
					
					//过滤掉已经推荐过的文章
					recommendKit.filterRecommendedEssay(toBeRecommended,userId);
					//过滤掉用户已经看过的文章
					recommendKit.filterViewedEssay(toBeRecommended, userId);
					//如果可推荐文章数目超过了系统默认为CB算法设置的单日推荐上限数（N），则去掉一部分多余的可推荐新闻，剩下的N个新闻才进行推荐
					if(toBeRecommended.size()>N){

					}
					System.out.println("fiter: " + toBeRecommended.size());
					recommendKit.insertEssayRecommendation(userId, toBeRecommended,RecommendAlgorithm.CB);
					count+=toBeRecommended.size();
				}
			}
			System.out.println("CB recommend has contributed " + (count/students.size()) + " recommending news on average");
			System.out.println("CB recommend finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}
	
	/*
	 * 准备预选材料关键词
	 */
	public void genrateMaterialKeyword() throws Exception {
		
		
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		this.essayKeywordMapper = session.getMapper(TblEssayKeywordMapper.class);
		
		try
		{
			
			// 文章及对应关键词列表的Map
			HashMap<Long, List<Keyword>> essayKeyWordsMap = new HashMap<Long, List<Keyword>>();
			

			
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria().andIdIsNotNull();
			List<TblEssayWithBLOBs> essays = essayMapper.selectByExampleWithBLOBs(essayExample);
			for (TblEssayWithBLOBs essay:essays)
			{
				List<Keyword> keywords = TFIDF.getTFIDF(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM);
				essayKeyWordsMap.put(essay.getId(), keywords);
				
				System.out.println("essay " + essay.getId() + " , "  );
				TblEssayKeyword essayKeyword = new TblEssayKeyword();
				JSONArray kws = new JSONArray();
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
					
					JSONObject kw = new JSONObject();
					kw.put("name", keyword.getName());
					kw.put("score", keyword.getScore());
					kw.put("freq", keyword.getFreq());
					kws.add(kw);
				}
				essayKeyword.setEssayId(essay.getId());
				essayKeyword.setEssayType(essay.getType());
				essayKeyword.setKeyword(JSONArray.toJSONString(kws));
				essayKeyword.setGrade(essay.getGrade());
				System.out.println("essay " + essay.getId() + " , " + JSONArray.toJSONString(kws));
				essayKeywordMapper.insert(essayKeyword);
				
			}
			
			session.commit();
			
			System.out.println("keyword generation finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}
	
	
	public void recommendByEssay(List<Long> students) throws Exception {
		
		this.studentMapper = session.getMapper(TblStudentMapper.class);
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		
		try
		{
			int count=0;
			System.out.println("CB recommend start at "+ new Date());
			
			// 首先进行用户喜好关键词列表的衰减更新+用户当日历史浏览记录的更新
			UserPrefRefresher userPrefRefresher = new UserPrefRefresher();
			userPrefRefresher.refreshStudentPreference(students);
			System.out.println("==========================> student quantity : " + students.size());
			
			// 文章及对应关键词列表的Map
			HashMap<Long, List<Keyword>> essayKeyWordsMap = new HashMap<Long, List<Keyword>>();
			HashMap<Long, Integer> essayTypeMap = new HashMap<Long, Integer>();
			// 用户喜好关键词列表
			HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = recommendKit
					.getStudentEssayPrefListMap(students);

			
			for (Long studentId : students) {
				System.out.println("!!!!!!! student " + studentId + " : " + userPrefListMap.get(studentId));;
			}
			
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria().andIdIsNotNull();
			List<TblEssayWithBLOBs> essays = essayMapper.selectByExampleWithBLOBs(essayExample);
			for (TblEssayWithBLOBs essay:essays)
			{
				List<Keyword> keywords = TFIDF.getTFIDF(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM);
				essayKeyWordsMap.put(essay.getId(), keywords);
				System.out.println("news " + essay.getId() + " , "  );
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
				}
				essayTypeMap.put(essay.getId(), essay.getType());
			}

			for (Long userId : students)
			{
				Map<Long, Double> tempMatchMap = new HashMap<Long, Double>();
				Iterator<Long> ite = essayKeyWordsMap.keySet().iterator();
				while (ite.hasNext())
				{
					Long newsId = ite.next();
					int moduleId = essayTypeMap.get(newsId);
					if (null != userPrefListMap.get(userId).get(moduleId))
						tempMatchMap.put(newsId,
								getMatchValue(userPrefListMap.get(userId).get(moduleId), essayKeyWordsMap.get(newsId)));
					else
						continue;
				}
				System.err.println("============================> temp match : " + JSONObject.toJSONString(tempMatchMap));
				// 去除匹配值为0的项目
				removeZeroItem(tempMatchMap);
				if (!(tempMatchMap.toString().equals("{}")))
				{
					tempMatchMap = sortMapByValue(tempMatchMap);
					Set<Long> toBeRecommended=tempMatchMap.keySet();
					System.out.println("======================> to be recommended : " + toBeRecommended.size());
					for (Long rec : toBeRecommended) {
						System.out.println(rec);
					}
					
					//过滤掉已经推荐过的文章
					recommendKit.filterRecommendedEssay(toBeRecommended,userId);
					//过滤掉用户已经看过的文章
					recommendKit.filterViewedEssay(toBeRecommended, userId);
					//如果可推荐文章数目超过了系统默认为CB算法设置的单日推荐上限数（N），则去掉一部分多余的可推荐新闻，剩下的N个新闻才进行推荐
					if(toBeRecommended.size()>N){

					}
					System.out.println("fiter: " + toBeRecommended.size());
					recommendKit.insertEssayRecommendation(userId, toBeRecommended,RecommendAlgorithm.CB);
					count+=toBeRecommended.size();
				}
			}
			System.out.println("CB recommend has contributed " + (count/students.size()) + " recommending news on average");
			System.out.println("CB recommend finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}
	

	public PytheResult recommendEverytime(Long recommendStudentId) {
		
		this.studentMapper = session.getMapper(TblStudentMapper.class);
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		this.essayKeywordMapper = session.getMapper(TblEssayKeywordMapper.class);
		
		Set<Long> toBeRecommended = null;
		List<Long> students = new ArrayList<Long>();
		students.add(recommendStudentId);
		
		try
		{
			int count=0;
			System.out.println("CB recommend start at "+ new Date());
			
			// 首先进行用户喜好关键词列表的衰减更新+用户当日历史浏览记录的更新
			UserPrefRefresher userPrefRefresher = new UserPrefRefresher();
			userPrefRefresher.refreshOneStudentPreference(recommendStudentId);
			System.out.println("==========================> student quantity : " + students.size());
			
			// 文章及对应关键词列表的Map
			HashMap<Long, List<Keyword>> essayKeyWordsMap = new HashMap<Long, List<Keyword>>();
			HashMap<Long, Integer> essayTypeMap = new HashMap<Long, Integer>();
			// 用户喜好关键词列表
			HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = recommendKit
					.getStudentEssayPrefListMap(students);

			
			for (Long studentId : students) {
				System.out.println("!!!!!!! student " + studentId + " : " + userPrefListMap.get(studentId));;
			}
			
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria();
			List<TblEssay> essays = essayMapper.selectByExample(essayExample);
			System.out.println("run to this line !");
			for (TblEssay essay:essays)
			{
				//从预计算结果中读取keywords
				TblEssayKeyword essayKeywordRecord = essayKeywordMapper.selectByPrimaryKey(essay.getId());
				System.out.println(essayKeywordRecord.getKeyword());
				JSONArray keywordArray = JSONArray.parseArray(essayKeywordRecord.getKeyword());
				List<Keyword> keywords = new ArrayList<Keyword>();
				
				for (Iterator iterator = keywordArray.iterator(); iterator.hasNext();) {
					JSONObject kw = (JSONObject) iterator.next();
					Keyword k = new Keyword(kw.getString("name"), kw.getDouble("score"));
					keywords.add(k);
					System.out.println(k + " finish !");
				}
				
				
						
				essayKeyWordsMap.put(essay.getId(), keywords);
				System.out.println("essay " + essay.getId() + " , "  );
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
				}
				essayTypeMap.put(essay.getId(), essay.getType());
			}

			for (Long userId : students)
			{
				Map<Long, Double> tempMatchMap = new HashMap<Long, Double>();
				Iterator<Long> ite = essayKeyWordsMap.keySet().iterator();
				while (ite.hasNext())
				{
					Long eId = ite.next();
					int tId = essayTypeMap.get(eId);
					if (null != userPrefListMap.get(userId).get(tId))
						tempMatchMap.put(eId,
								getMatchValue(userPrefListMap.get(userId).get(tId), essayKeyWordsMap.get(eId)));
					else
						continue;
				}
				System.err.println("============================> temp match : " + JSONObject.toJSONString(tempMatchMap));
				// 去除匹配值为0的项目
				removeZeroItem(tempMatchMap);
				if (!(tempMatchMap.toString().equals("{}")))
				{
					tempMatchMap = sortMapByValue(tempMatchMap);
					toBeRecommended=tempMatchMap.keySet();
					System.out.println("======================> to be recommended : " + toBeRecommended.size());
					for (Long rec : toBeRecommended) {
						System.out.println(rec + "," + tempMatchMap.get(rec));
					}
					
					//过滤掉已经推荐过的文章
					recommendKit.filterRecommendedEssay(toBeRecommended,userId);
					//过滤掉用户已经看过的文章
					recommendKit.filterViewedEssay(toBeRecommended, userId);
					//如果可推荐文章数目超过了系统默认为CB算法设置的单日推荐上限数（N），则去掉一部分多余的可推荐新闻，剩下的N个新闻才进行推荐
					if(toBeRecommended.size()>N){
						recommendKit.removeOverEssays(toBeRecommended,N);
					}
					System.out.println("fiter: " + toBeRecommended.size());
					recommendKit.insertEssayRecommendation(userId, toBeRecommended,RecommendAlgorithm.CB);
					count+=toBeRecommended.size();
					for (Long rec : toBeRecommended) {
						System.out.println("!!! " + rec + "," + tempMatchMap.get(rec));
					}
				}
			}
			System.out.println("CB recommend has contributed " + (count/students.size()) + " recommending news on average");
			System.out.println("CB recommend finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return PytheResult.ok(toBeRecommended);
	}

	public PytheResult recommendByEssay(Long readingId, Long recommendStudentId) {
		
		this.studentMapper = session.getMapper(TblStudentMapper.class);
		this.essayMapper = session.getMapper(TblEssayMapper.class);
		this.essayKeywordMapper = session.getMapper(TblEssayKeywordMapper.class);
		this.essayRecommendationMapper = session.getMapper(TblEssayRecommendationMapper.class);
		
		Set<Long> toBeRecommended = null;
		List<Long> students = new ArrayList<Long>();
		students.add(recommendStudentId);
		
		try
		{
			int count=0;
			System.out.println("CB recommend start at "+ new Date());
			
			//计算每篇文章关键词
//			TblEssayWithBLOBs reading = essayMapper.selectByPrimaryKey(readingId);
//			List<Keyword> kws = TFIDF.getTFIDF(essayWithBLOBs.getTitle(), essayWithBLOBs.getContentText(), KEY_WORDS_NUM);
//			CustomizedHashMap<String, Double> essayPreListMap = new CustomizedHashMap<String, Double>();
//			for (Keyword keyword : kws) {
//				essayPreListMap.put(keyword.getName(), keyword.getScore());
//			}
			
			// 文章及对应关键词列表的Map
			HashMap<Long, List<Keyword>> essayKeyWordsMap = new HashMap<Long, List<Keyword>>();
			HashMap<Long, Integer> essayGradeMap = new HashMap<Long, Integer>();
			HashMap<Long, Integer> essayTypeMap = new HashMap<Long, Integer>();
//			// 指定文章的关键词列表
//			HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = recommendKit
//					.getStudentEssayPrefListMap(students);

			
			
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria();
			List<TblEssay> essays = essayMapper.selectByExample(essayExample);
			System.out.println("run to this line !");
			
			HashMap<Long, CustomizedHashMap<String, Double>> essayPreListMap = new HashMap<Long, CustomizedHashMap<String, Double>>();
			
			for (TblEssay e:essays)
			{
				
				
				//从预计算结果中读取keywords
				TblEssayKeyword essayKeywordRecord = essayKeywordMapper.selectByPrimaryKey(e.getId());
				System.out.println(essayKeywordRecord.getKeyword());
				JSONArray keywordArray = JSONArray.parseArray(essayKeywordRecord.getKeyword());
				List<Keyword> keywords = new ArrayList<Keyword>();
				
				for (Iterator iterator = keywordArray.iterator(); iterator.hasNext();) {
					JSONObject kw = (JSONObject) iterator.next();
					Keyword k = new Keyword(kw.getString("name"), kw.getDouble("score"));
					keywords.add(k);
					System.out.println(k + " finish !");
				}
				
				essayKeyWordsMap.put(e.getId(), keywords);
				essayGradeMap.put(e.getId(), e.getGrade());
				System.out.println("essay " + e.getId() + " , "  );
				for (Keyword keyword : keywords) {
					System.out.println("      " + keyword  );
				}
			}	
			System.out.println("essay keyword set size : " + essayKeyWordsMap.size());
			
			CustomizedHashMap<String, Double> preListMap = new CustomizedHashMap<String, Double>();
			for (TblEssay e:essays)
			{	
				System.out.println("compute essay : " + e.getId());
				TblEssayWithBLOBs essay = essayMapper.selectByPrimaryKey(e.getId());
				
				//计算每篇文章关键词
//				List<Keyword> kws = TFIDF.getTFIDF(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM);
//				CustomizedHashMap<String, Double> preListMap = new CustomizedHashMap<String, Double>();
//				for (Keyword keyword : kws) {
//					preListMap.put(keyword.getName(), keyword.getScore());
//				}
				
				TblEssayKeyword essayKeywordRecord = essayKeywordMapper.selectByPrimaryKey(e.getId());
				System.out.println(essayKeywordRecord.getKeyword());
				JSONArray keywordArray = JSONArray.parseArray(essayKeywordRecord.getKeyword());
				List<Keyword> keywords = new ArrayList<Keyword>();
				
				for (Iterator iterator = keywordArray.iterator(); iterator.hasNext();) {
					JSONObject kw = (JSONObject) iterator.next();
					Keyword k = new Keyword(kw.getString("name"), kw.getDouble("score"));
					keywords.add(k);
					
					preListMap.put(kw.getString("name"), kw.getDouble("score"));
					
				}
				System.out.println("preListMap size : " + preListMap.size());
				System.out.println("essay keyword set size : " + essayKeyWordsMap.size());
				
				Map<Long, Double> tempMatchMap = new HashMap<Long, Double>();
				Iterator<Long> ite = essayKeyWordsMap.keySet().iterator();
				
				
				while (ite.hasNext())
				{
					Long eId = ite.next();
					//去除级别相差超过2的项
					Integer toBeComparedGrade = essayGradeMap.get(eId);
					System.out.println("!!!!!! compared essay : " + eId  + "," + toBeComparedGrade.intValue());
					if(toBeComparedGrade.intValue() >= e.getGrade().intValue()
						&& toBeComparedGrade.intValue() <= (e.getGrade().intValue()+2) 
					    && null != essayPreListMap
					    && !eId.equals(e.getId()))
					{
						tempMatchMap.put(eId,
								getMatchValue(preListMap, essayKeyWordsMap.get(eId)));
					}	
					else
					{
//						System.out.println("!!!!!!!!!!!!!!! level gap");
					}
					
				}
				System.err.println("============================> temp match : " + JSONObject.toJSONString(tempMatchMap));
				// 去除匹配值为0的项目
				removeZeroItem(tempMatchMap);
				
				if (!(tempMatchMap.toString().equals("{}")))
				{
					tempMatchMap = sortMapByValue(tempMatchMap);
					toBeRecommended=tempMatchMap.keySet();
					System.out.println("======================> to be recommended : " + toBeRecommended.size());
					for (Long rec : toBeRecommended) {
						System.out.println(rec + "," + tempMatchMap.get(rec));
					}
					
					
					//最多推荐20篇
					if(toBeRecommended.size()>20){
						recommendKit.removeOverEssays(toBeRecommended,20);
					}
					System.out.println("fiter: " + toBeRecommended.size());

					count+=toBeRecommended.size();
					for (Long rec : toBeRecommended) {
						System.out.println("!!! " + rec + "," + tempMatchMap.get(rec));
					}
					
					TblEssayRecommendation  er = new TblEssayRecommendation();
					er.setEssayid(essay.getId());
					er.setType(essay.getType());
					er.setRecommendation(JsonUtils.objectToJson(toBeRecommended));
					essayRecommendationMapper.insert(er);
					
					System.out.println("essay " + essay.getId() + " , " + JsonUtils.objectToJson(toBeRecommended));
					
					preListMap.clear();
				}
				session.commit();
				
			}

			
			System.out.println("CB recommend has contributed " + (count/students.size()) + " recommendings on average");
			System.out.println("CB recommend finished at "+new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//过滤掉用户已经看过的文章
		recommendKit.filterViewedEssay(toBeRecommended, recommendStudentId);
		return PytheResult.ok(toBeRecommended);
	}

	private void removeLevelGapItem(TblEssayWithBLOBs essay, Map<Long, Double> map) {
		
		HashSet<Long> toBeDeleteItemSet = new HashSet<Long>();
		Iterator<Long> ite = map.keySet().iterator();
		while (ite.hasNext())
		{
			Long newsId = ite.next();
			if (map.get(newsId) <= 0)
			{
				toBeDeleteItemSet.add(newsId);
			}
		}
		for (Long item : toBeDeleteItemSet)
		{
			map.remove(item);
		}
		
	}
	
	
	
}
