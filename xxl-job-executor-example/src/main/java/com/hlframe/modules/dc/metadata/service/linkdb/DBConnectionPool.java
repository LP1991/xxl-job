/********************** 版权声明 *************************
 * 文件名: DBConnectionPool.java
 * 包名: com.hzhl.core.dbpool
 * 版权：杭州华量软件
 ********************************************************
 *
 * 创建者：pjd   创建时间：2015年11月6日 下午12:09:26
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/** 
 * @类名: com.hzhl.core.dbpool.DBConnectionPool 
 * @职责说明: 可视化平台  数据库连接池
 * @创建者: pjd
 * @创建时间: 2015年11月6日 下午13:15:26   
 */
public class DBConnectionPool {

	Logger logger = LoggerFactory.getLogger(getClass());

	// 已取出的连接
	private int checkedOut;
	// 空闲连接队列
	@SuppressWarnings("rawtypes")
	private Vector freeConnections = new Vector();
	// 最大连接
	private int maxConn;
	// 连接池名称
	private String name;
	// jdbc 配置URL
	private String URL;
	// 数据库用户
	private String user;
	// 数据库用户密码
	private String password;

    /**
     * 实现功能 : 创建连接池
     * @创建时间: 2015年11月6日 下午1:20:46
     * @param name	连接池名称
     * @param URL	JDBC连接URL
     * @param user	数据库用户
     * @param password	连接密码
     * @param maxConn	最大连接数
     */
	public DBConnectionPool(String name, String URL, String user, String password, int maxConn) {
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;
	}  

    /**
     * @职责说明: 释放连接
     * @参数: @param con    
     * @返回: void   
     * @创建者: pjd 
     * @创建时间: 2015年11月6日 下午1:24:23
     */
    @SuppressWarnings("unchecked")
	public synchronized void freeConnection(java.sql.Connection con) {
		//添加到池中
		freeConnections.addElement(con);
		checkedOut--;
		notifyAll();
	}  

    /**
     * @方法名称: getConnection 
     * @职责说明: 获取连接
     * @参数: @return    
     * @返回: java.sql.Connection   
     * @创建者: pjd 
     * @创建时间: 2015年11月6日 下午1:25:05
     */
    @SuppressWarnings("resource")
	public synchronized java.sql.Connection getConnection() {
		java.sql.Connection con = null;
		if (freeConnections.size() > 0) {
			// 空闲连接池不为空 取第一条
			con = (java.sql.Connection) freeConnections.firstElement();
			freeConnections.removeElementAt(0);
			try {
				// 如果连接已关闭 则新建
				if (con.isClosed()) {
					logger.info("Removed bad connection from " + name);
					con = getConnection();
				}
			} catch (SQLException e) {
				logger.info("Removed bad connection from " + name);
				// 递归获取 直至取到可用的连接
				con = getConnection();
			}
		} else if (maxConn == 0 || checkedOut < maxConn) {
			con = newConnection();
		}
		if (con != null) {
			checkedOut++;
		}
		return con;
	}  

    /**
     * @方法名称: getConnection 
     * @职责说明: 获取连接  超出该时间则返回空
     * @参数: @param timeout  最长响应时间  单位毫秒 
     * @参数: @return    
     * @返回: java.sql.Connection   
     * @创建者: pjd 
     * @创建时间: 2015年11月6日 下午1:29:47
     */
	public synchronized java.sql.Connection getConnection(long timeout) {
		long startTime = new Date().getTime();
		java.sql.Connection con;
		while ((con = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				logger.info("getConnection:"+timeout+"ms", e);
			}
			if ((new Date().getTime() - startTime) >= timeout) {
				return null;
			}
		}
		return con;
	}  

    /**
     * @方法名称: release 
     * @职责说明: 释放所有连接
     * @参数:     
     * @返回: void   
     * @创建者: pjd 
     * @创建时间: 2015年11月6日 下午1:32:27
     */
    @SuppressWarnings("rawtypes")
	public synchronized void release() {
		Enumeration allConnections = freeConnections.elements();
		// 依次关闭所有连接
		while (allConnections.hasMoreElements()) {
			java.sql.Connection con = (java.sql.Connection) allConnections.nextElement();
			try {
				con.close();
				logger.info("Closed connection for pool " + name);
			} catch (SQLException e) {
				logger.error("Can't close connection for pool " + name, e);
			}
		}
		freeConnections.removeAllElements();
	}  


	/**
	 * 
	 * @方法名称: newConnection 
	 * @职责说明: 根据用户名 创建数据库连接
	 * @参数: @return    
	 * @返回: java.sql.Connection   
	 * @创建者: pjd 
	 * @创建时间: 2015年11月6日 下午1:33:18
	 */
	private java.sql.Connection newConnection() {
		java.sql.Connection con = null;
		try {
			if (user == null) {
				con = DriverManager.getConnection(URL);
			} else {
				con = DriverManager.getConnection(URL, user, password);
			}
			logger.info("Created a new connection in pool " + name);
		} catch (SQLException e) {
			logger.error("Can't create a new connection for " + URL, e);
			return null;
		}
		return con;
	} 
}
