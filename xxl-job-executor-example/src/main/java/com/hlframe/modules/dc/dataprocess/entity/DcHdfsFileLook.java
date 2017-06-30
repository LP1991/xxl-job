package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

import java.util.Date;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcHdfsFile.java 
 * @职责说明: hdfs文件远程获取处理对象
 * @创建者: huanggw
 * @创建时间: 2016年11月23日 下午3:03:27
 */
public class DcHdfsFileLook extends DataEntity<DcHdfsFileLook>{
	private String folderName;//文件名 例如 hbase
	private String Size;//大小
	private String pathName;//路径 显示/路径下的数据的所有路径 例如/hbase
	private String tempPath;//存储查询临时路径 例如/
	private String group;//goup
	private String permissions;
	private Date DateT;//成功时间 
	private String owner;//创建人
	private boolean isDir;//是否是目录 
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getSize() {
		return Size;
	}
	public void setSize(String size) {
		Size = size;
	}
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public String getTempPath() {
		return tempPath;
	}
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public Date getDateT() {
		return DateT;
	}
	public void setDateT(Date dateT) {
		DateT = dateT;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public boolean isDir() {
		return isDir;
	}
	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	
	
	
}
