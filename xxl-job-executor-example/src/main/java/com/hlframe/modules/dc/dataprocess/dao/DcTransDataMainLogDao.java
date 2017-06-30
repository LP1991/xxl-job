/********************** 版权声明 *************************
 * 文件名: DcTransDataMainLogDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:41:13
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataMainLog;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcTransDataMainLogDao.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:41:13
 */
@MyBatisDao
public interface DcTransDataMainLogDao extends CrudDao<DcTransDataMainLog> {

	/**
	 * @方法名称: getLatestTaskLog 
	 * @实现功能: 获取最近的处理任务日志
	 * @param jobId
	 * @return
	 * @create by peijd at 2016年12月3日 下午6:12:14
	 */
	DcTransDataMainLog getLatestTaskLog(String jobId);

}
