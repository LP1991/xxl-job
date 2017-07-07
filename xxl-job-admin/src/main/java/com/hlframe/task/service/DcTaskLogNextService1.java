/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.task.dao.DcTaskLogNextDao1;
import com.hlframe.modules.dc.task.entity.DcTaskLogNext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 调度任务下次执行时间Service
 * @author hladmin
 * @version 2016-11-27
 */
@Service
@Transactional(readOnly = true)
public class DcTaskLogNextService1 extends CrudService<DcTaskLogNextDao1, DcTaskLogNext> {

	public DcTaskLogNext get(String id) {
		return super.get(id);
	}
	
	public List<DcTaskLogNext> findList(DcTaskLogNext dcTaskLogNext) {
		return super.findList(dcTaskLogNext);
	}
	
	public Page<DcTaskLogNext> findPage(Page<DcTaskLogNext> page, DcTaskLogNext dcTaskLogNext) {
		return super.findPage(page, dcTaskLogNext);
	}

	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public void save(DcTaskLogNext dcTaskLogNext) {
		DcTaskLogNext dcTaskLogNextOld = this.get(dcTaskLogNext);
		if (dcTaskLogNextOld == null) {
			dao.insert(dcTaskLogNext);
		} else {
			dao.update(dcTaskLogNext);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(DcTaskLogNext dcTaskLogNext) {
		super.delete(dcTaskLogNext);
	}
	
}