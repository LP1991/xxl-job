/********************** 版权声明 *************************
 * 文件名: DcHdfsFile.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月23日 下午3:03:27
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcHdfsFile.java 
 * @职责说明: HDFS文件对象
 * @创建者: peijd
 * @创建时间: 2016年11月23日 下午3:03:27
 */
public class DcHdfsFile {

	private String fileName;	//文件名
	private String fileSize;	//文件大小
	private String user;		//所属用户
	private String group;		//用户组
	private String permissions;	//权限
	private String createDate;	//创建时间
	
	private String filePath;	//文件路径
	private String content;		//文件内容
	
	// @return the fileName
	public String getFileName() {
		return fileName;
	}
	// @param fileName the fileName to set
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	// @return the fileSize
	public String getFileSize() {
		return fileSize;
	}
	// @param fileSize the fileSize to set
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	// @return the user
	public String getUser() {
		return user;
	}
	// @param user the user to set
	public void setUser(String user) {
		this.user = user;
	}
	// @return the group
	public String getGroup() {
		return group;
	}
	// @param group the group to set
	public void setGroup(String group) {
		this.group = group;
	}
	// @return the permissions
	public String getPermissions() {
		return permissions;
	}
	// @param permissions the permissions to set
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	// @return the createDate
	public String getCreateDate() {
		return createDate;
	}
	// @param createDate the createDate to set
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	// @return the filePath
	public String getFilePath() {
		return filePath;
	}
	// @param filePath the filePath to set
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	// @return the content
	public String getContent() {
		return content;
	}
	// @param content the content to set
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
