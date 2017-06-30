/********************** 版权声明 *************************
 * 文件名: DcJobExportDataDao.java
 * 包名: com.hlframe.modules.dc.dataexport.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月23日 下午5:01:37
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataexport.entity.DcJobExportData;
import org.apache.ibatis.annotations.Param;

/** 
 * @类名: com.hlframe.modules.dc.dataexport.dao.DcJobExportDataDao.java 
 * @职责说明: 数据导出Dao
 * @创建者: peijd
 * @创建时间: 2017年2月23日 下午5:01:37
 */
@MyBatisDao
public interface DcJobExportDataDao extends CrudDao<DcJobExportData> {

	/**
	 * @方法名称: getObjByName 
	 * @实现功能: TODO
	 * @param jobName
	 * @return
	 * @create by peijd at 2017年2月23日 下午5:18:07
	 */
	DcJobExportData getObjByName(@Param("jobName") String jobName, @Param("id") String jobId);

}
