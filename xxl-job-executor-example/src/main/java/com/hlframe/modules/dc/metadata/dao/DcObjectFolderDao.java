/********************** 版权声明 *************************
 * 文件名: DcObjectFolderDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午11:25:32
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectFileInfo;
import com.hlframe.modules.dc.metadata.entity.DcObjectFolder;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectFolderDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午11:25:32
 */
@MyBatisDao
public interface DcObjectFolderDao extends CrudDao<DcObjectFolder> {

	/*根据folderurl判断文件夹是否存在*/
	public DcObjectFolder getFolderUrl(DcObjectFolder dcObjectFolder);
	
	/**
	 * @方法名称: deleteName 
	 * @实现功能: 删除所属路径记录
	 * @param folderName
	 * @return
	 * @create by hgw at 2016年11月19日 下午2:04:18
	 */
	void deleteName(@Param(value = "folderName") String folderName);

	/**
	 * @name: getFolders4es
	 * @funciton:
	 * @param
	 * @return
	 * @Create by lp at 2017/3/23 11:36
	 * @throws
	 */
	List<Map<String, Object>> getFolders4es(DcObjectMain obj);

	/**
	 * @方法名称: buildFileInfo
	 * @实现功能: TODO获取文件夹对象
	 * @param id
	 * @return
	 * @create by peijd at 2017年3月8日 下午1:37:19
	 */
	public DcObjectFileInfo buildFolderInfo(String id);
}
