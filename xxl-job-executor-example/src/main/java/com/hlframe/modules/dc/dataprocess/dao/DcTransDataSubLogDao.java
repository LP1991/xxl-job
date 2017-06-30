/********************** 版权声明 *************************
 * 文件名: DcTransDataSubLogDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:41:49
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSubLog;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcTransDataSubLogDao.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:41:49
 */
@MyBatisDao
public interface DcTransDataSubLogDao extends CrudDao<DcTransDataSubLog> {

	/**
	 * @方法名称: getLatestProLog 
	 * @实现功能: 获取处理过程最近日志
	 * @param subId
	 * @return
	 * @create by peijd at 2016年12月3日 下午3:42:40
	 */
	DcTransDataSubLog getLatestProLog(String subId);

}
