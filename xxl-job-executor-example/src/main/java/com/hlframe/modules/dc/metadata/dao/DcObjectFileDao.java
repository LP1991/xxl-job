/********************** 版权声明 *************************
 * 文件名: DcObjectFileDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午11:23:58
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectFile;
import com.hlframe.modules.dc.metadata.entity.DcObjectFileInfo;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectFileDao.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午11:23:58
 */
@MyBatisDao
public interface DcObjectFileDao extends CrudDao<DcObjectFile> {
	
	public List<Map<String, Object>> getByFileBelong(@Param(value = "fileBelong") String fileBelong);

	/**
	 * @方法名称: deleteByFileBelong
	 * @实现功能: 按照文件归属删除
	 * @param dcObjectFile
	 * @create by yuzh at 2016年12月6日 上午11:02:46
	 */
	public void deleteByFileBelong(DcObjectFile dcObjectFile);

	/**
	 * @方法名称: deleteName
	 * @实现功能: 删除所属路径记录
	 * @param fileUrl
	 * @return
	 * @create by hgw at 2016年11月19日 下午2:04:18
	 */
	void deleteName(@Param(value = "fileUrl") String fileUrl);

    List<Map<String,Object>> getFiles4es(DcObjectMain obj);
	/**
	 * @方法名称: buildFileInfoList
	 * @实现功能: TODO
	 * @param param
	 * @return
	 * @create by peijd at 2017年3月8日 下午1:37:02
	 */
	public List<DcObjectFileInfo> buildFileInfoList(DcObjectFileInfo param);

	/**
	 * @方法名称: buildFileInfo
	 * @实现功能: TODO
	 * @param id
	 * @return
	 * @create by peijd at 2017年3月8日 下午1:37:19
	 */
	public DcObjectFileInfo buildFileInfo(String id);
/*
查询文件和文件夹内容
 */
	List<DcObjectFile> dcobjectlist(DcObjectFile qdd);
/*
获取文件文件夹的单条数据内容
 */
	DcObjectFile getdc(String tid);


}
