/********************** 版权声明 *************************
 * 文件名: DcTaskMainDao.java
 * 包名: com.hlframe.modules.dc.schedule.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月14日 下午2:29:15
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.schedule.dao.DcTaskMainDao.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月14日 下午2:29:15
 */
@MyBatisDao
public interface DcTaskMainDao extends CrudDao<DcTaskMain>{

	/**
	 * @方法名称: getTaskName 
	 * @实现功能: TODO
	 * @param taskName
	 * @return
	 * @create by cdd at 2016年11月15日 下午4:59:12
	 */
	public DcTaskMain getTaskName(String taskName);
	/**
	 * @name: getTaskCataAndResource
	 * @funciton:
	 * @param 
	 * @return
	 * @Create by lp at 2017/3/21 17:27
	 * @throws 
	 */
	List<Map<String,Object>> getTaskCataAndResource();

    List<Map<String,Object>> countTaskStatus();
}
