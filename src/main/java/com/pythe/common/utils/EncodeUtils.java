package com.pythe.common.utils;

import java.util.Random;

/**
 * 采用MD5加密解密
 * @author tfq
 * @datetime 2011-10-13
 */
public class EncodeUtils {

	
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
    	String url = "https://wx.ingcart.com?a";
    	
    	
         int  num = 10;
         
         //加密
         String str = String.valueOf(num);
         String result = "";
         
    	 Random r=new Random();
         for(char c : str.toCharArray()){
        	 Integer  b = Integer.valueOf(c);
        	 Integer a = b+17;
        	 //产生一个 0~9的随机数
             result = result+byteAsciiToChar(a) + r.nextInt(10);
         }
         
         String base = "QWERTYUIOPSADFGHJKLZXCVBNMabcdefghijklmnopqrstuvwxyz0123456789-";
         int rondom= r.nextInt(base.length());
         result = base.charAt(rondom)+result;
         System.out.println("加密后的字符串"+ result);
         
         //解密
         String string  = result.substring(1);
         string = string.replaceAll("[^a-z^A-Z]", "");
        // System.out.println(string);
         
         String decodeResult = "";
         for(char c : string.toCharArray()){
        	 decodeResult  =  decodeResult + byteAsciiToChar((int)c - 17);
        	 //产生一个 0~9的随机数
         }
         System.out.println(Integer.valueOf(decodeResult));
    }  
    
    public static String decode(String result){
        String string  = result.substring(1);
        string = string.replaceAll("[^a-z^A-Z]", "");
//        System.out.println(string);
        
        String decodeResult = "";
        for(char c : string.toCharArray()){
       	 decodeResult  =  decodeResult + byteAsciiToChar((int)c - 17);
       	 //产生一个 0~9的随机数
        }
        
        return decodeResult;
    }
  
    
    /** 
     * 方法一：将char 强制转换为byte 
     * @param ch
     */  
    public static byte charToByteAscii(char ch){  
        byte byteAscii = (byte)ch;  
          
        return byteAscii;  
    }  
    /** 
     * 同理，ascii转换为char 直接int强制转换为char 
     * @param ascii 
     * @return 
     */  
    public static char byteAsciiToChar(int ascii){  
        char ch = (char)ascii;  
        return ch;  
    }  

}
