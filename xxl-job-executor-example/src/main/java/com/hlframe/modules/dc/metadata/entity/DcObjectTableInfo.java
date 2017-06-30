/********************** 版权声明 *************************
 * 文件名: DcObjectTableInfo.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年3月7日 下午9:03:16
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectTableInfo.java 
 * @职责说明: 数据表 元数据对象
 * @创建者: peijd
 * @创建时间: 2017年3月7日 下午9:03:16
 */
public class DcObjectTableInfo extends DcObjectMain {

	private static final long serialVersionUID = 1L;
	private String objId;
	private String tableName;	//数据表
	private String tableLink;	//数据源连接 
	private int dataNum;		//数据量 记录数
	
	private String dbType;		//数据库类别-DB_TYPE
	private String dbDataBase;	//database名称-DB_DATABASE
	private String storeType;	//增量更新/全表更新
	private String remarks;
	
	/**
	 * @return the objId
	 */
	public String getObjId() {
		return objId;
	}
	/**
	 * @param objId the objId to set
	 */
	public void setObjId(String objId) {
		this.objId = objId;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the tableLink
	 */
	public String getTableLink() {
		return tableLink;
	}
	/**
	 * @param tableLink the tableLink to set
	 */
	public void setTableLink(String tableLink) {
		this.tableLink = tableLink;
	}
	/**
	 * @return the dataNum
	 */
	public int getDataNum() {
		return dataNum;
	}
	// @return the dbType
	public String getDbType() {
		return dbType;
	}
	// @param dbType the dbType to set
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	// @return the dbDataBase
	public String getDbDataBase() {
		return dbDataBase;
	}
	// @param dbDataBase the dbDataBase to set
	public void setDbDataBase(String dbDataBase) {
		this.dbDataBase = dbDataBase;
	}
	/**
	 * @param dataNum the dataNum to set
	 */
	public void setDataNum(int dataNum) {
		this.dataNum = dataNum;
	}
	/**
	 * @return the storeType
	 */
	public String getStoreType() {
		return storeType;
	}
	/**
	 * @param storeType the storeType to set
	 */
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
