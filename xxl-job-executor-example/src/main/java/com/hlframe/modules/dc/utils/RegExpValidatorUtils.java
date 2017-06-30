/********************** 版权声明 *************************
 * 文件名: RegExpValidatorUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：hgw   创建时间：2017年3月31日 上午11:54:08
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @类名: com.hlframe.modules.dc.utils.RegExpValidatorUtils.java 
 * @职责说明: TODO
 * @创建者: hgw
 * @创建时间: 2017年3月31日 上午11:54:08
 */
public class RegExpValidatorUtils {

	/**
	 * 
	 * @方法名称: isIP 
	 * @实现功能: 验证IP地址加端口号
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 * @create by hgw at 2017年3月31日 上午11:57:16
	 */
	public static boolean isIPAndPort(String str) {
	String regex = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\:\\d{1,5}\\b";
	return match(regex, str);
	}
	
	public static boolean isDTable(String str){
		String regex="^[A-Za-z]{1,}[A-Za-z1-9_-]{0,}+$";
		return match(regex,str);
	}
	/**
	 * 
	 * @方法名称: isDirPath 
	 * @实现功能: 匹配是否是相对路径
	 * @param str
	 * @return
	 * @create by hgw at 2017年3月31日 下午2:12:56
	 */
	public static boolean isDirPath(String str) {
	String regex = "((/.*/)|(/))([^/]+$)";
	return match(regex, str);
	}
	/**
	 * 
	 * @方法名称: match 
	 * @实现功能: TODO
	 * @param regex 正则表达式字符串
	 * @param str 要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 * @create by hgw at 2017年3月31日 上午11:54:31
	 */
	private static boolean match(String regex, String str) {
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(str);
	return matcher.matches();
	}
}
