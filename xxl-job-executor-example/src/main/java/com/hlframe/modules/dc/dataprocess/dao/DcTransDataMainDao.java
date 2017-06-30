/********************** 版权声明 *************************
 * 文件名: DcTransDataMainDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:38:19
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataMain;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcTransDataMainDao.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:38:19
 */
@MyBatisDao
public interface DcTransDataMainDao extends CrudDao<DcTransDataMain> {

	/**
	 * @方法名称: getJobName 
	 * @实现功能: 根据名称获取对象 
	 * @param jobName
	 * @return
	 * @create by peijd at 2016年11月30日 下午8:15:04
	 */
	DcTransDataMain getJobName(String jobName);

}
