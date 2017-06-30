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
 * @职责说明: 数据转换Job
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午8:56:13
 */
public class DcJobTransData extends DataEntity<DcJobTransData> {

	private static final long serialVersionUID = 1L;
	
	public static final String TOLINK_HDFS = "1";
	public static final String TOLINK_HIVE = "2";
	public static final String TOLINK_HBASE = "3";
	
	/**  任务状态: 0-编辑; 1-测试; 9-添加任务 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_TEST = "1";
	public static final String TASK_STATUS_TASK = "9";
	
	protected String jobName;		//任务名称
	protected String jobDesc;		//任务描述
	protected String fromLink;		//连接源
	protected String toLink;		//连接目标
	protected String appendType;	//更新方式
	protected String logDir;		//日志路径
	protected String status;		//任务状态
	protected int sortNum;
	
	
	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the jobDesc
	 */
	public String getJobDesc() {
		return jobDesc;
	}
	/**
	 * @param jobDesc the jobDesc to set
	 */
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	/**
	 * @return the fromLink
	 */
	public String getFromLink() {
		return fromLink;
	}
	/**
	 * @param fromLink the fromLink to set
	 */
	public void setFromLink(String fromLink) {
		this.fromLink = fromLink;
	}
	/**
	 * @return the toLink
	 */
	public String getToLink() {
		return toLink;
	}
	/**
	 * @param toLink the toLink to set
	 */
	public void setToLink(String toLink) {
		this.toLink = toLink;
	}
	/**
	 * @return the appendType
	 */
	public String getAppendType() {
		return appendType;
	}
	/**
	 * @param appendType the appendType to set
	 */
	public void setAppendType(String appendType) {
		this.appendType = appendType;
	}
	// @return the logDir
	public String getLogDir() {
		return logDir;
	}
	// @param logDir the logDir to set
	public void setLogDir(String logDir) {
		this.logDir = logDir;
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
	/**
	 * @return the sortNum
	 */
	public int getSortNum() {
		return sortNum;
	}
	/**
	 * @param sortNum the sortNum to set
	 */
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
	
}
