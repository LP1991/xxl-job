/********************** 版权声明 *************************
 * 文件名: DcTaskMainService.java
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

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.dataprocess.entity.*;
import com.hlframe.modules.dc.dataprocess.service.*;
import com.hlframe.schedule.dao.DcTaskMainDao;
import com.hlframe.schedule.entity.DcTaskMain;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DcTaskMainService extends CrudService<DcTaskMainDao, DcTaskMain> {

	/**
	 * @实现功能: 数据权限过滤
	 * @create by yuzh at 2016年12月15日 15:30:29
	 */
	public Page<DcTaskMain> findPage(Page<DcTaskMain> page, DcTaskMain dcTaskMain) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcTaskMain.getSqlMap().put("dsf", dataScopeFilter(dcTaskMain.getCurrentUser(),"o","u"));
		// 设置分页参数
		dcTaskMain.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(dcTaskMain));
		return super.findPage(page, dcTaskMain);
	}

	/**
	 * @方法名称: getTaskName 
	 * @实现功能: TODO
	 * @param taskName
	 * @return
	 * @create by cdd at 2016年11月15日 下午4:56:27
	 */
	public DcTaskMain getTaskName(String taskName) {
		return dao.getTaskName(taskName);
	}


	/**
	 * @方法名称: insert 
	 * @实现功能: 新增调度任务   这里需要指定Id, 不能用平台默认的save方法
	 * @param taskMain
	 * @create by peijd at 2016年12月5日 下午3:19:43
	 */
	@Transactional(readOnly = true)
	public void insert(DcTaskMain taskMain) {
		Assert.notNull(taskMain);
		//设置为新纪录
		taskMain.setIsNewRecord(true);
		taskMain.preInsert();
		Assert.isTrue(dao.insert(taskMain)>0);
	}

}
