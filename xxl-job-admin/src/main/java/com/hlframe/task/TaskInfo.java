package com.hlframe.task;

public class TaskInfo {
	private String taskid; // 唯一ID，建议用业务ID代替，管理任务开启关闭的唯一键
	private String runid; // 唯一ID，建议用业务ID代替，管理任务开启关闭的唯一键
	private String taskName; // 任务的名称，日志中使用
	private TaskType taskTpye; // 任务的类型 1 类 2 jar 3 shell、bat
	private String className; // 需要执行的类名
	private String methodName; // 需要执行的方法名
	private String paramsType; // 参数类型
	private String params; // 参数
	private String scheduleExpr; // 执行时间的表达式
	private String filePath; // 上传的文件路径
	private String fileName; // shell的文件名 or bat的文件名
	private String executeType; // 执行方式， 0-自动 1-手动
	
	private Boolean syncFlag;	//同步标志 : true-同步执行; false-异步执行	add by peijd
	
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public TaskType getTaskTpye() {
		return taskTpye;
	}
	public void setTaskTpye(TaskType taskTpye) {
		this.taskTpye = taskTpye;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getParamsType() {
		return paramsType;
	}
	public void setParamsType(String paramsType) {
		this.paramsType = paramsType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getScheduleExpr() {
		return scheduleExpr;
	}
	public void setScheduleExpr(String scheduleExpr) {
		this.scheduleExpr = scheduleExpr;
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
	public String getExecuteType() {
		return executeType;
	}
	public void setExecuteType(String executeType) {
		this.executeType = executeType;
	}
	public String getRunid() {
		return runid;
	}
	public void setRunid(String runid) {
		this.runid = runid;
	}
	// @return the syncFlag
	public Boolean getSyncFlag() {
		if(null==syncFlag){
			return false;
		}
		return syncFlag;
	}
	// @param syncFlag the syncFlag to set
	public void setSyncFlag(Boolean syncFlag) {
		this.syncFlag = syncFlag;
	}
	
	
}
