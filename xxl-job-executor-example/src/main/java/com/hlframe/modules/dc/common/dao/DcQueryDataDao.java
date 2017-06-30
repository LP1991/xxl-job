/********************** 版权声明 *************************
 * 文件名: TraceParamDao.java
 * 包名: com.hlframe.modules.biz.dao
 * 版权:	杭州华量软件  hlframe
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年5月31日 下午4:34:57
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.common.dao;

import com.hlframe.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @类名: com.hlframe.modules.dc.common.dao.DcQueryDataDao.java 
 * @职责说明: 通用查询数据对象DAO
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午4:44:13
 */
@MyBatisDao
public interface DcQueryDataDao {

	/**
	 * 根据动态字段/条件/排序 查询对象列表  -peijd
	 * @param metaSql 动态SQL
	 * @return
	 */
	public List<Map<String, Object>> queryMetaDataList(@Param(value = "metaSql") String metaSql);

	/**
	 * @方法名称: getFieldByField
	 * @实现功能: 根据某个字段获取另一个表中的某个字段
	 * @param metaSql
	 * @return
	 * @create by cdd at 2016年11月23日 下午4:10:16
	 */
	public String getFieldByField(@Param(value = "metaSql") String metaSql);

	/**
	 * @方法名称: delDataByField
	 * @实现功能: 根据某个字段删除某个表里的数据
	 * @param metaSql
	 * @create by cdd at 2017年1月19日 下午2:05:28
	 */
	public void delDataByField(@Param(value = "metaSql") String metaSql);

	/**
	 * @方法名称: getFieldByManyField
	 * @实现功能: 根据多个字段得到某个表里的数据
	 * @param metaSql
	 * @create by cdd at 2017年1月19日 下午2:05:28
	 */
	public String getFieldByManyField(@Param(value = "metaSql") String metaSql);

	/**
	 * @方法名称: updateFieldByField
	 * @实现功能: 根据多个字段更新某个表里的数据
	 * @param metaSql
	 * @create by cdd at 2017年1月19日 下午2:05:28
	 */
	public void updateFieldByField(@Param(value = "metaSql") String metaSql);

}
