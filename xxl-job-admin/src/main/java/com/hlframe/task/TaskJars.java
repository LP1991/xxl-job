package com.hlframe.task;

import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.Reflections;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.common.dao.DcDataResult;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.schedule.service.DcTaskTimeService;
import com.hlframe.task.entity.DcTaskLogRun;
import com.hlframe.task.service.DcTaskLogRunService;
import com.hlframe.common.MyClassLoader;


import java.io.File;
import java.net.URL;
import java.util.Date;

public class TaskJars implements ITask {
	/**
	 * 执行外部多个jar包任务，需要打包rar或者zip上传，并指定执行类、方法和参数
	 * @param taskInfo
	 * @return
	 */
	public DcDataResult doTask(final TaskInfo taskInfo) {
		final DcDataResult result = new DcDataResult();
		try {
			//新建线程实现
			Thread syncThread = new Thread(new Runnable() {
				public void run() {
					DcTaskLogRunService run = SpringContextHolder.getBean(DcTaskLogRunService.class);
					DcTaskLogRun runobj = new DcTaskLogRun();
					DcTaskTime dcTaskTime=new DcTaskTime();
					DcTaskTimeService obj = SpringContextHolder.getBean(DcTaskTimeService.class);
					try {
						// 记录任务开始日志
						runobj.setIsNewRecord(true);
						runobj.setId(IdGen.uuid());
						runobj.setTaskid(taskInfo.getTaskid());
						runobj.setRunid(taskInfo.getRunid());
						runobj.setStartdate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_RUNNING);
						run.save(runobj);
						
						// 加载jar
						URL[] urls = new URL[] {};
						MyClassLoader classLoader = new MyClassLoader(urls, null);
						File dir = new File(taskInfo.getFilePath());
						loadJar(dir, urls, classLoader);
						
						// 执行类
						Class<?> clz = classLoader.loadClass(taskInfo.getClassName());
						// Method method = clz.getMethod(taskInfo.getMethodName(), new Class[]{double.class,double.class});
						// method.setAccessible(true); // 私有的方法通过发射可以修改其访问权限
						Object[] argspara = new Object[]{};
						String programs = taskInfo.getPrograms();
						if (StringUtils.isNotEmpty(programs)) {
							argspara = programs.split(",");
						}
						DcDataResult result2 =(DcDataResult) Reflections.invokeMethodByName(clz.newInstance(), taskInfo.getMethodName(), argspara);
						
						// 记录任务结束日志
						runobj.setIsNewRecord(false);
						runobj.setEnddate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
						if (result2 != null) {
							runobj.setRemarks(result2.toString());
						}
						run.save(runobj);
						
						// 非自动清空下，需要将任务设置为完成状态
						if (!DcTaskTime.TASK_TRIGGERTYPE_AUTO.equalsIgnoreCase(taskInfo.getExecuteType())) {
							dcTaskTime.setId(taskInfo.getTaskid());
							dcTaskTime.setStatus(DcTaskTime.TASK_STATUS_SUCCESS);
							obj.updateStatus(dcTaskTime); 
						}
						result.setRst_flag(true);
						result.setRst_std_msg(result2.getRst_std_msg());
					} catch (Exception e) {
						result.setRst_flag(false);
						result.setRst_err_msg(e.getMessage()+"\n");
						e.printStackTrace();
						// 记录任务错误日志
						runobj.setIsNewRecord(false);
						runobj.setEnddate(new Date());
						runobj.setStatus(DcTaskTime.TASK_STATUS_ERROR);
						runobj.setRemarks("执行任务[" + taskInfo.getTaskName() + "]失败 : " + e.getMessage());
						run.save(runobj);
						
						// 非自动清空下，需要将任务设置为完成错误
						if (!DcTaskTime.TASK_TRIGGERTYPE_AUTO.equalsIgnoreCase(taskInfo.getExecuteType())) {
							dcTaskTime.setId(taskInfo.getTaskid());
							dcTaskTime.setStatus(DcTaskTime.TASK_STATUS_ERROR);
							obj.updateStatus(dcTaskTime); 
						}
					}
				}
				
				/**
				 * 加载jar
				 * @param dir
				 */
				private void loadJar(File dir, URL[] urls, MyClassLoader classLoader) throws Exception {
					File[] fs = dir.listFiles();
					for (File file : fs) {
						if (file.isDirectory()) {
							loadJar(file, urls, classLoader);
						} else {
							if (file.getName().endsWith("jar") || file.getName().endsWith("JAR")) {
								classLoader.addJar(file.toURI().toURL());
							}
						}
					}
				}
			});
			syncThread.start();
			//同步执行标志, 任务执行完成再返回
			if(taskInfo.getSyncFlag()){
				syncThread.join();
			}
			
		} catch (Exception e) {
			result.setRst_flag(false);
			result.setRst_err_msg("任务["+taskInfo.getTaskName()+"]执行失败! "+e.getMessage());
			logger.error("-->TaskJars["+taskInfo.getTaskName()+"].doTask: ", e);
		}
		return result;
	}
}