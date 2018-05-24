//package com.pythe.controller;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import org.bouncycastle.jcajce.provider.symmetric.Twofish;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.pythe.common.utils.HttpClientUtil;
//import com.pythe.common.utils.JsonUtils;
//import com.sun.tools.internal.xjc.model.SymbolSpace;
//
//public class Test {
//	public static void main(String[] args) throws Exception {
//		// try {
//		// FileInputStream instream = new FileInputStream(new
//		// File("src/main/resources/resource/apiclient_cert.p12"));
//		// } catch (FileNotFoundException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		// uploadFile
//		// ("139.199.212.174", 21, "ftpuser","pythe888", "/home/ftpuser/www",
//		// "/image/2019/04", "1.png", inputStream);
//		// String str =
//		// "/image/2017/3/30/store_2054369272o6zAJszZtla1bslHFhnJTbWBTVik1490879237598.jpg";
//		//// // String basePath,String filePath, String filename
//		//// String basePath ="/home/ftpuser/www";
//		// String filePath = str.substring(0,str.lastIndexOf("/"));
//		// String fileName = str.substring(str.lastIndexOf("/")+1);
//		// System.out.println(filePath);
//		// System.out.println(fileName);
//		//// //str.substring());
//		//
//		// System.out.println(1500d/279d +" "+2722d/378d);
//		//
//		// //json转换为list
//		// String json = "[1,2,3,4]";
//		// List<Long> list = JSONObject.parseArray(json, Long.class);
//		// list.forEach(x->System.out.println(x));
//		// 处理成语字典
//		// String word = "八仙过海";
//		// String url =
//		// "http://v.juhe.cn/chengyu/query?key=230edf9b74f25b838db1dc831d25d9c9&word="+word;
//		// String result = HttpClientUtil.doGet(url, null);
//		// String data = JSONObject.parseObject(result).getString("result");
//		// if (data==null) {
//		// System.out.println("查询不到该成语的相关信息");
//		// }else {
//		// JSONObject json = JSONObject.parseObject(data);
//		// //拼音
//		// String pinyin = json.getString("pinyin");
//		// //成语解释
//		// String chengyujs =json.getString("chengyujs");
//		// //成语出处
//		// String from_ = json.getString("from_");
//		// //举例
//		// String e = json.getString("example");
//		// //语法
//		// String yufa = json.getString("yufa");
//		// //词语解释
//		// String yiyujs = json.getString("yiyujs");
//		// String str = "";
//		// if (pinyin!=null) {
//		// str=str+"<h2>"+word+"</h2>"+"<dd><dl>拼音："+pinyin+"<dl></dd>";
//		// }
//		// if (yufa!=null) {
//		// str=str+"<dd>语法："+yufa+"</dl></dd>";
//		// }
//		// str = str +"<hr>" +"<h3>"+"成语解释"+"</h3>";
//		// if (chengyujs!=null) {
//		// str = str + "<ul><li>【解释】："+chengyujs+"</li></ul>";
//		// }
//		// if (from_!=null) {
//		// str = str + "<ul><li>【出处】："+from_+"</li></ul>";
//		// }
//		// if (e!=null) {
//		// str = str +"<ul><li>【举例】："+e+"</li></ul>";
//		// }
//		// str =str +"<hr>";
//		// System.out.println(str);
//
//		// }
//
////		String word = "聚";
////		String url = "http://v.juhe.cn/xhzd/query?key=9e7245d24b2b5a4cf099cdb1b14f84d1&word=" + word;
////		String result = HttpClientUtil.doGet(url, null);
////		String data = JSONObject.parseObject(result).getString("result");
////		if (data == null) {
////			System.out.println("查询不到该成语的相关信息");
////		} else {
////			JSONObject json = JSONObject.parseObject(data);
////			// //拼音
////			// String pinyin = json.getString("py");
////			// //成语解释
////			String bushou =json.getString("bushou");
////			// 成语出处
////			JSONArray jijie = json.getJSONArray("jijie");
////			JSONArray xiangjie = json.getJSONArray("xiangjie");
////			
////			
////			String str = "";
////			str = str + "<h2>" + word + "</h2>";
////			if (!jijie.getString(0).equals("")) {
////				
////				str = str +"<h3><span>基本释义 </span></h3>";
////				str = str + "<ul><li><b>" + "部首" + "</b>—" + bushou + "</li></ul>";
////				int i = 0;
////				int flag = 0;
////				String s = "";
////				for (i = 1; i < jijie.size(); i++) {
////					if (jijie.getString(i - 1).equals(word)) {
////						str = str + "<ul><li><b>" + word + "</b>" + jijie.getString(i) + "</li></ul>";
////						i = i + 1;
////					} else {
////						if (jijie.getString(i).equals(word)) {
////							str = str + "<ol>" + s + "</ol>";
////							s = "";
////							continue;
////						}
////						String back_element = jijie.getString(i - 1);
////						if (back_element.contains("笔画数") || back_element.contains("部首")) {
////							continue;
////						}
////						if (back_element.equals("")) {
////							continue;
////						}
////						s = s + "<li>" + back_element+ "</li>";
////					}
////				}
////				str = str + "<ol>" + s + "</ol>";
////				str = str + "<hr>";
////			}
////
////			if (!xiangjie.getString(0).equals("")) {
////				str = str +"<h3><span>详细解释 </span></h3>";
////				int i = 0;
////				int flag = 0;
////				String s = "";
////				for (i = 1; i < xiangjie.size(); i++) {
////					if (xiangjie.getString(i - 1).equals(word)) {
////						str = str + "<ul><li><b>" + word + "</b>" + xiangjie.getString(i) + "</li></ul>";
////						i = i + 1;
////					} else {
////						if (xiangjie.getString(i).equals(word)) {
////							str = str + "<ol>" + s + "</ol>";
////							s = "";
////							continue;
////						}
////						String back_element = xiangjie.getString(i - 1);
////						if (back_element.contains("笔画数") || back_element.contains("部首")) {
////							continue;
////						}
////						if (back_element.equals("")) {
////							continue;
////						}
////						s = s + "<li>" + back_element+ "</li>";
////					}
////				}
////				str = str + "<ol>" + s + "</ol>";
////				str = str + "<hr>";
////				System.out.println(str);
////			}
////			
////		}
//		
//	
//
//		
//		
////		Date today = new Date();
////        Calendar c=Calendar.getInstance();
////        c.setTime(today);
////        int weekday=c.get(Calendar.DAY_OF_WEEK);
////        System.out.println(weekday);
//
//		// if (yufa!=null) {
//		// str=str+"<dd>语法："+yufa+"</dl></dd>";
//		// }
//		// str = str +"<hr>" +"<h3>"+"成语解释"+"</h3>";
//		// if (chengyujs!=null) {
//		// str = str + "<ul><li>【解释】："+chengyujs+"</li></ul>";
//		// }
//		// if (from_!=null) {
//		// str = str + "<ul><li>【出处】："+from_+"</li></ul>";
//		// }
//		// if (e!=null) {
//		// str = str +"<ul><li>【举例】："+e+"</li></ul>";
//		// }
//		// str =str +"<hr>";
////        System.out.println(todayForWeek());
//		String s = "{0:0,1:0,2:0,3:0,4:0,5:0,6:0,7:0,8:0}";
//		Map<Integer, Integer> map = JsonUtils.json2Map(s);
//		map.put(0, map.get(0)+1);
//		
//		
//        System.out.println(map.get(0));
//	}
//	
//	
//    /**
//     * 判断当前日期是星期几
//     */
// public static int todayForWeek() throws Exception {
//  //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//  Calendar c = Calendar.getInstance();
//  //c.setTime(format.parse(pTime));
//  c.setTime(new Date());
//  int dayForWeek = 0;
//  if(c.get(Calendar.DAY_OF_WEEK) == 1){
//   dayForWeek = 7;
//  }else{
//   dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
//  }
//  return dayForWeek;
// }
// 
// 
//}
