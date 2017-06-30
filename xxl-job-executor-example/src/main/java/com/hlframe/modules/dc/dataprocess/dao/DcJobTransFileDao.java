/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFile;

/**
 * 采集传输文件DAO接口
 * @author phy
 * @version 2016-11-23
 */
@MyBatisDao
public interface DcJobTransFileDao extends CrudDao<DcJobTransFile> {

	/**
	 * @方法名称: getJobName 
	 * @实现功能: TODO
	 * @param jobName
	 * @return
	 * @create by yuzh at 2016年11月25日 下午4:35:19
	 */
	Object getJobName(String jobname);
}