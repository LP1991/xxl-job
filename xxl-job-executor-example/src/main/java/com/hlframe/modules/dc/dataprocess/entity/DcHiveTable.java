package com.hlframe.modules.dc.dataprocess.entity;


import com.hlframe.common.persistence.DataEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcHiveTable.java 
 * @职责说明: hive表管理
 * @创建者: cdd
 * @创建时间: 2017年1月7日 上午11:52:26
 */
public class DcHiveTable extends DataEntity<DcHiveTable> {

	private static final long serialVersionUID = 1L;
	
	/** hive table状态(0-新建;1-生成表;9-导入数据), 用于控制hive数据的同步,编辑,删除及导入操作 add by peijd 2017-3-29 **/
	public static final String TABLE_STATUS_NEW = "0";
	public static final String TABLE_STATUS_INIT = "1";
	public static final String TABLE_STATUS_EXPORT = "9";
	
	private String tableName;//数据表名称
	private String tableDesc;//数据表描述
	private String tableSpace;//数据表空间
	private String separatorSign;//分割符
	private String owner;//拥有者
	private String tableType;///数据表类型
	private String location;//位置
	private String isLoadData;//是否导入数据
	private Date createTime;//创建时间
	
	private String status;//状态	add by peijd 2017-3-29
	
	/** 数据库驱动URL **/
	public final static Map<String, String> separatorMap = new HashMap<String, String>();
	static{
		//separatorMap.put("'^A'", ",");		//
		//separatorMap.put("'^B'", "");			//
		//separatorMap.put("'^C'", "");	// 
		separatorMap.put("Tab(\t)", "\\t");	//以\Tab键分割字符串
		separatorMap.put("Comma(,)", ",");	//以逗号分割字符串
		separatorMap.put("Space", " ");			//以空格分割字符串
		separatorMap.put("Other", "Other");				//
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableDesc() {
		return tableDesc;
	}
	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}
	public String getTableSpace() {
		return tableSpace;
	}
	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}
	public String getSeparatorSign() {
		return separatorSign;
	}
	public void setSeparatorSign(String separatorSign) {
		this.separatorSign = separatorSign;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIsLoadData() {
		return isLoadData;
	}
	public void setIsLoadData(String isLoadData) {
		this.isLoadData = isLoadData;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date date) {
		this.createTime = date;
	}
	// @return the status
	public String getStatus() {
		return status;
	}
	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}
	
}
