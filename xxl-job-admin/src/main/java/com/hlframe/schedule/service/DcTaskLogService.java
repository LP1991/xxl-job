/********************** 版权声明 *************************
 * 文件名: DcTaskLogService.java
 * 包名: com.hlframe.schedule.service
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.schedule.dao.DcTaskLogDao;
import com.hlframe.schedule.entity.DcTaskLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DcTaskLogService extends CrudService<DcTaskLogDao, DcTaskLog> {
	private static  DcTaskLogDao dcTaskLogDao = SpringContextHolder.getBean( DcTaskLogDao.class);


	/**
	 * @方法名称: getTaskName 
	 * @实现功能:
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