/********************** 版权声明 *************************
 * 文件名: DcTaskMain.java
 * 包名: com.hlframe.modules.dc.schedule.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月14日 下午2:20:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * @类名: com.hlframe.modules.dc.schedule.entity.DcTaskMain.java 
 * @职责说明: 调度任务实体类
 * @创建者: cdd
 * @创建时间: 2016年11月14日 下午2:20:39
 */
public class DcTaskMain extends DataEntity<DcTaskMain>{
	private static final long serialVersionUID = 1L;
	
	/**  调度任务类型: 1-内部类; 2-jar; 3-bat or shell脚本(视操作系统而定)  **/
	public static final String TASK_TYPE_INNERCLASS = "1";
	public static final String TASK_TYPE_JAR = "2";
	public static final String TASK_TYPE_SHELL = "3";
	
	/**  调度任务状态(数据字典dc_taskMain_status): 0-编辑; 8-添加队列; 9-添加调度 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_QUEUE = "8";
	public static final String TASK_STATUS_JOB = "9";

	/** 调度任务来源类型(数据字典DC_TASK_SRCTYPE): 目前根据大类区分,以后根据任务子类细分 peijd  **/
	public static final String TASK_FROMTYPE_TASK_CUSTOM = "01";	//自定义任务
	public static final String TASK_FROMTYPE_EXTRACT_RMDB = "11";	//DB采集任务
	public static final String TASK_FROMTYPE_EXTRACT_FILE = "12";	//文件采集任务
	public static final String TASK_FROMTYPE_EXTRACT_INTF = "13";	//接口采集任务
	public static final String TASK_FROMTYPE_EXTRACT_HDFS = "14";	//HDFS采集任务
	public static final String TASK_FROMTYPE_PROCESS_DESIGN = "21";	//数据转换设计
	public static final String TASK_FROMTYPE_PROCESS_SCRIPT = "22";	//数据转换脚本
	public static final String TASK_FROMTYPE_EXPORT_RMDB = "31";	//数据导出
	
	private String taskName;//任务名
    private String methodName;//方法名
	private String taskDesc;//任务描述
	private String priority;//优先级
	private String status;//状态
	private String parameter;//参数
	
	private String programTpye;
	
	private String className;
	private String taskType;
	private String taskPath;	//任务类型(01-用户自定义; 11-DB数据采集; 12-文件采集; 13-接口采集; 14-hdfs采集; 21-数据转换设计; 22-数据转换脚本; 31-数据导出; )
	private String filePath; 	// 仅上传时使用
	private String fileName;
	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}
	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
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
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
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
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getTaskPath() {
		return taskPath;
	}
	public void setTaskPath(String taskPath) {
		this.taskPath = taskPath;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getProgramTpye() {
		return programTpye;
	}
	public void setProgramTpye(String programTpye) {
		this.programTpye = programTpye;
	}

	


}
