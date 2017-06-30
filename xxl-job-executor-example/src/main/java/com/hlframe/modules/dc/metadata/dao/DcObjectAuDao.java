
package com.hlframe.modules.dc.metadata.dao;


import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectAu;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcSysObjm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectAuDao.java 
 * @职责说明: dc权限管理用户dao
 * @创建者: yuzh
 * @创建时间: 2016年11月19日 下午3:15:59
 */
@MyBatisDao
public interface DcObjectAuDao extends CrudDao<DcObjectAu> {

	/**
	 * @方法名称: pass 
	 * @实现功能: 通过申请
	 * @param obj
	 * @create by yuzh at 2016年11月19日 下午4:15:08
	 */
	void pass(DcObjectAu obj);

	/**
	 * @方法名称: nopass 
	 * @实现功能: 撤回申请
	 * @param obj
	 * @create by yuzh at 2016年11月19日 下午4:30:40
	 */
	void nopass(DcObjectAu obj);

	/**
	 * @方法名称: getById 
	 * @实现功能: TODO
	 * @param id
	 * @return
	 * @create by yuzh at 2017年1月18日 下午2:09:56
	 */
	DcObjectAu getById(String id);

	/**
	 * @方法名称: getAccreList 
	 * @实现功能: 获取元数据权限列表(根据用户id或者对象Id获取)
	 * @param userId	用户Id
	 * @param fileId	元数据Id
	 * @return
	 * @create by peijd at 2017年2月25日 下午1:22:56
	 */
	List<DcSysObjm> getAccreList(@Param(value = "userId") String userId, @Param(value = "fileId") String fileId);

	/**
	 * @param id
	 * @return
	 */
	List<Map<String, String>> getAccre(@Param(value = "id") String id);
	
	/**
	 * @param dcObjectMain  查看我的数据
	 * @return
	 */
	List<DcObjectMain> queryMyDataList(DcObjectMain dcObjectMain);

	/**
	 * @param dcObjectMain
	 * @return
	 */
	List<DcObjectMain> colleCtDataList(DcObjectMain dcObjectMain);

/*
  获取权限里面的fileId来查询不同表的名称、、
  baog
  2017/6/19
 */
	DcObjectAu Main(String mid);
	DcObjectAu transdata(String tid);
	DcObjectAu transintf(String sid);
	DcObjectAu file(String fid);
	DcObjectAu hdfs(String hid);
	DcObjectAu export(String eid);

}
