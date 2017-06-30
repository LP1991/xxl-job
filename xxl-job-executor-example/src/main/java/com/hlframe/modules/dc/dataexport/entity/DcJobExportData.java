/********************** 版权声明 *************************
 * 文件名: DcHdfsFile.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月23日 下午3:03:27
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * @类名: com.hlframe.modules.dc.dataexport.entity.DcJobExportData.java 
 * @职责说明: hadoop集群 数据导出对象
 * @创建者: peijd
 * @创建时间: 2017年2月23日 下午4:43:54
 */
public class DcJobExportData extends DataEntity<DcJobExportData> {

	private static final long serialVersionUID = 1L;
	
	/** 数据更新模式: 只是insert; 只是update; insertAndUpdate  **/ 
	public static final String UPDATEMODE_INSERTONLY = "insertonly";
	public static final String UPDATEMODE_UPDATEONLY = "updateonly";
	public static final String UPDATEMODE_ALLOWINSERT = "allowinsert";
	
	/**  任务状态: 0-编辑; 1-测试; 9-添加任务 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_TEST = "1";
	public static final String TASK_STATUS_TASK = "9";
	
	
	private String jobName;			//job_name 任务名称
	private String jobDesc;			//job_desc 任务描述
	private String fromLink;		// from_link  源数据类型(1-hdfs;2-hive)
	private String metaDataId;		//metaData_id 元数据Id
	private String dataPath;		//data_path HDFS 文件路径/表名
	private Integer mapNum;			//map_num 并行任务数
	private String fieldSplitBy;	//field_split_by 字段分隔符
	private String nullString;		//null_string null字符串列替换值
	private String nullNonString;	// null_non_string null非字符串列替换值
	
	private String toLink;			//to_link 目标数据源连接
	private String schemaName;		//schema_name 数据库schema
	private String tableName;		//table_name 目标数据表
	private String assignColumn;	//assign_column 是否指定字段
	private String tableColumn;		//table_column 导入字段列名
	private String isClearData;		//is_clear_data 是否清空目标表数据(1-是;0-否)
	private String isUpdate;		//is_update 是否更新(1-是;0-否)
	private String updateKey;		//update_key 主键更新列,更新模式下以该字段作为更新依据
	private String updateMode;		//update_mode 更新策略, updateonly(默认) allowinsert
	private String logDir;			//log_dir 日志目录
	private String status;			//status 状态
	private Integer sortNum;		//sort_num 排序
	
	
	//虚拟字段 不入库保存  
	private Integer accre;			//个人操作权限 1-有权限; 0-无权限
	private String toLinkName;		//目标数据库连接
	private String dbType;			//数据库类别
	private boolean clearDataFlag;	//是否清空
	private boolean updateFlag;		//是否更新
	
	
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
	// @return the metaDataId
	public String getMetaDataId() {
		return metaDataId;
	}
	// @param metaDataId the metaDataId to set
	public void setMetaDataId(String metaDataId) {
		this.metaDataId = metaDataId;
	}
	// @return the dataPath
	public String getDataPath() {
		return dataPath;
	}
	// @param dataPath the dataPath to set
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
	// @return the mapNum
	public Integer getMapNum() {
		return mapNum;
	}
	// @param mapNum the mapNum to set
	public void setMapNum(Integer mapNum) {
		this.mapNum = mapNum;
	}
	// @return the fieldSplitBy
	public String getFieldSplitBy() {
		return fieldSplitBy;
	}
	// @param fieldSplitBy the fieldSplitBy to set
	public void setFieldSplitBy(String fieldSplitBy) {
		this.fieldSplitBy = fieldSplitBy;
	}
	// @return the nullString
	public String getNullString() {
		return nullString;
	}
	// @param nullString the nullString to set
	public void setNullString(String nullString) {
		this.nullString = nullString;
	}
	// @return the nullNonString
	public String getNullNonString() {
		return nullNonString;
	}
	// @param nullNonString the nullNonString to set
	public void setNullNonString(String nullNonString) {
		this.nullNonString = nullNonString;
	}
	// @return the toLink
	public String getToLink() {
		return toLink;
	}
	// @param toLink the toLink to set
	public void setToLink(String toLink) {
		this.toLink = toLink;
	}
	// @return the schemaName
	public String getSchemaName() {
		return schemaName;
	}
	// @param schemaName the schemaName to set
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	// @return the tableName
	public String getTableName() {
		return tableName;
	}
	// @param tableName the tableName to set
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	// @return the assignColumn
	public String getAssignColumn() {
		return assignColumn;
	}
	// @param assignColumn the assignColumn to set
	public void setAssignColumn(String assignColumn) {
		this.assignColumn = assignColumn;
	}
	// @return the tableColumn
	public String getTableColumn() {
		return tableColumn;
	}
	// @param tableColumn the tableColumn to set
	public void setTableColumn(String tableColumn) {
		this.tableColumn = tableColumn;
	}
	// @return the isClearData
	public String getIsClearData() {
		return isClearData;
	}
	// @param isClearData the isClearData to set
	public void setIsClearData(String isClearData) {
		this.isClearData = isClearData;
	}
	// @return the isUpdate
	public String getIsUpdate() {
		return isUpdate;
	}
	// @param isUpdate the isUpdate to set
	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}
	// @return the updateKey
	public String getUpdateKey() {
		return updateKey;
	}
	// @param updateKey the updateKey to set
	public void setUpdateKey(String updateKey) {
		this.updateKey = updateKey;
	}
	// @return the updateMode
	public String getUpdateMode() {
		return updateMode;
	}
	// @param updateMode the updateMode to set
	public void setUpdateMode(String updateMode) {
		this.updateMode = updateMode;
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
	public Integer getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	// @return the accre
	public Integer getAccre() {
		return accre;
	}
	// @param accre the accre to set
	public void setAccre(Integer accre) {
		this.accre = accre;
	}
	// @return the dbType
	public String getDbType() {
		return dbType;
	}
	// @param dbType the dbType to set
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	// @return the clearDataFlag
	public boolean getClearDataFlag() {
		return "1".equals(isClearData) ;
	}
	// @param clearDataFlag the clearDataFlag to set
	public void setClearDataFlag(boolean clearDataFlag) {
		this.clearDataFlag = clearDataFlag;
	}
	// @return the updateFlag
	public boolean getUpdateFlag() {
		return updateFlag;
	}
	// @param updateFlag the updateFlag to set
	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}
	// @return the toLinkName
	public String getToLinkName() {
		return toLinkName;
	}
	// @param toLinkName the toLinkName to set
	public void setToLinkName(String toLinkName) {
		this.toLinkName = toLinkName;
	}
	
	
}
