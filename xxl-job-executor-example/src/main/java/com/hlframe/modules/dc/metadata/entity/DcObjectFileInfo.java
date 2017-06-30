/********************** 版权声明 *************************
 * 文件名: DcObjectFile.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:35:23
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectFile.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:35:23
 */
public class DcObjectFileInfo extends DcObjectMain {

	private static final long serialVersionUID = 1L;
	
	private String objId;
	private String fileName;
	private String fileBelong;
	private String fileUrl;
	private int isStruct;
	private String splitter;
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
	 * @return the fileBelong
	 */
	public String getFileBelong() {
		return fileBelong;
	}
	/**
	 * @param fileBelong the fileBelong to set
	 */
	public void setFileBelong(String fileBelong) {
		this.fileBelong = fileBelong;
	}
	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}
	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
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
	 * @return the splitter
	 */
	public String getSplitter() {
		return splitter;
	}
	/**
	 * @param splitter the splitter to set
	 */
	public void setSplitter(String splitter) {
		this.splitter = splitter;
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
