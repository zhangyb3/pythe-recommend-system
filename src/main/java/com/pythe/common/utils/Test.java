package com.pythe.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class Test {
	private static final String EVERY_TYPE_NUM_INIT = "{\"故事\":0,\"科学\":0,\"诗词曲\":0,\"美文\":0,\"古文\":0,\"传记\":0,\"小说\":0,\"历史\":0,\"杂文\":0}";


	private static final String EVERY_WEEK_NUM_INIT = "{\"周一\":0.0,\"周二\":0.0,\"周三\":0.0,\"周四\":0.0,\"周五\":0.0,\"周六\":0.0,\"周日\":0.0}";
	
	public static void main(String[] args) throws Exception {
		
//		String string ="{\"text\":[\"content1\"],\"img\":[\"img1\",\"img2\"],\"audio\":[\"audio1\"]}";
//		System.out.println(string);
//		
//		System.out.println(DateUtils.todayForWeekByString());
		
//		Map<String, Object> a = JsonUtils.json2MapAboutStringAndObject(EVERY_WEEK_NUM_INIT);
//		System.out.println(Double.parseDouble(a.get("周一").toString())+2.0);
		System.out.println(EVERY_WEEK_NUM_INIT);
		
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("周一", 0+"");
//		map.put("周二",  0+"");
//		Map<String, Double> weekMap = JsonUtils.json2MapAboutString2Double(JsonUtils.objectToJson(map));
		//weekMap.put("周一", weekMap.get("周一") +15.3);
		
		//String today = DateUtils.todayForWeekByString();
		
		//Map<String, Double> weekMap = JsonUtils.json2MapAboutString2Double(EVERY_WEEK_NUM_INIT);
		//System.out.println(weekMap.get(today)+1d);
//		weekMap.put(today, weekMap.get(today) +15);
//		
//		System.out.println(weekMap.get(today));
	}


}
