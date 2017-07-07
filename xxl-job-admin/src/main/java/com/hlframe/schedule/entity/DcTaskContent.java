/********************** 版权声明 *************************
 * 文件名: DcTaskContent.java
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

import java.util.Date;

/**
 * 
 * @类名: com.hlframe.modules.dc.schedule.entity.DcTaskContent.java
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月29日 上午10:53:13
 */
public class DcTaskContent extends DcTaskTime {

	private static final long serialVersionUID = 1L;

	// ** dc_task_log_next **//
	private String taskId; // 调度主键
	private Date nextTime; // 下次执行时间

	/** 采集脚本 sqoop script存储到remarks字段中 **/

	public DcTaskContent() {

	}

	public DcTaskContent(DcTaskTime dcTaskTime) {
		this.id = dcTaskTime.getId();
		this.scheduleName = dcTaskTime.getScheduleName();
		this.scheduleExpr = dcTaskTime.getScheduleExpr();
		this.scheduleDesc = dcTaskTime.getScheduleDesc();
		this.triggerType = dcTaskTime.getTriggerType();
		this.status = dcTaskTime.getStatus();
		this.result = dcTaskTime.getResult();
		this.createDept = dcTaskTime.getCreateDept();
		this.taskfromtype = dcTaskTime.getTaskfromtype();
		this.taskfromid = dcTaskTime.getTaskfromid();
		this.taskfromname = dcTaskTime.getTaskfromname();
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Date getNextTime() {
		return nextTime;
	}

	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}
}
