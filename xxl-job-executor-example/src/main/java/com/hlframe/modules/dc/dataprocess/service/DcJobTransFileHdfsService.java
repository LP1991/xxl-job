/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransFileHdfsDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFileHdfs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 采集传输文件Service
 * @author phy
 * @version 2016-11-23
 */
@Service
@Transactional(readOnly = true)
public class DcJobTransFileHdfsService extends CrudService<DcJobTransFileHdfsDao, DcJobTransFileHdfs> {


	@Autowired
	private DcJobTransFileHdfsDao dcJobTransFileHdfsDao;
	@Transactional(readOnly = false)
	public void update(DcJobTransFileHdfs dcJobTransFileHdfs) {
		dcJobTransFileHdfsDao.update(dcJobTransFileHdfs);
		
	}
}