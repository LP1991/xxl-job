/********************** 版权声明 *************************
 * 文件名: DcTaskQueue.java
 * 包名: com.hlframe.schedule.entity
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.entity;

import com.hlframe.common.persistence.DataEntity;


public class DcTaskQueue extends DataEntity<DcTaskQueue> {
	
	private static final long serialVersionUID = 1L;
	
	/** 任务队列状态 0-保存; 9-已添加调度 **/
	public static final String QUEUE_STATUS_SAVE = "0";
	public static final String QUEUE_STATUS_SCHEDULE = "9";
	
	private String queueName; 	//队列名称 queue_name
	private String queueDesc; 	//队列描述 queue_desc
	private String status; 		//状态 status()
	private Integer priority;	//优先级 	数字越小,级别越高 priority
	
	
	private int accre;	//权限设置
	
	// @return the queueName
	public String getQueueName() {
		return queueName;
	}
	// @param queueName the queueName to set
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	// @return the queueDesc
	public String getQueueDesc() {
		return queueDesc;
	}
	// @param queueDesc the queueDesc to set
	public void setQueueDesc(String queueDesc) {
		this.queueDesc = queueDesc;
	}
	// @return the status
	public String getStatus() {
		return status;
	}
	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}
	// @return the accre
	public int getAccre() {
		return accre;
	}
	// @param accre the accre to set
	public void setAccre(int accre) {
		this.accre = accre;
	}
	// @return the priority
	public Integer getPriority() {
		return priority;
	}
	// @param priority the priority to set
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	
}
