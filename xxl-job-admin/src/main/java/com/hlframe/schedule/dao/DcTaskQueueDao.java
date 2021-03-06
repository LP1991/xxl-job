/********************** 版权声明 *************************
 * 文件名: DcTaskQueueDao.java
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
import com.hlframe.schedule.entity.DcTaskQueue;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface DcTaskQueueDao extends CrudDao<DcTaskQueue> {

	/**
	 * @方法名称: getObjByName 
	 * @实现功能: 判断名字是否重复
	 * @param queueName
	 * @param id
	 * @return
	 * @create by peijd at 2017年3月1日 下午2:49:28
	 */
	DcTaskQueue getObjByName(@Param("queueName") String queueName, @Param("id") String id);

}
