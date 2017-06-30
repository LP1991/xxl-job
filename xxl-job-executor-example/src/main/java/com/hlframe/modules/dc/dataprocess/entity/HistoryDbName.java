package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * Hive查询历史Entity
 * @author hgw
 *
 */
public class HistoryDbName extends DataEntity<HistoryDbName>{
	private String HSql;  //用户输入sql
	private String HDate; //查询日期
	private String HType; //查询状态 //三种状态 1.查询成功，2.查询失败，3.错误异常
	private String HUser; //查询用户
	private String HDbName; //查询数据库
	private String count;  //查询个数
	public String getHSql() {
		return HSql;
	}
	public void setHSql(String hSql) {
		HSql = hSql;
	}
	public String getHDate() {
		return HDate;
	}
	public void setHDate(String hDate) {
		HDate = hDate;
	}
	public String getHType() {
		return HType;
	}
	public void setHType(String hType) {
		HType = hType;
	}
	public String getHUser() {
		return HUser;
	}
	public void setHUser(String hUser) {
		HUser = hUser;
	}
	public String getHDbName() {
		return HDbName;
	}
	public void setHDbName(String hDbName) {
		HDbName = hDbName;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
	
}
