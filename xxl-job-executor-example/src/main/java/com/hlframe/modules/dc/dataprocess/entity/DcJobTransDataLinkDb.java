/********************** 版权声明 *************************
 * 文件名: DcJobTransData.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午8:56:13
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcJobTransData.java 
 * @职责说明: 数据采集jod-连接源设置
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午8:56:13
 */
public class DcJobTransDataLinkDb extends DataEntity<DcJobTransDataLinkDb> {

private static final long serialVersionUID = 1L;
	
	private String jobId;			//JobId
	private String schemaName;		//schema名称
	private String tableName;		//数据表名称
	private String tableSql;		//查询脚本
	private String tableColumn;		//字段
	private String partitionColumn;	//分区字段
	private String partitionNull;	//分区字段是否可为空值
	private String boundaryQuery;	//
	private String status;
	
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
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
	 * @return the tableSql
	 */
	public String getTableSql() {
		return tableSql;
	}
	/**
	 * @param tableSql the tableSql to set
	 */
	public void setTableSql(String tableSql) {
		this.tableSql = tableSql;
	}
	/**
	 * @return the tableColumn
	 */
	public String getTableColumn() {
		return tableColumn;
	}
	/**
	 * @param tableColumn the tableColumn to set
	 */
	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}
	/**
	 * @return the partitionColumn
	 */
	public String getPartitionColumn() {
		return partitionColumn;
	}
	/**
	 * @param partitionColumn the partitionColumn to set
	 */
	public void setPartitionColumn(String partitionColumn) {
		this.partitionColumn = partitionColumn;
	}
	/**
	 * @return the partitionNull
	 */
	public String getPartitionNull() {
		return partitionNull;
	}
	/**
	 * @param partitionNull the partitionNull to set
	 */
	public void setPartitionNull(String partitionNull) {
		this.partitionNull = partitionNull;
	}
	/**
	 * @return the boundaryQuery
	 */
	public String getBoundaryQuery() {
		return boundaryQuery;
	}
	/**
	 * @param boundaryQuery the boundaryQuery to set
	 */
	public void setBoundaryQuery(String boundaryQuery) {
		this.boundaryQuery = boundaryQuery;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	
}
