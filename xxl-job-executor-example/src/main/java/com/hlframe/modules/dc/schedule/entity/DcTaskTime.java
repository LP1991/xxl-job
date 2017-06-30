/********************** 版权声明 *************************
 * 文件名: DcTaskTime.java
 * 包名: com.hlframe.modules.dc.schedule.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月14日 下午2:19:48
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.entity;

import com.hlframe.common.persistence.DataEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/** 
 * @类名: com.hlframe.modules.dc.schedule.entity.DcTaskTime.java 
 * @职责说明:调度时间信息实体类
 * @创建者: cdd
 * @创建时间: 2016年11月14日 下午2:19:48
 */
public class DcTaskTime extends DataEntity<DcTaskTime> implements Job{
	private static final long serialVersionUID = 1L;
	
	/** 调度任务状态对应数据字典dc_taskTimeFlag: 0-未执行; 1-执行中; 9-成功;8-失败 **/
	public static final String TASK_STATUS_INIT = "0";
	public static final String TASK_STATUS_RUNNING = "1";
	public static final String TASK_STATUS_ERROR = "8";
	public static final String TASK_STATUS_SUCCESS = "9";
	
	/** 调度任务触发方式: 0-自动; 1-手动; 2-条件 **/
	public static final String TASK_TRIGGERTYPE_AUTO = "0";
	public static final String TASK_TRIGGERTYPE_HAND = "1";
	public static final String TASK_TRIGGERTYPE_CONDITION = "2";
	
	/** 调度任务来源类型: 自定义任务类型在DcTaskMain中定义 peijd  **/
	public static final String TASK_FROMTYPE_TASKQUEUE = "02";		//任务队列
	
	protected String scheduleName;
	protected String scheduleExpr;
	protected String urlLink;
	protected String scheduleDesc;
	protected String triggerType;//触发方式( 0-自动; 1-手动)
	protected String status;	//执行状态
	protected String result;	//上次执行结果
	private Date nexttime;		//下次执行时间
	protected String createDept;
	
	protected String taskfromtype;
	protected String taskfromid;
	protected String taskfromname;
	
	
	
	/**
	 * @return the scheduleName
	 */
	public String getScheduleName() {
		return scheduleName;
	}
	/**
	 * @param scheduleName the scheduleName to set
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	/**
	 * @return the scheduleExpr
	 */
	public String getScheduleExpr() {
		return scheduleExpr;
	}
	/**
	 * @param scheduleExpr the scheduleExpr to set
	 */
	public void setScheduleExpr(String scheduleExpr) {
		this.scheduleExpr = scheduleExpr;
	}
	/**
	 * @return the scheduleDesc
	 */
	public String getScheduleDesc() {
		return scheduleDesc;
	}
	/**
	 * @param scheduleDesc the scheduleDesc to set
	 */
	public void setScheduleDesc(String scheduleDesc) {
		this.scheduleDesc = scheduleDesc;
	}
	
	
	/**
	 * @return the triggerName
	 */
	public String getTriggerType() {
		return triggerType;
	}
	/**
	 * @param triggerName the triggerName to set
	 */
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
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
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return the createDept
	 */
	public String getCreateDept() {
		return createDept;
	}
	/**
	 * @param createDept the createDept to set
	 */
	public void setCreateDept(String createDept) {
		this.createDept = createDept;
	}
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		
	}
	public String getTaskfromtype() {
		return taskfromtype;
	}
	public void setTaskfromtype(String taskfromtype) {
		this.taskfromtype = taskfromtype;
	}
	public String getTaskfromid() {
		return taskfromid;
	}
	public void setTaskfromid(String taskfromid) {
		this.taskfromid = taskfromid;
	}
	public String getTaskfromname() {
		return taskfromname;
	}
	public void setTaskfromname(String taskfromname) {
		this.taskfromname = taskfromname;
	}
	// @return the nexttime
	public Date getNexttime() {
		return nexttime;
	}
	// @param nexttime the nexttime to set
	public void setNexttime(Date nexttime) {
		this.nexttime = nexttime;
	}

	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
	}
}
