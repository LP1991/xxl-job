/********************** 版权声明 *************************
 * 文件名: DcTaskTimeDao.java
 * 包名: com.hlframe.modules.dc.schedule.dao
 * 版权:	杭州华量软件  hldc
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月15日 下午6:47:54
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.schedule.entity.DcTaskTimeRef;

/** 
 * @类名: com.hlframe.modules.dc.schedule.dao.DcTaskTimeDao.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月15日 下午6:47:54
 */
@MyBatisDao
public interface DcTaskTimeRefDao extends CrudDao<DcTaskTimeRef>{
	public DcTaskTimeRef getDataByClassName(String className);
}
