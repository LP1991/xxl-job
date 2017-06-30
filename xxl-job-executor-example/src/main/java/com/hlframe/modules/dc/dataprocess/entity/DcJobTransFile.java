/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.entity;


import com.hlframe.common.persistence.DataEntity;
import com.hlframe.common.utils.excel.annotation.ExcelField;

/**
 * 采集传输文件Entity
 * @author phy
 * @version 2016-11-23
 */
public class DcJobTransFile extends DataEntity<DcJobTransFile> {
	
	private static final long serialVersionUID = 1L;
	

	/**  任务状态: 0-编辑; 1-测试; 9-添加任务 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_TEST = "1";
	public static final String TASK_STATUS_TASK = "9";
	
	private String description;		// 描述
	private String jobname;			// 任务名称
	private String  uploadTime;		// 最后执行时间

	private String fileType;		// 文件类型
	private String status;			// 任务状态

	private DcJobTransFileHdfs dcJobTransFileHdfs;		// 文件路径
	/** 只读字段, 页面传参用 **/
	private Integer accre;  //操作权限

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public DcJobTransFile() {
		super();
	}

	public DcJobTransFile(String id){
		super(id);
	}

	@ExcelField(title="描述", align=2, sort=7)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@ExcelField(title="任务名称", align=2, sort=8)
	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public DcJobTransFileHdfs getDcJobTransFileHdfs() {
		return dcJobTransFileHdfs;
	}

	public void setDcJobTransFileHdfs(DcJobTransFileHdfs dcJobTransFileHdfs) {
		this.dcJobTransFileHdfs = dcJobTransFileHdfs;
	}

	// @return the status
	public String getStatus() {
		return status;
	}

	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAccre() {
		return accre;
	}

	public void setAccre(Integer accre) {
		this.accre = accre;
	}
}