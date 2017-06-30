package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveField;

import java.util.List;
import java.util.Map;

/**
 * @类名: com.hlframe.modules.dc.dataprocess.dao.DcHiveFieldDao.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2017年1月11日 上午9:32:13
 */
@MyBatisDao
public interface DcHiveFieldDao extends CrudDao<DcHiveField> {
/**
 * @方法名称: countByBelong2Id 
 * @实现功能: 根据belong2Id 查看此表在字段表里是否有数据
 * @param belong2Id
 * @return
 * @create by cdd at 2017年1月12日 下午7:35:04
 */
	String countByBelong2Id(String belong2Id);

	/**
	 * @方法名称: getFieldNameAndType 
	 * @实现功能: 根据belong2Id获得字段名称及类型
	 * @param belong2Id
	 * @return
	 * @create by cdd at 2017年1月12日 下午7:38:41
	 */
List<Map<String, Object>> getFieldNameAndType(String belong2Id);

/**
 * @方法名称: deleteByBelong2Id 
 * @实现功能: 根据belong2Id删除字段表里的数据
 * @param belong2Id
 * @create by cdd at 2017年1月24日 上午11:32:30
 */
public void deleteByBelong2Id(String belong2Id);

/**
 * @方法名称: batchInsert 
 * @实现功能: 批量插入数据
 * @param list
 * @return
 * @create by cdd at 2017年1月24日 上午11:33:36
 */
int batchInsert(List<DcHiveField> list);//批量插入数据

}
