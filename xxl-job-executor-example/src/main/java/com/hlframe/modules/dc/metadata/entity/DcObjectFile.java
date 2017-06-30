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

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.sys.entity.Office;
import com.hlframe.modules.sys.entity.User;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectFile.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:35:23
 */
public class DcObjectFile extends DataEntity<DcObjectFile>{

	private static final long serialVersionUID = 1L;
	
	/** 文件结构化标记 **/
	public static final int FILE_ISSTRUCT_Y = 1;
	public static final int FILE_ISSTRUCT_N = 0;
	
	private String objId;
	private String fileName;
	private String fileBelong;
	private String fileUrl;
	private int isStruct;
	private String splitter;
	private String objName;	//对象名称
	//private String remarks;
	private String objType;
	private User user;//业务负责人
	private Office office;//业务部门
	private String objCode;	//对象编码
	/** 复合元数据main对象信息 用于传参  **/


	//对象类型
	private String systemId;	//业务系统Id
	private String objDesc;	//对象描述
	private String managerPer;//责任人
	private String managerOrg;//责任部门
	//权限
	protected int accre;

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

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjCode() {
		return objCode;
	}

	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}

	public String getObjDesc() {
		return objDesc;
	}

	public void setObjDesc(String objDesc) {
		this.objDesc = objDesc;
	}

	public String getManagerPer() {
		return managerPer;
	}

	public void setManagerPer(String managerPer) {
		this.managerPer = managerPer;
	}

	public String getManagerOrg() {
		return managerOrg;
	}

	public void setManagerOrg(String managerOrg) {
		this.managerOrg = managerOrg;
	}

	public int getAccre() {
		return accre;
	}

	public void setAccre(int accre) {
		this.accre = accre;
	}
}
