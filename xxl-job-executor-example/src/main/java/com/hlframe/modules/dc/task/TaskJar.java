package com.hlframe.modules.dc.task;

import com.hlframe.modules.dc.common.dao.DcDataResult;

/**
 * @类名: com.hlframe.modules.dc.task.TaskJar.java 
 * @职责说明: 执行jar包任务
 * @修改者: peijd
 * @修改时间: 2017年3月16日 上午11:47:53
 */
public class TaskJar implements ITask {
	/**
	 * 执行外部单个jar包任务
	 * @param taskInfo  TODO
	 * @return
	 */
	public DcDataResult doTask(final TaskInfo taskInfo) {
		DcDataResult result = new DcDataResult();
		result.setRst_flag(true);
		result.setRst_std_msg("TODO");
		return result;
	}
}
