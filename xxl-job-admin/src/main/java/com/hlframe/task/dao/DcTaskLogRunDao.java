/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.task.entity.DcTaskLogRun;

/**
 * 调度子任务执行情况DAO接口
 * @author hladmin
 * @version 2016-11-27
 */
@MyBatisDao
public interface DcTaskLogRunDao extends CrudDao<DcTaskLogRun> {
}