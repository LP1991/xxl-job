/********************** 版权声明 *************************
 * 文件名: DcObjectCataRefDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月7日 下午6:52:34
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectCataRefDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月7日 下午6:52:34
 */
@MyBatisDao
public interface DcObjectCataRefDao extends CrudDao<DcObjectCataRef> {
	
	void batchInsert(List<DcObjectCataRef> DcObjectCataRef);

	DcObjectCataRef quanglist(String cataId);
}


