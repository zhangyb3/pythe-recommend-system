package com.pythe.common.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RegexUtils {

	/***
	 * 获取所有标签中的内容
	 * 
	 * @param HTML
	 * @return
	 */
	public static List<String> getTagUrl(String HTML) {
		List<String> tagUrl = new ArrayList<String>();
		Pattern p = Pattern.compile(">([^<]*)</");
		Matcher m = p.matcher(HTML);
		while (m.find()) {
			tagUrl.add(m.group(1));
		}
		return tagUrl;
	}

	/***
	 * 得到字串里面的所有中文字符
	 * 
	 * @param HTML
	 * @return
	 */
	public static String getContentInHTML(String HTML) {
		Document doc = Jsoup.parse(HTML);
		String text = doc.body().text().replace("　　", "").trim();
		String s = "";
		String regex = "([\u4e00-\u9fa5]+)";
		Matcher matcher = Pattern.compile(regex).matcher(text);
		while (matcher.find()) {
			s = s + matcher.group();
		}
		return s;
	}

	
	/***
	 * 判断字符串是否仅仅有数字
	 * 
	 * @param HTML
	 * @return
	 */
	public static Boolean isNumber(String word) {
		return word.matches("[两每零一二两三四五六七八九十几]+");
	}
	
	
	
	/***
	 * 获取所有标签中的内容
	 * 
	 * @param HTML
	 * @return
	 */
	public static String getTagUrlByString(String HTML) {
		// 在提取前为防止留下其他符号导致前端显示有问题，
		String string = "";
		Pattern p = Pattern.compile(">([^<]*)</");
		Matcher m = p.matcher(HTML);
		while (m.find()) {
			string = string + m.group(1) + "\n";
		}
		return string.replace("\n\n\n", "\n").replace("\t", "").replace("\n\n", "\n");
	}

	// 判断是否含中文字符
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	// 判断是否只有中文字符
	public static boolean isOnlyContainChinese(String str) {
		return str.matches("[\u4e00-\u9fa5]+");
	}
	
	
	
	/**
	 * 判断某个字符在某个字符串上出现几次
	 * @param string
	 * @param c
	 * @return
	 */
	public static int countChar(String string,String c) {
	    //判断某个字符出现几次
	    int len1 = string.length();//获取原来的字符串长度
	    String s1 = string.replaceAll(c ,"");
	    int len2 = s1.length();
	    int lenTimes = len1-len2;//出现的次数
	    return lenTimes;
	}
	
	
	/**
	 * 在某个字符的中间切成2半
	 * @param string
	 * @param c
	 * @return
	 */
	public static LinkedList<String> partitionBetweenChar(String string,String c) {
	    //判断某个字符出现几次
	    int len1 = string.length();//获取原来的字符串长度
	    String s1 = string.replaceAll(c ,"");
	    int len2 = s1.length();
	    int lenTimes = len1-len2;//出现的次数
	    int k =lenTimes/2;
	    Matcher slashMatcher = Pattern.compile(c).matcher(string);  
        int mIdx = 0;  
  
        while(slashMatcher.find()) {  
            mIdx++;  
            //当"/"符号第三次出现的位置  
            if(mIdx == k){  
                break;  
            }  
        }  
        
        LinkedList<String> list =new LinkedList<String>();
        list.add(string.substring(0,slashMatcher.start()));
        list.add(string.substring(slashMatcher.start()+1));
        return list;
	}
	
	

}
