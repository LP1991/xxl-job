/********************** 版权声明 *************************
 * 文件名: DcTaskDemo.java
 * 包名: com.hzhl.dc.schedule.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月11日 上午9:56:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.schedule.service;

import com.hlframe.modules.dc.schedule.entity.DcTaskLog;
import com.hlframe.modules.dc.utils.DcBeanUtils;
import com.hlframe.modules.dc.utils.DcEsUtil;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.elasticsearch.client.Client;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @类名: com.hlframe.modules.dc.schedule.service.DcDemoTaskService.java 
 * @职责说明: 调度任务demo 仅供其他任务参考
 * @创建者: peijd
 * @创建时间: 2016年11月11日 上午9:56:22
 */
@Service
public class DcDemoTaskService implements Job{

	/**
	 * @throws ParseException
	 * @throws Exception
	 * @方法名称: checkEsIndex 
	 * @实现功能: 检查es 索引是否存在
	 * @create by peijd at 2016年11月11日 上午9:59:26
	 */
	public String checkEsIndex() throws ParseException {
		String result = "";
		try {
			String indexName = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "");
			Client client = DcEsUtil.getClient();
			System.out.println("----->调度任务demo:  索引["+indexName+"]"+(DcEsUtil.checkIndex(client, indexName)?"已存在...":"不存在!!!"));
			result = "调度任务demo:  索引["+indexName+"]"+(DcEsUtil.checkIndex(client, indexName)?"已存在...":"不存在!!!");
		} catch (Exception e) {
		
			System.out.println("索引异常"+e);
		}
		return  result;
	}
	
	@Override
	public void execute(JobExecutionContext arg0) {
	 DcTaskLogService dcTaskLogService = DcBeanUtils.getBean(DcTaskLogService.class);
		DcTaskLog dcTaskLog = new DcTaskLog();
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(sdf.format(new Date()));
			Date startDate = sdf.parse( sdf.format(new Date()));
			dcTaskLog.setStartDate(startDate);  
			
			dcTaskLog.setClassName("com.hlframe.modules.dc.service.DcDemoTaskService");
			dcTaskLog.setMethodName("checkEsIndex");
			
			String result = checkEsIndex();
			dcTaskLog.setParams("");
			dcTaskLog.setStatus("0");//正在运行
			Date endDate =  sdf.parse( sdf.format(new Date()));
			dcTaskLog.setEndDate(endDate);
			long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别
			dcTaskLog.setResults("本任务用时"+""+diff+"秒"+" 结果："+result);
		} catch (ParseException e) {
			e.printStackTrace();
			dcTaskLog.setStatus("1");
			dcTaskLog.setException("索引异常"+e);
		}
		dcTaskLogService.save(dcTaskLog);

	}
public static void main(String[] args) {
	 DcTaskLogService dcTaskLogService =DcBeanUtils.getBean(DcTaskLogService.class);
	 System.out.println(dcTaskLogService);
}
}
