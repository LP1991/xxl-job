/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.entity;


import com.hlframe.common.persistence.DataEntity;
import com.hlframe.common.utils.excel.annotation.ExcelField;

import java.util.Date;

/**
 * 调度任务下次执行时间Entity
 * @author hladmin
 * @version 2016-11-27
 */
public class DcTaskLogNext extends DataEntity<DcTaskLogNext> {
	
	private static final long serialVersionUID = 1L;
	private String taskid;		// 调度主键
	private Date nexttime;		// 下次执行时间
	
	public DcTaskLogNext() {
		super();
	}

	public DcTaskLogNext(String id){
		super(id);
	}

	@ExcelField(title="调度主键", align=2, sort=1)
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	@ExcelField(title="下次执行时间", align=2, sort=2)
	public Date getNexttime() {
		return nexttime;
	}

	public void setNexttime(Date nexttime) {
		this.nexttime = nexttime;
	}
	
}