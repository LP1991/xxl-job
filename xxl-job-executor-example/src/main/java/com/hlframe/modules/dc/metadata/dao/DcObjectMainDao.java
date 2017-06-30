/********************** 版权声明 *************************
 * 文件名: DcDataSourceDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月7日 下午1:40:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectMainDao.java
 * @职责说明: 元数据对象
 * @创建者: peijd
 * @创建时间: 2016年11月8日 下午2:38:52
 */
@MyBatisDao
public interface DcObjectMainDao extends CrudDao<DcObjectMain> {

	/**
	 * @方法名称: findListByCata
	 * @实现功能: 根据分类信息查询数据对象
	 * @param obj
	 * @return
	 * @create by peijd at 2016年11月9日 下午2:14:29
	 */
	List<DcObjectMain> findListByCata(DcObjectMain obj);


	/**
	 * @方法名称: deleteName
	 * @实现功能: 删除所属路径记录
	 * @param objName
	 * @return
	 * @create by hgw at 2016年11月19日 下午2:04:18
	 */
	void deleteName(@Param(value = "objName") String objName);

	void batchInsert(List<DcObjectMain> dataList);

	/**
	 * @name: getCataInfo2EsById @funciton: use for update obj's catalog to
	 * es @param @return @Create by lp at 2017/3/14 20:08 @throws
	 */
	List<Map<String, Object>> getCataInfo2EsById(@Param(value = "objId") String objId);

	List<Map<String, Object>> queryMetaBySql(@Param(value = "metaSql") String sql);
	/*
	 * 查看我申请的信息
	 */

	List<DcObjectMain> quanglist(DcObjectMain qid);
	/*
	 *查看我的收藏 
	 */
	
	List<DcObjectMain> shochanlist(DcObjectMain sid);
	/*
	 * 查字段详细详情
	 */
	DcObjectMain tnadmin(String tid);

	DcObjectMain getById(String id);

}

