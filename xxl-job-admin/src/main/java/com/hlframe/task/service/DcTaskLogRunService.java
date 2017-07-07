/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.task.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.task.dao.DcTaskLogRunDao;
import com.hlframe.task.entity.DcTaskLogRun;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 调度子任务执行情况Service
 * @author hladmin
 * @version 2016-11-27
 */
@Service
@Transactional(readOnly = true)
public class DcTaskLogRunService extends CrudService<DcTaskLogRunDao, DcTaskLogRun> {

	public DcTaskLogRun get(String id) {
		return super.get(id);
	}
	
	public List<DcTaskLogRun> findList(DcTaskLogRun dcTaskLogRun) {
		return super.findList(dcTaskLogRun);
	}
	
	public Page<DcTaskLogRun> findPage(Page<DcTaskLogRun> page, DcTaskLogRun dcTaskLogRun) {
		return super.findPage(page, dcTaskLogRun);
	}

	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public void save(DcTaskLogRun dcTaskLogRun) {
		//备注字段长度设置 
		String remark = dcTaskLogRun.getRemarks();
		if(StringUtils.isNotBlank(remark) && remark.length()>800){
			dcTaskLogRun.setRemarks(remark.substring(0, 800)+"...");
		}
		if (dcTaskLogRun.getIsNewRecord()) {
			dao.insert(dcTaskLogRun);
		}else{
			dao.update(dcTaskLogRun);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(DcTaskLogRun dcTaskLogRun) {
		super.delete(dcTaskLogRun);
	}
	
}