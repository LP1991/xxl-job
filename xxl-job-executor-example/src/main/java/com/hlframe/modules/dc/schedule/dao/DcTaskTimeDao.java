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
import com.hlframe.modules.dc.schedule.entity.DcTaskContent;
import com.hlframe.modules.dc.schedule.entity.DcTaskTime;

/** 
 * @类名: com.hlframe.modules.dc.schedule.dao.DcTaskTimeDao.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月15日 下午6:47:54
 */
@MyBatisDao
public interface DcTaskTimeDao extends CrudDao<DcTaskTime>{

	/**
	 * @方法名称: getTaskName 
	 * @实现功能: TODO
	 * @param taskName
	 * @return
	 * @create by cdd at 2016年11月15日 下午4:59:12
	 */
	public DcTaskTime getScheduleName(String scheduleName);
	
	public void updateStatus(DcTaskTime entity);

	/**
	 * 根据业务ID获取对象，临时使用，目前仅允许一个调度和一个业务ID绑定
	 */
	public DcTaskTime getByTaskfromid(DcTaskTime entity);

	/**
	 * 
	 * @方法名称: buildTaskContent 
	 * @实现功能: 组件DcTaskContent对象
	 * @param id
	 * @return
	 * @create by cdd at 2016年11月29日 上午8:49:06
	 */
	public DcTaskContent buildTaskContent(String id);
}
