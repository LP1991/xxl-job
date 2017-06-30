/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFileHdfs;

/**
 * 采集传输文件DAO接口
 * @author phy
 * @version 2016-11-23
 */
@MyBatisDao
public interface DcJobTransFileHdfsDao extends CrudDao<DcJobTransFileHdfs> {

	void updateNoPakey(DcJobTransFileHdfs dcJobTransFileHdfs);
}