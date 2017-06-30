/********************** 版权声明 *************************
 * 文件名: DcTaskService.java
 * 包名: com.hlframe.modules.dc.schedule.service.task
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月16日 下午2:17:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.service.task;

import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

/** 
 * @类名: com.hlframe.modules.dc.schedule.service.task.DcTaskService.java 
 * @职责说明: 数据中心 任务运行接口, 所有内部类应实现该接口, 以便调度中心统一管理
 * @创建者: peijd
 * @创建时间: 2017年2月16日 下午2:17:22
 */
public interface DcTaskService extends IJobHandler{

	/**
	 * @方法名称: runTask 
	 * @实现功能: 任务运行 实现接口
	 * @param taskId
	 * @return
	 * @create by peijd at 2017年2月16日 下午2:22:03
	 */
	DcDataResult runTask(String taskId) throws Exception;

	@Override
	default ReturnT<String> execute(String... params) throws Exception{
		ReturnT<String> res = new ReturnT<>();
		if (params!=null && params.length>0){
			DcDataResult result = runTask(params[0]);
			if (result.getRst_flag()){
				res.setMsg(result.getRst_std_msg());
				res.setCode(ReturnT.SUCCESS_CODE);
			}else {
				res.setMsg(result.getRst_err_msg());
				res.setCode(ReturnT.FAIL_CODE);
			}
			res.setContent(result.getRemarks());
		}
		return res;
	}
}
