/********************** 版权声明 *************************
 * 文件名: DcTaskQuery.java
 * 包名: com.hlframe.schedule.entity
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.entity;



public class DcTaskQuery {
	public static final String SSH_STATUS_FREE = "0";//空闲
	public static final String SSH_STATUS_EMPLOY = "1";//占用
	
	private String status;//判断是否空闲  0代表空闲 1代表占用
//	private SSHExec ssh;  //ssh连接
//	private ConnBean cb;//数据库连接对象
	private int WeiZhi;//位置信息 
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
//	public SSHExec getSsh() {
//		return ssh;
//	}
//	public void setSsh(SSHExec ssh) {
//		this.ssh = ssh;
//	}
//	public ConnBean getCb() {
//		return cb;
//	}
//	public void setCb(ConnBean cb) {
//		this.cb = cb;
//	}
	public int getWeiZhi() {
		return WeiZhi;
	}
	public void setWeiZhi(int weiZhi) {
		WeiZhi = weiZhi;
	}
	
	
	
}
