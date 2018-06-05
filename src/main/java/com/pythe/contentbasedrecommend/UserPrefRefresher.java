/**
 * 
 */
package com.pythe.contentbasedrecommend;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ansj.app.keyword.Keyword;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pythe.algorithms.JsonKit;
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
import com.pythe.pojo.NewsLogs;
import com.pythe.pojo.NewsLogsExample;
import com.pythe.pojo.NewsWithBLOBs;
import com.pythe.pojo.TblEssayExample;
import com.pythe.pojo.TblEssayWithBLOBs;
import com.pythe.pojo.TblStudentEssayPreference;
import com.pythe.pojo.TblStudentEssayPreferenceExample;
import com.pythe.pojo.Users;
import com.pythe.pojo.UsersExample;
import com.pythe.pojo.VEssayViewLog;
import com.pythe.pojo.VEssayViewLogExample;

/**

 * 每次用户浏览新的新闻时，用以更新用户的喜好关键词列表
 */
@Service
public class UserPrefRefresher
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
		
	public UserPrefRefresher() throws Exception {
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
	
	//设置TFIDF提取的关键词数目
	private static final int KEY_WORDS_NUM = 10;
	
	//每次衰减系数
	private static final double DEC_COEE=0.85;
	
	
	public void refresh(){
			refresh(recommendKit.getUserList());
	}
	
	/**
	 * 按照推荐频率调用的方法，一般为一天执行一次。
	 * 定期根据前一天所有用户的浏览记录，在对用户进行喜好关键词列表TFIDF值衰减的后，将用户前一天看的新闻的关键词及相应TFIDF值更新到列表中去。
	 * @param userIdsCol
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Collection<Long> userIdsCol){
			//首先对用户的喜好关键词列表进行衰减更新
			autoDecRefresh(userIdsCol);
			//获得session实例  
			SqlSession session = sqlSessionFactory.openSession();
			this.usersMapper = session.getMapper(UsersMapper.class);
			
			//用户浏览新闻纪录：userBrowsexMap:<Long(userid),ArrayList<String>(newsid List)>
			HashMap<Long,ArrayList<Long>> userBrowsedMap=getBrowsedHistoryMap();
			//如果前一天没有浏览记录（比如新闻门户出状况暂时关停的情况下，或者初期用户较少的时候均可能出现这种情况），则不需要执行后续更新步骤
			if(userBrowsedMap.size()==0)
			{
				System.out.println("no browse !!!");
				return;
			}
			
			for (Long key : userBrowsedMap.keySet()) {
				System.out.println("user " + key + " ,");
				ArrayList<Long> newsIds = userBrowsedMap.get(key);
				for (Long newsId : newsIds) {
					System.out.println("      " + newsId );
				}
			}
			//用户喜好关键词列表：userPrefListMap:<String(userid),String(json))>
			HashMap<Long,CustomizedHashMap<Integer,CustomizedHashMap<String,Double>>> userPrefListMap=recommendKit.getUserPrefListMap(userBrowsedMap.keySet());
			//新闻对应关键词列表与模块ID：newsTFIDFMap:<String(newsid),List<Keyword>>,<String(newsModuleId),Integer(moduleid)>
			HashMap<String,Object> newsTFIDFMap=getNewsTFIDFMap();
			
			//开始遍历用户浏览记录，更新用户喜好关键词列表
			//对每个用户（外层循环），循环他所看过的每条新闻（内层循环），对每个新闻，更新它的关键词列表到用户的对应模块中
			Iterator<Long> ite=userBrowsedMap.keySet().iterator();
			
			while(ite.hasNext()){
				Long userId=ite.next();
				ArrayList<Long> newsList=userBrowsedMap.get(userId);
				for(Long news:newsList){
					Integer moduleId=(Integer) newsTFIDFMap.get(news+"moduleid");
					//获得对应模块的（关键词：喜好）map
					CustomizedHashMap<String,Double> rateMap=userPrefListMap.get(userId).get(moduleId);
					//获得新闻的（关键词：TFIDF值）map
					List<Keyword> keywordList=(List<Keyword>) newsTFIDFMap.get(news.toString());
					Iterator<Keyword> keywordIte=keywordList.iterator();
					while(keywordIte.hasNext()){
						Keyword keyword=keywordIte.next();
						String name=keyword.getName();
						if(rateMap.containsKey(name)){
							rateMap.put(name, rateMap.get(name)+keyword.getScore());
						}
						else{
							rateMap.put(name,keyword.getScore());
						}
					}
					userPrefListMap.get(userId);
				}
			}
			Iterator<Long> iterator=userBrowsedMap.keySet().iterator();
			while(iterator.hasNext()){
				Long userId=iterator.next();
				try
				{
					System.out.println("userPreList " + userId + " : " + (userPrefListMap.get(userId)));
//					Db.update("update users set pref_list='"+userPrefListMap.get(userId)+"' where id=?",userId);
					Users users = usersMapper.selectByPrimaryKey(userId);
					users.setPrefList( userPrefListMap.get(userId).toString() );
					users.setLatestLogTime(new Date());
					int updateResult = usersMapper.updateByPrimaryKeyWithBLOBs(users);
					session.commit();
					System.out.println("！！！！！！！！！！！！ update result : " + updateResult);
					System.out.println(usersMapper.selectByPrimaryKey(userId).getPrefList());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
	}
	
	/**
	 * 所有用户的喜好关键词列表TFIDF值随时间进行自动衰减更新
	 */
	public void autoDecRefresh(){
		autoDecRefresh(recommendKit.getUserList());
	}
	
	/**
	 * 所有用户的喜好关键词列表TFIDF值随时间进行自动衰减更新
	 */
	public void autoDecRefresh(Collection<Long> userIdsCol){
		//获得session实例  
		SqlSession session = sqlSessionFactory.openSession();
		this.usersMapper = session.getMapper(UsersMapper.class);
		
		try
		{
			String inQuery=RecommendKit.getInQueryStringWithSingleQuote(userIdsCol.iterator());
			if(inQuery.equals("()")){
				return;
			}
			
//			List<Users> userList=Users.dao.find("select id,pref_list from users where id in "+inQuery);
			UsersExample usersExample = new UsersExample();
			LinkedList<Long> list = new LinkedList<Long>();
			Long[] userIds = userIdsCol.toArray(new Long[userIdsCol.size()]);
			for (Long id : userIds) {
				list.add(id);
			}
			usersExample.createCriteria().andIdIn(list);
			List<Users> userList = usersMapper.selectByExampleWithBLOBs(usersExample);
			//用以更新的用户喜好关键词map的json串
			//用于删除喜好值过低的关键词
			ArrayList<String> keywordToDelete=new ArrayList<String>();
			for(Users user:userList){
				String newPrefList="{";
				HashMap<Integer,CustomizedHashMap<String,Double>> map=JsonKit.jsonPrefListtoMap(user.getPrefList());
				Iterator<Integer> ite=map.keySet().iterator();
				while(ite.hasNext()){
					//用户对应模块的喜好不为空
					Integer moduleId=ite.next();
					CustomizedHashMap<String,Double> moduleMap=map.get(moduleId);
					newPrefList+="\""+moduleId+"\":";
					//N:{"X1":n1,"X2":n2,.....}
					if(!(moduleMap.toString().equals("{}"))){
						Iterator<String> inIte=moduleMap.keySet().iterator();
						while(inIte.hasNext()){
							String key=inIte.next();
							//累计TFIDF值乘以衰减系数
							double result=moduleMap.get(key)*DEC_COEE;
							if(result<10){
								keywordToDelete.add(key);
							}
							moduleMap.put(key,result);
						}
					}
					for(String deleteKey:keywordToDelete){
						moduleMap.remove(deleteKey);
					}
					keywordToDelete.clear();
					newPrefList+=moduleMap.toString()+",";
				}
				newPrefList=newPrefList.substring(0,newPrefList.length()-1)+"}";
//				Db.update("update users set pref_list="+newPrefList+" where id=?",user.getId());
				Users users = usersMapper.selectByPrimaryKey(user.getId());
				users.setPrefList(newPrefList);
				usersMapper.updateByPrimaryKeyWithBLOBs(users);
				session.commit();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 提取出当天有浏览行为的用户及其各自所浏览过的新闻id列表
	 * @return
	 */
	private HashMap<Long,ArrayList<Long>> getBrowsedHistoryMap(){
		HashMap<Long, ArrayList<Long>> userBrowsedMap=new HashMap<Long,ArrayList<Long>>();
		try
		{
//			List<NewsLogs> newslogsList = NewsLogs.dao.find("select * from newslogs where view_time>"+RecommendKit.getSpecificDayFormat(0));
			List<NewsLogs> newslogsList = newsLogsMapper.queryNewsLogsTheDate(RecommendKit.getSpecificDayFormat(0));
			for(NewsLogs newslogs:newslogsList){
				if(userBrowsedMap.containsKey(newslogs.getUserId())){
					userBrowsedMap.get(newslogs.getUserId()).add(newslogs.getNewsId());
				}
				else{
					userBrowsedMap.put(newslogs.getUserId(), new ArrayList<Long>());
					userBrowsedMap.get(newslogs.getUserId()).add(newslogs.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return userBrowsedMap;
	}
	
	
	/**
	 * 获得浏览过的新闻的集合
	 * @return
	 */
	private HashSet<Long> getBrowsedNewsSet(){
		HashMap<Long,ArrayList<Long>> browsedMap=getBrowsedHistoryMap();
		HashSet<Long> newsIdSet=new HashSet<Long>();
		Iterator<Long> ite=getBrowsedHistoryMap().keySet().iterator();
		while(ite.hasNext()){
			Iterator<Long> inIte=browsedMap.get(ite.next()).iterator();
			while(inIte.hasNext()){
				newsIdSet.add(inIte.next());
			}
		}
		return newsIdSet;
	}
	
	/**
	 * 将所有当天被浏览过的新闻提取出来，以便进行TFIDF求值操作，以及对用户喜好关键词列表的更新。
	 * @return
	 */
	private HashMap<String,Object> getNewsTFIDFMap(){
		HashMap<String,Object> newsTFIDFMap=new HashMap<String,Object>();;
		try
		{
			Iterator<Long> ite=getBrowsedNewsSet().iterator();
			String newsIdListQuery="(";
			while(ite.hasNext()){
				long next=ite.next();
				newsIdListQuery+=next+",";
			}
			
//			//当天存在用户浏览记录
//			if(newsIdListQuery.length()>1){
//				newsIdListQuery=newsIdListQuery.substring(0, newsIdListQuery.length()-1)+")";
//				//提取出所有新闻的关键词列表及对应TF-IDf值，并放入一个map中
//				List<News> newsList=News.dao.find("select id,title,content,module_id from news where id in "+newsIdListQuery);
//				System.out.println("newsIdListQuery:"+newsIdListQuery);
//				for(News news:newsList){
//					newsTFIDFMap.put(String.valueOf(news.getId()), TFIDF.getTFIDE(news.getTitle(), news.getContent(),KEY_WORDS_NUM));
//					newsTFIDFMap.put(news.getId()+"moduleid", news.getModuleId());
//				}
//				for()
//			}
			
			newsIdListQuery=newsIdListQuery.substring(0, newsIdListQuery.length()-1)+")";
			//提取出所有新闻的关键词列表及对应TF-IDf值，并放入一个map中
//			List<NewsWithBLOBs> newsList = News.dao.find("select id,title,content,module_id from news where id in "+newsIdListQuery);
			NewsExample newsExample = new NewsExample();
			LinkedList<Long> list = new LinkedList<Long>();
			Long[] newsIds = getBrowsedNewsSet().toArray(new Long[getBrowsedNewsSet().size()]);
			for (Long id : newsIds) {
				list.add(id);
			}
			newsExample.createCriteria().andIdIn(list);
			List<NewsWithBLOBs> newsList = newsMapper.selectByExampleWithBLOBs(newsExample);
			for(NewsWithBLOBs news:newsList){
				newsTFIDFMap.put(String.valueOf(news.getId()), TFIDF.getTFIDF(news.getTitle(), news.getContent(),KEY_WORDS_NUM));
				newsTFIDFMap.put(news.getId()+"moduleid", news.getModuleId());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return newsTFIDFMap;
	}

	public void refreshStudentPreference(List<Long> students) {
		
		//首先对用户的喜好关键词列表进行衰减更新
		autoDecreaseStudentReference(students);
		
		//获得session实例  
		SqlSession session = sqlSessionFactory.openSession();
		this.essayViewLogMapper = session.getMapper(VEssayViewLogMapper.class);
		this.studentEssayPreferenceMapper = session.getMapper(TblStudentEssayPreferenceMapper.class);
		
		//用户浏览新闻纪录
		HashMap<Long,ArrayList<Long>> userBrowsedMap = getViewedHistoryMap(students);
		//如果前一天没有浏览记录（比如新闻门户出状况暂时关停的情况下，或者初期用户较少的时候均可能出现这种情况），则不需要执行后续更新步骤
		if(userBrowsedMap.size()==0)
		{
			System.out.println("no browse !!!");
			return;
		}
		
		for (Long key : userBrowsedMap.keySet()) {
			System.out.println("user " + key + " ,");
			ArrayList<Long> essayIds = userBrowsedMap.get(key);
			for (Long essayId : essayIds) {
				System.out.println("      " + essayId );
			}
		}
		
		//用户喜好关键词列表：userPrefListMap:<String(userid),String(json))>
		HashMap<Long,CustomizedHashMap<Integer,CustomizedHashMap<String,Double>>> userPrefListMap=recommendKit.getStudentEssayPrefListMap(userBrowsedMap.keySet());
		//文章对应关键词列表与类型ID：essayTFIDFMap:<String(essayId),List<Keyword>>,<String(type),Integer(type)>
		HashMap<String,Object> essayTFIDFMap = getEssayTFIDFMap(students);
		
		//开始遍历用户浏览记录，更新用户喜好关键词列表
		//对每个用户（外层循环），循环他所看过的每条文章（内层循环），对每个文章，更新它的关键词列表到用户的对应类型中
		Iterator<Long> ite=userBrowsedMap.keySet().iterator();
		
		while(ite.hasNext()){
			Long userId=ite.next();
			ArrayList<Long> essayIds=userBrowsedMap.get(userId);
			for(Long essayId:essayIds){
				Integer essayType=(Integer) essayTFIDFMap.get(essayId+"type");
				//获得对应类型的（关键词：喜好）map
				CustomizedHashMap<String,Double> rateMap=userPrefListMap.get(userId).get(essayType);
				//获得新闻的（关键词：TFIDF值）map
				List<Keyword> keywordList=(List<Keyword>) essayTFIDFMap.get(essayId.toString());
				Iterator<Keyword> keywordIte=keywordList.iterator();
				while(keywordIte.hasNext()){
					Keyword keyword=keywordIte.next();
					String name=keyword.getName();
					if(rateMap.containsKey(name)){
						rateMap.put(name, rateMap.get(name)+keyword.getScore());
					}
					else{
						rateMap.put(name,keyword.getScore());
					}
				}
				userPrefListMap.get(userId);
			}
		}
		Iterator<Long> iterator=userBrowsedMap.keySet().iterator();
		while(iterator.hasNext()){
			Long userId=iterator.next();
			try
			{
				System.out.println("userPreList " + userId + " : " + (userPrefListMap.get(userId)));
				TblStudentEssayPreference studentEssayPreference = studentEssayPreferenceMapper.selectByPrimaryKey(userId);
				studentEssayPreference.setPreference( userPrefListMap.get(userId).toString() );
				studentEssayPreference.setLastLogTime(new Date());
				int updateResult = studentEssayPreferenceMapper.updateByPrimaryKeyWithBLOBs(studentEssayPreference);
				session.commit();
				System.out.println("！！！！！！！！！！！！ update result : " + updateResult);
				System.out.println(studentEssayPreferenceMapper.selectByPrimaryKey(userId).getPreference());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 将所有当天被浏览过的文章提取出来，以便进行TFIDF求值操作，以及对用户喜好关键词列表的更新。
	 * @param students 
	 * @return
	 */
	private HashMap<String, Object> getEssayTFIDFMap(List<Long> students) {
		HashMap<String,Object> essayTFIDFMap=new HashMap<String,Object>();;
		try
		{
			Iterator<Long> ite = getViewedEssaysSet(students).iterator();
			String essayIdListQuery="(";
			while(ite.hasNext()){
				long next=ite.next();
				essayIdListQuery+=next+",";
			}
			

			
			essayIdListQuery=essayIdListQuery.substring(0, essayIdListQuery.length()-1)+")";
			
			//提取出所有文章的关键词列表及对应TF-IDf值，并放入一个map中
			TblEssayExample essayExample = new TblEssayExample();
			essayExample.createCriteria().andIdIn(Arrays.asList(getViewedEssaysSet(students).toArray(new Long[getViewedEssaysSet(students).size()])));
			List<TblEssayWithBLOBs> essays = essayMapper.selectByExampleWithBLOBs(essayExample);
			for(TblEssayWithBLOBs essay:essays){
				essayTFIDFMap.put(String.valueOf(essay.getId()), TFIDF.getTFIDF(essay.getTitle(), essay.getContentText(), KEY_WORDS_NUM));
				essayTFIDFMap.put(essay.getId()+"type", essay.getType());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return essayTFIDFMap;
	}

	
	/**
	 * 获得浏览过的文章的集合
	 * @return
	 */
	private HashSet<Long> getViewedEssaysSet(List<Long> students) {
		
		HashMap<Long,ArrayList<Long>> viewedMap = getViewedHistoryMap(students);
		HashSet<Long> essayIdSet = new HashSet<Long>();
		Iterator<Long> ite = getViewedHistoryMap(students).keySet().iterator();
		while(ite.hasNext()){
			Iterator<Long> inIte=viewedMap .get(ite.next()).iterator();
			while(inIte.hasNext()){
				essayIdSet.add(inIte.next());
			}
		}
		return essayIdSet;
	}

	
	/**
	 * 提取出当天有浏览行为的用户及其各自所浏览过的文章id列表
	 * @return
	 */
	private HashMap<Long, ArrayList<Long>> getViewedHistoryMap(List<Long> students) {
		
		HashMap<Long, ArrayList<Long>> userBrowsedMap=new HashMap<Long,ArrayList<Long>>();
		try
		{

			List<VEssayViewLog> essayslogsList = essayViewLogMapper.queryEssayViewLogsTheDate(RecommendKit.getSpecificDayFormat(0));
			for(VEssayViewLog essayViewLog:essayslogsList){
				if(students.contains(essayViewLog.getStudentId()))
				{
					if(userBrowsedMap.containsKey(essayViewLog.getStudentId())){
						userBrowsedMap.get(essayViewLog.getStudentId()).add(essayViewLog.getEssayId());
					}
					else{
						userBrowsedMap.put(essayViewLog.getStudentId(), new ArrayList<Long>());
						userBrowsedMap.get(essayViewLog.getStudentId()).add(essayViewLog.getEssayId());
					}
				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return userBrowsedMap;
	}

	private void autoDecreaseStudentReference(List<Long> students) {
		
		//获得session实例  
		SqlSession session = sqlSessionFactory.openSession();
		this.essayViewLogMapper = session.getMapper(VEssayViewLogMapper.class);
		this.studentEssayPreferenceMapper = session.getMapper(TblStudentEssayPreferenceMapper.class);
		
		try
		{
			String inQuery=RecommendKit.getInQueryStringWithSingleQuote(students.iterator());
			if(inQuery.equals("()")){
				return;
			}
			

			TblStudentEssayPreferenceExample studentEssayPreferenceExample = new TblStudentEssayPreferenceExample();
			studentEssayPreferenceExample.createCriteria().andStudentIdIn(students);
			List<TblStudentEssayPreference> studentEssayPreferences = studentEssayPreferenceMapper.selectByExampleWithBLOBs(studentEssayPreferenceExample);
			
			//用以更新的用户喜好关键词map的json串
			//用于删除喜好值过低的关键词
			ArrayList<String> keywordToDelete=new ArrayList<String>();
			for(TblStudentEssayPreference studentEssayPreference:studentEssayPreferences)
			{
				String newPrefList="{";
				HashMap<Integer,CustomizedHashMap<String,Double>> map=JsonKit.jsonPrefListtoMap(studentEssayPreference.getPreference());
				Iterator<Integer> ite=map.keySet().iterator();
				while(ite.hasNext()){
					//typeId为不同文章类型
					Integer typeId=ite.next();
					CustomizedHashMap<String,Double> typeMap=map.get(typeId);
					newPrefList+="\""+typeId+"\":";
					//N:{"X1":n1,"X2":n2,.....}
					if(!(typeMap.toString().equals("{}"))){
						Iterator<String> inIte=typeMap.keySet().iterator();
						while(inIte.hasNext()){
							String key=inIte.next();
							//累计TFIDF值乘以衰减系数
							double result=typeMap.get(key)*DEC_COEE;
							if(result<10){
								keywordToDelete.add(key);
							}
							typeMap.put(key,result);
						}
					}
					for(String deleteKey:keywordToDelete){
						typeMap.remove(deleteKey);
					}
					keywordToDelete.clear();
					newPrefList+=typeMap.toString()+",";
				}
				newPrefList=newPrefList.substring(0,newPrefList.length()-1)+"}";

				
				studentEssayPreference.setPreference(newPrefList);
				studentEssayPreferenceMapper.updateByPrimaryKeyWithBLOBs(studentEssayPreference);
				
				session.commit();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
				
	}

	public void refreshOneStudentPreference(Long studentId) {
		
		List<Long> students = new ArrayList<Long>();
		students.add(studentId);
		
		//首先对用户的喜好关键词列表进行衰减更新
		autoDecreaseStudentReference(students);
		
		//获得session实例  
		SqlSession session = sqlSessionFactory.openSession();
		this.essayViewLogMapper = session.getMapper(VEssayViewLogMapper.class);
		this.studentEssayPreferenceMapper = session.getMapper(TblStudentEssayPreferenceMapper.class);
		
		//用户浏览新闻纪录
		HashMap<Long,ArrayList<Long>> userBrowsedMap = getViewedHistoryMap(students);
		//如果前一天没有浏览记录（比如新闻门户出状况暂时关停的情况下，或者初期用户较少的时候均可能出现这种情况），则不需要执行后续更新步骤
		if(userBrowsedMap.size()==0)
		{
			System.out.println("no browse !!!");
			return;
		}
		
		for (Long key : userBrowsedMap.keySet()) {
			System.out.println("user " + key + " ,");
			ArrayList<Long> essayIds = userBrowsedMap.get(key);
			for (Long essayId : essayIds) {
				System.out.println("      " + essayId );
			}
		}
		
		//用户喜好关键词列表：userPrefListMap:<String(userid),String(json))>
		HashMap<Long,CustomizedHashMap<Integer,CustomizedHashMap<String,Double>>> userPrefListMap=recommendKit.getStudentEssayPrefListMap(userBrowsedMap.keySet());
		//文章对应关键词列表与类型ID：essayTFIDFMap:<String(essayId),List<Keyword>>,<String(type),Integer(type)>
		HashMap<String,Object> essayTFIDFMap = getEssayTFIDFMap(students);
		
		//开始遍历用户浏览记录，更新用户喜好关键词列表
		//对每个用户（外层循环），循环他所看过的每条文章（内层循环），对每个文章，更新它的关键词列表到用户的对应类型中
		Iterator<Long> ite=userBrowsedMap.keySet().iterator();
		
		while(ite.hasNext()){
			Long userId=ite.next();
			ArrayList<Long> essayIds=userBrowsedMap.get(userId);
			for(Long essayId:essayIds){
				Integer essayType=(Integer) essayTFIDFMap.get(essayId+"type");
				//获得对应类型的（关键词：喜好）map
				CustomizedHashMap<String,Double> rateMap=userPrefListMap.get(userId).get(essayType);
				//获得新闻的（关键词：TFIDF值）map
				List<Keyword> keywordList=(List<Keyword>) essayTFIDFMap.get(essayId.toString());
				Iterator<Keyword> keywordIte=keywordList.iterator();
				while(keywordIte.hasNext()){
					Keyword keyword=keywordIte.next();
					String name=keyword.getName();
					if(rateMap.containsKey(name)){
						rateMap.put(name, rateMap.get(name)+keyword.getScore());
					}
					else{
						rateMap.put(name,keyword.getScore());
					}
				}
				userPrefListMap.get(userId);
			}
		}
		Iterator<Long> iterator=userBrowsedMap.keySet().iterator();
		while(iterator.hasNext()){
			Long userId=iterator.next();
			try
			{
				System.out.println("userPreList " + userId + " : " + (userPrefListMap.get(userId)));
				TblStudentEssayPreference studentEssayPreference = studentEssayPreferenceMapper.selectByPrimaryKey(userId);
				studentEssayPreference.setPreference( userPrefListMap.get(userId).toString() );
				studentEssayPreference.setLastLogTime(new Date());
				int updateResult = studentEssayPreferenceMapper.updateByPrimaryKeyWithBLOBs(studentEssayPreference);
				session.commit();
				System.out.println("！！！！！！！！！！！！ update result : " + updateResult);
				System.out.println(studentEssayPreferenceMapper.selectByPrimaryKey(userId).getPreference());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
}
