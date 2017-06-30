/********************** 版权声明 *************************
 * 文件名: DcTransDataSubDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午2:40:19
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcTransDataSubDao.java 
 * @职责说明: 数据转换任务
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午2:40:19
 */
@MyBatisDao
public interface DcTransDataSubDao extends CrudDao<DcTransDataSub> {

	/**
	 * @方法名称: getJobName 
	 * @实现功能: 获取转换对象
	 * @param proName
	 * @return
	 * @create by peijd at 2016年12月1日 下午2:18:40
	 */
	DcTransDataSub getProName(DcTransDataSub param);

	/**
	 * @方法名称: queryTransNum 
	 * @实现功能: 获取转换任务数
	 * @param jobId
	 * @return
	 * @create by peijd at 2016年12月1日 下午5:06:55
	 */
	int queryTransNum(String jobId);

}
