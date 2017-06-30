/********************** 版权声明 *************************
 * 文件名: Pro.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：hgw   创建时间：2017年3月27日 下午7:45:02
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.Pro.java 
 * @职责说明: TODO
 * @创建者: hgw
 * @创建时间: 2017年3月27日 下午7:45:02
 */
public class Pro extends DataEntity<Pro>{
	private String key;//键
	private String value;//值
	private String desc;//描述
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
