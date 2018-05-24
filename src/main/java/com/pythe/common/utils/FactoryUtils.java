package com.pythe.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;





public class FactoryUtils {
	

	   
	   
	/**
	 * 生成UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static Integer GradetranferLevel(Integer gradeId){
		int defaultReadingGrade = 0;
		
    	int bit_0 = gradeId%10;
    	int bit_1 = (gradeId/10)%10;
    	int bit_2 = ((gradeId/10)/10)%10;
    	
    	if(bit_2==1 && bit_1>=1 && bit_1<=3)
    	{
    		defaultReadingGrade = 1;
    	}
    	if(bit_2==1 && bit_1>=4 && bit_1<=6)
    	{
    		defaultReadingGrade = 2;
    	}
    	if(bit_2==2 && bit_1>=1 && bit_1<=2)
    	{
    		defaultReadingGrade = 3;
    	}
    	if( (bit_2==2 && bit_1==3) || (bit_2==3 && bit_1==1) )
    	{
    		defaultReadingGrade = 4;
    	}
    	if(bit_2==3 && bit_1>=2 && bit_1<=3)
    	{
    		defaultReadingGrade = 5;
    	}
    	
    	return defaultReadingGrade;
	}
	
	
/**
	 * 生成M	D5_CODE
	 * @return
	 */
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	// D55C4AC7A4AE1E47E1265BE4B20C1BB2
	//5BEDEBEDAB1AB7FD32DC0AB18F2A0BB4
	  /**
     * 获取签名 md5加密(微信支付必须用MD5加密)
     * 获取支付签名
     * @param params
     * @return
     */
    public static String getSign(SortedMap<String, String> params,String WX_KEY){
        String sign = null;
        StringBuffer sb = new StringBuffer();
        Set es = params.entrySet();
        Iterator iterator = es.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)&& !"key".equals(k)) {
                sb.append(k+"="+v+"&");
            }
        }
        sb.append("key="+WX_KEY);
//        if (WX_KEY!=null) {
//        	
//		}
      
        sign = FactoryUtils.MD5(sb.toString()).toUpperCase();
        return sign;
    }
    
    
  

    /**
     * 参数进行XML化
     * @param map,sign
     * @return
     */
    public static String parseString2Xml(Map<String, String> map,String sign){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = map.entrySet();
        Iterator iterator = es.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            sb.append("<"+k+">"+v+"</"+k+">");
        }
        sb.append("<sign>"+sign+"</sign>");
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * 参数进行XML化
     * @param map,sign
     * @return
     */
    public static String parseString2Xml(Map<String, String> map){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = map.entrySet();
        Iterator iterator = es.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            sb.append("<"+k+">"+v+"</"+k+">");
        }
        
        sb.append("</xml>");
        return sb.toString();
    }
    
    
    /**
	 * 得到本地IP地址
	 * 
	 * @return
     * @throws UnknownHostException 
	 */
	public static String getLocalAddress() throws UnknownHostException{
		return InetAddress.getLocalHost().getHostAddress();
	}

    
    /**
	 * Message验证码
	 */
	public static String genMessageVerificationCode() {
		//加上三位随机数
		Random random = new Random();
		int code = random.nextInt(9999);
		//如果不足三位前面补0
		String str = String.format("%04d", code);
		
		return str;
	}
    
	
	
	/**
	 * 类别转换
	 * {"故事":0,"科学":0,"诗词曲":0,"美文":0,"文言文":0,"传记":0,"小说":0,"历史":0,"杂文":0}
	 * */
	public static String Integer2StringInAriticleType(Integer num){
		  switch (num)
	  		{
		    		case 0:
		    		{
		    			return "故事";
		    		}
		    		case 1:
		    		{
		    			return "科学";
		    		}
		    		case 2:
		    		{
		    			return "诗词曲";
		    		}
		    		case 3:
		    		{
		    			return "美文";
		    		}
		    		case 4:
		    		{
		    			return "文言文";
		    		}
		    		case 5:
		    		{
		    			return "传记";
		    		}
		    		case 6:
		    		{
		    			return "小说";
		    		}
		    		case 7:
		    		{
		    			return "历史";
		    		}
		    		case 8:
		    		{
		    			return "杂文";
		    		}	
		    		case 10:
		    			return "课文";
		    		
		  			default :
		  			{
		  				return null;
		  			}
	  		}
	}
	


	/**
	 * 判断str2包含多少个str1中的字符
	 * 
	 * @param str1
	 * @param str2
	 * @return counter
	 */
	public static int countStr(String str1, String str2) {
		int counter = 0;
		str1 = RegexUtils.getContentInHTML(str1);
		str2 = RegexUtils.getContentInHTML(str2);
		for (char string : str1.toCharArray()) {
			if (str2.indexOf(string) != -1) {
				counter = counter + 1;
			}
		}
		return counter;
	}
	
	
	
	/**
	 * 判断某个str1是否和str2匹配最贴切的一个
	 * 
	 * @param str1
	 * @param str2
	 * @return counter
	 */
	public static Boolean findAppropriateStr(String str1, String str2) {
		int counter = 0;
		str1 = RegexUtils.getContentInHTML(str1);
		str2 = RegexUtils.getContentInHTML(str2);
		for (char string : str1.toCharArray()) {
			if (str2.indexOf(string) != -1) {
				counter = counter + 1;
			}
		}
		
		LinkedList<Integer> list = new LinkedList<>();
		list.add(str1.length());
		list.add(str2.length());
		int min = Integer.MAX_VALUE;
		for (Integer l : list) {
			if (l<min) {
				min = l;
			}
		}
		if (min==counter) {
			return true;
		}else{
			return false;
		}
	}
	
	
	//年级切换为grade
	public static Integer GradeNameswitchGrade(String grade){
		  switch (grade)
	  		{
		    		case "一年级上册":
		    		{
		    			return 1;
		    		}
		    		case "一年级下册":
		    		{
		    			return 1;
		    		}
		    		case "二年级上册":
		    		{
		    			return 1;
		    		}
		    		case "二年级下册":
		    		{
		    			return 1;
		    		}
		    		case "三年级上册":
		    		{
		    			return 2;
		    		}
		    		case "三年级下册":
		    		{
		    			return 2;
		    		}
		    		case "四年级上册":
		    		{
		    			return 2;
		    		}
		    		case "四年级下册":
		    		{
		    			return 2;
		    		}
		    		case "五年级上册":
		    		{
		    			return 2;
		    		}
		    		case "五年级下册":
		    		{
		    			return 2;
		    		}
		    		case "六年级上册":
		    		{
		    			return 2;
		    		}
		    		case "六年级下册":
		    		{
		    			return 2;
		    		}
		    		case "初一上册":
		    		{
		    			return 3;
		    		}
		    		case "初一下册":
		    		{
		    			return 3;
		    		}
		    		case "初二上册":
		    		{
		    			return 3;
		    		}
		    		case "初二下册":
		    		{
		    			return 3;
		    		}
		    		
		    		case "初三上册":
		    		{
		    			return 4;
		    		}
		    		case "初三下册":
		    		{
		    			return 4;
		    		}
		    		
		    		case "高一上册":
		    		{
		    			return 4;
		    		}
		    		case "高一下册":
		    		{
		    			return 4;
		    		}
		    		
		    		case "高二上册":
		    		{
		    			return 5;
		    		}
		    		case "高二下册":
		    		{
		    			return 5;
		    		}
		    		case "高三上册":
		    		{
		    			return 5;
		    		}
		    		case "高三下册":
		    		{
		    			return 5;
		    		}
		  			default :
		  			{
		  				return -1;
		  			}
	  		}
	}
	
}
