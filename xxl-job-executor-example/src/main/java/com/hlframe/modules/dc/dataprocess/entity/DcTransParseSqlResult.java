/********************** 版权声明 *************************
 * 文件名: DcTransParseSqlResult.java
 * 包名: com.hlframe.modules.dc.dataprocess
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月3日 下午12:40:42
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.DcTransParseSqlResult.java 
 * @职责说明: 转换脚本 sql解析对象
 * @创建者: peijd
 * @创建时间: 2016年12月3日 下午12:40:42
 */
public class DcTransParseSqlResult {

	//操作标记
	private String operate;
	//条件
	private String condition;
	//转换来源表
	private String srcTable;
	//转换目标表
	private String tarTable;
	//目标表字段
	private List<Map<String, String>> tableFieldList;
	//字段转换关系
	private List<String> fieldRefList;
	//转换任务Id
//	private String jobId;
	//转换过程Id
//	private String processId;
	
	// @return the operate
	public String getOperate() {
		return operate;
	}
	// @param operate the operate to set
	public void setOperate(String operate) {
		this.operate = operate;
	}
	// @return the condition
	public String getCondition() {
		return condition;
	}
	// @param condition the condition to set
	public void setCondition(String condition) {
		this.condition = condition;
	}
	// @return the srcTable
	public String getSrcTable() {
		return srcTable;
	}
	// @param srcTable the srcTable to set
	public void setSrcTable(String srcTable) {
		this.srcTable = srcTable;
	}
	// @return the tarTable
	public String getTarTable() {
		return tarTable;
	}
	// @param tarTable the tarTable to set
	public void setTarTable(String tarTable) {
		this.tarTable = tarTable;
	}
	// @return the tableFieldList
	public List<Map<String, String>> getTableFieldList() {
		return tableFieldList;
	}
	// @param tableFieldList the tableFieldList to set
	public void setTableFieldList(List<Map<String, String>> tableFieldList) {
		this.tableFieldList = tableFieldList;
	}
	// @return the fieldRefList
	public List<String> getFieldRefList() {
		return fieldRefList;
	}
	// @param fieldRefList the fieldRefList to set
	public void setFieldRefList(List<String> fieldRefList) {
		this.fieldRefList = fieldRefList;
	}
	
}
