/********************** 版权声明 *************************
 * 文件名: DcObjectTable.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:33:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.sys.entity.Office;
import com.hlframe.modules.sys.entity.User;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectTable.java 
 * @职责说明: 数据表对象
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:33:39
 */
public class DcObjectTable extends DataEntity<DcObjectTable>  {
	
	/** 数据表更新类别 1-全量更新;2-增量更新 **/
	public static final String TD_STORE_TYPE_WHOLE = "1";
	public static final String TD_STORE_TYPE_APPEND = "2";
	
	private static final long serialVersionUID = 1L;
	private String objId;
	private String tableName;	//数据表
	private String tableLink;	//数据源连接 
	private int dataNum;		//数据量 记录数
	
	private String dbType;		//数据库类别-DB_TYPE
	private String dbDataBase;	//database名称-DB_DATABASE
	private String storeType;	//增量更新/全表更新
	//private String remarks;
	
	/** 复合元数据main对象信息 用于传参  **/
	private String objCode;	//对象编码
	private String objName;	//对象名称
	private String objType;	//对象类型
	private String systemId;	//业务系统Id
	private String objDesc;	//对象描述
	private String managerPer;//责任人
	private String managerOrg;//责任部门
	
	private User user;//业务负责人
	private Office office;//业务部门	
	
	//字段列表
	private String fieldArr;

	private int accre;		//权限  页面权限控制
	
	
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
	/*public String getRemarks() {
		return remarks;
	}*/
	/**
	 * @param remarks the remarks to set
	 */
	/*public void setRemarks(String remarks) {
		this.remarks = remarks;
	}*/
	public String getObjCode() {
		return objCode;
	}
	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public String getObjType() {
		return objType;
	}
	public void setObjType(String objType) {
		this.objType = objType;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getObjDesc() {
		return objDesc;
	}
	public void setObjDesc(String objDesc) {
		this.objDesc = objDesc;
	}
	public String getManagerPer() {
		return managerPer;
	}
	public void setManagerPer(String managerPer) {
		this.managerPer = managerPer;
	}
	public String getManagerOrg() {
		return managerOrg;
	}
	public void setManagerOrg(String managerOrg) {
		this.managerOrg = managerOrg;
	}
	public String getFieldArr() {
		return fieldArr;
	}
	public void setFieldArr(String fieldArr) {
		this.fieldArr = fieldArr;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	public int getAccre() {
		return accre;
	}
	public void setAccre(int accre) {
		this.accre = accre;
	}
	
}
