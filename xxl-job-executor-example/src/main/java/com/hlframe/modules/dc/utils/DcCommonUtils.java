/********************** 版权声明 *************************
 * 文件名: DcCommonUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午4:20:12
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcQueryDataDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;


/** 
 * @类名: com.hlframe.modules.dc.utils.DcCommonUtils.java 
 * @职责说明: 数据中心通用工具类
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午4:20:12
 */
public class DcCommonUtils {

	//通用查询数据Dao
	private static DcQueryDataDao dcQueryDataDao = ((DcQueryDataDao)SpringContextHolder.getBean("dcQueryDataDao"));
	
	
	/**
	 * @方法名称: buildDictDataFromTable 
	 * @实现功能: 根据查询条件 获取数据表内容, 组建为数据字典样式
	 * @param tableName		表名称
	 * @param fields		字段列表
	 * @param filter		过滤条件
	 * @param orderBy		排序字段
	 * @return
	 * @create by peijd at 2016年11月16日 下午4:27:16
	 */
	public static List<Map<String, Object>> buildDictDataFromTable(String tableName, String fields, String filter, String orderBy){
		return queryDataFromTable(tableName, fields, filter, orderBy, null);
	}

	/**
	 * @方法名称: queryLimitDataFromTable
	 * @实现功能: 查询表中限定数据集
	 * @params  [tableName, limitNum]
	 * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @create by peijd at 2017/4/18 10:06
	 */
	public static List<Map<String, Object>> queryLimitDataFromTable(String tableName, Integer limitNum){
		return queryDataFromTable(tableName, "*", null, null, limitNum);
	}

	/**
	 * @方法名称: queryDataFromTable
	 * @实现功能: 从表中查询数据
	 * @params  [tableName, fields, filter, orderBy, limitNum]
	 * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @create by peijd at 2017/4/18 10:02
	 */
	public static List<Map<String, Object>> queryDataFromTable(String tableName, String fields, String filter, String orderBy, Integer limitNum){
		Assert.hasText(tableName);
		Assert.hasText(fields);
		StringBuilder metaSql = new StringBuilder(200);
		//拼接动态Sql
		metaSql.append("SELECT ").append(fields).append(" FROM ").append(tableName);
		if(StringUtils.isNotEmpty(filter)){
			metaSql.append(" WHERE ").append(filter);
		}
		if(StringUtils.isNotEmpty(orderBy)){
			metaSql.append(" ORDER BY ").append(orderBy);
		}
		if(null!=limitNum && limitNum>0){
			metaSql.append(" LIMIT ").append(limitNum);
		}
		return dcQueryDataDao.queryMetaDataList(metaSql.toString());
	}

	/**
	 * @方法名称: existTableName
	 * @实现功能: 查询系统中数据表名 是否存在
	 * @params  [tableName]
	 * @return  boolean
	 * @create by peijd at 2017/4/18 14:23
	 */
	public static boolean existTableName(String tableName){
		Assert.hasText(tableName);
		StringBuilder checkSql = new StringBuilder(64);
		checkSql.append("SELECT TABLE_NAME,TABLE_TYPE,TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE ");
		if(tableName.indexOf(".")>0){

			checkSql.append(" TABLE_NAME = '").append(tableName.split("[.]")[1]);
			checkSql.append("' AND TABLE_SCHEMA='").append(tableName.split("[.]")[0]).append("'");
		}else{
			checkSql.append(" TABLE_NAME = '").append(tableName).append("'");
		}
		return  CollectionUtils.isNotEmpty(dcQueryDataDao.queryMetaDataList(checkSql.toString()));
	}
	
	/**
	 * @方法名称: queryDataBySql 
	 * @实现功能: 根据查询sql 获取数据列表
	 * @param querySql 	查询SQL
	 * @return
	 * @create by peijd at 2016年11月16日 下午4:32:04
	 */
	public static List<Map<String, Object>> queryDataBySql(String querySql){
		Assert.hasText(querySql);
		return dcQueryDataDao.queryMetaDataList(querySql);
	}
	
	/**
	 * 
	 * @方法名称: getFieldByField 
	 * @实现功能: 根据某个字段获取另一个表中的某个字段
	 * @param oldFieldName
	 * @return newFieldName
	 * @create by cdd at 2016年11月23日 下午4:15:12
	 */
		public static String getFieldByField(String oldFieldName, String fieldValue, String tableName, String newFieldName){
			Assert.hasText(oldFieldName);
			StringBuilder tableSql =  new StringBuilder(128);
			tableSql.append("select ").append(newFieldName).append(" from ").append(tableName);
			tableSql.append(" where ").append(oldFieldName).append("='").append(fieldValue).append("'");
			tableSql.append(" and del_flag=0");
			return dcQueryDataDao.getFieldByField(tableSql.toString());
		}
		
		/**
		 * @方法名称: delDataByField 
		 * @实现功能: TODO
		 * @param fieldName
		 * @param fieldValue
		 * @param tableName
		 * @create by cdd at 2017年1月13日 下午2:20:19
		 */
		public static  void delDataByField(String fieldName, String fieldValue, String tableName){
			Assert.hasText(fieldName);
			Assert.hasText(fieldValue);
			Assert.hasText(tableName);
			StringBuilder tableSql =  new StringBuilder(128);
			tableSql.append("delete ").append(" from ").append(tableName);
			tableSql.append(" where ").append(fieldName).append("='").append(fieldValue).append("'");
			 dcQueryDataDao.delDataByField(tableSql.toString());
		}

		/**
		 * @方法名称: getFieldByManyField 
		 * @实现功能: TODO
		 * @param querySql
		 * @return
		 * @create by cdd at 2017年1月20日 下午1:08:39
		 */
		public static String getFieldByManyField(String querySql) {
			Assert.hasText(querySql);
			return dcQueryDataDao.getFieldByManyField(querySql);
		}

		/**
		 * @方法名称: updateFieldByField 
		 * @实现功能:根据某个字段更新某张表里的某个字段
		 * @param oldFieldName
		 * @param newFieldName
		 * @param tableName
		 * @create by cdd at 2017年1月23日 下午4:17:52
		 */
		public static void updateFieldByField(String oldFieldName, String oldFieldValue, String newFieldName, String newFieldValue, String tableName) {
			Assert.hasText(oldFieldName);
			StringBuilder tableSql =  new StringBuilder(128);
			tableSql.append("update ").append(tableName).append(" set ").append(newFieldName);
			tableSql.append("='").append(newFieldValue).append("'").append(" where ").append(oldFieldName).append("='").append(oldFieldValue).append("'");
			
			 dcQueryDataDao.updateFieldByField(tableSql.toString());
		}

		public static String objType2String(String type){
			if(type == null){
				return null;
			}
			switch (type){
			case DcObjectMain.OBJ_TYPE_TABLE:
				return "table";
			case DcObjectMain.OBJ_TYPE_FIELD:
				return "field";
			case DcObjectMain.OBJ_TYPE_FILE:
				return "file";
			case DcObjectMain.OBJ_TYPE_INTER:
				return "interface";
			case DcObjectMain.OBJ_TYPE_FOLDER:
				return "folder";
			case DcObjectMain.OBJ_TYPE_DATABASE:
				return "database";
				default: return null;
		}
		}
}
