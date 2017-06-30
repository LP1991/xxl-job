/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobHdfslog;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFileHdfs;

/**
 * 采集传输文件DAO接口
 * @author phy
 * @version 2016-11-23
 */
@MyBatisDao
public interface DcJobHdfslogDao extends CrudDao<DcJobHdfslog> {

	void update(DcJobTransFileHdfs dcJobTransFileHdfs);

	/**
	 * @方法名称: getFullPath 
	 * @实现功能: TODO
	 * @param dcJobHdfslog
	 * @return
	 * @create by yuzh at 2016年11月24日 下午7:23:37
	 */
	DcJobHdfslog getFullPath(DcJobHdfslog dcJobHdfslog);
}