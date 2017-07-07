package com.hlframe.core.jobbean;

import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.core.enums.ExecutorFailStrategyEnum;
import com.hlframe.core.route.ExecutorRouteStrategyEnum;
import com.hlframe.core.schedule.XxlJobDynamicScheduler;
import com.hlframe.core.util.PropertiesUtil;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.schedule.service.DcTaskTimeService;
import com.hlframe.task.TaskInfo;
import com.hlframe.task.entity.DcJobLog;
import com.hlframe.task.entity.DcTaskLogQquartz;
import com.hlframe.task.service.DcTaskLogQquartzService;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author xuxueli 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
public class RemoteHttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		TaskInfo taskInfo = new TaskInfo();
		DcTaskLogQquartzService run = SpringContextHolder.getBean(DcTaskLogQquartzService.class);
		DcTaskLogQquartz runobj = new DcTaskLogQquartz();

		// load job
		JobKey jobKey = context.getTrigger().getJobKey();
		String jobId = jobKey.getName();
		DcTaskTime taskTime = XxlJobDynamicScheduler.getDcTaskTimeService().get(jobId);

		Assert.notNull(taskTime);
		try {
			taskInfo = XxlJobDynamicScheduler.getDcTaskTimeService().buildTaskInfo(taskTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int failStrategy = Integer.parseInt(PropertiesUtil.getString("job.execute.failStrategy"));
		// trigger request
		TriggerParam triggerParam = new TriggerParam();
		triggerParam.setRunId(taskTime.getId());
		triggerParam.setExecutorHandler(taskInfo.getClassName());   // handler name
		triggerParam.setExecutorParams(taskInfo.getParams());     //  method params
//        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
//		triggerParam.setGlueType(jobInfo.getGlueType());  // use for glue type
//		triggerParam.setGlueSource(jobInfo.getGlueSource());  // use for glue type
//		triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());  // use for glue type
//		暂时屏蔽日志任务
//		triggerParam.setLogId(jobLog.getId());
//		triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());

		// 记录调度开始日志
		runobj.setIsNewRecord(true);
		runobj.setId(IdGen.uuid());
		runobj.setTaskid(taskInfo.getTaskid());
		runobj.setStartdate(new Date());
		runobj.setStatus(DcTaskTime.TASK_STATUS_RUNNING);
		run.save(runobj);

		// do trigger
		ReturnT<String> triggerResult = doTrigger(triggerParam, taskInfo, runobj);

		// fail retry
		if (triggerResult.getCode()==ReturnT.FAIL_CODE &&
				ExecutorFailStrategyEnum.match(failStrategy) == ExecutorFailStrategyEnum.FAIL_RETRY) {
			ReturnT<String> retryTriggerResult = doTrigger(triggerParam, taskInfo, runobj);

			triggerResult.setCode(retryTriggerResult.getCode());
			triggerResult.setMsg(triggerResult.getMsg() + "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br><br>" +retryTriggerResult.getMsg());
		}
		/*
		// log part-2
		jobLog.setTriggerCode(triggerResult.getCode());
		jobLog.setTriggerMsg(triggerResult.getMsg());
		XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);

		// monitor trigger
		JobFailMonitorHelper.monitor(jobLog.getId());
		logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
*/
		if (triggerResult.getCode()==ReturnT.FAIL_CODE){
			// 记录调度结束日志
			runobj.setIsNewRecord(false);
			runobj.setEnddate(new Date());
			runobj.setStatus(DcTaskTime.TASK_STATUS_ERROR);
			run.save(runobj);
		}else {
			// 记录调度结束日志
			runobj.setIsNewRecord(false);
			runobj.setEnddate(new Date());
			runobj.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
			run.save(runobj);
		}

		// 记录下一次调度时间
		DcTaskTimeService next = SpringContextHolder.getBean(DcTaskTimeService.class);
		DcTaskTime nextobj = new DcTaskTime();
		nextobj.setId(taskInfo.getTaskid());
		nextobj.setNexttime(context.getNextFireTime());
		next.save(nextobj);
    }

    public ReturnT<String> doTrigger(TriggerParam triggerParam, TaskInfo jobInfo, DcJobLog jobLog){
		StringBuffer triggerSb = new StringBuffer();

		// executor address list
		ArrayList<String> addressList = null;
		Assert.notNull(addressList);
		//get config properties from config file.
		String addressString = PropertiesUtil.getString("job.execute.addressList");
		int blockStrategy = Integer.parseInt(PropertiesUtil.getString("job.execute.blockStrategy"));
		int failStrategy = Integer.parseInt(PropertiesUtil.getString("job.execute.failStrategy"));
		int routeStrategy = Integer.parseInt(PropertiesUtil.getString("job.execute.routeStrategy"));

		addressList = new ArrayList<String>(Arrays.asList(addressString.split(",")));

		triggerSb.append("<br>阻塞处理策略：").append(ExecutorBlockStrategyEnum.match(blockStrategy).getTitle());
        triggerSb.append("<br>失败处理策略：").append(ExecutorFailStrategyEnum.match(failStrategy).getTitle());
		triggerSb.append("<br>地址列表：").append(addressList!=null?addressList.toString():"");
		if (CollectionUtils.isEmpty(addressList)) {
			triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
			return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
		}

		// trigger remote executor
		if (addressList.size() == 1) {
			String address = addressList.get(0);
			jobLog.setExecutorAddress(address);

			ReturnT<String> runResult = runExecutor(triggerParam, address);
			triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

			return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
		} else {
			// executor route strategy
			ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(routeStrategy);
			triggerSb.append("<br>路由策略：").append(executorRouteStrategyEnum!=null?(executorRouteStrategyEnum.name() + "-" + executorRouteStrategyEnum.getTitle()):null);
			if (executorRouteStrategyEnum == null) {
				triggerSb.append("<br>----------------------<br>").append("调度失败：").append("执行器路由策略为空");
				return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
			}

			if (executorRouteStrategyEnum != ExecutorRouteStrategyEnum.FAILOVER) {
				// get address
				String address = executorRouteStrategyEnum.getRouter().route(jobInfo.getId(), addressList);
				jobLog.setExecutorAddress(address);

				// run
				ReturnT<String> runResult = runExecutor(triggerParam, address);
				triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

				return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
			} else {
				for (String address : addressList) {
					// beat
					ReturnT<String> beatResult = beatExecutor(address);
					triggerSb.append("<br>----------------------<br>").append(beatResult.getMsg());

					if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
						jobLog.setExecutorAddress(address);

						ReturnT<String> runResult = runExecutor(triggerParam, address);
						triggerSb.append("<br>----------------------<br>").append(runResult.getMsg());

						return new ReturnT<String>(runResult.getCode(), triggerSb.toString());
					}
				}
				return new ReturnT<String>(ReturnT.FAIL_CODE, triggerSb.toString());
			}
		}
	}

	/**
	 * run executor
	 * @param address
	 * @return
	 */
	public ReturnT<String> beatExecutor(String address){
		ReturnT<String> beatResult = null;
		try {
			ExecutorBiz executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
			beatResult = executorBiz.beat();
		} catch (Exception e) {
			logger.error("", e);
			beatResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
		}

		StringBuffer sb = new StringBuffer("心跳检测：");
		sb.append("<br>address：").append(address);
		sb.append("<br>code：").append(beatResult.getCode());
		sb.append("<br>msg：").append(beatResult.getMsg());
		beatResult.setMsg(sb.toString());

		return beatResult;
	}

	/**
	 * run executor
	 * @param triggerParam
	 * @param address
	 * @return
	 */
	public ReturnT<String> runExecutor(TriggerParam triggerParam, String address){
		ReturnT<String> runResult = null;
		try {
			ExecutorBiz executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address).getObject();
			runResult = executorBiz.run(triggerParam);
		} catch (Exception e) {
			logger.error("", e);
			runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
		}

		StringBuffer sb = new StringBuffer("触发调度：");
		sb.append("<br>address：").append(address);
		sb.append("<br>code：").append(runResult.getCode());
		sb.append("<br>msg：").append(runResult.getMsg());
		runResult.setMsg(sb.toString());

		return runResult;
	}

}