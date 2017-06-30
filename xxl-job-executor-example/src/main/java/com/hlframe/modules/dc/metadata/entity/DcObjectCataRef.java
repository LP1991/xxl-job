/********************** 版权声明 *************************
 * 文件名: DcObjectCataRef.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月7日 下午6:54:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectCataRef.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月7日 下午6:54:22
 */
public class DcObjectCataRef extends DataEntity<DcObjectCataRef>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objId;
	private String cataId;
	private String oldCataId;
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
	 * @return the cataId
	 */
	public String getCataId() {
		return cataId;
	}
	/**
	 * @param cataId the cataId to set
	 */
	public void setCataId(String cataId) {
		this.cataId = cataId;
	}
	/**
	 * @return the oldCataId
	 */
	public String getOldCataId() {
		return oldCataId;
	}
	/**
	 * @param oldCataId the oldCataId to set
	 */
	public void setOldCataId(String oldCataId) {
		this.oldCataId = oldCataId;
	}
	
}
