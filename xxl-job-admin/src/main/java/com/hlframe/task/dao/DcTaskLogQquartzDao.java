/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.task.entity.DcTaskLogQquartz;

/**
 * 调度日志DAO接口
 * @author hladmin
 * @version 2016-11-27
 */
@MyBatisDao
public interface DcTaskLogQquartzDao extends CrudDao<DcTaskLogQquartz> {
}