package com.pythe.common.utils;

import java.math.BigDecimal;

/**
 * 数字格工具类
 * @author Administrator
 *
 */
public class NumberUtils {

	/**
	 * 格式化小数
	 * @param str 字符串
	 * @param scale 四舍五入的位数
	 * @return 格式化小数
	 */
	public static double formatDouble(double num, int scale) {
		BigDecimal bd = new BigDecimal(num);  
		return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	/**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                    String hex = Integer.toHexString(buf[i] & 0xFF);
                    if (hex.length() == 1) {
                            hex = '0' + hex;
                    }
                    sb.append(hex.toUpperCase());
            }
            System.out.println("!!!!!!!!!!!!!!!!!!! " + sb.toString());
            return sb.toString();
    }
    
    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
            if (hexStr.length() < 1)
                    return null;
            byte[] result = new byte[hexStr.length()/2];
            for (int i = 0;i< hexStr.length()/2; i++) {
                    int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
                    int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
                    result[i] = (byte) (high * 16 + low);
            }
            return result;
    }

    
    /**将16进制字符串数组转为二进制字节数组
     * @param hexArray
     * @return
     */
    public static byte[] parseHexArray2ByteArray(String[] hexArray) {
            if (hexArray.length < 1)
                    return null;
            byte[] result = new byte[hexArray.length];
            for (int i = 0;i< hexArray.length; i++) 
            {
                result[i] = (byte) Integer.parseInt(hexArray[i], 10);;
            }
            return result;
    }
    
    /**将二进制字节数组转为16进制字符串数组
     * @param byteArray
     * @return
     */
    public static String parseByteArray2HexArray(byte buf[]) {
    	StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                
                sb.append(hex.toUpperCase());
                if(i < buf.length -1)
                {
                	sb.append(',');
                }
        }
        System.out.println("!!!!!!!!!!!!!!!!!!! " + sb.toString());
        return sb.toString();
    }
    
	
}
