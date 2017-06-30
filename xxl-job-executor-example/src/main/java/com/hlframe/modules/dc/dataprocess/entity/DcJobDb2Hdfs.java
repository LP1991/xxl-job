/********************** 版权声明 *************************
 * 文件名: DcJobTransDataDB.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月17日 上午10:59:23
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcJobDb2Hdfs.java 
 * @职责说明: 数据对象转换  数据库到Hdfs
 * @创建者: peijd
 * @创建时间: 2016年11月17日 上午10:59:23
 */
public class DcJobDb2Hdfs extends DataEntity<DcJobDb2Hdfs> {

	private static final long serialVersionUID = 1L;
	
	/** Trans DB-转换任务配置 (DcJobTransData) **/
	private String jobName;		//任务名称
	private String jobDesc;		//任务描述
	private String fromLink;	//连接源
	private String toLink;		//连接目标
	private String appendType;	//更新方式
	private String logDir;		//日志路径
	private String status;
	private int sortNum;
	
	/** link-转换源信息 (DcJobTransDataLinkDb) **/
	private String schemaName;		//数据库连接schema
	private String tableName;		//数据表名称
	private String tableSql;		//查询脚本
	private String tableColumn;		//字段
	private String partitionColumn;	//分区字段
	private String partitionNull;	//分区字段是否可为空值
	private String boundaryQuery;	//
	
	/** link-转换目标信息 (DcJobTransDataLinkHdfs) **/
	private String outputFormat; // 输出格式
	private String overRideNull; // 是否覆盖null
	private String nullValue; // null值替代为...
	private String compresFormat; // 压缩格式
	private String outputDir; // 输出路径
	private String outputTable; // 输出数据表(hive/hbase)
	private String isCreateTable; 	// 是否建表(hbase/hive)
	private String keyField; 		// 主键字段(hbase)
	private String columnFamily; 	// 列族名称(hbase)
	private String incrementType; // 增量方式(keyId/timeStamp)
	private String incrementField; // 增量字段(根据哪个字段进行增量)
	private String incrementValue; // 增量值(从哪个值开始增量)
	private String partitionField; 	// hive分区字段	partition_field
	private String partitionValue; 	// hive分区值(支持变量引入)	partition_value
	
	/**  查询字段, 不存储  **/
	protected String dbConnName;	//DB_数据源连接名称
	protected String dbServerType;	//DB_数据库类型

	/** 参数字段, 仅用于表单参数传值, hive/hbase配置属性在同一个表单中,所以用两个参数区分 **/
	private String tbNameHive; 		// hive表名称
	private String tbNameHbase; 	// hbase表名称
	private Boolean createTbHive; 	// 是否创建hive表
	private Boolean createTbHbase; 	// 是否创建hbase表
	
	/** 采集脚本 sqoop script存储到remarks字段中 **/
	
	public DcJobDb2Hdfs(){		
	}
	
	public DcJobDb2Hdfs(DcJobTransData transData){
		this.id = transData.getId();
		this.jobName = transData.getJobName();
		this.jobDesc = transData.getJobDesc();
		this.fromLink = transData.getFromLink();
		this.toLink = transData.getToLink();
		this.appendType = transData.getAppendType();
		this.status = transData.getStatus();
		this.sortNum = transData.getSortNum();
		this.logDir = transData.getLogDir();
	}
	
	//权限
		protected int accre;
	
	
	// @return the jobName
	public String getJobName() {
		return jobName;
	}

	// @param jobName the jobName to set
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	// @return the jobDesc
	public String getJobDesc() {
		return jobDesc;
	}

	// @param jobDesc the jobDesc to set
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	// @return the fromLink
	public String getFromLink() {
		return fromLink;
	}

	// @param fromLink the fromLink to set
	public void setFromLink(String fromLink) {
		this.fromLink = fromLink;
	}

	// @return the toLink
	public String getToLink() {
		return toLink;
	}

	// @param toLink the toLink to set
	public void setToLink(String toLink) {
		this.toLink = toLink;
	}

	// @return the appendType
	public String getAppendType() {
		return appendType;
	}

	// @param appendType the appendType to set
	public void setAppendType(String appendType) {
		this.appendType = appendType;
	}

	// @return the logDir
	public String getLogDir() {
		return logDir;
	}

	// @param logDir the logDir to set
	public void setLogDir(String logDir) {
		this.logDir = logDir;
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

	// @return the schemaName
	public String getSchemaName() {
		return schemaName;
	}

	// @param schemaName the schemaName to set
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the tableSql
	 */
	public String getTableSql() {
		return tableSql;
	}
	/**
	 * @param tableSql the tableSql to set
	 */
	public void setTableSql(String tableSql) {
		this.tableSql = tableSql;
	}
	/**
	 * @return the tableColumn
	 */
	public String getTableColumn() {
		return tableColumn;
	}
	/**
	 * @param tableColumn the tableColumn to set
	 */
	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}
	/**
	 * @return the partitionColumn
	 */
	public String getPartitionColumn() {
		return partitionColumn;
	}
	/**
	 * @param partitionColumn the partitionColumn to set
	 */
	public void setPartitionColumn(String partitionColumn) {
		this.partitionColumn = partitionColumn;
	}
	/**
	 * @return the partitionNull
	 */
	public String getPartitionNull() {
		return partitionNull;
	}
	/**
	 * @param partitionNull the partitionNull to set
	 */
	public void setPartitionNull(String partitionNull) {
		this.partitionNull = partitionNull;
	}
	/**
	 * @return the boundaryQuery
	 */
	public String getBoundaryQuery() {
		return boundaryQuery;
	}
	/**
	 * @param boundaryQuery the boundaryQuery to set
	 */
	public void setBoundaryQuery(String boundaryQuery) {
		this.boundaryQuery = boundaryQuery;
	}
	/**
	 * @return the outputFormat
	 */
	public String getOutputFormat() {
		return outputFormat;
	}
	/**
	 * @param outputFormat the outputFormat to set
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
	 * @param overRideNull the overRideNull to set
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
	 * @param nullValue the nullValue to set
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
	 * @param compresFormat the compresFormat to set
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
	 * @param outputDir the outputDir to set
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

	// @return the dbConnName
	public String getDbConnName() {
		return dbConnName;
	}

	// @param dbConnName the dbConnName to set
	public void setDbConnName(String dbConnName) {
		this.dbConnName = dbConnName;
	}

	// @return the dbServerType
	public String getDbServerType() {
		return dbServerType;
	}

	// @param dbServerType the dbServerType to set
	public void setDbServerType(String dbServerType) {
		this.dbServerType = dbServerType;
	}

	/**
	 * @return the accre
	 */
	public int getAccre() {
		return accre;
	}

	/**
	 * @param accre the accre to set
	 */
	public void setAccre(int accre) {
		this.accre = accre;
	}

	// @return the tbNameHive
	public String getTbNameHive() {
		return tbNameHive;
	}

	// @param tbNameHive the tbNameHive to set
	public void setTbNameHive(String tbNameHive) {
		this.tbNameHive = tbNameHive;
	}

	// @return the tbNameHbase
	public String getTbNameHbase() {
		return tbNameHbase;
	}

	// @param tbNameHbase the tbNameHbase to set
	public void setTbNameHbase(String tbNameHbase) {
		this.tbNameHbase = tbNameHbase;
	}

	// @return the createTbHive
	public Boolean getCreateTbHive() {
		return createTbHive;
	}

	// @param createTbHive the createTbHive to set
	public void setCreateTbHive(Boolean createTbHive) {
		this.createTbHive = createTbHive;
	}

	// @return the createTbHbase
	public Boolean getCreateTbHbase() {
		return createTbHbase;
	}

	// @param createTbHbase the createTbHbase to set
	public void setCreateTbHbase(Boolean createTbHbase) {
		this.createTbHbase = createTbHbase;
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
