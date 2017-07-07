package com.hlframe.task;

import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.schedule.service.DcTaskTimeService;
import com.hlframe.task.entity.DcTaskLogQquartz;
import com.hlframe.task.service.DcTaskLogQquartzService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class TaskJarsQuartzJob implements Job {
	/**
	 * jar包的调度任务
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TaskInfo taskInfo = new TaskInfo();
		DcTaskLogQquartzService run = SpringContextHolder.getBean(DcTaskLogQquartzService.class);
		DcTaskLogQquartz runobj = new DcTaskLogQquartz();
		try {

			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			taskInfo.setTaskid((String) dataMap.get("id")); // 必须是调度任务的ID，后去需要维护该调度任务的状态和日志
			taskInfo.setRunid((String) dataMap.get("runid"));
			taskInfo.setTaskName((String) dataMap.get("taskName")); // 任务的名称，日志中使用
			taskInfo.setClassName((String) dataMap.get("className")); // 需要执行的类名
			taskInfo.setMethodName((String) dataMap.get("methodName")); // 需要执行的方法名
			taskInfo.setPrograms((String) dataMap.get("programs")); // 参数
			taskInfo.setFilePath((String) dataMap.get("filePath"));
			taskInfo.setExecuteType(DcTaskTime.TASK_TRIGGERTYPE_AUTO); // 设为自动

			// 记录调度开始日志
			runobj.setIsNewRecord(true);
			runobj.setId(IdGen.uuid());
			runobj.setTaskid(taskInfo.getTaskid());
			runobj.setStartdate(new Date());
			runobj.setStatus(DcTaskTime.TASK_STATUS_RUNNING);
			run.save(runobj);
			
			TaskJars tc = new TaskJars();
			tc.doTask(taskInfo);

			// 记录调度结束日志
			runobj.setIsNewRecord(false);
			runobj.setEnddate(new Date());
			runobj.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
			run.save(runobj);

			// 记录下一次调度时间
			DcTaskTimeService next = SpringContextHolder.getBean(DcTaskTimeService.class);
			DcTaskTime nextobj = new DcTaskTime();
			nextobj.setId(taskInfo.getTaskid());
			nextobj.setNexttime(context.getNextFireTime());
			next.save(nextobj);
			
		} catch (Exception e) {
			// 记录调度错误日志
			runobj.setIsNewRecord(false);
			runobj.setEnddate(new Date());
			runobj.setStatus(DcTaskTime.TASK_STATUS_ERROR);
			runobj.setRemarks("执行任务[" + taskInfo.getTaskName() + "]失败 : " + e.getMessage());
			run.save(runobj);
		}
	}
}