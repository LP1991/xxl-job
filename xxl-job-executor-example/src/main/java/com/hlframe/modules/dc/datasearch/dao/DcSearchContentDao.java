/********************** 版权声明 *************************
 * 文件名: DcSearchContentDao.java
 * 包名: com.hlframe.modules.dc.datasearch.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年11月8日 上午10:05:19
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.datasearch.dao;

import com.hlframe.common.persistence.TreeDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.datasearch.entity.DcSearchContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.datasearch.dao.DcSearchContentDao.java 
 * @职责说明: 分类明细树用户dao
 * @创建者: yuzh
 * @创建时间: 2016年11月8日 上午10:05:19
 */
	@MyBatisDao
	public interface DcSearchContentDao extends TreeDao<DcSearchContent> {

	/**
	 * @方法名称: geUnmark 
	 * @实现功能: TODO
	 * @return
	 * @create by yuzh at 2016年12月8日 上午10:54:19
	 * modify by Primo 添加根据ID查询功能 2017.03.16
	 */
	List<DcSearchContent> getUnmark(@Param("id") String id);

	/**
	 * @方法名称: getCatalogRefById
	 * @实现功能: TODO
	 * @return
	 * @create by primo at 2017年3月15日
	 */
	List<String> getCatalogRefById(String id);
	/**
	 * @方法名称: findUniqueByPr
	 * @实现功能:取出源数据分类中的分类明细的单条数据的方法
	 * @create bao gang 2017年4月13日 上午17:52:47
	 */
	
	public DcSearchContent findUniqueByPr(@Param(value = "propertyName") String propertyName, @Param(value = "value") Object value, @Param(value = "cataItemId") String cataItemId);



	}


