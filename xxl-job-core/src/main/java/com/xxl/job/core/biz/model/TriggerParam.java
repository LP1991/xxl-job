package com.xxl.job.core.biz.model;


import java.io.Serializable;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private String taskId;
    private String runId;
    private String taskName; // 任务的名称，日志中使用

    private String executorHandler;
    private String executorParams;
    private String paramsType; // 参数类型
    private String executorBlockStrategy;

    private String glueType;
    private String glueSource;
    private long glueUpdatetime;

    private int logId;
    private long logDateTime;

    private String className; // 需要执行的类名
    private String methodName; // 需要执行的方法名
    private String scheduleExpr; // 执行时间的表达式
    private String filePath; // 上传的文件路径
    private String fileName; // shell的文件名 or bat的文件名
    private String executeType; // 执行方式， 0-自动 1-手动
    private Boolean syncFlag;	//同步标志 : true-同步执行; false-异步执行



    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(String executorParams) {
        this.executorParams = executorParams;
    }

    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }

    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    public void setGlueUpdatetime(long glueUpdatetime) {
        this.glueUpdatetime = glueUpdatetime;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getParamsType() {
        return paramsType;
    }

    public void setParamsType(String paramsType) {
        this.paramsType = paramsType;
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

    public Boolean getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    @Override
    public String toString() {
        return "TriggerParam{" +
                "jobId=" + taskId +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParams='" + executorParams + '\'' +
                ", executorBlockStrategy='" + executorBlockStrategy + '\'' +
                ", glueType='" + glueType + '\'' +
                ", glueSource='" + glueSource + '\'' +
                ", glueUpdatetime=" + glueUpdatetime +
                ", logId=" + logId +
                ", logDateTime=" + logDateTime +
                '}';
    }
}
