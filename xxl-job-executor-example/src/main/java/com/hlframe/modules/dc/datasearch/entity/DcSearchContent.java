/********************** 版权声明 *************************
 * 文件名: DcSearchContent.java
 * 包名: com.hlframe.modules.dc.datasearch.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年11月8日 上午10:05:05
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.datasearch.entity;

import com.hlframe.common.persistence.TreeEntity;

/** 
 * @类名: com.hlframe.modules.dc.datasearch.entity.DcSearchContent.java 
 * @职责说明: 分类明细树实体类
 * @创建者: yuzh
 * @创建时间: 2016年11月8日 上午10:05:05
 */
public class DcSearchContent extends TreeEntity<DcSearchContent>{
	private static final long serialVersionUID = 1L;
	//private DcSearchContent parent;	// 父级菜单
	//private String parentIds; // 所有父级编号
	private String cataItemId; 	// 条目id
	private String cataName; 	// 名称
	private String cataCode; 	// 编码
	private String status; 	// 状态
	
	public DcSearchContent(){
		super();
	}
	public DcSearchContent(String id){
		super(id);
	}

	public DcSearchContent getParent() {
		return parent;
	}

	public void setParent(DcSearchContent parent) {
		this.parent = parent;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getCataItemId() {
		return cataItemId;
	}

	public void setCataItemId(String cataItemId) {
		this.cataItemId = cataItemId;
	}

	public String getCataName() {
		return cataName;
	}

	public void setCataName(String cataName) {
		this.cataName = cataName;
	}

	public String getCataCode() {
		return cataCode;
	}

	public void setCataCode(String cataCode) {
		this.cataCode = cataCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
