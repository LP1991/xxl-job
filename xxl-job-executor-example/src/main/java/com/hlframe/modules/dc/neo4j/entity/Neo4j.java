/********************** 版权声明 *************************
 * 文件名: neo4j.java
 * 包名: com.hzhl.query
 * 版权:	杭州华量软件  jerseyHive
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月22日 上午11:59:45
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.neo4j.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hzhl.query.neo4j.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月22日 上午11:59:45
 */
public class Neo4j extends DataEntity<Neo4j>{
	/**
	 * id我个人感没什么用但是前面他写了，其余的除了过程描述都为必填字段
	 */
	private static final long serialVersionUID = 1L;
	//结果状态定义
 public final static String DC_RESULT_FLAG_TRUE = "s";
	private String id;
	private String tableName;//表名
	private String tarName;//目标表名
	private String processId;//过程id
	private String processName;//过程名
	private String processDes;//过程描述
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the tarName
	 */
	public String getTarName() {
		return tarName;
	}
	/**
	 * @param tarName the tarName to set
	 */
	public void setTarName(String tarName) {
		this.tarName = tarName;
	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}
	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	/**
	 * @return the processDes
	 */
	public String getProcessDes() {
		return processDes;
	}
	/**
	 * @param processDes the processDes to set
	 */
	public void setProcessDes(String processDes) {
		this.processDes = processDes;
	}
	
}
