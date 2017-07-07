/********************** 版权声明 *************************
 * 文件名: DcTaskLogDao.java
 * 包名: com.hlframe.schedule.dao
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.dao;


import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.schedule.entity.DcTaskLog;


@MyBatisDao
public interface DcTaskLogDao extends CrudDao<DcTaskLog> {

	/**
	 * @方法名称: getDataByName 
	 * @实现功能: TODO
	 * @param className
	 * @return
	 * @create by cdd at 2016年11月21日 下午3:01:44
	 */
	DcTaskLog getDataByName(String className);

	/**
	 * @方法名称: getData 
	 * @实现功能: TODO
	 * @return
	 * @create by cdd at 2016年11月21日 下午4:55:48
	 */
	DcTaskLog getData();


}
