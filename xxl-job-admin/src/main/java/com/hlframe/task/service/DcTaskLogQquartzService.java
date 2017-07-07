/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.task.dao.DcTaskLogQquartzDao;
import com.hlframe.task.entity.DcTaskLogQquartz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 调度日志Service
 * @author hladmin
 * @version 2016-11-27
 */
@Service
@Transactional(readOnly = true)
public class DcTaskLogQquartzService extends CrudService<DcTaskLogQquartzDao, DcTaskLogQquartz> {

	public DcTaskLogQquartz get(String id) {
		return super.get(id);
	}
	
	public List<DcTaskLogQquartz> findList(DcTaskLogQquartz dcTaskLogQquartz) {
		return super.findList(dcTaskLogQquartz);
	}
	
	public Page<DcTaskLogQquartz> findPage(Page<DcTaskLogQquartz> page, DcTaskLogQquartz dcTaskLogQquartz) {
		return super.findPage(page, dcTaskLogQquartz);
	}
	
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public void save(DcTaskLogQquartz dcTaskLogQquartz) {
		if (dcTaskLogQquartz.getIsNewRecord()) {
			dao.insert(dcTaskLogQquartz);
		}else{
			dao.update(dcTaskLogQquartz);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(DcTaskLogQquartz dcTaskLogQquartz) {
		super.delete(dcTaskLogQquartz);
	}
	
}