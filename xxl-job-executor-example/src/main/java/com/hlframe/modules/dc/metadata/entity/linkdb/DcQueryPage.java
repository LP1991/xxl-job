/********************** 版权声明 *************************
 * 文件名: DcQueryPage.java
 * 包名: com.hlframe.modules.dc.metadata.entity.linkdb
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午3:27:03
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity.linkdb;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午3:27:03
 */
public class DcQueryPage {
	//数据表所属schema
	private String schema;
	//表名 不可为空
	private String tableName;
	//主键/唯一字段   sqlserver排序分页时必须 
	private String keyField;
	//字段列表
	private List<String> fieldList;
	//过滤条件
	private String filter;
	//排序
	private String orderBy;
	//起始页数 默认为1
	private Integer start;
	//每页数据量 为空则默认20
	private Integer limit;
	//第一行数
	private Integer firstrownum;
	//最后行数
	private Integer lastrownum;
	
	//获取查询字段列表
	public String getFieldStr() {
		if (CollectionUtils.isNotEmpty(fieldList)) {
			StringBuilder fieldStr = new StringBuilder(64);
			for (String field : fieldList) {
				fieldStr.append(fieldStr.length()>0?",":"").append(field);
			}
			return fieldStr.toString();
		}
		return " * ";
	}
	
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<String> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public Integer getStart() {
		return null==start?1:(start>0? start:1);
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLimit() {
		return null==limit?20:(limit>0?limit:20);
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public Integer getFirstrownum() {
		return null==firstrownum? (start-1)*limit : firstrownum;
	}
	public void setFirstrownum(Integer firstrownum) {
		this.firstrownum = firstrownum;
	}
	public Integer getLastrownum() {
		return null==lastrownum? start*limit : lastrownum;
	}
	public void setLastrownum(Integer lastrownum) {
		this.lastrownum = lastrownum;
	}


}
