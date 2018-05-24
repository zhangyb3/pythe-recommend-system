/**
 * 
 */
package com.pythe.contentbasedrecommend;

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

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Session;
import com.pythe.algorithms.PropGetKit;
import com.pythe.algorithms.RecommendAlgorithm;
import com.pythe.algorithms.RecommendKit;
import com.pythe.mapper.NewsLogsMapper;
import com.pythe.mapper.NewsMapper;
import com.pythe.mapper.RecommendationsMapper;
import com.pythe.mapper.TblEssayMapper;
import com.pythe.mapper.TblStudentEssayPreferenceMapper;
import com.pythe.mapper.TblStudentEssayRecommendationMapper;
import com.pythe.mapper.TblStudentMapper;
import com.pythe.mapper.UsersMapper;
import com.pythe.mapper.VEssayViewLogMapper;
import com.pythe.pojo.News;
import com.pythe.pojo.NewsExample;
import com.pythe.pojo.NewsWithBLOBs;
import com.pythe.pojo.TblEssayExample;
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
	
//	@Autowired
	private RecommendationsMapper recommendationsMapper;
	
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
				List<Keyword> keywords = TFIDF.getTFIDE(news.getTitle(), news.getContent(), KEY_WORDS_NUM);
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
				List<Keyword> keywords = TFIDF.getTFIDE(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM);
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
}
