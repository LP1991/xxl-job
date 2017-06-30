/********************** 版权声明 *************************
 * 文件名: DcFileUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月24日 下午3:32:54
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.FileUtils;
import com.hlframe.common.utils.StringUtils;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcFileUtils.java 
 * @职责说明: 文件处理工具类
 * @创建者: peijd
 * @创建时间: 2016年11月24日 下午3:32:54
 */
public class DcFileUtils extends FileUtils {
	
	/**
	 * @方法名称: convertFileSize 
	 * @实现功能: 转换文件大小
	 * @param size
	 * @return
	 * @create by peijd at 2016年11月24日 下午3:34:08
	 */
	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}
	
	/**
	 * @方法名称: formatSqoopLog 
	 * @实现功能: 格式化SqoopLog日志信息  根据时间格式化
	 * @param srcString	待处理字符串
	 * @param newLine	换行符
	 * @param cmdFlag	命令标记  Import/Export , 不同命令不同解析
	 * @return
	 * @create by peijd at 2016年12月14日 下午2:50:23
	 * e.g. formatSqoopLog("16/12/14 14:43:42 INFO tool.CodeG;16/12/14 14:43:43 INFO manager;16/12/14 14:43:43 INFO orm", \<br\>) ==\> \<br\>16/12/14 14:43:42 INFO tool.CodeG;\<br\>16/12/14 14:43:43 INFO manager;\<br\>16/12/14 14:43:43 INFO orm  
	 */
	public static String formatSqoopLog(String srcString, String newLine, String cmdFlag){
		if(StringUtils.isNotEmpty(srcString)){
			newLine = StringUtils.isBlank(newLine)?"\r\n":newLine;
			String replaceStr = srcString.trim().substring(0,5);	//取前5位
			
			//任务执行成功, 截取job运行信息
			if(srcString.indexOf("completed successfully")>0){
				StringBuilder log = new StringBuilder(256);
				String jobId = StringUtils.substringBetween(srcString, "Submitting tokens for job:", replaceStr);
				log.append(newLine).append("[jobId]: ").append(jobId);
				if(srcString.indexOf("INFO mapreduce.Job: Counters:")>0){
					String jobInfo = StringUtils.substringAfter(srcString, "INFO mapreduce.Job: Counters:").substring(4);
					log.append(newLine).append("[job用时]: ").append(StringUtils.substringBetween(jobInfo, "INFO mapreduce."+cmdFlag+"JobBase:", ")")).append(")");
					log.append(newLine).append("[job采集数据]: ").append(StringUtils.substringBetween(jobInfo, "INFO mapreduce."+cmdFlag+"JobBase:", "."));
					log.append(newLine).append("[job执行概况]: ");
					log.append(newLine).append(StringUtils.substringBefore(jobInfo, replaceStr).replaceAll("		", newLine+"		- "));
				}
				return log.toString();
			}else{	//将明细信息换行显示
				return srcString.replaceAll(replaceStr, newLine+replaceStr);
			}
		}
		return "";
	}
	
	/**
	 * @方法名称: formatSqoopLog 
	 * @实现功能: 格式化SqoopLog日志信息  根据时间格式化, 默认换行符"\r\n"
	 * @param srcString
	 * @return
	 * @create by peijd at 2016年12月14日 下午3:00:36
	 */
	public static String formatSqoopLog(String srcString){
		return formatSqoopLog(srcString, "\r\n", "Import");
	}
	
	public static void main(String[] args) {
//		System.out.println(convertFileSize(201391023));	// --> 192 MB
//		System.out.println(convertFileSize(2013));		// --> 2.0 KB
		
		String srcString = "  16/12/14 14:43:42 INFO tool.CodeG;16/12/14 14:43:43 INFO manager;16/12/14 14:43:43 INFO orm  ";
		System.out.println(formatSqoopLog(srcString, "<br>", "Export"));
		System.out.println(formatSqoopLog(srcString));
	}
}
