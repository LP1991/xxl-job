/********************** 版权声明 *************************
 * 文件名: DcTaskQueueRef.java
 * 包名: com.hlframe.modules.dc.schedule.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年3月1日 下午2:16:21
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.schedule.entity.DcTaskQueueRef.java 
 * @职责说明: 任务队列 关联任务
 * @创建者: peijd
 * @创建时间: 2017年3月1日 下午2:16:21
 */
public class DcTaskQueueRef extends DataEntity<DcTaskQueueRef> {
	private static final long serialVersionUID = 1L;
	
	/** 前置队列状态 数据字典(dc_taskqueue_prestatus) 0-执行;1-执行成功;2-执行失败; **/
	public static final String PRE_TASK_STATUS_START="0";
	public static final String PRE_TASK_STATUS_SUCCESS="1";
	public static final String PRE_TASK_STATUS_EXCEPTION="2";
	
	/** 任务运行结果 0-未运行;1-运行中;8-失败;9-成功 **/
	public static final String TASK_RESULT_INIT="0";
	public static final String TASK_RESULT_RUNNING="1";
	public static final String TASK_RESULT_EXCEPTION="8";
	public static final String TASK_RESULT_SUCCESS="9";
	

	private String queueId; //任务队列Id queue_id
	private String taskId; 	//任务Id task_Id
	private String preTaskId; //前置任务Id pre_task_id
	private String preTaskStatus; //前置队列状态(0-执行;1-执行成功;2-执行失败;) pre_task_status
	private String taskResult; //运行结果(0-未运行;1-运行中;8-失败;9-成功)  task_result
	private String taskStatus; //任务运行状态 task_status
	private Integer sortNum; //显示顺序 sort_num
	private String queueName;
	//任务冗余字段 不在db中存储
	private DcTaskMain task;	//当前任务
	private DcTaskMain preTask;	//前置任务
	
	// @return the queueId
	public String getQueueId() {
		return queueId;
	}
	// @param queueId the queueId to set
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	// @return the taskId
	public String getTaskId() {
		return taskId;
	}
	// @param taskId the taskId to set
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	// @return the preTaskId
	public String getPreTaskId() {
		if(null!=preTask){
			return preTask.getId();
		}
		return preTaskId;
	}
	// @param preTaskId the preTaskId to set
	public void setPreTaskId(String preTaskId) {
		this.preTaskId = preTaskId;
	}
	// @return the preTaskStatus
	public String getPreTaskStatus() {
		return preTaskStatus;
	}
	// @param preTaskStatus the preTaskStatus to set
	public void setPreTaskStatus(String preTaskStatus) {
		this.preTaskStatus = preTaskStatus;
	}
	// @return the taskResult
	public String getTaskResult() {
		return taskResult;
	}
	// @param taskResult the taskResult to set
	public void setTaskResult(String taskResult) {
		this.taskResult = taskResult;
	}
	// @return the taskStatus
	public String getTaskStatus() {
		return taskStatus;
	}
	// @param taskStatus the taskStatus to set
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	// @return the sortNum
	public Integer getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	// @return the task
	public DcTaskMain getTask() {
		return task;
	}
	// @param task the task to set
	public void setTask(DcTaskMain task) {
		this.task = task;
	}
	// @return the preTask
	public DcTaskMain getPreTask() {
		return preTask;
	}
	// @param preTask the preTask to set
	public void setPreTask(DcTaskMain preTask) {
		this.preTask = preTask;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
