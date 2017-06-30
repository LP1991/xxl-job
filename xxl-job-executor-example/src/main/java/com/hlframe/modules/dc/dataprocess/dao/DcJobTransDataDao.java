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
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransData;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcJobTransDataDao.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午8:55:36
 */
@MyBatisDao
public interface DcJobTransDataDao extends CrudDao<DcJobTransData> {

	

	/**
	 * @方法名称: buildJobData 
	 * @实现功能: 构建 Db2Hdfs存储对象
	 * @return
	 * @create by peijd at 2016年11月17日 下午2:33:50
	 */
	DcJobDb2Hdfs buildJobData(String id);

	/**
	 * @方法名称: getJobName 
	 * @实现功能: TODO
	 * @param jobName
	 * @return
	 * @create by yuzh at 2016年11月25日 下午1:52:18
	 */
	Object getJobName(String jobName);

	/**
	 * @方法名称: buildList 
	 * @实现功能: 构建对象列表
	 * @param obj
	 * @return
	 * @create by peijd at 2016年12月5日 下午5:35:22
	 */
	List<DcJobDb2Hdfs> buildList(DcJobDb2Hdfs obj);


}
