
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectAu.java 
 * @职责说明: dc权限申请实体类
 * @创建者: yuzh
 * @创建时间: 2016年11月19日 下午3:11:42
 */
public class DcObjectAu extends DataEntity<DcObjectAu> {

	private static final long serialVersionUID = 1L;

	/** 审核状态 **/
	public static final String STATUS_TODO = "未处理";
	public static final String STATUS_REJECT = "已撤回";
	public static final String STATUS_PASS = "已通过";

	private String userId;
	private String fileId;
	private String userName;
	private String fileName;
	private String from;
	private String status;
	protected int accre;		//权限  页面权限控制
	/**
	 * @return the accre
	 */
	public int getAccre() {
		return accre;
	}
	/**
	 * @param accre the accre to set
	 */
	public void setAccre(int accre) {
		this.accre = accre;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}
	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
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
	 * @param i
	 */
	
	
}
