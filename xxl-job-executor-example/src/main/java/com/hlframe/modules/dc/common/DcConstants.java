/********************** 版权声明 *************************
 * 文件名: DcCommon.java
 * 包名: com.hlframe.modules.dc
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月8日 上午10:47:04
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.common;

/**
 * @类名: com.hlframe.modules.dc.DcConstants.java 
 * @职责说明: 数据中心 静态环境变量 
 * @创建者: peijd
 * @创建时间: 2016年11月8日 上午10:47:04
 */
public class DcConstants {
	
	//数据源转换  默认数据源连接
	public final static String DC_TRANSLATE_DEFAULT_CONN = "defaultTransConnection";
	//数据源转换SQL, 换行符转换
	public final static String DC_TRANSLATE_SWAPLINE = "__nl__";	
	
	//结果状态定义
	public final static String DC_RESULT_FLAG_TRUE = "T";
	public final static String DC_RESULT_FLAG_FALSE = "F";
	
	
	/** 数据对象类型(数据表/接口/文件/字段/指标...) 设置 **/
	public final static String DC_DATATYPE_TABLE = "table";
	public final static String DC_DATATYPE_INTERFACE = "interface";
	public final static String DC_DATATYPE_FILE = "file";
	public final static String DC_DATATYPE_FIELD = "field";
	public final static String DC_DATATYPE_INDEX = "index";
}
