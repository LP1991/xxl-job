/********************** 版权声明 *************************
 * 文件名: DcObjectFieldDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午11:22:48
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectFieldDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午11:22:48
 */
@MyBatisDao
public interface DcObjectFieldDao extends CrudDao<DcObjectField> {
	
	public List<Map<String, Object>> getByBelong2Id(@Param(value = "belong2Id") String belong2Id);

	public void batchInsert(List<DcObjectField> fieldList);//批量加入数据

	/**
	 * @方法名称: deleteByBelong2Id
	 * @实现功能: 根据表名 删除字段
	 * @param belong2Id
	 * @create by peijd at 2017年3月31日 下午4:49:58
	 */
	public void deleteByBelong2Id(@Param(value = "belong2Id") String belong2Id);

	public List<DcObjectField> getDB2Id(@Param(value = "belong2Id") String belong2Id);

}
