package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveTable;

import java.util.List;

/**
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcHiveTableDao.java 
 * @职责说明: DcHiveTable的dao层
 * @创建者: cdd
 * @创建时间: 2017年1月24日 上午11:34:07
 */
@MyBatisDao
public interface DcHiveTableDao extends CrudDao<DcHiveTable> {

	Object getTableName(String tableName);//获取表名

	int batchInsert(List<DcHiveTable> dataList);//批量插入数据

}
