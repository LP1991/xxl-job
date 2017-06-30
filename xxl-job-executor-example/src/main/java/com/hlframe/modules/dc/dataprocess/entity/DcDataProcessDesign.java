/********************** 版权声明 *************************
 * 文件名: DcDataProcessDesign.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月21日 上午9:28:02
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcDataProcessDesign.java 
 * @职责说明: 数据转换过程设计
 * @创建者: peijd
 * @创建时间: 2017年2月21日 上午9:28:02
 */
public class DcDataProcessDesign extends DataEntity<DcDataProcessDesign> {

	private static final long serialVersionUID = 1L;
	
	/** 任务编辑状态 用于控制脚本的更新状态 0-编辑; 1-测试; 9-调度 **/
	public static final String JOB_STATUS_EDIT = "0";
	public static final String JOB_STATUS_TEST = "1";
	public static final String JOB_STATUS_JOB = "9";
	
	private String designName;	//设计名称 	DESIGN_NAME
	private String designDesc;	//设计描述 	DESIGN_DESC
	private String designJson;	//设计json 	DESIGN_JSON
	private String designScript;//转换脚本 	DESIGN_SCRIPT
	private String status;		//状态		STATUS
	private String sortNum;		//显示顺序	SORT_NUM
	
	// @return the designName
	public String getDesignName() {
		return designName;
	}
	// @param designName the designName to set
	public void setDesignName(String designName) {
		this.designName = designName;
	}
	// @return the designDesc
	public String getDesignDesc() {
		return designDesc;
	}
	// @param designDesc the designDesc to set
	public void setDesignDesc(String designDesc) {
		this.designDesc = designDesc;
	}
	// @return the designJson
	public String getDesignJson() {
		return designJson;
	}
	// @param designJson the designJson to set
	public void setDesignJson(String designJson) {
		this.designJson = designJson;
	}
	// @return the designScript
	public String getDesignScript() {
		return designScript;
	}
	// @param designScript the designScript to set
	public void setDesignScript(String designScript) {
		this.designScript = designScript;
	}
	// @return the status
	public String getStatus() {
		return status;
	}
	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}
	// @return the sortNum
	public String getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(String sortNum) {
		this.sortNum = sortNum;
	}
	
	
}
