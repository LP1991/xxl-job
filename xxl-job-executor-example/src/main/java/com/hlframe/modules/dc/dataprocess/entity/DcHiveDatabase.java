/********************** 版权声明 *************************
 * 文件名: DcHiveDatabase.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2017年1月10日 下午4:01:04
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcHiveDatabase.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2017年1月10日 下午4:01:04
 */
public class DcHiveDatabase extends DataEntity<DcHiveDatabase> {
	private static final long serialVersionUID = 1L;
	private String database;//数据库名
	
	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}
	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
}
