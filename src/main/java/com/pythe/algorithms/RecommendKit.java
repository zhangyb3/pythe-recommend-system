/**
 * 
 */
package com.pythe.algorithms;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Session;
import com.pythe.common.utils.JsonUtils;
import com.pythe.contentbasedrecommend.CustomizedHashMap;
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
import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.NewsLogsExample;
import com.pythe.pojo.Recommendations;
import com.pythe.pojo.RecommendationsExample;
import com.pythe.pojo.TblStudentEssayPreference;
import com.pythe.pojo.TblStudentEssayPreferenceExample;
import com.pythe.pojo.TblStudentEssayRecommendation;
import com.pythe.pojo.TblStudentExample;
import com.pythe.pojo.Users;
import com.pythe.pojo.UsersExample;
import com.pythe.pojo.VEssayViewLog;
import com.pythe.pojo.VEssayViewLogExample;


public class RecommendKit
{
	@Value("${BEFORE_DAYS}")
	private static Integer BEFORE_DAYS;
	
	@Value("${ACTIVE_DAYS}")
	private Integer ACTIVE_DAYS;
	
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
	
	public RecommendKit() throws Exception {
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
		
		beforeDays = PropGetKit.getInt("beforeDays");
    }
	
	public static final Logger logger=Logger.getLogger(RecommendKit.class);
	
	/**
	 * 推荐新闻的时效性天数，即从推荐当天开始到之前beforeDays天的新闻属于仍具有时效性的新闻，予以推荐。
	 */
	private static int beforeDays ;
//	private static int beforeDays = PropGetKit.BEFORE_DAYS;

	/**
	 * @return the inRecDate 返回时效时间的"year-month-day"的格式表示，方便数据库的查询
	 */
	public static String getInRecDate()
	{
		return getSpecificDayFormat(beforeDays);
	}
	
	/**
	 * @return the inRecDate 返回时效时间的"year-month-day"的格式表示，方便数据库的查询
	 */
	public static String getInRecDate(int beforeDays)
	{
		return getSpecificDayFormat(beforeDays);
	}

	/**
	 * @return the inRecDate 返回时效时间timestamp形式表示，方便其他推荐方法在比较时间先后时调用
	 */
	public static Timestamp getInRecTimestamp(int before_Days)
	{
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.add(Calendar.DAY_OF_MONTH, before_Days); // 设置为前beforeNum天
		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 过滤方法filterOutDateNews() 过滤掉失去时效性的新闻（由beforeDays属性控制）
	 */
	public void filterOutDateNews(Collection<Long> col, Long userId)
	{
		try
		{
			String newsids = getInQueryString(col.iterator());
			if (!newsids.equals("()"))
			{
//				List<News> newsList = News.dao.find("select id,news_time from news where id in " + newsids);
				
				NewsExample newsExample = new NewsExample();
				LinkedList<Long> list = new LinkedList<Long>();
				Long[] colIds = col.toArray(new Long[col.size()]);
				for (Long id : colIds) {
					list.add(id);
				}
				newsExample.createCriteria().andIdIn(list);
				List<News> newsList = newsMapper.selectByExample(newsExample);

				for(News news:newsList)
				{
					if (news.getNewsTime().before(getInRecTimestamp(beforeDays)))
					{
						col.remove(news.getId());
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 过滤方法filterBrowsedNews() 过滤掉已经用户已经看过的新闻
	 */
	public void filterBrowsedNews(Collection<Long> col, Long userId)
	{
		try
		{
			
//			List<NewsLogs> newslogsList = Newslogs.dao.find("select news_id from newslogs where user_id=?",userId);
			NewsLogsExample newsLogsExample = new NewsLogsExample();
			newsLogsExample.createCriteria().andUserIdEqualTo(userId);
			List<NewsLogs> newslogsList = newsLogsMapper.selectByExample(newsLogsExample);
			System.out.println("viewed : " + newslogsList.size());
			for (NewsLogs newslog:newslogsList)
			{
				if (col.contains(newslog.getNewsId()))
				{
					col.remove(newslog.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 过滤方法filterReccedNews() 过滤掉已经推荐过的新闻（在recommend表中查找）
	 */
	public void filterReccedNews(Collection<Long> col, Long userId)
	{
		try
		{
			//但凡近期已经给用户推荐过的新闻，都过滤掉
//			List<Recommendations> recommendationList = Recommendations.dao.find("select news_id from recommendations where user_id=? and derive_time>?",userId,getInRecDate());
			List<Recommendations> recommendationList = recommendationsMapper.queryCertainUserRecentlyRecommendations(userId,getInRecDate());
			System.out.println("reced : " + recommendationList.size());
			for (Recommendations recommendation:recommendationList)
			{
				if (col.contains(recommendation.getNewsId()))
				{
					col.remove(recommendation.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有用户的Id列表
	 * 
	 * @return
	 */
	public ArrayList<Long> getUserList()
	{
		ArrayList<Long> users = new ArrayList<Long>();
		try
		{
//			List<Users> userList = Users.dao.find("select id from users");
			UsersExample usersExample = new UsersExample();
			usersExample.createCriteria();
			List<Users> userList = usersMapper.selectByExample(usersExample);
			for (Users user:userList)
			{
				users.add(user.getId());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public int getbeforeDays()
	{
		return beforeDays;
	}

	public void setbeforeDays(int beforeDays)
	{
		this.beforeDays = beforeDays;
	}

	public static String getSpecificDayFormat(int before_Days)
	{
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.add(Calendar.DAY_OF_MONTH, before_Days); // 设置为前beforeNum天
		Date d = calendar.getTime();
		return "'" + date_format.format(d) + "'";
	}

	/**
	 * 获取所有用户的喜好关键词列表
	 * 
	 * @return
	 */
	public HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> getUserPrefListMap(
			Collection<Long> userSet)
	{
		//获得session实例  
        SqlSession session = sqlSessionFactory.openSession();
		usersMapper = session.getMapper(UsersMapper.class);
		
		HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = null;
		try
		{
			String userPrefListQuery = getInQueryStringWithSingleQuote(userSet.iterator());
			if (!userPrefListQuery.equals("()"))
			{
//				List<Users> userList = Users.dao.find("select id,pref_list from users where id in " + userPrefListQuery);
				UsersExample usersExample = new UsersExample();
				LinkedList<Long> list = new LinkedList<Long>();
				Long[] userIds = userSet.toArray(new Long[userSet.size()]);
				for (Long id : userIds) {
					list.add(id);
				}
				usersExample.createCriteria().andIdIn(list);
				List<Users> userList = usersMapper.selectByExampleWithBLOBs(usersExample);
				userPrefListMap = new HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>>();
				for (Users user:userList)
				{
					System.out.println("=================================> preflist :" + user.getPrefList());
					userPrefListMap.put(user.getId(), JsonKit.jsonPrefListtoMap(user.getPrefList()));
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userPrefListMap;
	}
	
	/**
	 * 获取所有用户的喜好关键词列表
	 * 
	 * @return
	 */
	public HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> getStudentEssayPrefListMap(
			Collection<Long> userSet)
	{
		//获得session实例  
        SqlSession session = sqlSessionFactory.openSession();
		studentEssayPreferenceMapper = session.getMapper(TblStudentEssayPreferenceMapper.class);
		
		HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = null;
		try
		{
			String userPrefListQuery = getInQueryStringWithSingleQuote(userSet.iterator());
			if (!userPrefListQuery.equals("()"))
			{
				TblStudentEssayPreferenceExample studentEssayPreferenceExample = new TblStudentEssayPreferenceExample();
				studentEssayPreferenceExample.createCriteria().andStudentIdIn(Arrays.asList(userSet.toArray(new Long[userSet.size()])));
				List<TblStudentEssayPreference> studentEssayPreferences = studentEssayPreferenceMapper.selectByExampleWithBLOBs(studentEssayPreferenceExample);
				userPrefListMap = new HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>>();
				for (TblStudentEssayPreference studentEssayPreference:studentEssayPreferences)
				{
					System.out.println("=================================> student " + studentEssayPreference.getStudentId() + " preference :" + studentEssayPreference.getPreference());
					userPrefListMap.put(studentEssayPreference.getStudentId(), JsonKit.jsonPrefListtoMap(studentEssayPreference.getPreference()));
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userPrefListMap;
	}

	/**
	 * 用以select语句中使用in (n1，n2,n3...)范围查询的字符串拼接
	 * 
	 * @param ite
	 *            待查询对象集合的迭代器
	 * @return 若迭代集合不为空:"(n1,n2,n3)"，若为空："()"
	 */
	public static <T> String getInQueryString(Iterator<T> ite)
	{
		String inQuery = "(";
		while (ite.hasNext())
		{
			inQuery += ite.next() + ",";
		}
		if (inQuery.length() > 1)
		{
			inQuery = inQuery.substring(0, inQuery.length() - 1);
		}
		inQuery += ")";
		return inQuery;
	}

	public static <T> String getInQueryStringWithSingleQuote(Iterator<T> ite)
	{
		String inQuery = "(";
		while (ite.hasNext())
		{
			inQuery += "'" + ite.next() + "',";
		}
		if (inQuery.length() > 1)
		{
			inQuery = inQuery.substring(0, inQuery.length() - 1);
		}
		inQuery += ")";
		return inQuery;
	}

	/**
	 * 将推荐结果插入recommend表
	 * 
	 * @param userId
	 *            推荐目标用户id
	 * @param toBeRecommended
	 *            待推荐新闻集合的迭代器
	 * @param recAlgo
	 *            标明推荐结果来自哪个推荐算法(RecommendAlgorithm.XX)
	 */
	public void insertRecommend(Long userId, Collection<Long> toBeRecommended, int recAlgo)
	{
		//获得session实例  
//		SqlSession session = sqlSessionFactory.openSession();
		this.recommendationsMapper = session.getMapper(RecommendationsMapper.class);
		
		try
		{
			
			Date deriveTime = new Date();
			List<Long> recNewsIds = Arrays.asList(toBeRecommended.toArray(new Long[toBeRecommended.size()]));
			for (Long recNewsId : recNewsIds) 
			{
				Recommendations rec=new Recommendations();
				rec.setUserId(userId);
				rec.setDeriveAlgorithm(recAlgo);
				rec.setNewsId(recNewsId);
				rec.setDeriveTime(deriveTime);
				recommendationsMapper.insert(rec);
			}
			session.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Acquire a list of active users 
	 * "Active" means who read news recently ('recent' determined by method getInRecDate(), default in a month)
	 * 
	 * @return
	 */
	public List<Long> getActiveUsers()
	{
		try
		{
			int activeDay= PropGetKit.getInt("activeDay");
//			int activeDay = PropGetKit.ACTIVE_DAYS;
//			List<Users> userList=Users.dao.find("select distinct id,name from users where latest_log_time>" + getInRecDate(activeDay));
			List<Users> userList = usersMapper.queryActiveUsersAfterDate(getInRecDate(activeDay));
			List<Long> userIDList=new ArrayList<Long>();
			for(Users user:userList)
				userIDList.add(user.getId());
			return userIDList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("获取活跃用户异常！");
		return null;
	}
	
	public List<Long> getAllUsers(){
		try
		{
//			List<Users> userList=Users.dao.find("select distinct id,name from users");
			UsersExample usersExample = new UsersExample();
			usersExample.createCriteria().andIdIsNotNull();
			List<Users> userList = usersMapper.selectByExample(usersExample);
			List<Long> userIDList=new ArrayList<Long>();
			for(Users user:userList)
				userIDList.add(user.getId());
			return userIDList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.info("获取全体用户异常！");
		return null;
	}
	
	

	/**
	 * 去除数量上超过为算法设置的推荐结果上限值的推荐结果
	 * 
	 * @param set
	 * @param N
	 * @return
	 */
	public static void removeOverNews(Set<Long> set, int N)
	{
		int i = 0;
		Iterator<Long> ite = set.iterator();
		while (ite.hasNext())
		{
			if (i >= N)
			{
				ite.remove();
				ite.next();
			}
			else
			{
				ite.next();
			}
			i++;
		}
	}
	
	/**
	 * 去除数量上超过为算法设置的推荐结果上限值的推荐结果
	 * 
	 * @param set
	 * @param N
	 * @return
	 */
	public void removeOverEssays(Set<Long> set, int N)
	{
		int i = 0;
		Iterator<Long> ite = set.iterator();
		while (ite.hasNext())
		{
			if (i >= N)
			{
				ite.remove();
				ite.next();
			}
			else
			{
				ite.next();
			}
			i++;
		}
	}

	
	/**
	 * 过滤掉已经推荐过的文章（在tbl_student_essay_recommendation表中查找）
	 */
	public void filterRecommendedEssay(Set<Long> toBeRecommended, Long userId) {
		
		try
		{
			//但凡近期已经给用户推荐过的文章，都过滤掉

			List<TblStudentEssayRecommendation> recommendationList = studentEssayRecommendationMapper.queryCertainUserRecentlyRecommendations(userId,getInRecDate());
			if(!recommendationList.isEmpty())
			{
				TblStudentEssayRecommendation studentRecommendation = recommendationList.get(0);
				List<Long> recommendations = JsonUtils.jsonToList(studentRecommendation.getEssays(), Long.class);
				System.out.println("reced : " + recommendations.size());
				for (Long recommendation:recommendations)
				{
					if (toBeRecommended.contains(recommendation))
					{
						toBeRecommended.remove(recommendation);
					}
				}
			}
			else{
				System.out.println("reced : " + 0);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void filterViewedEssay(Set<Long> toBeRecommended, Long userId) {
		try
		{
			VEssayViewLogExample vEssayViewLogExample = new VEssayViewLogExample();
			vEssayViewLogExample.createCriteria().andStudentIdEqualTo(userId);
			List<VEssayViewLog> essayViewLogs = essayViewLogMapper.selectByExample(vEssayViewLogExample);
			System.out.println("viewed : " + essayViewLogs.size());
			for (VEssayViewLog essayViewLog:essayViewLogs)
			{
				if (toBeRecommended.contains(essayViewLog.getEssayId()))
				{
					toBeRecommended.remove(essayViewLog.getEssayId());
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertEssayRecommendation(Long userId, Set<Long> toBeRecommended, int cb) {
		
		this.studentEssayRecommendationMapper = session.getMapper(TblStudentEssayRecommendationMapper.class);
		try
		{
			Date now = new Date();
//			for (Long recommendationEssayId : toBeRecommended)
			{
				TblStudentEssayRecommendation rec = new TblStudentEssayRecommendation();
				rec.setStudentId(userId);
				rec.setEssays(JsonUtils.objectToJson(toBeRecommended));
				rec.setDeriveTime(now);
				rec.setDeriveAlgorithm(cb);
				studentEssayRecommendationMapper.insert(rec);
				
			}
			session.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
