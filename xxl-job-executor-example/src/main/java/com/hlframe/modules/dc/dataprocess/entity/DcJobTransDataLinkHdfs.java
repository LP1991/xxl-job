/********************** 版权声明 *************************
 * 文件名: DcJobTransData.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午8:56:13
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcJobTransData.java 
 * @职责说明: 数据采集jod-连接目标设置
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午8:56:13
 */
public class DcJobTransDataLinkHdfs extends DataEntity<DcJobTransDataLinkHdfs> {

	private static final long serialVersionUID = 1L;
	
	/** 增量采集方式 全量/序列/时间戳 **/
	public static final String INCREMENT_TYPE_WHOLE = "whole";
	public static final String INCREMENT_TYPE_SEQUENCE = "sequence";
	public static final String INCREMENT_TYPE_TIMESTAMP = "timeStamp";

	private String jobId; 			// JobId
	private String outputFormat; 	// 输出格式
	private String overRideNull; 	// 是否覆盖null
	private String nullValue; 		// null值替代为...
	private String compresFormat; 	// 压缩格式
	private String outputDir; 		// 输出路径
	private String outputTable; 	// 输出数据表(hive/hbase)
	private String isCreateTable; 	// 是否建表(hbase/hive)
	private String keyField; 		// 主键字段(hbase)
	private String columnFamily; 	// 列族名称(hbase)
	private String incrementType; 	// 增量方式(keyId/timeStamp)
	private String incrementField; 	// 增量字段(根据哪个字段进行增量)
	private String incrementValue; 	// 增量值(从哪个值开始增量)
	private String partitionField; 	// 分区字段(hive中使用)	partition_field
	private String partitionValue; 	// hive分区值(支持变量引入)	partition_value


	
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the outputFormat
	 */
	public String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * @param outputFormat
	 *            the outputFormat to set
	 */
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * @return the overRideNull
	 */
	public String getOverRideNull() {
		return overRideNull;
	}

	/**
	 * @param overRideNull
	 *            the overRideNull to set
	 */
	public void setOverRideNull(String overRideNull) {
		this.overRideNull = overRideNull;
	}

	/**
	 * @return the nullValue
	 */
	public String getNullValue() {
		return nullValue;
	}

	/**
	 * @param nullValue
	 *            the nullValue to set
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * @return the compresFormat
	 */
	public String getCompresFormat() {
		return compresFormat;
	}

	/**
	 * @param compresFormat
	 *            the compresFormat to set
	 */
	public void setCompresFormat(String compresFormat) {
		this.compresFormat = compresFormat;
	}

	/**
	 * @return the outputDir
	 */
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * @param outputDir
	 *            the outputDir to set
	 */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	// @return the outputTable
	public String getOutputTable() {
		return outputTable;
	}

	// @param outputTable the outputTable to set
	public void setOutputTable(String outputTable) {
		this.outputTable = outputTable;
	}

	// @return the incrementType
	public String getIncrementType() {
		return incrementType;
	}

	// @param incrementType the incrementType to set
	public void setIncrementType(String incrementType) {
		this.incrementType = incrementType;
	}

	// @return the incrementField
	public String getIncrementField() {
		return incrementField;
	}

	// @param incrementField the incrementField to set
	public void setIncrementField(String incrementField) {
		this.incrementField = incrementField;
	}

	// @return the incrementValue
	public String getIncrementValue() {
		return incrementValue;
	}

	// @param incrementValue the incrementValue to set
	public void setIncrementValue(String incrementValue) {
		this.incrementValue = incrementValue;
	}

	// @return the isCreateTable
	public String getIsCreateTable() {
		return isCreateTable;
	}

	// @param isCreateTable the isCreateTable to set
	public void setIsCreateTable(String isCreateTable) {
		this.isCreateTable = isCreateTable;
	}

	// @return the keyField
	public String getKeyField() {
		return keyField;
	}

	// @param keyField the keyField to set
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	// @return the columnFamily
	public String getColumnFamily() {
		return columnFamily;
	}

	// @param columnFamily the columnFamily to set
	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;
	}

	public String getPartitionField() {
		return partitionField;
	}

	public void setPartitionField(String partitionField) {
		this.partitionField = partitionField;
	}

	public String getPartitionValue() {
		return partitionValue;
	}

	public void setPartitionValue(String partitionValue) {
		this.partitionValue = partitionValue;
	}
}
