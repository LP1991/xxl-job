/********************** 版权声明 *************************
 * 文件名: DcObjectFolder.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:35:08
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectFolder.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:35:08
 */
public class DcObjectFolder extends DataEntity<DcObjectFolder>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objId;
	private String folderName;
	private String folderUrl;
	private int isStruct;
	private String remarks;
	
	/**
	 * @return the objId
	 */
	public String getObjId() {
		return objId;
	}
	/**
	 * @param objId the objId to set
	 */
	public void setObjId(String objId) {
		this.objId = objId;
	}
	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}
	/**
	 * @param folderName the folderName to set
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	/**
	 * @return the folderUrl
	 */
	public String getFolderUrl() {
		return folderUrl;
	}
	/**
	 * @param folderUrl the folderUrl to set
	 */
	public void setFolderUrl(String folderUrl) {
		this.folderUrl = folderUrl;
	}
	/**
	 * @return the isStruct
	 */
	public int getIsStruct() {
		return isStruct;
	}
	/**
	 * @param isStruct the isStruct to set
	 */
	public void setIsStruct(int isStruct) {
		this.isStruct = isStruct;
	}
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}
