/********************** 版权声明 *************************
 * 文件名: DcObjectLink.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月10日 上午10:16:28
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectLink.java 
 * @职责说明: 存储数据字段/对象的链路关系
 * @创建者: peijd
 * @创建时间: 2016年12月10日 上午10:16:28
 */
public class DcObjectLink extends DataEntity<DcObjectLink> {

	private static final long serialVersionUID = 1L;
	
	/** 连接类型 **/
	public static final String LINK_TYPE_INTFC = "interface";
	public static final String LINK_TYPE_TABLE = "table";
	public static final String LINK_TYPE_FILE = "file";
	public static final String LINK_TYPE_FIELD = "field";
	
	/** 数据来源, 转换任务,数据采集,转换设计 **/
	public static final String DATA_SOURCE_TRANSJOB = "trans_job";
	public static final String DATA_SOURCE_EXTRACT_DB = "extract_db";
	public static final String DATA_SOURCE_EXTRACT_INTF = "extract_intf";
	public static final String DATA_SOURCE_TRANSDESIGN = "trans_design";
	public static final String DATA_SOURCE_EXTRACT_FILE = "extract_file";
	public static final String DATA_SOURCE_EXTRACT_HDFS = "extract_hdfs";
	public static final String DATA_SOURCE_EXPORT_DB = "export_db";
	
	//DATA_SOURCE 数据转换来源
	private String dataSource;
	//PROCESS_ID 转换过程Id
	private String processId;
	//LINK_TYPE 连接类型(对象/字段)
	private String linkType;
	//SRC_OBJ_ID 源对象ID
	private String srcObjId;
	//TAR_OBJ_ID 目标对象ID
	private String tarObjId;
	//TAR_OBJ_TYPE 目标对象/字段类型
	private String tarObjType;
	//TRANS_PARAM 转换参数
	private String transParam;
	//LINK_JSON 链路json
	private String linkJson;	
	//RELATION_EXP 关系表达式
	private String relationExp;
	
	//转换过程设计, 保存数据表转换的链路时, 需保存各任务的序号及执行脚本 
	//SORT_NUM 输出任务序号
	private Integer sortNum;
	//OUTPUT_SCRIPT 输出脚本
	private String outputScript;
	
	// @return the linkType
	public String getLinkType() {
		return linkType;
	}
	// @param linkType the linkType to set
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	// @return the dataSource
	public String getDataSource() {
		return dataSource;
	}
	// @param dataSource the dataSource to set
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	// @return the processId
	public String getProcessId() {
		return processId;
	}
	// @param processId the processId to set
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	// @return the srcObjId
	public String getSrcObjId() {
		return srcObjId;
	}
	// @param srcObjId the srcObjId to set
	public void setSrcObjId(String srcObjId) {
		this.srcObjId = srcObjId;
	}
	// @return the tarObjId
	public String getTarObjId() {
		return tarObjId;
	}
	// @param tarObjId the tarObjId to set
	public void setTarObjId(String tarObjId) {
		this.tarObjId = tarObjId;
	}
	// @return the linkJson
	public String getLinkJson() {
		return linkJson;
	}
	// @param linkJson the linkJson to set
	public void setLinkJson(String linkJson) {
		this.linkJson = linkJson;
	}
	// @return the tarObjType
	public String getTarObjType() {
		return tarObjType;
	}
	// @param tarObjType the tarObjType to set
	public void setTarObjType(String tarObjType) {
		this.tarObjType = tarObjType;
	}
	// @return the transParam
	public String getTransParam() {
		return transParam;
	}
	// @param transParam the transParam to set
	public void setTransParam(String transParam) {
		this.transParam = transParam;
	}
	// @return the relationExp
	public String getRelationExp() {
		return relationExp;
	}
	// @param relationExp the relationExp to set
	public void setRelationExp(String relationExp) {
		this.relationExp = relationExp;
	}
	// @return the sortNum
	public Integer getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	// @return the outputScript
	public String getOutputScript() {
		return outputScript;
	}
	// @param outputScript the outputScript to set
	public void setOutputScript(String outputScript) {
		this.outputScript = outputScript;
	}
	
}
