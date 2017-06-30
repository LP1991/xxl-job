/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.entity;


import com.hlframe.common.persistence.DataEntity;

/**
 * 采集传输文件Entity
 * @author phy
 * @version 2016-11-23
 */
public class DcJobHdfslog extends DataEntity<DcJobHdfslog> {
	
	private static final long serialVersionUID = 1L;
	private String jobId;		// 描述
	private String fullpath;		// 任务名称
	private String uploadTime;		// 最后执行时间
	private String status;
	
	public DcJobHdfslog() {
		super();
	}

	public DcJobHdfslog(String jobId){
		super(jobId);
	}

	
	public String getFullpath() {
		return fullpath;
	}
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
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

	
}