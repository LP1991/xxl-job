/********************** 版权声明 *************************
 * 文件名: DcObjectTableDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午11:29:28
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcObjectTable;
import com.hlframe.modules.dc.metadata.entity.DcObjectTableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectTableDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午11:29:28
 */
@MyBatisDao
public interface DcObjectTableDao extends CrudDao<DcObjectTable> {

	/**
	 * @方法名称: buildTableInfoList
	 * @实现功能: TODO
	 * @param param
	 * @return
	 * @create by peijd at 2017年3月8日 下午1:22:20
	 */
	List<DcObjectTableInfo> buildTableInfoList(DcObjectTableInfo param);

	/**
	 * @方法名称: buildTableInfo
	 * @实现功能: TODO
	 * @param id	对象Id
	 * @return
	 * @create by peijd at 2017年3月8日 下午1:22:40
	 */
	DcObjectTableInfo buildTableInfo(String id);

    List<Map<String,Object>> getTables4es(DcObjectMain obj);
	/**
	 * @方法名称:byNameToId
	 * @实现功能：根据表名数据库获取id
	 * @param  表名 数据库
	 * @create by hgw at 2017年3月9日  晚上7:55:40
	 */
	DcObjectTable byNameToId(DcObjectTable dcObjectTable);

	/**
	 * @方法名称:byNameToId
	 * @实现功能：批量插入
	 * @param  List<DcObjectTable>
	 * @create by hgw at 2017年3月9日  晚上7:55:40
	 */
	void batchInsert(List<DcObjectTable> list);

	/**
	 * @方法名称:byNameToId
	 * @实现功能：根据数据库获取id
	 * @param  数据库
	 * @create by hgw at 2017年3月9日  晚上7:55:40
	 */
	public List<Map<String, Object>> getByDbNameId(@Param(value = "dbDataBase") String dbDataBase);
	
	/**
	 * 
	 * @方法名称: findTablePage 
	 * @实现功能: 查询元数据和表信息分页信息
	 * @param dcObjectTable
	 * @return
	 * @create by hgw at 2017年5月8日 上午10:53:00
	 */
	public List<DcObjectTable> findTableList(DcObjectTable dcObjectTable);
	


	/**
	 * 查字段详细详情
	 * @param id
	 * @return
	 * @create by hgw at 2017年5月8日 上午10:53:00
	 */
	public DcObjectTable tnadmin(@Param(value = "id") String id);

	/**
	 *
	 * @方法名称: findTablePage
	 * @实现功能: 查询单条关联信息
	 * @param getTable
	 * @return
	 * @create by hgw at 2017年5月8日 上午10:53:00
	 */
	public DcObjectTable getTable(DcObjectTable dcObjectTable);
}
