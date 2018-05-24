/**
 * 
 */
package com.pythe.hotrecommend;

import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.pythe.algorithms.RecommendAlgorithm;
import com.pythe.algorithms.RecommendKit;
import com.pythe.mapper.NewsLogsMapper;
import com.pythe.mapper.NewsMapper;
import com.pythe.mapper.RecommendationsMapper;
import com.pythe.mapper.UsersMapper;
//import com.pythe.mapper.VNewsVisitStatisticsMapper;
//import com.pythe.mapper.VRecommendationStatisticsMapper;
import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.Recommendations;
import com.pythe.pojo.VNewsVisitStatistics;
import com.pythe.pojo.VRecommendationStatistics;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月30日 基于“热点新闻”生成的推荐，一般用于在CF和CB算法推荐结果数较少时进行数目的补充
 */
@Service
public class HotRecommender implements RecommendAlgorithm
{
	
	
//	@Autowired
	private NewsMapper newsMapper;
	
//	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
//	@Autowired
	private UsersMapper usersMapper;
	
//	@Autowired
	private RecommendationsMapper recommendationsMapper;
	
private static SqlSessionFactory sqlSessionFactory;
	
	private static SqlSession session;
	
	public HotRecommender() throws Exception {
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
		
		
		
	}
	
	
	public static final Logger logger=Logger.getLogger(HotRecommender.class);
	
	// 热点新闻的有效时间
	public static int beforeDays = -10;
	// 推荐系统每日为每位用户生成的推荐结果的总数，当CF与CB算法生成的推荐结果数不足此数时，由该算法补充
	public static int TOTAL_REC_NUM = 20;
	// 将每天生成的“热点新闻”ID，按照新闻的热点程度从高到低放入此List
	private static ArrayList<Long> topHotNewsList = new ArrayList<Long>();

	@Override
	public void recommend(List<Long> users) throws Exception
	{
		RecommendKit recommendKit = new RecommendKit();
		System.out.println("HR start at "+new Date());
		int count=0;
		Timestamp timestamp = getCertainTimestamp(0, 0, 0);
		for (Long userId : users)
		{
			try
			{
				//获得已经预备为当前用户推荐的新闻，若数目不足达不到单次的最低推荐数目要求，则用热点新闻补充
//				Recommendations recommendation=Recommendations.dao.findFirst("select user_id,count(*) as recnums from recommendations where derive_time>'" + timestamp
//								+ "' and user_id='" + userId + "' group by user_id");
				VRecommendationStatistics vRecommendationStatistics = recommendationsMapper.queryRecommendationStatisticsAfterDate("'" + timestamp + "'", userId);
//				boolean flag=(recommendation!=null);
				boolean flag=(vRecommendationStatistics!=null);
				int delta=flag?TOTAL_REC_NUM - vRecommendationStatistics.getRecommendNum().intValue():TOTAL_REC_NUM;
				Set<Long> toBeRecommended = new HashSet<Long>();
				if (delta > 0)
				{
					int i = topHotNewsList.size() > delta ? delta : topHotNewsList.size();
					while (i-- > 0)
						toBeRecommended.add(topHotNewsList.get(i));
				}
				recommendKit.filterBrowsedNews(toBeRecommended, userId);
				recommendKit.filterReccedNews(toBeRecommended, userId);
				recommendKit.insertRecommend(userId, toBeRecommended, RecommendAlgorithm.HR);
				count+=toBeRecommended.size();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("HR has contributed " + (users.size()==0?0:count/users.size()) + " recommending news on average");
		System.out.println("HR end at "+new Date());

	}

	public void formTodayTopHotNewsList()
	{
		topHotNewsList.clear();
		ArrayList<Long> hotNewsTobeReccommended = new ArrayList<Long>();
		try
		{
//			List<Newslogs> newslogsList=Newslogs.dao.find("select news_id,count(*) as visitNums from newslogs where view_time>"
//							+ RecommendKit.getInRecDate(beforeDays) + " group by news_id order by visitNums desc");
			
			List<VNewsVisitStatistics> vnvses = newsLogsMapper.queryNewsVisitStatisticsAfterDate(RecommendKit.getInRecDate(-10));
			for (VNewsVisitStatistics vNewsVisitStatistics : vnvses) {
				hotNewsTobeReccommended.add(vNewsVisitStatistics.getNewsId());
			}
//			for (Newslogs newslog:newslogsList)
//			{
//				hotNewsTobeReccommended.add(newslog.getNewsId());
//			}
			for (Long news : hotNewsTobeReccommended)
			{
				topHotNewsList.add(news);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static List<Long> getTopHotNewsList()
	{
		return topHotNewsList;
	}

	public static int getTopHopNewsListSize()
	{
		return topHotNewsList.size();
	}

	private Timestamp getCertainTimestamp(int hour, int minute, int second)
	{
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.set(Calendar.HOUR_OF_DAY, hour); // 设置为前beforeNum天
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return new Timestamp(calendar.getTime().getTime());
	}

	@Override
	public void recommendTest(List<Long> users) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
