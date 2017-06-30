/********************** 版权声明 *************************
 * 文件名: DcTaskLogService.java
 * 包名: com.hlframe.modules.dc.schedule.service
 * 版权:	杭州华量软件  hldc
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月21日 上午9:54:51
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.schedule.dao.DcTaskLogDao;
import com.hlframe.modules.dc.schedule.entity.DcTaskLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * @类名: com.hlframe.modules.dc.schedule.service.DcTaskLogService.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月21日 上午9:54:51
 */
@Service
@Transactional(readOnly = true)
public class DcTaskLogService extends CrudService<DcTaskLogDao, DcTaskLog> {
	@Autowired
	private DcTaskLogDao dcTaskLogDao;


	/**
	 * @方法名称: getTaskName 
	 * @实现功能: TODO
	 * @param className
	 * @return
	 * @create by cdd at 2016年11月15日 下午4:56:27
	 */
	public DcTaskLog getDataByName(String className) {
		return dcTaskLogDao.getDataByName(className);
	}
	public DcTaskLog getData() {
		return dcTaskLogDao.getData();
	}

}