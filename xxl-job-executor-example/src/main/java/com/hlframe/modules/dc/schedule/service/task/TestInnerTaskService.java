/********************** 版权声明 *************************
 * 文件名: TestTaskService.java
 * 包名: pjd
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月16日 下午6:10:15
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.service.task;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Service;

/** 
 * @类名: com.hlframe.modules.dc.schedule.service.task.TestInnerTaskService.java 
 * @职责说明: 调度任务内部类测试
 * @创建者: peijd
 * @创建时间: 2017年2月16日 下午6:10:15
 */
@Service
public class TestInnerTaskService  implements DcTaskService {

	/**
	 * Override
	 * @方法名称: runTask 
	 * @实现功能: 执行任务
	 * @param taskId 参数信息
	 * @return
	 * @throws Exception
	 * @create by peijd at 2017年2月16日 下午6:10:56
	 */
	@SuppressWarnings("static-access")
	@Override
	public DcDataResult runTask(String taskId) throws Exception {
		System.out.println("--->begin runTask[TestInnerTaskService] param("+taskId+"): "+DateUtils.getDateTime());
		//当前任务执行10秒 
		Thread.currentThread().sleep(10000);
		DcDataResult result = new DcDataResult();
		result.setRst_flag(true);
		result.setRst_std_msg("测试任务:TestTaskService"+DateUtils.getDateTime());
		System.out.println("--->finish runTask[TestInnerTaskService] param("+taskId+"): "+result.getRst_std_msg());
		return result;
	}

}
