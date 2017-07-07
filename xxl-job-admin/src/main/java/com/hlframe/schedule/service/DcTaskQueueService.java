/********************** 版权声明 *************************
 * 文件名: DcTaskQueueService.java
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

import com.hlframe.common.QuartzUtil;
import com.hlframe.common.dao.DcDataResult;
import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.schedule.dao.DcTaskQueueDao;
import com.hlframe.schedule.dao.DcTaskQueueRefDao;
import com.hlframe.schedule.entity.DcTaskMain;
import com.hlframe.schedule.entity.DcTaskQueue;
import com.hlframe.schedule.entity.DcTaskQueueRef;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class DcTaskQueueService extends CrudService<DcTaskQueueDao, DcTaskQueue>  {
	
	@Autowired	//任务关联Dao
	private DcTaskQueueRefDao refDao;
	@Autowired	//任务主表service
	private DcTaskMainService taskMainService;
//	@Autowired	//调度service
	private DcTaskTimeService dcTaskTimeService = null;
	@Autowired	//任务主表
	private DcTaskMainService dcTaskMainService;
	/**
	 * @方法名称: getDcTaskTimeService 
	 * @实现功能: 获取调度任务Service
	 * @return
	 * @create by peijd at 2017年3月3日 下午2:12:29
	 */
	private DcTaskTimeService initDcTaskTimeService(){
		//service为空时, 通过spring初始化
		if(null==dcTaskTimeService){
			//同步代码块, 避免重复创建对象
			synchronized (DcTaskQueueService.class) {
				if(null==dcTaskTimeService){
					dcTaskTimeService = SpringContextHolder.getBean(DcTaskTimeService.class);
				}
			}
		}
		return dcTaskTimeService;
	}
	
	
	/**
	 * @方法名称: getObjByName 
	 * @实现功能: 根据队列名称获取 队列信息, 主用于重复验证
	 * @param queueName
	 * @param id
	 * @return
	 * @create by peijd at 2017年3月1日 下午2:48:15
	 */
	public DcTaskQueue getObjByName(String queueName, String id) {
		Assert.hasText(queueName);
		return dao.getObjByName(queueName, id);
	}

	/**
	 * @方法名称: initTaskQueueMap 
	 * @实现功能: 初始化任务队列Map
	 * @param taskList
	 * @return
	 * @create by peijd at 2017年3月16日 下午7:48:17
	 */
	private Map<String, Map<String, String>> initTaskQueueMap(List<DcTaskQueueRef> taskList) {
		Map<String, Map<String, String>> taskMap = new HashMap<String, Map<String, String>>(); 
		Map<String, String> statusMap = null, //记录任务状态
							refMap = new HashMap<String, String>();	//记录任务依赖关系
		for(DcTaskQueueRef ref: taskList){
			statusMap = new HashMap<String, String>();
			statusMap.put("status", DcTaskQueueRef.TASK_RESULT_INIT);	//任务状态
			if(StringUtils.isNotBlank(ref.getPreTaskId())){
				refMap.put(ref.getPreTaskId(), ref.getId());			//依赖关系: preTaskId:afterTaskId
				statusMap.put("preTaskId",ref.getPreTaskId());			//前置任务Id
			}
			taskMap.put(ref.getId(), statusMap);
		}
		for(String taskId: taskMap.keySet()){
			if(refMap.containsKey(taskId)){
				taskMap.get(taskId).put("afterTaskId",refMap.get(taskId));
			}
		}
		return taskMap;
	}


	/**
	 * @方法名称: deleteQueue 
	 * @实现功能: 删除任务队列
	 * @param taskQueue
	 * @create by peijd at 2017年3月2日 下午2:56:26
	 */
	@Transactional(readOnly = false)
	public void deleteQueue(DcTaskQueue taskQueue) {
		Assert.notNull(taskQueue);
		taskQueue = dao.get(taskQueue);
		//已添加调度  先删除调度
		if(DcTaskQueue.QUEUE_STATUS_SCHEDULE.equals(taskQueue.getStatus())){
			throw new ServiceException("该任务已添加调度任务!");
			/*删除调度任务信息
			DcTaskTime dcTaskTime = new DcTaskTime();
			dcTaskTime.setTaskfromid(taskQueue.getId());
			dcTaskTime.setTaskfromtype(DcTaskTime.TASK_FROMTYPE_TASKQUEUE);
			initDcTaskTimeService().delete(dcTaskTime);*/
		}
		
		dao.delete(taskQueue);
	}

	/**
	 * @方法名称: getTaskRefList 
	 * @实现功能: 获取队列 包含的任务列表
	 * @param taskRef
	 * @return
	 * @create by peijd at 2017年3月1日 下午7:42:22
	 */
	public List<DcTaskQueueRef> getTaskRefList(DcTaskQueueRef taskRef) {
		Assert.notNull(taskRef);
		List<DcTaskQueueRef> taskList = refDao.findList(taskRef);
		if(CollectionUtils.isEmpty(taskList)){
			taskList = new ArrayList<DcTaskQueueRef>();
		}
		return taskList;
	}

	/**
	 * @方法名称: runTask 
	 * @实现功能: 运行队列中单项任务(手动测试)
	 * @param taskId
	 * @param syncFlag	是否同步执行
	 * @return
	 * @throws Exception 
	 * @create by peijd at 2017年3月1日 下午2:53:22
	 */
	@Transactional(readOnly = false)
	public DcDataResult runQueueTask(String taskId, boolean syncFlag) {
		Assert.hasText(taskId);
		DcDataResult result = new DcDataResult();
		DcTaskMain task = taskMainService.get(taskId);
		Assert.notNull(task);

		try {

			//构建任务信息
			TaskInfo taskInfo = initDcTaskTimeService().buildTaskInfo(null, task);
			//运行任务  另起线程 同步执行任务, 得到任务的状态及运行结果
			taskInfo.setSyncFlag(syncFlag);
			return QuartzUtil.doTaskProcess(taskInfo);
		} catch (Exception e) {
			result.setRst_err_msg("任务执行异常!");
			result.setRst_flag(false);
			logger.error("--> runTask ", e);
			return result;
		}


	}


	/**
	 * @方法名称: getTaskRef 
	 * @实现功能: 获取任务项信息
	 * @param id
	 * @return
	 * @create by peijd at 2017年3月1日 下午5:47:51
	 */
	public DcTaskQueueRef getTaskRef(String id) {
		Assert.hasText(id);
		return refDao.get(id);
	}

	/**
	 * @方法名称: deleteTask 
	 * @实现功能: 删除任务项
	 * @param obj
	 * @create by peijd at 2017年3月1日 下午6:14:43
	 */
	@Transactional(readOnly = false)
	public void deleteTask(DcTaskQueueRef obj) {
		Assert.notNull(obj.getId());
		refDao.delete(obj.getId());
		//TODO 取消任务状态 如果是"添加队列" 且没有其他队列引用, 则更新为'编辑'状态
		DcTaskMain task = dcTaskMainService.get(obj.getTask().getId());
		//有任务队列引用  更新状态
		if(CollectionUtils.isNotEmpty(getTaskRefList(obj.getTask().getId(), null))){
			task.setStatus(task.TASK_STATUS_QUEUE);
		}else{
			task.setStatus(task.TASK_STATUS_EDIT);
		}
		dcTaskMainService.save(task);
	}

	/**
	 * @方法名称: checkTask 
	 * @实现功能: 检查队列任务是否存在
	 * @param obj
	 * @return
	 * @create by peijd at 2017年3月1日 下午7:00:09
	 */
	public String checkTask(DcTaskQueueRef obj) {
		Assert.notNull(obj);
		return null==refDao.getTaskInfo(obj)?"true":"false";
	}

	/**
	 * @方法名称: findTaskRefPage 
	 * @实现功能: 获取任务列表
	 * @param page
	 * @param taskQueueRef
	 * @return
	 * @create by peijd at 2017年3月2日 上午8:58:21
	 */
	public Page<DcTaskQueueRef> findTaskRefPage(Page<DcTaskQueueRef> page, DcTaskQueueRef taskQueueRef) {
		taskQueueRef.setPage(page);
		page.setList(refDao.findList(taskQueueRef));
		return page;
	}

	/**
	 * @方法名称: saveTask 
	 * @实现功能: 保存队列子任务
	 * @param taskQueueRef
	 * @create by peijd at 2017年3月2日 下午1:44:40
	 */
	@Transactional(readOnly = false)
	public void saveTask(DcTaskQueueRef taskQueueRef) {
		Assert.notNull(taskQueueRef);
		if(StringUtils.isNotBlank(taskQueueRef.getId())){
			taskQueueRef.preUpdate();
			refDao.update(taskQueueRef);
		}else{
			taskQueueRef.preInsert();
			refDao.insert(taskQueueRef);
			
			//设置任务状态  如果是新增状态 则更新为"添加队列"
			DcTaskMain main = taskMainService.get(taskQueueRef.getTaskId());
			if(StringUtils.isBlank(main.getStatus()) || DcTaskMain.TASK_STATUS_EDIT.equals(main.getStatus())){
				main.setStatus(DcTaskMain.TASK_STATUS_QUEUE);
				taskMainService.save(main);
			}
		}
	}

	/**
	 * @方法名称: add2Schedule 
	 * @实现功能: 添加调度任务
	 * @param taskQueue
	 * @create by peijd at 2017年3月2日 下午2:50:31
	 */
	@Transactional(readOnly = false)
	public void add2Schedule(DcTaskQueue taskQueue) {

		//添加调度任务
		DcTaskTime dcTaskTime = new DcTaskTime();
		dcTaskTime.setScheduleName(taskQueue.getQueueName());
		dcTaskTime.setScheduleDesc(taskQueue.getQueueDesc());
		dcTaskTime.setTriggerType(DcTaskTime.TASK_TRIGGERTYPE_HAND);	//'手动执行'状态
		dcTaskTime.setStatus(DcTaskTime.TASK_STATUS_INIT);				//'未执行' 状态
		dcTaskTime.setTaskfromid(taskQueue.getId());					//任务来源
		dcTaskTime.setTaskfromtype(DcTaskTime.TASK_FROMTYPE_TASKQUEUE);	//任务类型 任务队列
		dcTaskTime.setTaskfromname(taskQueue.getQueueName());
		initDcTaskTimeService().save(dcTaskTime);
		
		//更新队列状态
		taskQueue.setStatus(DcTaskQueue.QUEUE_STATUS_SCHEDULE);	//添加调度 状态
		dao.update(taskQueue);
	}


	/**
	 * @方法名称: getTaskRefList 
	 * @实现功能: 根据任务Id, 获取队列任务列表, 若指定queueId, 则排除该队列
	 * @param taskId			任务Id
	 * @param excludeQueueId	排除队列Id
	 * @return
	 * @create by peijd at 2017年3月10日 下午4:14:30
	 */
	public List<DcTaskQueueRef> getTaskRefList(String taskId, String excludeQueueId) {
		Assert.hasText(taskId);
		DcTaskQueueRef param = new DcTaskQueueRef();
		param.setTaskId(taskId);
		if(StringUtils.isNotBlank(excludeQueueId)){
			param.setQueueId(excludeQueueId);
		}
		return refDao.findQueueTaskList(param);
	}


	/**
	 * @方法名称: updateQueueTaskStatus 
	 * @实现功能:  依次更新队列中任务状态 
	 * @param queue
	 * @create by peijd at 2017年3月10日 下午4:35:56
	 */
	@Transactional(readOnly = false)
	public void updateQueueTaskStatus(DcTaskQueue queue) {
		//任务列表
		DcTaskQueueRef param = new DcTaskQueueRef();
		param.setQueueId(queue.getId());
		List<DcTaskQueueRef> taskList = refDao.findList(param);
		DcTaskMain task = null;
		//遍历任务列表, 判断各任务状态
		for(DcTaskQueueRef ref: taskList){
			//如果该任务被调度任务引用  则不处理
			if(DcTaskMain.TASK_STATUS_JOB.equals(ref.getTask().getStatus())){
				continue;
			}
			//是否被其他队列引用  若引用则不处理
			if(CollectionUtils.isNotEmpty(getTaskRefList(ref.getTask().getId(), queue.getId()))){
				continue;
			}
			//无引用 更新为编辑状态
			task = new DcTaskMain();
			task.setId(ref.getTask().getId());
			task.setStatus(task.TASK_STATUS_EDIT);
			taskMainService.save(task);
		}
	}


}
