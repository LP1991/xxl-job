/********************** 版权声明 *************************
 * 文件名: DcTaskTimeRefService.java
 * 包名: com.hlframe.schedule.service
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.schedule.dao.DcTaskTimeRefDao;
import com.hlframe.schedule.entity.DcTaskTimeRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DcTaskTimeRefService extends CrudService<DcTaskTimeRefDao, DcTaskTimeRef>{

	/**
	 * @方法名称: getDataByClassName 
	 * @实现功能: TODO
	 * @param className
	 * @return
	 * @create by cdd at 2016年11月18日 下午2:14:33
	 */
	public DcTaskTimeRef getDataByClassName(String className) {
		return dao.getDataByClassName(className);
	}

}
