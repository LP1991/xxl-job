/********************** 版权声明 *************************
 * 文件名: DcObjectField.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:34:40
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectField.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:34:40
 */
public class DcObjectField extends DataEntity<DcObjectField>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objId;
	private String belong2Id;
	private String fieldName;
	private String fieldDesc;
	private String fieldType;
	private int fieldLeng;
	private int decimalNum;
	private int isKey;
	private String isNull;			//是否为空
	private String defaultVal;	//默认值
	private String validateRule;
	private int sortNum;
	
	
	public DcObjectField(String id){
		super();
		this.id=id;
	}
	public DcObjectField() {
		// TODO Auto-generated constructor stub
	}
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
	 * @return the belong2Id
	 */
	public String getBelong2Id() {
		return belong2Id;
	}
	/**
	 * @param belong2Id the belong2Id to set
	 */
	public void setBelong2Id(String belong2Id) {
		this.belong2Id = belong2Id;
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the fieldDesc
	 */
	public String getFieldDesc() {
		return fieldDesc;
	}
	/**
	 * @param fieldDesc the fieldDesc to set
	 */
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}
	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}
	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * @return the fieldLeng
	 */
	public int getFieldLeng() {
		return fieldLeng;
	}
	/**
	 * @param fieldLeng the fieldLeng to set
	 */
	public void setFieldLeng(int fieldLeng) {
		this.fieldLeng = fieldLeng;
	}
	/**
	 * @return the decimalNum
	 */
	public int getDecimalNum() {
		return decimalNum;
	}
	/**
	 * @param decimalNum the decimalNum to set
	 */
	public void setDecimalNum(int decimalNum) {
		this.decimalNum = decimalNum;
	}
	/**
	 * @return the isKey
	 */
	public int getIsKey() {
		return isKey;
	}
	/**
	 * @param isKey the isKey to set
	 */
	public void setIsKey(int isKey) {
		this.isKey = isKey;
	}
	/**
	 * @return the validateRule
	 */
	public String getValidateRule() {
		return validateRule;
	}
	/**
	 * @param validateRule the validateRule to set
	 */
	public void setValidateRule(String validateRule) {
		this.validateRule = validateRule;
	}
	/**
	 * @return the sortNum
	 */
	public int getSortNum() {
		return sortNum;
	}
	/**
	 * @param sortNum the sortNum to set
	 */
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defultVal) {
		this.defaultVal = defultVal;
	}
}
