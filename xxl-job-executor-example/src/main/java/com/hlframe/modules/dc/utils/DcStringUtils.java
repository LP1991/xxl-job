package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.StringUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

public class DcStringUtils extends StringUtils {

	/**
	 * @方法名称: isNotNull 
	 * @实现功能: 判断对象是否为空
	 * @param obj
	 * @return
	 * @create by cdd at 2017年1月12日 下午3:19:17
	 */
	public static boolean isNotNull(Object obj){
		return obj!=null && isNotBlank(String.valueOf(obj));
	}
	
	/**
	 * @方法名称: getObjValue 
	 * @实现功能: 获取
	 * @param data
	 * @return
	 * @create by peijd at 2017年1月17日 下午3:25:46
	 */
	public static String getObjValue(Object data){
    	return getObjValue(data, "");
    }

	/**
	 * @方法名称: getObjValue
	 * @实现功能: 将对象转为字符串
	 * @create by peijd at 2017/4/24 13:59
	 * @param data		目标对象
	 * @param defaultVal 默认值
	 * @return
	 */
	public static String getObjValue(Object data, String defaultVal){
    	if(null==data || StringUtils.isEmpty(String.valueOf(data))){
    		return defaultVal;
    	}
    	return String.valueOf(data);
    }
	
	/**
	 * @方法名称: firstChar2Upper 
	 * @实现功能: 将字符串首字符转大写，后面的不变
	 * @param s 待转换字符串
	 * @return
	 * @create by peijd at 2017年2月16日 下午3:32:19
	 */
	 public static String firstChar2Upper(String s) {
		 if(isBlank(s)){
			 return s;
		 }
		 if(1==s.length()){
			 return upperCase(s);
		 }else{
			 return upperCase(s.substring(0, 1))+s.substring(1);
		 }
    }
	 
	 /**
	  * @方法名称: firstChar2Lower 
	  * @实现功能: 将字符串首字符转小写，后面的不变
	  * @param s 待转换字符串
	  * @return
	  * @create by peijd at 2017年2月16日 下午3:38:20
	  */
	 public static String firstChar2Lower(String s) {
		 if(isBlank(s)){
			 return s;
		 }
		 if(1==s.length()){
			 return lowerCase(s);
		 }else{
			 return lowerCase(s.substring(0, 1))+s.substring(1);
		 }
	 }

	/**
	 * @方法名称: formatJsonStr
	 * @实现功能: 格式json字符串, 去除一些特殊符号,
	 * @param srcString
	 * @return
	 * @create by peijd at 2017/4/20 17:51
	 */
	public static String formatJsonStr(String srcString){
		 Assert.hasText(srcString);
		 return srcString
				 .replaceAll("\\s", "")
				 .replaceAll("\\t", "")
				 .replaceAll("\\&quot;", "\"")
				 .replaceAll(" ", "");
	 }

	/**
	 * @方法名称: getCurrentContextPath
	 * @实现功能: 获取当前应用路径
	 * @param request	httpRequest
	 * @param includeIPFlag	是否包含ip地址
	 * @return  java.lang.String
	 * @create by peijd at 2017/5/8 15:31
	 */
	public static String getCurrentContextPath(HttpServletRequest request, Boolean includeIPFlag){
		Assert.notNull(request);
		if (includeIPFlag){
			// request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/"+request.getContextPath()
			return new StringBuilder(64)
					.append(request.getScheme()).append("://")		//协议
					.append(request.getServerName()).append(":")	//IP地址
					.append(request.getServerPort())	//端口
					.append(request.getContextPath())	//context
					.toString();
		}
		return request.getContextPath();
	 }
	
}
