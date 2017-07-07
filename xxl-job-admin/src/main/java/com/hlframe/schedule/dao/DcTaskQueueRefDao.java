/********************** 版权声明 *************************
 * 文件名: DcTaskQueueRefDao.java
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
import com.hlframe.schedule.entity.DcTaskQueueRef;


import java.util.List;


@MyBatisDao
public interface DcTaskQueueRefDao extends CrudDao<DcTaskQueueRef> {

	/**
	 * @方法名称: getTaskInfo 
	 * @实现功能: 获取任务信息
	 * @param obj
	 * @return
	 * @create by peijd at 2017年3月1日 下午7:04:38
	 */
	DcTaskQueueRef getTaskInfo(DcTaskQueueRef obj);
	
	/**
	 * @方法名称: findQueueTaskList 
	 * @实现功能: 根据任务Id, 获取队列任务列表 
	 * @param obj
	 * @return
	 * @create by peijd at 2017年3月10日 下午4:24:32
	 */
	List<DcTaskQueueRef> findQueueTaskList(DcTaskQueueRef obj);

}
