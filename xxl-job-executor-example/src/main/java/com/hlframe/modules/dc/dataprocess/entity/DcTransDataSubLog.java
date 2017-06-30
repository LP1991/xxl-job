/********************** 版权声明 *************************
 * 文件名: DcTransDataSubLog.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:13:23
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

import java.util.Date;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcTransDataSubLog.java 
 * @职责说明: 数据转换过程日志
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:13:23
 */
public class DcTransDataSubLog extends DataEntity<DcTransDataSubLog> {

	private static final long serialVersionUID = 1L;
	
	private String jobId;
	private String subId;
	private Date beginTime;
	private Date endTime;
	private String status;
	
	
	// @return the jobId
	public String getJobId() {
		return jobId;
	}
	// @param jobId the jobId to set
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	// @return the subId
	public String getSubId() {
		return subId;
	}
	// @param subId the subId to set
	public void setSubId(String subId) {
		this.subId = subId;
	}
	// @return the beginTime
	public Date getBeginTime() {
		return beginTime;
	}
	// @param beginTime the beginTime to set
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	// @return the endTime
	public Date getEndTime() {
		return endTime;
	}
	// @param endTime the endTime to set
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	// @return the status
	public String getStatus() {
		return status;
	}
	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}
	// @return the serialversionuid
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
