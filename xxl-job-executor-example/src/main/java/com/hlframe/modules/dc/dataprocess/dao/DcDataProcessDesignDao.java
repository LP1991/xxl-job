/********************** 版权声明 *************************
 * 文件名: DcDataProcessDesignDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月21日 上午9:31:27
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcDataProcessDesign;
import org.apache.ibatis.annotations.Param;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcDataProcessDesignDao.java 
 * @职责说明: 数据转换过程设计 Dao
 * @创建者: peijd
 * @创建时间: 2017年2月21日 上午9:31:27
 */
@MyBatisDao
public interface DcDataProcessDesignDao extends CrudDao<DcDataProcessDesign> {

	/**
	 * @方法名称: getDesignByName 
	 * @实现功能: 根据名称获取对象, 主要用于重名验证
	 * @param designName	
	 * @param id
	 * @return	
	 * @create by peijd at 2017年2月21日 上午9:43:04
	 */
	DcDataProcessDesign getDesignByName(@Param("designName") String designName, @Param("id") String id);

}
