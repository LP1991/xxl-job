/********************** 版权声明 *************************
 * 文件名: DcSysObjm.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月25日 下午1:26:50
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcSysObjm.java 
 * @职责说明: 元数据对象-用户 权限
 * @创建者: peijd
 * @创建时间: 2017年2月25日 下午1:26:50
 */
public class DcSysObjm extends DataEntity<DcSysObjm> {

	private static final long serialVersionUID = 1L;
	
	private String userId;			//role_id 用户Id
	private String objMainId;		//obj_main_id 元数据对象Id
	
	// @return the userId
	public String getUserId() {
		return userId;
	}
	// @param userId the userId to set
	public void setUserId(String userId) {
		this.userId = userId;
	}
	// @return the objMainId
	public String getObjMainId() {
		return objMainId;
	}
	// @param objMainId the objMainId to set
	public void setObjMainId(String objMainId) {
		this.objMainId = objMainId;
	}
	
	
}
