/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.entity;


import com.hlframe.common.persistence.DataEntity;

/**
 * 采集传输文件Entity
 * @author phy
 * @version 2016-11-23
 */
public class DcJobTransFileHdfs extends DataEntity<DcJobTransFileHdfs> {
	
	private static final long serialVersionUID = 1L;
	private String jobId;		// 任务名称

	private String ip;		// IP地址
	private int port;		// 端口
	private String account;		// 账号
	private String password;		// 密码
	private String pathname;		// 文件路径
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPathname() {
		return pathname;
	}
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}
	
}