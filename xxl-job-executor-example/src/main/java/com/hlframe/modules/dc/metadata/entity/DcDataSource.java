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
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;

import java.util.HashMap;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.entity.DcDataSource.java 
 * @职责说明: 数据源连接  实体类
 * @创建者: peijd
 * @创建时间: 2016年11月7日 下午1:36:10
 */
public class DcDataSource extends DataEntity<DcDataSource> {

	private static final long serialVersionUID = 1L;

	/** 数据库类别 **/
	public final static String DB_SERVER_TYPE_ORACLE = "dc_oracle";
	public final static String DB_SERVER_TYPE_MYSQL = "dc_mysql";
	public final static String DB_SERVER_TYPE_SQLSERVER = "dc_sqlserver";
	public final static String DB_SERVER_TYPE_DB2 = "dc_db2";
	public final static String DB_SERVER_TYPE_POSTGRESQL = "dc_postgresql";
	public final static String DB_SERVER_TYPE_IMPALA = "dc_impala";
	public final static String DB_SERVER_TYPE_HIVE = "dc_hive";
	
	/** 数据库驱动类 数据库id与数据字典dc_datasource_type的value保持一致 **/
	public final static Map<String, String> dbDriverMap = new HashMap<String, String>();
	static{
		dbDriverMap.put(DB_SERVER_TYPE_ORACLE, "oracle.jdbc.driver.OracleDriver");	//oracle	需加载:ojdbc14.jar
		dbDriverMap.put(DB_SERVER_TYPE_MYSQL, "com.mysql.jdbc.Driver");				//mysql		需加载:mysql-connector-java-x.x.xx-bin.jar
		dbDriverMap.put(DB_SERVER_TYPE_SQLSERVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");	//sqlserver2005,需加载:sqljdbc.jar  
		dbDriverMap.put(DB_SERVER_TYPE_DB2, "com.ibm.db2.jcc.DB2Driver");				//DB2(Type 4): 需加载: db2jcc.jar db2jcc_license_cu.jar
		dbDriverMap.put(DB_SERVER_TYPE_POSTGRESQL, "org.postgresql.Driver");			//postgresql: 需加载..
		dbDriverMap.put(DB_SERVER_TYPE_IMPALA, "com.cloudera.impala.jdbc41.Driver");	//impala: 需加载ImpalaJDBC41.jar 与cdh中组件版本匹配
		dbDriverMap.put(DB_SERVER_TYPE_HIVE, "org.apache.hive.jdbc.HiveDriver");		//hive: 需加载HiveJDBC41.jar 与cdh中组件版本匹配
	}
	
	/** 数据库驱动URL **/
	public final static Map<String, String> dbConnUrlMap = new HashMap<String, String>();
	static{
		dbConnUrlMap.put(DB_SERVER_TYPE_ORACLE, "jdbc:oracle:thin:@");		//oracle e.g.jdbc:oracle:thin:@<host>:<port>:<SID> 
		dbConnUrlMap.put(DB_SERVER_TYPE_MYSQL, "jdbc:mysql://");			//mysql: e.g.jdbc:mysql://<host>:<port>/<database_name>
		dbConnUrlMap.put(DB_SERVER_TYPE_SQLSERVER, "jdbc:sqlserver://");	// sqlserver 2005	e.g.jdbc:sqlserver://<server_name>:<port> 
		dbConnUrlMap.put(DB_SERVER_TYPE_DB2, "jdbc:db2://");				//DB2(Type 4) e.g.jdbc:db2://<host>[:<port>]/<database_name>
		dbConnUrlMap.put(DB_SERVER_TYPE_POSTGRESQL, "jdbc:postgresql://");	//jdbc:postgresql://<host>:<port>/<database_name>
		dbConnUrlMap.put(DB_SERVER_TYPE_IMPALA, "jdbc:impala://");			//jdbc:impala://10.1.20.137:21050/default
		dbConnUrlMap.put(DB_SERVER_TYPE_HIVE, "jdbc:hive2://");				//jdbc:hive2://10.1.20.137:10000/default
	}
	
	private String connName;
	private String connDesc;
	private String serverType;
	private String serverIP;
	private String serverPort;
	private String serverName;
	private String driverClass;
	private String serverUrl;
	private String serverUser;
	private String serverPswd;
	private String status;
	private int sortNum;
	/**
	 * @return the connName
	 */
	public String getConnName() {
		return connName;
	}
	/**
	 * @param connName the connName to set
	 */
	public void setConnName(String connName) {
		this.connName = connName;
	}
	/**
	 * @return the connDesc
	 */
	public String getConnDesc() {
		return connDesc;
	}
	/**
	 * @param connDesc the connDesc to set
	 */
	public void setConnDesc(String connDesc) {
		this.connDesc = connDesc;
	}
	/**
	 * @return the serverType
	 */
	public String getServerType() {
		return serverType;
	}
	/**
	 * @param serverType the serverType to set
	 */
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	/**
	 * @return the serverIP
	 */
	public String getServerIP() {
		return serverIP;
	}
	/**
	 * @param serverIP the serverIP to set
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	/**
	 * @return the serverPort
	 */
	public String getServerPort() {
		return serverPort;
	}
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}
	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	/**
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}
	/**
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	/**
	 * @return the serveruUser
	 */
	public String getServerUser() {
		return serverUser;
	}
	/**
	 * @param serverUser the serveruUser to set
	 */
	public void setServerUser(String serverUser) {
		this.serverUser = serverUser;
	}
	/**
	 * @return the serverPswd
	 */
	public String getServerPswd() {
		return serverPswd;
	}
	/**
	 * @param serverPswd the serverPswd to set
	 */
	public void setServerPswd(String serverPswd) {
		this.serverPswd = serverPswd;
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
	 * @return the sortUum
	 */
	public int getSortNum() {
		return sortNum;
	}
	/**
	 * @param sortUum the sortUum to set
	 */
	public void setSortNum(int sortUum) {
		this.sortNum = sortUum;
	}
	
	
	
}
