package com.pythe.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 字符串工具类
 * 
 * @author Administrator
 *
 */
public class StringUtils {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}

	/**
	 * 判断字符串是否不为空
	 * 
	 * @param str
	 *            字符串
	 * @return 是否不为空
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str);
	}

	/**
	 * 截断字符串两侧的逗号
	 * 
	 * @param str
	 *            字符串
	 * @return 字符串
	 */
	public static String trimComma(String str) {
		if (str.startsWith(",")) {
			str = str.substring(1);
		}
		if (str.endsWith(",")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 补全两位数字
	 * 
	 * @param str
	 * @return
	 */
	public static String fulfuill(String str) {
		if (str.length() == 2) {
			return str;
		} else {
			return "0" + str;
		}
	}

	/**
	 * 从拼接的字符串中提取字段
	 * 
	 * @param str
	 *            字符串
	 * @param delimiter
	 *            分隔符
	 * @param field
	 *            字段
	 * @return String
	 */
	public static String getFieldFromConcatString(String str, String delimiter, String field) {
		String[] fields = str.split(delimiter);
		for (String concatField : fields) {
			// searchKeywords=|clickCategoryIds=1,2,3
			// serchKeywords中的值拿不到就会报ArrayIndexOutOfBoundsException
			if (concatField.split("=").length == 2) {
				String fieldName = concatField.split("=")[0];
				String fieldValue = concatField.split("=")[1];
				if (fieldName.equals(field)) {
					return fieldValue;
				}
			}
		}
		return null;
	}

	/**
	 * 从拼接的字符串中给字段设置值
	 * 
	 * @param str
	 *            字符串
	 * @param delimiter
	 *            分隔符
	 * @param field
	 *            字段名
	 * @param newFieldValue
	 *            新的field值
	 * @return 字段值
	 */
	public static String setFieldInConcatString(String str, String delimiter, String field, String newFieldValue) {
		String[] fields = str.split(delimiter);

		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].split("=")[0];
			if (fieldName.equals(field)) {
				String concatField = fieldName + "=" + newFieldValue;
				fields[i] = concatField;
				break;
			}
		}

		StringBuffer buffer = new StringBuffer("");
		for (int i = 0; i < fields.length; i++) {
			buffer.append(fields[i]);
			if (i < fields.length - 1) {
				buffer.append("|");
			}
		}

		return buffer.toString();
	}

	/**
	 * 字符串分组统计
	 * 
	 * @return 最大值
	 */
	public static int stringGroupingStatistics(List<String> words, Map<Integer, List<String>> wordRepositories) {

		Map<Integer, Integer> groupCount = new HashMap<Integer, Integer>();
		Iterator<Integer> it = wordRepositories.keySet().iterator();
		for (; it.hasNext();) {
			groupCount.put(it.next(), 0);
		}

		for (String word : words) {
			Iterator<Integer> iterator = wordRepositories.keySet().iterator();
			for (; iterator.hasNext();) {
				Integer level = iterator.next();
				List<String> wordRepository = wordRepositories.get(level);
				if (wordRepository.contains(word)) {
					groupCount.put(level, groupCount.get(level) + 1);
				}
			}
		}

		int max = 0;
		int result = -1;
		Iterator<Integer> cit = groupCount.keySet().iterator();
		while (cit.hasNext()) {
			Integer level = cit.next();
			int count = groupCount.get(level);
			if (count > max) {
				max = count;
				result = level;
			}
		}
		return result;
	}

	// 生成随机数字和字母,
	public static String getStringRandom(int length) {

		String val = "";
		Random random = new Random();

		// 参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {

			// String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// //输出字母还是数字
			// if( "char".equalsIgnoreCase(charOrNum) ) {
			// //输出是大写字母还是小写字母
			// int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
			// val += (char)(random.nextInt(26) + temp);
			// } else if( "num".equalsIgnoreCase(charOrNum) ) {
			// val += String.valueOf(random.nextInt(10));
			// }

			val += (char) (random.nextInt(26) + 97);
		}
		return val;
	}

	// 生成随机数字和字母,
	public static String getStringByLength(int length) {
		String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		String temp = "";
		for (int i = 0; i < length; i++) {
			// 随机获取 0-61 数字 4次 charAt(num);
			int num = random.nextInt(str.length());
			char c1 = str.charAt(num); // 索引从0开始 到61
			temp += c1;
		}

		return temp;
	}

	/**
	 * 向某个字符串中间插入某个字符串
	 * 
	 * @param s
	 *            为字符串
	 * @param element
	 *            为插入的字符串
	 * @param startStr
	 *            为从哪里开始插入
	 * @param endStr
	 *            为从哪里结束插入
	 * @return
	 * 
	 */
	public static String insertStrBetweenString(String s, String element, String startStr, String endStr) {
		int start = s.indexOf(startStr);
		int end = s.indexOf(endStr, s.indexOf(startStr));
		return s.substring(0, start + startStr.length()) + "\"" + element + "\"" + s.substring(end);
	}

}
