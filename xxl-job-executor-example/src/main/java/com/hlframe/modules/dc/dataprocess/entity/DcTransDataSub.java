/********************** 版权声明 *************************
 * 文件名: DcTransDataSub.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:10:28
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub.java 
 * @职责说明: 数据转换过程
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:10:28
 */
public class DcTransDataSub extends DataEntity<DcTransDataSub>{
	private static final long serialVersionUID = 1L;
	
	/** 转换引擎 大数据平台目前支持dc_hive/dc_impala/dc_spark/dc_sqoop(数据采集)  **/
	public static final String TRANSLATE_ENGINE_HIVE = "dc_hive";
	public static final String TRANSLATE_ENGINE_IMPALA = "dc_impala";
	public static final String TRANSLATE_ENGINE_SPARKSQL = "dc_spark";
	public static final String TRANSLATE_ENGINE_SQOOP = "dc_sqoop";
	
	/** 转换类别 尽量细分转换类别 10-数据转换;20-数据采集;30-数据导出  **/
	public static final String TRANS_TYPE_TRANSLATE = "10";		//数据转换
	public static final String TRANS_TYPE_EXTRACT = "20";		//数据采集
	public static final String TRANS_TYPE_EXTRACT_DB = "21";	//DB采集
	public static final String TRANS_TYPE_EXTRACT_FILE = "22";	//文件采集
	public static final String TRANS_TYPE_EXPORT = "30";		//数据导出
	
	private String jobId;
	private String transName;
	private String transDesc;
	//转换过程处理引擎	trans_engine
	private String transEngine;	
	//转换步骤连接源	trans_conn
	private String transConn;
	//数据转换类别	trans_type	(10-数据转换;20-数据采集;30-数据导出)
	private String transType;
	//转换脚本
	private String transSql;
	private String transRst;
	private String status;
	private int sortNum;
	
	//trans_filter 转换过滤条件
	private String transFilter;
	
	
	// @return the jobId
	public String getJobId() {
		return jobId;
	}
	// @param jobId the jobId to set
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	// @return the transName
	public String getTransName() {
		return transName;
	}
	// @param transName the transName to set
	public void setTransName(String transName) {
		this.transName = transName;
	}
	// @return the transDesc
	public String getTransDesc() {
		return transDesc;
	}
	// @param transDesc the transDesc to set
	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}
	// @return the transSql
	public String getTransSql() {
		return transSql;
	}
	// @param transSql the transSql to set
	public void setTransSql(String transSql) {
		this.transSql = transSql;
	}
	// @return the transRst
	public String getTransRst() {
		return transRst;
	}
	// @param transRst the transRst to set
	public void setTransRst(String transRst) {
		this.transRst = transRst;
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
	public int getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
	// @return the transConn
	public String getTransConn() {
		return transConn;
	}
	// @param transConn the transConn to set
	public void setTransConn(String transConn) {
		this.transConn = transConn;
	}
	// @return the transEngine
	public String getTransEngine() {
		return transEngine;
	}
	// @param transEngine the transEngine to set
	public void setTransEngine(String transEngine) {
		this.transEngine = transEngine;
	}
	// @return the transFilter
	public String getTransFilter() {
		return transFilter;
	}
	// @param transFilter the transFilter to set
	public void setTransFilter(String transFilter) {
		this.transFilter = transFilter;
	}
	// @return the transType
	public String getTransType() {
		return transType;
	}
	// @param transType the transType to set
	public void setTransType(String transType) {
		this.transType = transType;
	}

	
}
