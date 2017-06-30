package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcJobTransHdfs.java 
 * @职责说明: HDFS实体类
 * @创建者: cdd
 * @创建时间: 2016年11月26日 下午4:52:26
 */
public class DcJobTransHdfs extends DataEntity<DcJobTransHdfs> {

	private static final long serialVersionUID = 1L;
	
	/**  任务状态: 0-编辑; 1-测试; 9-添加任务 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_TEST = "1";
	public static final String TASK_STATUS_TASK = "9";
	
	protected String jobName; // 任务名称
	protected String jobDesc; // 任务描述
	protected String srcHdfsAddress;// 源Hdfs地址
	protected String srcHdfsDir; // 文件路径
	protected String srcHdfsVersion;// 源Hdfs版本
	protected String outPutDir; // 目标路径
	protected String logDir; // 目标路径
	protected int copyNum; // 并发拷贝数
	protected String isOverride; // 是否覆盖
	protected String status;//状态
    protected int sortNum;//排序
    protected  String sqr;//是否是目录
	/** 只读字段, 页面传参用 **/
	private Integer accre;  //操作权限
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getSrcHdfsAddress() {
		return srcHdfsAddress;
	}

	public void setSrcHdfsAddress(String srcHdfsAddress) {
		this.srcHdfsAddress = srcHdfsAddress;
	}

	public String getSrcHdfsDir() {
		return srcHdfsDir;
	}

	public void setSrcHdfsDir(String srcHdfsDir) {
		this.srcHdfsDir = srcHdfsDir;
	}

	public String getSrcHdfsVersion() {
		return srcHdfsVersion;
	}

	public void setSrcHdfsVersion(String srcHdfsVersion) {
		this.srcHdfsVersion = srcHdfsVersion;
	}

	public String getOutPutDir() {
		return outPutDir;
	}

	public void setOutPutDir(String outPutDir) {
		this.outPutDir = outPutDir;
	}

	public String getSqr() {
		return sqr;
	}

	public void setSqr(String sqr) {
		this.sqr = sqr;
	}

	/**
	 * @return the logDir
	 */
	public String getLogDir() {
		return logDir;
	}

	/**
	 * @param logDir the logDir to set
	 */
	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	public int getCopyNum() {
		return copyNum;
	}

	public void setCopyNum(int copyNum) {
		this.copyNum = copyNum;
	}

	public String getIsOverride() {
		return isOverride;
	}

	public void setIsOverride(String isOverride) {
		this.isOverride = isOverride;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public Integer getAccre() {
		return accre;
	}

	public void setAccre(Integer accre) {
		this.accre = accre;
	}
}
