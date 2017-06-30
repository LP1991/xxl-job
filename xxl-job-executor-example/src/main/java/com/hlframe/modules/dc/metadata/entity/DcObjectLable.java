/********************** 版权声明 *************************
 * 文件名: DcObjectLable.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:35:48
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectLable.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:35:48
 */
public class DcObjectLable extends DataEntity<DcObjectLable>  {

	private static final long serialVersionUID = 1L;
	
	private String labelName;
	private String labelDesc;
	private String delFlag;
	private String status;
	private String remarks;
	
	/**
	 * @return the lABEL_NAME
	 */
	public String getLABEL_NAME() {
		return labelName;
	}
	/**
	 * @param lABEL_NAME the lABEL_NAME to set
	 */
	public void setLABEL_NAME(String lABEL_NAME) {
		labelName = lABEL_NAME;
	}
	/**
	 * @return the lABEL_DESC
	 */
	public String getLABEL_DESC() {
		return labelDesc;
	}
	/**
	 * @param lABEL_DESC the lABEL_DESC to set
	 */
	public void setLABEL_DESC(String lABEL_DESC) {
		labelDesc = lABEL_DESC;
	}
	/**
	 * @return the dEL_FLAG
	 */
	public String getDEL_FLAG() {
		return delFlag;
	}
	/**
	 * @param dEL_FLAG the dEL_FLAG to set
	 */
	public void setDEL_FLAG(String dEL_FLAG) {
		delFlag = dEL_FLAG;
	}
	/**
	 * @return the sTATUS
	 */
	public String getSTATUS() {
		return status;
	}
	/**
	 * @param sTATUS the sTATUS to set
	 */
	public void setSTATUS(String sTATUS) {
		status = sTATUS;
	}
	/**
	 * @return the rEMARKS
	 */
	public String getREMARKS() {
		return remarks;
	}
	/**
	 * @param rEMARKS the rEMARKS to set
	 */
	public void setREMARKS(String rEMARKS) {
		remarks = rEMARKS;
	}
	

}
