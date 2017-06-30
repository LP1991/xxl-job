/********************** 版权声明 *************************
 * 文件名: DcFullObject.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午2:56:57
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

/**
 * @类名: com.hlframe.modules.dc.metadata.entity.DcDataObject.java 
 * @职责说明: 数据对象信息表
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午2:56:57
 */
public class DcDataObject extends DcObjectMain {

	private static final long serialVersionUID = 1L;
	
	//补充信息 - 数据表
	private String schemaName;
	private String tableLink;
	private String tableName;
	private String storeType;
	private String tableRemarks;

	//补充信息 - 接口
	

	//补充信息 - 字段
	

	//补充信息 - 指标
	
	public DcDataObject(DcObjectMain mainObj){
		if(null!=mainObj){
			this.id = mainObj.getId();
			this.objCode = mainObj.getObjCode();
			this.objName = mainObj.getObjName();
			this.objType = mainObj.getObjType();
			this.systemId = mainObj.getSystemId();
			this.objDesc = mainObj.getObjDesc();
			this.managerPer = mainObj.getManagerPer();
			this.managerOrg = mainObj.getManagerOrg();
			this.status = mainObj.getStatus();
			this.sortNum = mainObj.getSortNum();
		}
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
	// @return the schemaName
	public String getSchemaName() {
		return schemaName;
	}

	// @param schemaName the schemaName to set	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
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
	 * @return the tableRemarks
	 */
	public String getTableRemarks() {
		return tableRemarks;
	}
	/**
	 * @param tableRemarks the tableRemarks to set
	 */
	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks = tableRemarks;
	}
	
}
