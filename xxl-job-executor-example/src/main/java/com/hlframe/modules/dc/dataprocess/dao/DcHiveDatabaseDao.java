/********************** 版权声明 *************************
 * 文件名: DcHiveDatabaseDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2017年1月10日 下午4:12:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveDatabase;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcHiveDatabaseDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2017年1月10日 下午4:12:22
 */
@MyBatisDao
public interface DcHiveDatabaseDao extends CrudDao<DcHiveDatabase>{
	List<DcHiveDatabase> getHiveDatabase();//获得数据库名
	
	/*void ByDatabaseDelete(DcHiveDatabase dcHiveDatabase);*/
	
	DcHiveDatabase byDatabaseGet(DcHiveDatabase DcHiveDatabase);
	
	/**
	 * 
	 * @方法名称: getDatabaseName 
	 * @实现功能: 获取数据库名称
	 * @param database
	 * @return
	 * @create by hgw at 2017年4月12日 下午3:07:20
	 */
	Object getDatabaseName(String database);
}
