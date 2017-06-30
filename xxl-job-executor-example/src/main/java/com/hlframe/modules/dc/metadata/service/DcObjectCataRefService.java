/********************** 版权声明 *************************
 * 文件名: DcObjectCataRefService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月7日 下午7:00:05
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.metadata.dao.DcObjectCataRefDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.DcObjectCataRefService.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2016年12月7日 下午7:00:05
 */
 @Service
 @Transactional(readOnly = false)
public class DcObjectCataRefService extends CrudService<DcObjectCataRefDao , DcObjectCataRef> {

	@Autowired
	private DcObjectMainDao dcObjectMainDao;

	/**
	 * * modify By lp at 2017年3月7日 下午7:25:05 添加同步到Es的信息
	 *
	 * @param cataId update by YuZH at batchInsert
	 */
	public void firstInsert(String cataId) {
		List<DcObjectMain> dcObjectMain = dcObjectMainDao.findAllList(new DcObjectMain());
		List<DcObjectCataRef> refList = new ArrayList<DcObjectCataRef>();//新建批量添加list by yzh
		for (int i = 0; i < dcObjectMain.size(); i++) {
			DcObjectMain o = dcObjectMain.get(i);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(o.getId());
			dcObjectCataRef.setCataId(cataId);
			refList.add(dcObjectCataRef);//添加对象 by yzh
		}
		dao.batchInsert(refList);//调用批量新增方法 by yzh
	}

	/**
	 * @param dcObjectCataRef
	 * @方法名称: update
	 * @实现功能: TODO
	 * @create by yuzh at 2016年12月8日 下午7:25:05
	 */
	public void update(DcObjectCataRef dcObjectCataRef) {
		dao.update(dcObjectCataRef);
	}


	/**
	 * @方法名称: findAll
	 * @实现功能: 查询所有的referance, use for init ES data.
	 * @create by lp at 2017年3月7日 下午7:25:05
	 */
	@Deprecated
	public List<DcObjectCataRef> findAll() {
//Todo		dao.update(dcObjectCataRef);
		return dao.findList(new DcObjectCataRef());
	}


	public DcObjectCataRef quanglist(String cataId) {
		return dao.quanglist(cataId);
	}
}

