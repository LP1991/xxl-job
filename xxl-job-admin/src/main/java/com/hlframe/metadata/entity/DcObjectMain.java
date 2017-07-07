/********************** 版权声明 *************************
 * 文件名: DcDataSource.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月7日 下午1:36:10
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.metadata.entity;

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.sys.entity.Office;
import com.hlframe.modules.sys.entity.User;

/**
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectMain.java 
 * @职责说明: 数据对象实体类   --公共 元数据信息
 * @创建者: peijd
 * @创建时间: 2016年11月8日 下午2:33:11
 */
public class DcObjectMain extends DataEntity<DcObjectMain> {

	private static final long serialVersionUID = 1L;
	
	/** 任务类别jobType 01-DB采集,02-文件采集(FTP),03-hdfs采集, 04-接口数据采集;11-数据转换;12-数据处理任务(设计); 20-用户上传;30-HIVE数据采集   **/
	public static final String JOB_TYPE_EXTRACT_DB = "01";
	public static final String JOB_TYPE_EXTRACT_FILE = "02";
	public static final String JOB_TYPE_EXTRACT_HDFS = "03";
	public static final String JOB_TYPE_EXTRACT_INTF = "04";
	public static final String JOB_TYPE_TRANSLATE = "11";
	public static final String JOB_TYPE_PROCESS = "12";
	public static final String JOB_TYPE_UPLOAD = "20";
	public static final String JOB_TYPE_HIVE = "30";
	
	/**  是否数据中心元数据jobSrcFlag  Y-是;N-否(e.g.采集数据源/导出目标数据) **/
	public static final String JOB_SRC_FLAG_YES = "Y";
	public static final String JOB_SRC_FLAG_NO = "N";
	
	/**  对象类型objType  Y-是;N-否(e.g.采集数据源/导出目标数据) **/
	public final static String OBJ_TYPE_TABLE = "1";	//数据表
	public final static String OBJ_TYPE_FILE = "2";		//文件
	public final static String OBJ_TYPE_FIELD = "3";	//字段
	public final static String OBJ_TYPE_INTER = "4";	//接口
	public final static String OBJ_TYPE_FOLDER = "5";	//文件夹
	public final static String OBJ_TYPE_DATABASE = "6";	//数据库
	
	protected String objCode;	//对象编码
	protected String objName;	//对象名称
	protected String objType;	//对象类型
	protected String systemId;	//业务系统Id
	protected String objDesc;	//对象描述
	protected String managerPer;//责任人
	protected String managerOrg;//责任部门
	protected String status;	//状态
	protected int sortNum;		//排序
	//JOB_ID 任务ID
	protected String jobId; 
	//JOB_TYPE 任务类别(01-DB采集,02-文件采集,03-hdfs采集;11-数据转换; 20-用户上传)
	protected String jobType; 
	//JOB_SRC_FLAG  是否数据中心元数据,即存储于集群环境的元数据(Y-是;N-否)
	protected String jobSrcFlag; 
	
	
	protected int accre;		//权限  页面权限控制
	
	//查询参数,不存入物理表
	private String objCata;			//对象所属分类
	private String objCataItem;		//分类项目
	private String  cataid;
	private User user;//业务负责人
	private Office office;//业务部门	
	private String name;//用户名
	private String collectTime;//申请时间


	public String getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the objCode
	 */
	public String getObjCode() {
		return objCode;
	}
	/**
	 * @param objCode the objCode to set
	 */
	public void setObjCode(String objCode) {
		this.objCode = objCode;
	}
	/**
	 * @return the objName
	 */
	public String getObjName() {
		return objName;
	}
	/**
	 * @param objName the objName to set
	 */
	public void setObjName(String objName) {
		this.objName = objName;
	}
	/**
	 * @return the objType
	 */
	public String getObjType() {
		return objType;
	}
	/**
	 * @param objType the objType to set
	 */
	public void setObjType(String objType) {
		this.objType = objType;
	}
	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}
	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	/**
	 * @return the objDesc
	 */
	public String getObjDesc() {
		return objDesc;
	}
	/**
	 * @param objDesc the objDesc to set
	 */
	public void setObjDesc(String objDesc) {
		this.objDesc = objDesc;
	}
	/**
	 * @return the managerPer
	 */
	public String getManagerPer() {
		return managerPer;
	}
	/**
	 * @param managerPer the managerPer to set
	 */
	public void setManagerPer(String managerPer) {
		this.managerPer = managerPer;
	}
	/**
	 * @return the managerOrg
	 */
	public String getManagerOrg() {
		return managerOrg;
	}
	/**
	 * @param managerOrg the managerOrg to set
	 */
	public void setManagerOrg(String managerOrg) {
		this.managerOrg = managerOrg;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the sortNum
	 */
	public int getSortNum() {
		return sortNum;
	}
	/**
	 * @param sortNum the sortNum to set
	 */
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
	/**
	 * @return the objCata
	 */
	public String getObjCata() {
		return objCata;
	}
	/**
	 * @param objCata the objCata to set
	 */
	public void setObjCata(String objCata) {
		this.objCata = objCata;
	}
	/**
	 * @return the objCataItem
	 */
	public String getObjCataItem() {
		return objCataItem;
	}
	/**
	 * @param objCataItem the objCataItem to set
	 */
	public void setObjCataItem(String objCataItem) {
		this.objCataItem = objCataItem;
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
	// @return the jobId
	public String getJobId() {
		return jobId;
	}
	// @param jobId the jobId to set
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	// @return the jobType
	public String getJobType() {
		return jobType;
	}
	// @param jobType the jobType to set
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	// @return the jobSrcFlag
	public String getJobSrcFlag() {
		return jobSrcFlag;
	}
	// @param jobSrcFlag the jobSrcFlag to set
	public void setJobSrcFlag(String jobSrcFlag) {
		this.jobSrcFlag = jobSrcFlag;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}

	public String getCataid() {
		return cataid;
	}

	public void setCataid(String cataid) {
		this.cataid = cataid;
	}
}
