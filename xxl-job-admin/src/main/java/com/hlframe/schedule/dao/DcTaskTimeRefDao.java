/********************** 版权声明 *************************
 * 文件名: DcTaskTimeRefDao.java
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
import com.hlframe.schedule.entity.DcTaskTimeRef;

@MyBatisDao
public interface DcTaskTimeRefDao extends CrudDao<DcTaskTimeRef>{
	public DcTaskTimeRef getDataByClassName(String className);
}
