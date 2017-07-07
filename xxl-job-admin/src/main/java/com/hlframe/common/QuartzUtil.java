/********************** 版权声明 *************************
 * 文件名: QuartzUtil.java
 * 包名: com.hlframe.common
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/6/29
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.common;


import com.hlframe.common.dao.DcDataResult;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.schedule.entity.DcTaskTime;
import com.hlframe.schedule.service.DcTaskTimeService;
import com.hlframe.modules.sys.entity.User;
import com.hlframe.modules.sys.utils.UserUtils;
import com.hlframe.task.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 任务调度公共类
 * @remark
 * @author feizi
 * @time 2015-3-23下午3:04:12
 */
public class QuartzUtil {
    public final static String JOB_GROUP_NAME = "QUARTZ_JOBGROUP_NAME";//任务组
    public final static String TRIGGER_GROUP_NAME = "QUARTZ_TRIGGERGROUP_NAME";//触发器组
    private static Logger log = LoggerFactory.getLogger(QuartzUtil.class);//日志

    /**
     * 添加任务的方法
     * @param jobName  任务名
     * @param triggerName  触发器名
     * @param jobClass  执行任务的类
     * @param seconds  间隔时间
     * @throws SchedulerException
     */
    public static void addJob(String jobName,String triggerName,Class<? extends Job> jobClass,int seconds) throws SchedulerException{//addJob(任务组名，触发器名，类名，时间)
        log.info("==================initialization=================");
        //创建一个SchedulerFactory工厂实例
        SchedulerFactory sf = new StdSchedulerFactory();
        
        //通过SchedulerFactory构建Scheduler对象 
        Scheduler sche = sf.getScheduler();
        log.info("===================initialize finshed===================");
        log.info("==============add the Job to Scheduler==================");  

        //用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, JOB_GROUP_NAME).build();

        //构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()//创建一个新的TriggerBuilder来规范一个触发器
                            .withIdentity(triggerName, TRIGGER_GROUP_NAME)//给触发器起一个名字和组名
                            .startNow()//立即执行
                            .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(seconds)//时间间隔  单位：秒
                                .repeatForever()//一直执行
                            )
                            .build();//产生触发器
        //向Scheduler中添加job任务和trigger触发器
        sche.scheduleJob(jobDetail, trigger);
        //启动
        sche.start();
    }

 /*   *//**
     * 测试
     * @param args
     *//*
    public static void main(String[] args) {
        try {
            //添加第一个任务  每隔10秒执行一次
          //  QuartzUtil.addJob("job1", "trigger1", DcDemoTaskService.class, 15);
            //添加第二个任务  每隔20秒执行一次
      //   QuartzUtil.addJob("Job2", "trigger2", TestJobTwo.class, 5);//执行的步骤
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
*/
//    /**
//     * 增加任务的方法
//     * @方法名称: addJob 
//     * @实现功能: TODO
//     * @param cls
//     * @param jobName
//     * @param time
//     * @create by cdd at 2016年11月18日 下午1:47:53
//     */
//    public static void addJob(Class cls, String time) {  
//        try {  
//        	SchedulerFactory schedulerFactory = new StdSchedulerFactory();
//        	Scheduler scheduler = schedulerFactory.getScheduler();
//    /*  if(){
//    	  
//      }*/
//            JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity("job1", "group1").build();// jobName 任务名，任务组，
//            // 触发器  
//            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("myTrigger","group1")
//                    .startNow()
//                    .withSchedule(
//                    	     CronScheduleBuilder.cronSchedule(time)
//                        //.withRepeatCount(3) //重复运行3次
//                    ).build();
//      
//            scheduler.scheduleJob(jobDetail, trigger);  //添加job，以及其关联的trigger
//            //启动job
//            scheduler.start();
//        } catch (Exception e) {  
//            throw new RuntimeException(e);  
//        }  
//    }
    /**
     * 增加任务的方法
     * @方法名称: addJob 
     * @实现功能: TODO
     * @param cls
     * @param jobName
     * @param time
     * @create by cdd at 2016年11月18日 下午1:47:53
     */
    public static void addJob(Class cls, String time,String jobName,String jobGroupName,String triggerName,String triggerGroupName) {  
        try {  
        	SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        	Scheduler scheduler = schedulerFactory.getScheduler();
 
            JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName,jobGroupName).build();// jobName 任务名，任务组，
            // 触发器  
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName,triggerGroupName)
                    .startNow()
                    .withSchedule(
                    	     CronScheduleBuilder.cronSchedule(time) //.withRepeatCount(3) //重复运行3次
                    ).build();
      
            scheduler.scheduleJob(jobDetail, trigger);  //添加job，以及其关联的trigger
            //启动job
            scheduler.start();
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }
    /**
     * 暂停任务
     * @方法名称: pauseJob 
     * @实现功能: TODO
     * @param cls
     * @param time
     * @create by cdd at 2016年11月18日 下午1:47:53
     */
    public static void pauseJob(Class cls, String time) {  
        try {  
      
         /*   JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity("job1", "group1").build();// jobName 任务名，任务组，
            // 触发器  
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger","group1")
                    .startNow()
                    .withSchedule(
                    	     CronScheduleBuilder.cronSchedule(time)
                        //.withRepeatCount(3) //重复运行3次
                    ).build();
      */
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobKey jobKey = JobKey.jobKey("job1", "group1");
          /*  scheduler.scheduleJob(jobDetail, trigger);  //添加job，以及其关联的trigger
*/            //启动job
            scheduler.pauseJob(jobKey);
           
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }
    
    public static void resumeJob(Class cls, String time) {  
        try {  
      
         /*   JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity("job1", "group1").build();// jobName 任务名，任务组，
            // 触发器  
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger","group1")
                    .startNow()
                    .withSchedule(
                    	     CronScheduleBuilder.cronSchedule(time)
                        //.withRepeatCount(3) //重复运行3次
                    ).build();
      */
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        	Scheduler scheduler = schedulerFactory.getScheduler();
        	JobKey jobKey = JobKey.jobKey("job1", "group1");
        	scheduler.resumeJob(jobKey);     
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }

	/**
	 * 删除任务
	 * @param id
	 */
	public static void deleteJob(String id) {
		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobKey jobKey = JobKey.jobKey("job" + id, JOB_GROUP_NAME);
			scheduler.deleteJob(jobKey);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 检测任务是否存在
	 * @param id
	 */
	public static boolean checkExistsJob(String id) {
		boolean result = false;
		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			JobKey jobKey = JobKey.jobKey("job" + id, JOB_GROUP_NAME);
			result = scheduler.checkExists(jobKey);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * @方法名称: invokeMethod 
	 * @实现功能: TODO
	 * @param className
	 * @param methodName
	 * @param parameter
	 * @return 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @create by cdd at 2016年11月17日 下午8:27:17
	 */
	public static Object invokeMethod(Object className, String methodName, Object parameter) throws Exception {
		 Method method = className.getClass().getMethod(methodName,parameter.getClass()); 
	     return method.invoke(className, parameter);  
	}
	
	/**
	 * 执行任务入口
	 * @param taskInfo
	 * @return DcDataResult 任务执行结果
	 */
	public static DcDataResult doTaskProcess(TaskInfo taskInfo){
		DcDataResult result = new DcDataResult();
		//手动 自动 同意加载上次运运行结果 baog 2017/6/29 4:30
		DcTaskTime dcTaskTime=new DcTaskTime();
		DcTaskTimeService obj = SpringContextHolder.getBean(DcTaskTimeService.class);
		List<Map<String,Object>> ort = new ArrayList<>();
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select a.enddate as enddate,a.status as status from dc_task_log_run a WHERE taskid='"+taskInfo.getTaskid()+"' order by a.enddate desc");
		ort = DcCommonUtils.queryDataBySql(sqlBuf.toString());
		for(int i =0;i<ort.size();i++){
			dcTaskTime.setResult(String.valueOf(ort.get(i).get("status")));
			dcTaskTime.setId(taskInfo.getTaskid());
			User user = UserUtils.getUser();
			dcTaskTime.setUpdateBy(user);
			dcTaskTime.setUpdateDate(new Date());
			obj.save(dcTaskTime);
			break;
		}
		try {
			// 验证参数
			QuartzUtil.verifyParameters(taskInfo);
			
			if (DcTaskTime.TASK_TRIGGERTYPE_AUTO.equalsIgnoreCase(taskInfo.getExecuteType())) { // 自动
				Assert.hasText(taskInfo.getScheduleExpr(), "参数错误或参数不足！");

				Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();  
				// 创建规则  0/10 * * * * ?  每隔10秒执行
				CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(taskInfo.getScheduleExpr());
				Trigger trigger=TriggerBuilder.newTrigger()
						.withIdentity("trigger" + taskInfo.getTaskid(), QuartzUtil.TRIGGER_GROUP_NAME)
						.startNow().withSchedule(builder).build();
				JobDetail job = null;
				
				if (TaskType.CLASS.equals(taskInfo.getTaskTpye())) {
					// 创建job信息
					job = JobBuilder.newJob(TaskClassQuartzJob.class)
							.withIdentity("job" + taskInfo.getTaskid(), QuartzUtil.JOB_GROUP_NAME)
							.usingJobData("id", taskInfo.getTaskid())
							.usingJobData("runid", taskInfo.getRunid())
							.usingJobData("taskName", taskInfo.getTaskName())
							.usingJobData("className", taskInfo.getClassName())
							.usingJobData("methodName", taskInfo.getMethodName())
							.usingJobData("programTpye", taskInfo.getParamsType())
							.usingJobData("programs", taskInfo.getPrograms())
							.build();
				} else if (TaskType.JARS.equals(taskInfo.getTaskTpye())) {
					// 创建job信息
					job = JobBuilder.newJob(TaskJarsQuartzJob.class)
							.withIdentity("job" + taskInfo.getTaskid(), QuartzUtil.JOB_GROUP_NAME)
							.usingJobData("id", taskInfo.getTaskid())
							.usingJobData("runid", taskInfo.getRunid())
							.usingJobData("taskName", taskInfo.getTaskName())
							.usingJobData("className", taskInfo.getClassName())
							.usingJobData("methodName", taskInfo.getMethodName())
							.usingJobData("programTpye", taskInfo.getParamsType())
							.usingJobData("programs", taskInfo.getPrograms())
							.usingJobData("filePath", taskInfo.getFilePath())
							.build();
				} else if (TaskType.SHELL.equals(taskInfo.getTaskTpye())) {
					// 创建job信息
					job = JobBuilder.newJob(TaskShellQuartzJob.class)
							.withIdentity("job" + taskInfo.getTaskid(), QuartzUtil.JOB_GROUP_NAME)
							.usingJobData("id", taskInfo.getTaskid())
							.usingJobData("runid", taskInfo.getRunid())
							.usingJobData("taskName", taskInfo.getTaskName())
							.usingJobData("programTpye", taskInfo.getParamsType())
							.usingJobData("methodName", taskInfo.getMethodName())
							.usingJobData("programs", taskInfo.getPrograms())
							.usingJobData("filePath", taskInfo.getFilePath())
							.usingJobData("fileName", taskInfo.getFileName())
							.build();
				}
				
				scheduler.scheduleJob(job, trigger);
				scheduler.start();
				
				// 记录下一次调度时间
				DcTaskTimeService next = SpringContextHolder.getBean(DcTaskTimeService.class);
				DcTaskTime nextobj = new DcTaskTime();
				nextobj.setId(taskInfo.getTaskid());
				nextobj.setNexttime(trigger.getNextFireTime());
//				nextobj.preUpdate();
				nextobj.setUpdateBy(UserUtils.getUser());
				nextobj.setUpdateDate(new Date());
				next.save(nextobj);
				
				result.setRst_flag(true);
				result.setRst_std_msg("任务["+taskInfo.getTaskName()+"]执行成功!");
				
			} else { // 手动
				ITask task = null;
				if (TaskType.CLASS.equals(taskInfo.getTaskTpye())) {
					task = new TaskClass();
				} else if (TaskType.JARS.equals(taskInfo.getTaskTpye())) {
					task = new TaskJars();
				} else if (TaskType.SHELL.equals(taskInfo.getTaskTpye())) {
					task = new TaskShell();
				}
				return task.doTask(taskInfo);
			}
		} catch (Exception e) {
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
			log.error("-->doTaskProcess: ", e);
		}
		return result;
	}
	
	/**
	 * 验证参数
	 * @param taskInfo
	 * @throws SchedulerException
	 */
	private static void verifyParameters(TaskInfo taskInfo) throws SchedulerException {
		if (StringUtils.isEmpty(taskInfo.getTaskid())
			|| StringUtils.isEmpty(taskInfo.getTaskName())
			|| StringUtils.isEmpty(taskInfo.getRunid())) {
			throw new SchedulerException("参数错误或参数不足！");
		}
		
		if (TaskType.CLASS.equals(taskInfo.getTaskTpye())) {
			if (StringUtils.isEmpty(taskInfo.getClassName())
				|| StringUtils.isEmpty(taskInfo.getMethodName())) {
				throw new SchedulerException("参数错误或参数不足！");
			}
		} else if (TaskType.JARS.equals(taskInfo.getTaskTpye())) {
			if (StringUtils.isEmpty(taskInfo.getClassName())
				|| StringUtils.isEmpty(taskInfo.getMethodName())
				|| StringUtils.isEmpty(taskInfo.getFilePath())) {
				throw new SchedulerException("参数错误或参数不足！");
			}
		} else if (TaskType.SHELL.equals(taskInfo.getTaskTpye())) {
			if (StringUtils.isEmpty(taskInfo.getFilePath())
				|| StringUtils.isEmpty(taskInfo.getMethodName())
				|| StringUtils.isEmpty(taskInfo.getFileName())) {
				throw new SchedulerException("参数错误或参数不足！");
			}
		} else {
			throw new SchedulerException("错误的任务类型 ： " + taskInfo.getTaskTpye());
		}
	}
}