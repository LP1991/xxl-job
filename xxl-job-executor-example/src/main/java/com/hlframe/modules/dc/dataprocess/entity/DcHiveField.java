package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcHiveField.java 
 * @职责说明: 关于hive的字段信息
 * @创建者: cdd
 * @创建时间: 2017年1月9日 上午11:21:27
 */
public class DcHiveField extends DataEntity<DcHiveField> {

	private static final long serialVersionUID = 1L;
	
	private String belong2Id;//字段表Id
	private String fieldName;//字段名字
	private  String fieldDesc;//字段描述
	private String fieldType;//字段类型
	private String isKey;//主键？
	private Integer sortNum;//排序
	private String remarks;//备注
	
	public String getBelong2Id() {
		return belong2Id;
	}
	public void setBelong2Id(String belong2Id) {
		this.belong2Id = belong2Id;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldDesc() {
		return fieldDesc;
	}
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getIsKey() {
		return isKey;
	}
	public void setIsKey(String isKey) {
		this.isKey = isKey;
	}
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
