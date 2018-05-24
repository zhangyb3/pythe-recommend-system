package com.pythe.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 参数工具类
 * @author Administrator
 *
 */
public class ParamUtils {

	
	
//	public static Long getTaskIdFromArgs(String[] args) {
//		try {
//			if(args != null && args.length > 0) {
//				return Long.valueOf(args[0]);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}  
//		return null;
//	}
	
	/**
	 * 从JSON对象中提取参数
	 * @param jsonObject JSON对象
	 * @return 参数
	 */
	public static String getOneParam(JSONObject jsonObject, String field) {
		JSONArray jsonArray = jsonObject.getJSONArray(field);
		if(jsonArray != null && jsonArray.size() > 0) {
			return jsonArray.getString(0);
		}
		return null;
	}
	
	
	/**
	 * 从JSON对象中提取参数
	 * @param jsonObject JSON对象
	 * @return 参数
	 */
	public static Double getTwoParam(JSONObject jsonObject, String field) {
		JSONArray jsonArray = jsonObject.getJSONArray(field);
		if(jsonArray != null && jsonArray.size() > 0) {
			return jsonArray.getDouble(1);
		}
		return null;
	}
	
}
