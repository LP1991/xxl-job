/********************** 版权声明 *************************
 * 文件名: DcJobExportDataService.java
 * 包名: com.hlframe.modules.dc.dataexport.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月23日 下午5:03:31
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataexport.dao.DcJobExportDataDao;
import com.hlframe.modules.dc.dataexport.entity.DcJobExportData;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransData;
import com.hlframe.modules.dc.dataprocess.service.DcHdfsFileService;
import com.hlframe.modules.dc.dataprocess.service.DcJobTransCjThread;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;
import com.hlframe.modules.dc.metadata.entity.DcObjectAu;
import com.hlframe.modules.dc.metadata.service.DcDataSourceService;
import com.hlframe.modules.dc.metadata.service.DcObjectAuService;
import com.hlframe.modules.dc.metadata.service.linkdb.DbHandleService;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;
import com.hlframe.modules.dc.schedule.entity.DcTaskQuery;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.*;
import com.hlframe.modules.sys.utils.UserUtils;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.dataexport.service.DcJobExportDataService.java 
 * @职责说明: 数据导出Service
 * @创建者: peijd
 * @创建时间: 2017年2月23日 下午5:03:31
 */
@Service
@Transactional(readOnly = true)
public class DcJobExportDataService extends CrudService<DcJobExportDataDao, DcJobExportData> implements DcTaskService {

	@Autowired
	private DcDataSourceService dataSourceService;
	@Autowired
	private DbHandleService dbHandleService;
	@Autowired
	private DcHdfsFileService hdfsService;
	@Autowired	//权限表service
	private DcObjectAuService dcObjectAuService;

	/**
	 * @方法名称: getObjByName 
	 * @实现功能: 根据名称查找对象
	 * @param jobName	job名称 
	 * @return
	 * @create by peijd at 2017年2月23日 下午5:17:16
	 */
	public DcJobExportData getObjByName(String jobName, String jobId) {
		Assert.hasText(jobName);
		return dao.getObjByName(jobName, jobId);
	}
	
	/**
	 * Override
	 * @方法名称: save 
	 * @实现功能: 保存时设置默认记录状态
	 * @param entity
	 * @create by peijd at 2017年3月10日 上午11:28:16
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(DcJobExportData entity) {

		//设置记录默认状态
		if(StringUtils.isBlank(entity.getStatus()) ){
			entity.setStatus(entity.TASK_STATUS_EDIT);
		}
		super.save(entity);
	}
	
	/**
	 * Override
	 * @方法名称: runTask 
	 * @实现功能: 运行采集任务 
	 * @param jobId
	 * @return
	 * @throws Exception
	 * @create by peijd at 2017年2月27日 上午10:49:53
	 */
	@Transactional(readOnly = false)
	public DcDataResult runTask(String jobId) throws Exception {
		Assert.hasText(jobId);
		DcJobExportData jobData = get(jobId);
		
		DcDataResult taskResult = new DcDataResult();
		StringBuilder result = new StringBuilder(1024);
		
		try {
			//开始执行时间
			Date beginDate = new Date();
			//执行数据导出任务
			Result res = exportData(jobData);
			
			result.append("-->任务名称: ").append(jobData.getJobName());
			result.append("<br>-->开始时间: ").append(DateUtils.formatDateTime(beginDate));
			result.append("<br>-->结束时间: ").append(DateUtils.formatDateTime(new Date()));
			result.append("<br>-->调用结果: ").append(0==res.rc?"成功":"失败");	//返回结果判断异常
//			result.append("<br>-->调用结果: ").append("成功");	//产品演示, 全部返回成功
			result.append("<br>-->简要日志: <br>  ").append(DcFileUtils.formatSqoopLog(res.sysout, "<br>", "Export"));
			result.append("<br>-->详细日志: <br>  ").append(DcFileUtils.formatSqoopLog(res.error_msg, "<br>", "Export"));
			DcJobExportData job = new DcJobExportData();
			//保存日志路径
			job.setId(jobData.getId());
			job.setLogDir(hdfsService.buildBizLoggerPath(jobData.getId(), "exportData_"+beginDate.getTime()));
			//更新任务状态
			if(job.TASK_STATUS_EDIT.equals(jobData.getStatus())){
				job.setStatus(job.TASK_STATUS_TEST);
			}
			job.preUpdate();
			dao.update(job);
			
			//将日志记录到HDFS  默认为采集目录
			hdfsService.writeData2Hdfs(job.getLogDir(), result.toString().replaceAll("<br>", "\r\n"));
			taskResult.setRst_flag(true);
			taskResult.setRst_std_msg(result.toString());
			
		} catch (Exception e) {
			logger.error("-->export data to RDBMS: runTask",e);
			taskResult.setRst_err_msg(e.getMessage());
			taskResult.setRst_flag(false);
		}
		return taskResult;
	}
	
	/**
	 * @方法名称: exportData 
	 * @实现功能: 导出数据至Rdbms
	 * @param jobData	导出数据对象
	 * @return
	 * @create by peijd at 2017年2月27日 上午11:26:03
	 */
	private Result exportData(DcJobExportData jobData) {
		Result res = null;
		DcDataSource dataSource = dataSourceService.get(jobData.getToLink());
		
		//构建任务脚本
		String sqoopScript = buildTaskScript(jobData, dataSource);
		//创建ssh连接实例
		/*ConnBean cb = new ConnBean(DcPropertyUtils.getProperty("sqoop.client.address"),
						DcPropertyUtils.getProperty("sqoop.client.loginUser", "root"), 
						DcPropertyUtils.getProperty("sqoop.client.loginPswd"));
		SSHExec ssh = SSHExec.getInstance(cb);*/

		SSHExec ssh = null;
		DcTaskQuery dtq = null;

		//准备源路径与备份路径
		String tableName = jobData.getSchemaName()+"."+jobData.getTableName(), 
			   backTable = jobData.getSchemaName()+"."+ DcPropertyUtils.getProperty("rdbms.export.baktable.prefix")+jobData.getTableName();
		
		try {
			DcJobTransCjThread dcJobTransCjThread = new DcJobTransCjThread();
			dcJobTransCjThread.start();
			//返回结果以第一个线程为主， 具体执行细节需要参考日志信息。
			dcJobTransCjThread.join();
			dtq = dcJobTransCjThread.getDtq();
			ssh = dtq.getSsh();

			// 连接到hdfs
			ssh.connect();
			//先备份原有数据 
			backupData(dataSource, backTable, tableName);
			//是否清空原有数据
			if(jobData.getClearDataFlag()){
				dbHandleService.clearTableData(dataSource, tableName);
			}
			
			//执行采集脚本
			logger.debug("sqoopScript:-->"+sqoopScript);
			res = ssh.exec(new ExecCommand(sqoopScript));
			//返回的永远是错误标记... 根据返回错误信息 手工判断是否执行成功
			if(checkResultErr(res)){
				res.rc=0;
				return res;
			}else{
				//采集失败  还原数据, 将备份数据表数据恢复
				dbHandleService.copyData2TarTable(dataSource, backTable, tableName);
			}
		} catch (Exception e) {
			logger.error("-->export Data:", e);
		}finally{
			//任务结束 关闭占用状态
			dtq.setStatus(DcTaskQuery.SSH_STATUS_FREE);
			DcTaskQueryUtils.dcTaskQueryList.set(dtq.getWeiZhi(),dtq);
		}
				
		return res;
	}
	
	/**
	 * @方法名称: backupData 
	 * @实现功能: 备份数据, 初始化备份表, 插入备份表数据, 
	 * @param dataSource	数据源连接
	 * @param bakTable		备份表
	 * @param srcTable		数据表		
	 * @create by peijd at 2017年2月27日 下午5:52:30
	 */
	private void backupData(DcDataSource dataSource, String bakTable, String srcTable) {
		Assert.notNull(dataSource);
		Assert.hasText(bakTable);
		Assert.hasText(srcTable);
		
		//检查table 是否存在, 不存在则创建,存在则清空数据 
		if(dbHandleService.existTable(dataSource, bakTable)){
			//清空备份表
			dbHandleService.clearTableData(dataSource, bakTable);
			dbHandleService.copyData2TarTable(dataSource, srcTable, bakTable);
			
		}else{
			//创建备份表, 并插入数据
			dbHandleService.createTable(dataSource, bakTable, srcTable, true);
		}
		
	}


	/**
	 * @方法名称: buildTaskScript 
	 * @实现功能: 构建sqoop export 脚本
	 * @param jobData
	 * @param dataSource
	 * @return
	 * @create by peijd at 2017年2月27日 上午11:48:44
	 */
	private String buildTaskScript(DcJobExportData jobData, DcDataSource dataSource) {
		StringBuilder taskScript = new StringBuilder(200);
		//手动构建sqoop export 脚本 
		if(StringUtils.isBlank(jobData.getRemarks())){

			taskScript.append("sudo -u hdfs sqoop export");
			//设置线程数
			if(jobData.getMapNum()>0){
				taskScript.append(" -m ").append(jobData.getMapNum());
			}
			//指定数据表
			taskScript.append(" --table ").append(jobData.getTableName().toUpperCase());
			//添加列数据
			if(StringUtils.isNotEmpty(jobData.getTableColumn())){
				taskScript.append(" --columns ").append(jobData.getTableColumn());
			}
			String dataPath = jobData.getDataPath();
			//导出数据源设置   从hdfs导出
			if(DcJobTransData.TOLINK_HDFS.equals(jobData.getFromLink())){
				taskScript.append(" --export-dir ").append(dataPath);
				
			}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getFromLink())){	//从hive导出  拆分schema.table,未指定schema时默认为default
				if(dataPath.indexOf(".")>0){
					taskScript.append(" --hcatalog-database ").append(dataPath.split("[.]")[0]);
					taskScript.append(" --hcatalog-table ").append(dataPath.split("[.]")[1]);
				}else{
					taskScript.append(" --hcatalog-database default --hcatalog-table ").append(dataPath);
				}
				
			}
			//空值替换
			if(StringUtils.isNotEmpty(jobData.getNullString())){
				taskScript.append(" --input-null-string ").append(jobData.getNullString());
			}
			if(StringUtils.isNotEmpty(jobData.getNullNonString())){
				taskScript.append(" --input-null-non-string ").append(jobData.getNullNonString());
			}
			//字段分隔符
			if(StringUtils.isNotEmpty(jobData.getFieldSplitBy())){
				taskScript.append(" --input-fields-terminated-by ").append(jobData.getFieldSplitBy());
			}
			
			//增量方式
			if(!DcJobExportData.UPDATEMODE_INSERTONLY.equals(jobData.getUpdateMode())){
				taskScript.append(" --update-mode ").append(jobData.getUpdateMode()).append(" --update-key ").append(jobData.getUpdateKey());
			}
			
		}else{
			taskScript.append(jobData.getRemarks());
		}
		//构建数据源 连接信息
		buildDataBaseLink(jobData, taskScript, dataSource);
		
		logger.debug("--> sqoop task script: "+taskScript.toString());
		return taskScript.toString();
				
	}


	/**
	 * @方法名称: buildDataBaseLink 
	 * @实现功能: 构建数据source 
	 * @param jobData
	 * @param taskScript
	 * @param dataSource
	 * @create by peijd at 2017年2月27日 上午11:53:20
	 */
	private void buildDataBaseLink(DcJobExportData jobData, StringBuilder taskScript, DcDataSource dataSource) {
		// 数据源连接对象
		taskScript.append(" --connect ");
		// 构建数据源连接
		if (DcDataSource.DB_SERVER_TYPE_MYSQL.equalsIgnoreCase(dataSource.getServerType())) { // jdbc:mysql://10.1.20.86:3306/, 用数据表的schema
			taskScript.append("jdbc:mysql://").append(dataSource.getServerIP()).append(":").append(dataSource.getServerPort()).append("/").append(jobData.getSchemaName());

		} else if (DcDataSource.DB_SERVER_TYPE_ORACLE.equalsIgnoreCase(dataSource.getServerType())) { // jdbc:oracle:thin:@10.1.20.86:1521:orcl_test
			taskScript.append("jdbc:oracle:thin:@").append(dataSource.getServerIP()).append(":").append(dataSource.getServerPort()).append(":").append(dataSource.getServerName());
			// 指定驱动 taskScript.append(" --driver
			// oracle.jdbc.driver.OracleDriver");
			// 指定连接管理类 与驱动类存在冲突
			if (taskScript.indexOf("connection-manager") <= 0) {
				taskScript.append(" --connection-manager org.apache.sqoop.manager.OracleManager ");
			}
			// 指定table Schema名称
			if (taskScript.indexOf("--table") > 0) {
				taskScript.insert(taskScript.indexOf("--table") + 8, jobData.getSchemaName() + ".");
			}
			
		}else if(DcDataSource.DB_SERVER_TYPE_SQLSERVER.equalsIgnoreCase(dataSource.getServerType())){	//jdbc:sqlserver://10.1.21.13:1433;username=pjd16;password=pjd16;database=enjoy_shq
			taskScript.append("'jdbc:sqlserver://").append(dataSource.getServerIP()).append(":").append(dataSource.getServerPort());
			taskScript.append(";username=").append(dataSource.getServerUser()).append(";password=").append(Des.strDec(dataSource.getServerPswd()));
			taskScript.append(";database=").append(dataSource.getServerName()).append("'");
			
		}else if(DcDataSource.DB_SERVER_TYPE_DB2.equalsIgnoreCase(dataSource.getServerType())){	//jdbc:db2://10.1.20.86:50000/DCTEST
			taskScript.append("jdbc:db2://").append(dataSource.getServerIP()).append(":").append(dataSource.getServerPort()).append("/").append(dataSource.getServerName());
			//db2的表名 需加上schema名称
			if (taskScript.indexOf("--table") > 0) {
				taskScript.insert(taskScript.indexOf("--table") + 8, jobData.getSchemaName() + ".");
			}
			
		}else if(DcDataSource.DB_SERVER_TYPE_POSTGRESQL.equalsIgnoreCase(dataSource.getServerType())){	//jdbc:postgresql://10.1.20.86:5432/dctest
			taskScript.append("jdbc:postgresql://").append(dataSource.getServerIP()).append(":").append(dataSource.getServerPort()).append("/").append(dataSource.getServerName());
		}
		taskScript.append(" --username ").append(dataSource.getServerUser());
		taskScript.append(" --password ").append(Des.strDec(dataSource.getServerPswd()));
	}


	/**
	 * @方法名称: checkResultErr 
	 * @实现功能: 判断ssh是否执行成功, 如果是错误标记, 则通过result.err_msg 手动解析 
	 * @param res
	 * @return
	 * @create by peijd at 2016年12月14日 下午5:45:33
	 */
	private boolean checkResultErr(Result res) {
		if(res.isSuccess){
			return true;
		}
		
		//手工解析error信息 判断日志中是否存在'completed successfully'
		return res.error_msg.indexOf("completed successfully")>0;
	}
	
	/**
	 * @方法名称: previewDbData 
	 * @实现功能: 预览导出结果数据
	 * @param jobId
	 * @return
	 * @create by peijd at 2017年2月27日 上午10:58:06
	 */
	public List<Map<String, Object>> previewDbData(String jobId) {
		Assert.hasText(jobId);
		//构建数据对象
		DcJobExportData jobData = get(jobId);
		Assert.notNull(jobData);
		
		//构建查询SQL
		StringBuilder tableSql = new StringBuilder(128);
		tableSql.append("SELECT ").append(StringUtils.isEmpty(jobData.getTableColumn())?"*":jobData.getTableColumn());
		if(StringUtils.isNotEmpty(jobData.getTableName())){
			tableSql.append(" FROM ");
			//数据需要连接schema
			tableSql.append(dbHandleService.buildTableName(jobData.getToLink(), jobData.getSchemaName(), jobData.getTableName())).append(" ");
		}	
		//获取返回结果 默认显示
		return dbHandleService.queryLimitMetaSql(jobData.getToLink(),tableSql.toString(), Integer.parseInt(DcPropertyUtils.getProperty("extractdb.preview.datanum", "50")));
	}
	/**
	 * @方法名称: getAu 
	 * @实现功能: 发起权限申请请求
	 * @param obj
	 * @create by yuzh at 2017年1月19日 上午11:20:48
	 */
	@Transactional(readOnly = false)
	public void getAu(DcJobTransData obj) throws  Exception {
		DcObjectAu dcObjectAu = new DcObjectAu();
		dcObjectAu.setUserId(UserUtils.getUser().getId());
		dcObjectAu.setFileId(obj.getId());
		dcObjectAu.setStatus("未处理");
		dcObjectAu.setFrom("3");
		if(!DcStringUtils.isNotNull(dcObjectAuService.get(dcObjectAu))){
		dcObjectAuService.save(dcObjectAu);
			throw new Exception("已向管理员申请该任务操作权限，请等待管理员审核!");
		}else {
			dcObjectAuService.classify(dcObjectAu);
		}
	}
	
	/**
	 * Override
	 * @方法名称: delete 
	 * @实现功能: 删除之前验证任务状态
	 * @param entity
	 * @create by peijd at 2017年3月10日 上午10:25:41
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(DcJobExportData entity) {
		Assert.hasText(entity.getId());
		entity = dao.get(entity.getId());
		Assert.notNull(entity);
		// 判断任务状态 如果是调度任务  则不可删除
		if(DcJobExportData.TASK_STATUS_TASK.equals(entity.getStatus())){
			throw new ServiceException("该任务已添加调度任务, 不可删除!");
		}
		super.delete(entity);
	}

	/**
	 * @方法名称: updateStatus 
	 * @实现功能: 更新业务状态
	 * @param objId
	 * @param status
	 * @create by peijd at 2017年3月10日 下午2:51:29
	 */
	@Transactional(readOnly = false)		
	public void updateStatus(String objId, String status){
		Assert.hasText(objId);
		status = StringUtils.isEmpty(status)?DcJobExportData.TASK_STATUS_EDIT:status;
		DcJobExportData obj = new DcJobExportData();
		obj.setId(objId);
		obj.setStatus(status);
		obj.preUpdate();
		dao.update(obj);
	}

}
