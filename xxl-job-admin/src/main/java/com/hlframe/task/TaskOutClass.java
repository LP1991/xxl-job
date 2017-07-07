package com.hlframe.task;

import com.hlframe.common.dao.DcDataResult;

public class TaskOutClass implements ITask {
	/**
	 * 执行外部单个class任务
	 * @param taskInfo
	 * @return
	 */
	public DcDataResult doTask(final TaskInfo taskInfo) {
		DcDataResult result = new DcDataResult();
		result.setRst_flag(true);
		result.setRst_std_msg("TODO");
		return result;
	}
}
