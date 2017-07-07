/********************** 版权声明 *************************
 * 文件名: DcTaskTimeService.java
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
import com.hlframe.schedule.dao.DcTaskTimeDao;
import com.hlframe.schedule.entity.DcTaskContent;
import com.hlframe.schedule.entity.DcTaskMain;
import com.hlframe.schedule.entity.DcTaskQueue;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.task.TaskInfo;
import com.hlframe.task.TaskType;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional(readOnly = true)
public class DcTaskTimeService extends CrudService<DcTaskTimeDao, DcTaskTime> {

	@Autowired	//任务主表
	private DcTaskMainService dcTaskMainService;
	
	@Autowired	//任务队列
	private DcTaskQueueService taskQueueService;
	
	/**
	 * @实现功能: 数据权限过滤
	 * @create by yuzh at 2016年12月15日15:30:29
	 */
	public Page<DcTaskTime> findPage(Page<DcTaskTime> page, DcTaskTime dcTaskTime) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcTaskTime.getSqlMap().put("dsf", dataScopeFilter(dcTaskTime.getCurrentUser(),"o","u"));
		// 设置分页参数
		dcTaskTime.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(dcTaskTime));
		return super.findPage(page, dcTaskTime);
	}
	
	
	/**
	 * @方法名称: getTaskName 
	 * @实现功能: TODO
	 * @param scheduleName
	 * @return
	 * @create by cdd at 2016年11月15日 下午4:56:27
	 */
	public DcTaskTime getScheduleName(String scheduleName) {
		return dao.getScheduleName(scheduleName);
	}

	/**
	 * 修改状态
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void updateStatus(DcTaskTime entity) {
		entity.preUpdate();
		dao.updateStatus(entity);
	}
	
	/**
	 * Override
	 * @方法名称: delete 
	 * @实现功能: 删除调度任务
	 * @param taskTime
	 * @create by peijd at 2017年3月10日 下午3:20:04
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(DcTaskTime taskTime) {
		super.delete(taskTime);
		// TODO 更新调度任务状态   分两种情况 任务队列/调度任务
		if(taskTime.TASK_FROMTYPE_TASKQUEUE.equals(taskTime.getTaskfromtype())){
			DcTaskQueue queue = taskQueueService.get(taskTime.getTaskfromid());
			Assert.notNull(queue);
			//先更新队列状态
			queue.setStatus(queue.QUEUE_STATUS_SAVE);
			taskQueueService.save(queue);
			//依次更新队列中任务状态
			taskQueueService.updateQueueTaskStatus(queue);
			
		}else{	//普通任务
			//是否有任务队列引用, 有则更新状态为队列, 否则更新为编辑
			DcTaskMain task = dcTaskMainService.get(taskTime.getTaskfromid());
			Assert.notNull(task);
			
			//有任务队列引用  更新状态
			if(CollectionUtils.isNotEmpty(taskQueueService.getTaskRefList(taskTime.getTaskfromid(), null))){
				task.setStatus(task.TASK_STATUS_QUEUE);
			}else{
				task.setStatus(task.TASK_STATUS_EDIT);
			}
			dcTaskMainService.save(task);
		}
	}

	/**
	 * 根据业务ID获取对象，临时使用，目前仅允许一个调度和一个业务ID绑定
	 */
	public DcTaskTime getByTaskfromid(String taskfromid) {
		DcTaskTime dcTaskTime = new DcTaskTime();
		dcTaskTime.setTaskfromid(taskfromid);
		return dao.getByTaskfromid(dcTaskTime);
	}

	/**
	 * 
	 * @方法名称: buildTaskContent 
	 * @实现功能: 组件DcTaskContent对象
	 * @param id
	 * @return
	 * @create by cdd at 2016年11月29日 上午8:48:12
	 */
	public DcTaskContent buildTaskContent(String id) {
		Assert.hasText(id);
		return dao.buildTaskContent(id);
	}


	/**
	 * @方法名称: buildTaskInfo 
	 * @实现功能: 构建调度任务信息
	 * @param dcTaskTime
	 * @return
	 * @create by peijd at 2017年2月16日 下午8:06:14
	 */
	public TaskInfo buildTaskInfo(DcTaskTime dcTaskTime) throws Exception {
		Assert.notNull(dcTaskTime);
		TaskInfo task = new TaskInfo();
		task.setTaskid(dcTaskTime.getId());					// 必须是调度任务的ID，后去需要维护该调度任务的状态和日志
		task.setRunid(dcTaskTime.getTaskfromid());
		task.setTaskName(dcTaskTime.getScheduleName()); 	// 任务的名称，日志中使用
		task.setExecuteType(dcTaskTime.getTriggerType()); 	// 执行方式， 0-自动  1-手动
		task.setScheduleExpr(dcTaskTime.getScheduleExpr()); // 执行时间的表达式
		
		//构建任务信息 
		DcTaskMain dcTaskMain = buildTaskMain(dcTaskTime);
		//构建任务信息
		buildTaskInfo(task, dcTaskMain);
		return task;
	}

	/**
	 * @方法名称: buildTaskMain 
	 * @实现功能: 构建执行任务主体对象
	 * @param dcTaskTime
	 * @return
	 * @create by peijd at 2017年3月3日 下午1:32:33
	 */
	private DcTaskMain buildTaskMain(DcTaskTime dcTaskTime) {
		//任务队列
		if(DcTaskTime.TASK_FROMTYPE_TASKQUEUE.equals(dcTaskTime.getTaskfromtype())){
			DcTaskQueue taskQueue =  taskQueueService.get(dcTaskTime.getTaskfromid());
			Assert.notNull(taskQueue);
			DcTaskMain taskMain = new DcTaskMain();
			taskMain.setId(taskQueue.getId());
			taskMain.setTaskName(taskQueue.getQueueName());
			taskMain.setTaskDesc(taskQueue.getQueueDesc());
			taskMain.setMethodName("runTaskQueue");		//队列执行方法
			taskMain.setParameter(taskQueue.getId());	//队列参数 即当前主键
			taskMain.setClassName("com.hlframe.modules.dc.schedule.service.DcTaskQueueService");	//执行service
			taskMain.setPriority("1");
			taskMain.setTaskType(DcTaskMain.TASK_TYPE_INNERCLASS);	//内部类
			return taskMain;
		}else{
			//自定义任务
			return dcTaskMainService.get(dcTaskTime.getTaskfromid());
		}
	}


	/**
	 * @方法名称: buildTaskInfo 
	 * @实现功能: 构建任务信息
	 * @param task
	 * @param dcTaskMain
	 * @return
	 * @throws Exception
	 * @create by peijd at 2017年3月3日 上午8:26:50
	 */
	public TaskInfo buildTaskInfo(TaskInfo task, DcTaskMain dcTaskMain) throws Exception {
		if(null==task){
			task = new TaskInfo();
			task.setTaskid(dcTaskMain.getId());					// 必须是调度任务的ID，后去需要维护该调度任务的状态和日志
			task.setRunid(dcTaskMain.getId());
			task.setTaskName(dcTaskMain.getTaskName()); 	// 任务的名称，日志中使用
			task.setExecuteType(DcTaskTime.TASK_TRIGGERTYPE_HAND); 	// 执行方式， 0-自动  1-手动
		}
		task.setParamsType(dcTaskMain.getProgramTpye()); 	// 参数类型
		task.setParams(dcTaskMain.getParameter()); 		// 参数
		if (DcTaskMain.TASK_TYPE_INNERCLASS.equalsIgnoreCase(dcTaskMain.getTaskType())) { // 内部类
			task.setTaskTpye(TaskType.CLASS); 				// 任务的类型
			task.setClassName(dcTaskMain.getClassName()); 	// 需要执行的类名
			task.setMethodName(dcTaskMain.getMethodName()); // 需要执行的方法名

		} else if (DcTaskMain.TASK_TYPE_JAR.equalsIgnoreCase(dcTaskMain.getTaskType())) { // 外部Jar包
			task.setTaskTpye(TaskType.JARS); 				// 任务的类型
			task.setClassName(dcTaskMain.getClassName()); 	// 需要执行的类名
			task.setMethodName(dcTaskMain.getMethodName()); // 需要执行的方法名
			task.setFilePath(dcTaskMain.getFilePath()); 	// 上传的文件路径

		} else if (DcTaskMain.TASK_TYPE_SHELL.equalsIgnoreCase(dcTaskMain.getTaskType())) { // 外部Bat Or Shell
			task.setTaskTpye(TaskType.SHELL);
			task.setMethodName(dcTaskMain.getMethodName()); // 需要执行的方法名
			task.setFilePath(dcTaskMain.getFilePath()); 	// 上传的文件路径
			task.setFileName(dcTaskMain.getFileName()); 	// shell的文件名 or
															// bat的文件名
		} else {
			throw new SchedulerException("未知的任务管理类型！任务管理ID为： " + dcTaskMain.getId());
		}
		return task;
	}

	@Override
	@Transactional(readOnly = false)
	public void save(DcTaskTime entity) {
		if(entity.getUrlLink() == null){
			entity.setUrlLink(entity.getUrlLink()+"?id="+entity.getId());
		}
		super.save(entity);
	}
}
