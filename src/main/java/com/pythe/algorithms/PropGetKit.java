/**
 * 
 */
package com.pythe.algorithms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月30日 用以读取配置文件，获取对应属性
 */
@Service
public class PropGetKit
{
	@Value("${BEFORE_DAYS}")
	public static Integer BEFORE_DAYS;
	
	@Value("${ACTIVE_DAYS}")
	public static Integer ACTIVE_DAYS;
	
	@Value("${CB_REC_NUM}")
	public static Integer CB_REC_NUM;
	
	@Value("${TFIDF_KEYWORDS_NUM}")
	public static Integer TFIDF_KEYWORDS_NUM;
	
	private static final Logger logger = Logger.getLogger(PropGetKit.class);

	public static Properties propGetKit = new Properties();
	
	public PropGetKit(){
		
	}

	public static void loadProperties(String configFileName)
	{
		try
		{
			propGetKit.load(new FileInputStream(System.getProperty("user.dir") + "/WORKSPACE/GIT/pythe-recommend-system/src/main/resources/" + configFileName + ".properties"));
			
		}
		catch (FileNotFoundException e)
		{
			logger.error("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
		}
		catch (IOException e)
		{
			logger.error("装载文件--->失败!");
		}
	}

	public static String getString(String key)
	{
		return propGetKit.getProperty(key);
	}
	
	public static int getInt(String key)
	{
		return Integer.valueOf(propGetKit.getProperty(key));
	}
	
}
