/********************** 版权声明 *************************
 * 文件名: DcObjectLinkDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月10日 上午10:23:08
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectLinkDao.java 
 * @职责说明: 对象的链路关系 DAO
 * @创建者: peijd
 * @创建时间: 2016年12月10日 上午10:23:08
 */
@MyBatisDao
public interface DcObjectLinkDao extends CrudDao<DcObjectLink> {

	/**
	 * @方法名称: deleteByProcessId 
	 * @实现功能: 根据处理任务删除链路关系
	 * @param processId	处理任务Id
	 * @return
	 * @create by peijd at 2016年12月10日 上午11:13:21
	 */
	int deleteByProcessId(String processId);

	/**
	 * @方法名称: batchInsert 
	 * @实现功能: 批量插入转换过程的链路关系
	 * @param linkList
	 * @create by peijd at 2016年12月10日 上午11:27:38
	 */
	int batchInsert(List<DcObjectLink> linkList);

	/**
	 * @方法名称: getByParam 
	 * @实现功能: 根据参数获取对象
	 * @param param
	 * @return
	 * @create by peijd at 2016年12月17日 上午10:38:05
	 */
	DcObjectLink getByParam(DcObjectLink param);

	/**
	 * @方法名称: getcountByObjId 
	 * @实现功能: 根据源对象Id和目标对象Id判断此条数据是否存在
	 * @param srcObjId
	 * @param tarObjId
	 * @return
	 * @create by cdd at 2017年1月3日 上午9:35:44
	 */
	String getcountByObjId(String srcObjId, String tarObjId);

	DcObjectLink getById(String id);
}
