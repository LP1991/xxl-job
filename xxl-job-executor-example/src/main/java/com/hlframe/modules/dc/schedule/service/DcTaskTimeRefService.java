/********************** 版权声明 *************************
 * 文件名: DcTaskMainService.java
 * 包名: com.hlframe.modules.dc.schedule.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：cdd  创建时间：2016年11月14日 下午2:30:10
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.schedule.dao.DcTaskTimeRefDao;
import com.hlframe.modules.dc.schedule.entity.DcTaskTimeRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * @类名: com.hlframe.modules.dc.schedule.service.DcTaskMainService.java 
 * @职责说明: TODO
 * @创建者: cdd
 * @创建时间: 2016年11月14日 下午2:30:10
 */
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
