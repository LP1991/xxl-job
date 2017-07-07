/********************** 版权声明 *************************
 * 文件名: DcTaskTimeRef.java
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
import com.hlframe.modules.dc.schedule.service.DcDemoTaskService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DcTaskTimeRef extends DataEntity<DcTaskTimeRef> implements Job{
	private static final long serialVersionUID = 1L;
   private String  taskId;
   private String timeId;
   private String className;
   private String methodName;
   private String parameter;
   private String trigger;
   private String taskDesc;
/**
 * @return the taskId
 */
public String getTaskId() {
	return taskId;
}
/**
 * @param taskId the taskId to set
 */
public void setTaskId(String taskId) {
	this.taskId = taskId;
}
/**
 * @return the timeId
 */
public String getTimeId() {
	return timeId;
}
/**
 * @param timeId the timeId to set
 */
public void setTimeId(String timeId) {
	this.timeId = timeId;
}
/**
 * @return the className
 */
public String getClassName() {
	return className;
}
/**
 * @param className the className to set
 */
public void setClassName(String className) {
	this.className = className;
}
/**
 * @return the methodName
 */
public String getMethodName() {
	return methodName;
}
/**
 * @param methodName the methodName to set
 */
public void setMethodName(String methodName) {
	this.methodName = methodName;
}
/**
 * @return the parameter
 */
public String getParameter() {
	return parameter;
}
/**
 * @param parameter the parameter to set
 */
public void setParameter(String parameter) {
	this.parameter = parameter;
}

/**
 * @return the trigger
 */
public String getTrigger() {
	return trigger;
}
/**
 * @param trigger the trigger to set
 */
public void setTrigger(String trigger) {
	this.trigger = trigger;
}
/**
 * @return the taskDesc
 */
public String getTaskDesc() {
	return taskDesc;
}
/**
 * @param taskDesc the taskDesc to set
 */
public void setTaskDesc(String taskDesc) {
	this.taskDesc = taskDesc;
}
/* (non-Javadoc)
 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
 */
@Override
public void execute(JobExecutionContext arg0) throws JobExecutionException {
	 DcDemoTaskService dcDemoTaskService  = new DcDemoTaskService();
	//dcDemoTaskService.checkEsIndex();
	
}

   
}
