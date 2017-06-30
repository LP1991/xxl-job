/********************** 版权声明 *************************
 * 文件名: DcJobTransData.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午8:55:36
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobDb2Hdfs;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransHdfs;

/**
 * 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcJobTransHdfsDao.java
 * @职责说明: Hdfs处理dao
 * @创建者: cdd
 * @创建时间: 2016年11月27日 下午1:07:34
 */
@MyBatisDao
public interface DcJobTransHdfsDao extends CrudDao<DcJobTransHdfs> {

	/**
	 * @方法名称: buildJobData
	 * @实现功能: 构建 Db2Hdfs存储对象
	 * @return
	 * @create by peijd at 2016年11月17日 下午2:33:50
	 */
	DcJobDb2Hdfs buildJobData(String id);

	/**
	 * 
	 * @方法名称: getJobName
	 * @实现功能: 获得任务名称
	 * @param jobName
	 * @return
	 * @create by cdd at 2016年11月27日 下午1:08:17
	 */
	Object getJobName(String jobName);

}
