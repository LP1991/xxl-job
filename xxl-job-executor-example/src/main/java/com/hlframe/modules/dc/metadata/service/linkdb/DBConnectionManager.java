/********************** 版权声明 *************************
 * 文件名: DBConnectionManager.java
 * 包名: com.hzhl.core.dbpool
 * 版权：杭州华量软件
 ********************************************************
 *
 * 创建者：pjd   创建时间：2015年11月6日 下午12:08:25
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb;

import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;
import com.hlframe.modules.dc.metadata.service.DcDataSourceService;
import com.hlframe.modules.dc.utils.Des;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/** 
 * @类名: com.hzhl.core.dbpool.DBConnectionManager 
 * @职责说明: 数据库连接池管理
 * @创建者: pjd
 * @创建时间: 2015年11月6日 下午13:41:25   
 */
public class DBConnectionManager {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	//加载数据源连接Service
	@Autowired
	private static DcDataSourceService dcDataSourceService = SpringContextHolder.getBean(DcDataSourceService.class);
	
	// 唯一管理实例
	static private DBConnectionManager instance;
	//客户端连接数
	static private int clients;
	//数据库驱动
	@SuppressWarnings("rawtypes")
	private Vector drivers = new Vector();
	//数据连接池
	@SuppressWarnings("rawtypes")
	private Hashtable pools = new Hashtable();
	//默认连接数
	private int defaultConn = 5;
	
	/**
	 * @方法名称: getInstance 
	 * @职责说明: 获取连接管理实例  单例模式
	 * @参数: @return    
	 * @返回: DBConnectionManager   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:43:03
	 */
	public static synchronized DBConnectionManager getInstance() {
		if (instance == null) {
			instance = new DBConnectionManager();
		}
		clients++;
		return instance;
	}

	/**
	 * 私有构造函数 
	 * @创建时间: 2015年11月6日 下午1:44:25
	 */
	private DBConnectionManager() {
		//通过配置文件加载
//		initFromProp();
		//通过数据库 配置加载
		initFromDB(null);
	}

	/**
	 * @方法名称: freeConnection 
	 * @职责说明: 根据连接名称  存放连接池
	 * @参数: @param name	连接池名称 
	 * @参数: @param con    数据库连接
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:46:11
	 */
	public void freeConnection(String name, java.sql.Connection conn) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(conn);
		}
	}

	/**
	 * @方法名称: getConnection 
	 * @职责说明: 获取连接
	 * @参数: @param name  连接池名称
	 * @参数: @return    
	 * @返回: java.sql.Connection   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:48:39
	 */
	public java.sql.Connection getConnection(String name) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection();
		}else {
			//取不到数据源  重新在数据库中初始化
			initFromDB(name);
			pool = (DBConnectionPool) pools.get(name);
			return pool.getConnection();
		}
	}

	/**
	 * @方法名称: getConnection 
	 * @职责说明: 获取连接  限定时间
	 * @参数: @param name	连接池名称
	 * @参数: @param time	最长等待时间  单位 毫秒
	 * @参数: @return    
	 * @返回: java.sql.Connection   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:49:48
	 */
	public java.sql.Connection getConnection(String name, long time) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection(time);
		}else {
			//取不到数据源  重新在数据库中初始化
			initFromDB(name);
			pool = (DBConnectionPool) pools.get(name);
			return pool.getConnection(time);
		}
	}

	/**
	 * @方法名称: release 
	 * @职责说明: 释放所有的连接池
	 * @参数:     
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:50:56
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void release() {
		// Wait until called by the last client
		if (--clients != 0) {
			return;
		}

		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}
		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.info("drop JDBC driver " + driver.getClass().getName());
			} catch (SQLException e) {
				logger.info("Can't drop JDBC driver: " + driver.getClass().getName(), e);
			}
		}
	}

	/**
	 * @方法名称: createPools 
	 * @职责说明: 根据properties配置文件  初始化连接池
	 * @参数: @param props    
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:52:25
	 * <PRE>
	 * <poolname>.url         The JDBC URL for the database 
	 * <poolname>.user        A database user (optional) 
	 * <poolname>.password    A database user password (if user specified) 
	 * <poolname>.maxconn     The maximal number of connections (optional)
	 * </PRE>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createPools(Properties props) {
		Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					logger.info("No URL specified for " + poolName);
					continue;
				}
				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".maxconn", "10");
				try {
					defaultConn = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) {
					logger.error("Invalid maxconn value " + maxconn + " for " + poolName, e);
				}
				DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, defaultConn);
				pools.put(poolName, pool);
				logger.info("Initialized pool " + poolName);
			}
		}
	}

	/**
	 * @方法名称: initFromDB 
	 * @职责说明: 连接池管理  初始化 通过数据表: qd_dbsrc_connect
	 * @参数:     
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:54:11
	 */
	private void initFromDB(String dbSrcId) {
		//数据库连接配置服务类   动态初始化连接池
//		DcDataSourceService dbSrcService = SpringUtils.getBean("dcDataSourceService", DcDataSourceService.class);
		
		//获取所有数据库列表
		DcDataSource paramSrc = new DcDataSource();
//		paramSrc.setConnPoolFlag(DCConstants.ENABLED);
		//数据源ID不为空时  只初始化该数据源, 否则初始化所有数据源
		if (StringUtils.isNotBlank(dbSrcId)) {
			paramSrc.setId(dbSrcId);
		}
		try {
			List<DcDataSource> connConfiglist = dcDataSourceService.findList(paramSrc);
			Assert.notEmpty(connConfiglist, "未配置数据源连接!");
			
			//连接池数据集
			Set<String> driveSet = new HashSet<String>();
			for (DcDataSource connFig: connConfiglist) {
				driveSet.add(connFig.getDriverClass());
			}
			
			//加载驱动
			loadDrivers(driveSet);
			//创建连接池
			createPools(connConfiglist);
			
		} catch (Exception e) {
			logger.error("数据库连接 初始异常!", e);
		}
	}
	
	/** 
	 * @方法名称: createPools 
	 * @职责说明: 创建连接池
	 * @参数: @param connConfiglist    
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午6:02:01 
	 */ 
	@SuppressWarnings("unchecked")
	private void createPools(List<DcDataSource> connConfiglist) {
		for (DcDataSource conn: connConfiglist) {
			//连接池名称
			String poolName = conn.getId();
			String url = conn.getServerUrl();
			if (url == null) {
				logger.info("No URL specified for " + poolName);
				continue;
			}
			String user = conn.getServerUser();
			String password = Des.strDec(conn.getServerPswd());
			//初始化连接池
			DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, defaultConn);
			pools.put(poolName, pool);
			logger.info("Initialized pool " + poolName);
		}
		
	}

	/** 
	 * @方法名称: loadDrivers 
	 * @职责说明: 加载驱动
	 * @参数: @param driveSet    
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午5:58:21 
	 */ 
	@SuppressWarnings("unchecked")
	private void loadDrivers(Set<String> driveSet) {
		// 加载驱动
		for (String driverName : driveSet) {
			try {
				Driver driver = (Driver) Class.forName(driverName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				logger.info("Registered JDBC driver " + driverName);
			} catch (Exception e) {
				logger.error("register JDBC driver error "+ driverName, e);
			}
		}
	}

	/**
	 * @方法名称: initFromProp 
	 * @职责说明: 连接池管理  初始化 通过配置文件dbConfig.properties
	 * @参数:     
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:54:11
	 */
	private void initFromProp() {
		FileInputStream inputFile;
		try {
			//通过配置文件dbConfig.properties配置连接池
			inputFile = new FileInputStream("dbConfig.properties");
		} catch (FileNotFoundException ex) {
			logger.error(DBConnectionManager.class.getName(), ex);
			return;
		}
		//数据库配置文件
		Properties dbProps = new Properties();
		try {
			dbProps.load(inputFile);
		} catch (Exception e) {
			logger.error("Can't read the properties file. " + "Make sure dbConfig.properties is in the CLASSPATH",e);
			return;
		}
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	/**
	 * @方法名称: loadDrivers 
	 * @职责说明: 加载数据库驱动
	 * @参数: @param props    
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:58:28
	 */
	@SuppressWarnings("unchecked")
	private void loadDrivers(Properties props) {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				logger.info("Registered JDBC driver " + driverClassName);
			} catch (Exception e) {
				logger.error("Can't register JDBC driver: " + driverClassName, e);
			}
		}
	}

	public int getDefaultConn() {
		return defaultConn;
	}
	public void setDefaultConn(int defaultConn) {
		this.defaultConn = defaultConn;
	}

}
