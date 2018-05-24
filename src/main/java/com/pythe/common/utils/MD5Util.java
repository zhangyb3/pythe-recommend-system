package com.pythe.common.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 采用MD5加密解密
 * @author tfq
 * @datetime 2011-10-13
 */
public class MD5Util {

	/***
	 * MD5加码 生成32位md5码
	 */
	public static String string2MD5(String inStr){
		MessageDigest md5 = null;
		try{
			md5 = MessageDigest.getInstance("MD5");
		}catch (Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++){
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}

	/**
	 * 加密解密算法 执行一次加密，两次解密
	 */ 
	public static String convertMD5(String inStr){

		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++){
			BigInteger key = key(os,as);
			a[i] = (char) ( a[i] ^ key.intValue());
		}
		String s = new String(a);
		return s;

	}
	
	static String[] os = {"a","b"};
	static String[] as = {"1","2"};
	
	public static BigInteger key(String[] original, String[] associate){
		
		BigInteger o_sum = new BigInteger("0");
		for(String o: original)
		{
			o_sum = o_sum.add(new BigInteger(o,36));
		}
		
		BigInteger a_sum = new BigInteger("0");
		for(String a: associate)
		{
			a_sum = a_sum.add(new BigInteger(a,36));
		}
		
		BigInteger result = o_sum.divide(a_sum);
		//System.out.println(result);
		
		return result;
	}

	// 测试主函数
	public static void main(String args[]) {
		String s = new String("c=1&d=2");
		System.out.println("原始：" + s);
		System.out.println("MD5后：" + string2MD5(s));
		System.out.println("加密的：" + convertMD5(s));
		System.out.println("解密的：" + convertMD5(convertMD5(s)));
		


	}
}
