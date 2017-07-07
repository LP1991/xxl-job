package com.hlframe.task;

import com.hlframe.common.dao.DcDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ITask {
	
	Logger logger = LoggerFactory.getLogger(ITask.class);
	
	/**
	 * 单次执行任务
	 * @param taskInfo
	 * @return DcDataResult 结果对象
	 */
	DcDataResult doTask(TaskInfo taskInfo);
}
