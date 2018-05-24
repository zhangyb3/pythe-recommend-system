package com.pythe.common.utils;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
//import com.sun.tools.javac.api.Formattable.LocalizedString;

public class LCS {
	//返回最长匹配的公共子序子串
		public static String getLCS(String x,String y){
			int substringLength1 = x.length();
			int substringLength2 = y.length(); // 具体大小可自行设置

			Long startTime = System.nanoTime();
			// 构造二维数组记录子问题x[i]和y[i]的LCS的长度
			int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];

			// 动态规划计算所有子问题
			for (int i = substringLength1 - 1; i >= 0; i--) {
				for (int j = substringLength2 - 1; j >= 0; j--) {
					if (x.charAt(i) == y.charAt(j))
						opt[i][j] = opt[i + 1][j + 1] + 1; // 参考上文我给的公式。
					else
						opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]); 
				}
			}
//			System.out.println("substring1:" + x);
//			System.out.println("substring2:" + y);


			int i = 0, j = 0;
			//最长公共子序算法LCS
			String str="";
			while (i < substringLength1 && j < substringLength2) {
				if (x.charAt(i) == y.charAt(j)) {
					str=str+x.charAt(i);
					i++;
					j++;
				} else if (opt[i + 1][j] >= opt[i][j + 1])
					i++;
				else
					j++;
			}
			Long endTime = System.nanoTime();
			//System.out.println(" Totle time is " + (endTime - startTime) + " ns");
			return str;
		}
		
		
		
		//返回最长匹配的公共子序子串
		public static String getLCSDetailed(String x,String y){
			int substringLength1 = x.length();
			int substringLength2 = y.length(); // 具体大小可自行设置

			Long startTime = System.nanoTime();
			// 构造二维数组记录子问题x[i]和y[i]的LCS的长度
			int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];

			// 动态规划计算所有子问题
			for (int i = substringLength1 - 1; i >= 0; i--) {
				for (int j = substringLength2 - 1; j >= 0; j--) {
					if (x.charAt(i) == y.charAt(j))
						opt[i][j] = opt[i + 1][j + 1] + 1; // 参考上文我给的公式。
					else
						opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]); 
				}
			}
//			System.out.println("substring1:" + x);
//			System.out.println("substring2:" + y);


			int i = 0, j = 0;
			//最长公共子序算法LCS
			String str="";
			Integer min =Integer.MAX_VALUE;
			Integer target = null;
			
			while (i < substringLength1 && j < substringLength2) {
				if (x.charAt(i) == y.charAt(j)) {
					if (min>i) {
						min =i;
						target = j;
					}
					str=str+x.charAt(i);
					i++;
					j++;
				} else if (opt[i + 1][j] >= opt[i][j + 1])
					i++;
				else
					j++;
			}
			Long endTime = System.nanoTime();

			if (str.length()==3) {
				if (min==0) {
					return y.substring(target, target+4);
				}else{
					return y.substring(target-1, target+3);
				}
			}
			
			return "x";
		}
}
