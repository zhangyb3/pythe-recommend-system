package com.pythe.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pythe自定义响应结构
 */
public class JsonUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
    	try {
			String string = MAPPER.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 将json结果集转化为对象
     * 
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
    	try {
    		List<T> list = MAPPER.readValue(jsonData, javaType);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 将json数据转换成pojo对象Stack
     * <p>Title: jsonToStack</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>Stack<T> jsonToStack(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Stack.class, beanType);
    	try {
    		Stack<T> stack = MAPPER.readValue(jsonData, javaType);
    		return stack;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    /**
     * 将json数据转换成pojo对象set
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>LinkedHashSet<T> jsonToSet(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(LinkedHashSet.class, beanType);
    	try {
    		LinkedHashSet<T> list = MAPPER.readValue(jsonData, javaType);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    
    /**
     * 将json数据转换成map
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static Map<String,Integer> json2Map(String json){
    	return JSON.parseObject(json, Map.class);
    }
    
    public static Map<String,Double> json2MapAboutString2Double(String json){
    	return JSON.parseObject(json, Map.class);
    }
    
    
    public static Map<String, Object> json2MapAboutStringAndObject(String json){
    	return JSON.parseObject(json, Map.class);
    }
    
    
    
    public static LinkedHashMap<String, String> json2LinkedHashMap(String json){
    	
    	return JSON.parseObject(json, LinkedHashMap.class);
    }
    
    
    
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonMapToList(String jsonData,String target,  Class<T> beanType) {
		List<T> list = JSONObject.parseObject(jsonData)
				.getJSONArray(target)
				.toJavaList(beanType);
		return list;
    }
    
    /**
     * 向某个
     * @param str
     * @param key
     * @param value
     * @return
     */
	public static String setValueForJson(String str, String key,String value){
		key = "\""+key+"\":";
		int tag = str.indexOf(key);
		String start = str.substring(0,tag );
		String end = str.substring(str.indexOf("]", tag)+1);
		String middle = key+value;
		return start+middle+end;
	}

    
}
