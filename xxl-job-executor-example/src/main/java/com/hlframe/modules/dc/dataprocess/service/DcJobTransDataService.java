/********************** 版权声明 *************************
 * 文件名: DcJobTransDataService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月16日 下午9:00:57
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.MyBeanUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.DcConstants;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransDataDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransDataLinkDbDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransDataLinkHdfsDao;
import com.hlframe.modules.dc.dataprocess.entity.*;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainVersionDao;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.dc.metadata.service.DcDataSourceService;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.metadata.service.DcObjectAuService;
import com.hlframe.modules.dc.metadata.service.DcObjectLinkService;
import com.hlframe.modules.dc.metadata.service.linkdb.DbHandleService;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;
import com.hlframe.modules.dc.schedule.entity.DcTaskQuery;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.*;
import com.hlframe.modules.sys.utils.UserUtils;
import com.xxl.job.core.biz.model.ReturnT;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcJobTransDataService.java
 * @职责说明: 数据转换任务 Service
 * @创建者: peijd
 * @创建时间: 2016年11月16日 下午9:00:57
 */
@Service
@Transactional(readOnly = true)
public class DcJobTransDataService extends CrudService<DcJobTransDataDao, DcJobTransData> implements DcTaskService {

	@Autowired
	private DcJobTransDataLinkDbDao linkDbDao;
	
	@Autowired	
	private DcJobTransDataLinkHdfsDao linkHdfsDao;
	
	@Autowired	//数据链路 Service
	private DcObjectLinkService linkService;
	
	@Autowired	//数据源连接Service
	private DcDataSourceService dcDataSourceService;

	@Autowired	//数据源引擎 Service 用于执行异构数据库的脚本
	private DbHandleService dbHandleService;
	
	@Autowired	//hdfs Service
	private DcHdfsFileService hdfsService;
	
	@Autowired	//元数据存储Service
	private DcMetadataStroeService dcMetadataStroeService;
	
	@Autowired	//权限表service
	private DcObjectAuService dcObjectAuService;
	
	@Autowired //源数据service
	private DcObjectMainDao dcObjectMainDao;
	
	@Autowired
	private DcCommonService dcCommonService;

	@Autowired	//数据转换引擎 Service
	private DcTransEngineService engineService;

	@Autowired
	private DcObjectMainVersionDao dcObjectMainVersionDao;
	
//	private DcQueryHiveService queryHiveService = null;
	
	/**
	 * @方法名称: save 
	 * @实现功能: 保存数据转换任务
	 * @param jobData
	 * @create by peijd at 2016年11月17日 下午2:36:18
	 */
	@Transactional(readOnly = false)
	public void save(DcJobDb2Hdfs jobData) {
		Assert.notNull(jobData);
		DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
		try {
			String jobId = StringUtils.isNotBlank(jobData.getId())?jobData.getId():IdGen.uuid();
			DcJobTransData mainJob = new DcJobTransData();
			//复制对象内容 至Job主表 并保存
			MyBeanUtils.copyBean2Bean(mainJob, jobData);
			mainJob.setId(jobId);
			//设置记录默认状态
			if(StringUtils.isBlank(jobData.getStatus()) ){
				mainJob.setStatus(mainJob.TASK_STATUS_EDIT);
			}
			super.save(mainJob);
			
			//fromLink
			DcJobTransDataLinkDb fromLink = new DcJobTransDataLinkDb();
			MyBeanUtils.copyBean2Bean(fromLink, jobData);
			fromLink.setJobId(jobId);
			if(jobData.getIsNewRecord()){
				fromLink.setId(IdGen.uuid());
				linkDbDao.insert(fromLink);
			}else{
				linkDbDao.update(fromLink);
			}
			
			//toLink
			DcJobTransDataLinkHdfs toLink = new DcJobTransDataLinkHdfs();
			MyBeanUtils.copyBean2Bean(toLink, jobData);
			toLink.setJobId(jobId);
			//设置hbase/hive表字段
			if(DcJobTransData.TOLINK_HIVE.equals(mainJob.getToLink())){			//采集至hive
				jobData.setOutputTable(jobData.getTbNameHive());
				toLink.setOutputTable(jobData.getTbNameHive());
				toLink.setIsCreateTable(jobData.getCreateTbHive()?DcConstants.DC_RESULT_FLAG_TRUE:DcConstants.DC_RESULT_FLAG_FALSE);
			}else if(DcJobTransData.TOLINK_HBASE.equals(mainJob.getToLink())){	//采集至hbase
				jobData.setOutputTable(jobData.getTbNameHive());
				toLink.setOutputTable(jobData.getTbNameHbase());
				toLink.setIsCreateTable(jobData.getCreateTbHbase()?DcConstants.DC_RESULT_FLAG_TRUE:DcConstants.DC_RESULT_FLAG_FALSE);
			}
			String outputDir = toLink.getOutputDir();
			
			//如果输出目录以'/'结尾, 去除最后一个'/'
			if(outputDir.endsWith("/")){
				toLink.setOutputDir(StringUtils.stripEnd(outputDir, "/"));
			}
			if(jobData.getIsNewRecord()){
				toLink.setId(IdGen.uuid());
				linkHdfsDao.insert(toLink);
			}else{
				linkHdfsDao.update(toLink);
			}
			
			//目标表  设计链路关系  
			String targetObject = toLink.getOutputTable();
			//如果是hdfs文件  则保存为job名称
			if(DcJobTransData.TOLINK_HDFS.equals(mainJob.getToLink())){
				targetObject = jobData.getJobName();
			}else{
				//若未指定表空间, 则默认使用default, 主要为避免数据链路重名问题
				if(targetObject.indexOf(".")<0){
					targetObject = "default."+targetObject;
				}
			}
			
			DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
			//根据jobId 获取源对象
			DcObjectMain param = new DcObjectMain();
			param.setJobId(jobId);
			param.setJobSrcFlag("N");
			srcMain = dcObjectMainDao.get(param);
			//元数据版本设置信息
			dcObjectMainVersion.setJobId(jobId);
			if(null==srcMain){
				//元数据版本状态设为增加
				dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
				srcMain = new DcObjectMain();
				srcMain.setId(IdGen.uuid());
			}else{
				//元数据版本状态设为编辑
				dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
			}
				//判断src_flag 如果是N 则更新源对象
		
			//保存数据源对象   
//			DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
			srcMain.setJobId(jobId);							//任务Id
			srcMain.setJobType(DcObjectMain.JOB_TYPE_EXTRACT_DB);		//采集
			srcMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);		//外部数据
			srcMain.setObjCode(jobData.getJobName());					//任务编码
			srcMain.setObjName(jobData.getJobName());
			srcMain.setObjDesc(jobData.getJobDesc());					//备注信息
			srcMain.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
			jobData.setId(jobId);
			DcObjectTable srcTable = new DcObjectTable(); 				//元数据 table补充
			srcTable.setId(srcMain.getId());
			srcTable.setObjId(srcMain.getId());
			srcTable.setDbDataBase(jobData.getSchemaName());			//schema 名称
			srcTable.setTableName(jobData.getTableName());				//表名
			srcTable.setTableLink(jobData.getFromLink());				//数据库连接
			srcTable.setDbType(jobData.getDbServerType());				//数据库类别(null ??)
			
			List<DcObjectField> srcFieldList = new ArrayList<DcObjectField>();	//数据表字段 暂不存储
			dcMetadataStroeService.insertTableVersion(srcMain,dcObjectMainVersion,srcTable);
			dcMetadataStroeService.obj2MySQL(srcMain, srcTable, srcFieldList);
			//保存目标对象
			DcObjectMain tarMain = saveTransMetaData(jobData);
			//保存元数据 链路关系
			linkService.configObjLinkByExtractDB(jobId, srcMain.getId(), tarMain.getId());
		} catch (Exception e) {
			logger.error("DcJobTransDataService.save",e);
			throw new ServiceException(e);
		}
		
	
		
	}

	/**
	 * @方法名称: buildJobData 
	 * @实现功能: 根据jobId  构建job对象
	 * @param jobid
	 * @return
	 * @create by peijd at 2016年11月17日 下午2:18:00
	 */
	public DcJobDb2Hdfs buildJobData(String jobid) {
		Assert.hasText(jobid);
		return dao.buildJobData(jobid);
	}
	
	/**
	 * Override
	 * @方法名称: delete 
	 * @实现功能: 删除Job信息, 删除关联信息
	 * @param entity
	 * @create by peijd at 2016年11月22日 下午4:13:57
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(DcJobTransData entity) {
		Assert.hasText(entity.getId());
		entity = dao.get(entity.getId());
		Assert.notNull(entity);
		
		// 判断任务状态 如果是调度任务  则不可删除
		if(DcJobTransData.TASK_STATUS_TASK.equals(entity.getStatus())){
			throw new ServiceException("该任务已添加调度任务, 不可删除!");  
		}
		
		DcJobTransDataLinkDb delLink = new DcJobTransDataLinkDb();
		delLink.setJobId(entity.getId());
		linkDbDao.deleteByLogic(delLink);
		
		DcJobTransDataLinkHdfs delHdfs = new DcJobTransDataLinkHdfs();
		delHdfs.setJobId(entity.getId());
		linkHdfsDao.deleteByLogic(delHdfs);
		
		//逻辑删除
		dao.deleteByLogic(entity);
		
		//删除源数据记录
		DcObjectMain dcObjectMain = new DcObjectMain();
		dcObjectMain.setJobId(entity.getId());
		List<DcObjectMain> dcMainList = dcObjectMainDao.findList(dcObjectMain);
		for(DcObjectMain m:dcMainList) {
			dcObjectMainDao.deleteByLogic(m);
			//Es删除对应记录
			dcCommonService.deleteEsByIndex(m.getId(), "table");
		}
	}

	/**
	 * @方法名称: runTask 
	 * @实现功能: 运行采集任务  
	 * @param jobId	jobId
	 * @create by peijd at 2016年11月17日 下午5:00:20
	 * @update by peijd at 2017-02-16 实现调度任务接口
	 */
	@Transactional(readOnly = false)
	public DcDataResult runTask(String jobId) throws Exception {
		Assert.hasText(jobId);
		
		DcDataResult taskResult = new DcDataResult();
		StringBuilder result = new StringBuilder(1024);
		//构建数据对象
		DcJobDb2Hdfs jobData = buildJobData(jobId);
		Assert.notNull(jobData);
		
		try {
			//开始执行时间
			Date beginDate = new Date();
			//执行DB采集任务
			Result res = extractDbData(jobData);
			result.append("-->任务名称: ").append(jobData.getJobName());
			result.append("<br>-->开始时间: ").append(DateUtils.formatDateTime(beginDate));
			result.append("<br>-->结束时间: ").append(DateUtils.formatDateTime(new Date()));
			result.append("<br>-->调用结果: ").append(res.rc==0?"成功":"失败");
			result.append("<br>-->简要日志: <br>  ").append(DcFileUtils.formatSqoopLog(res.sysout, "<br>", "Import"));
			result.append("<br>-->详细日志: <br>  ").append(DcFileUtils.formatSqoopLog(res.error_msg, "<br>", "Import"));
			DcJobTransData job = new DcJobTransData();
			//保存日志路径
			job.setId(jobData.getId());
			job.setLogDir(hdfsService.buildBizLoggerPath(jobData.getId(), "extract_DB_"+beginDate.getTime()));
			//更新其他信息(采集至hive/hbase, 如果第一次创建了表, 后续任务则不再创建)
			if(!DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink()) && DcConstants.DC_RESULT_FLAG_TRUE.equals(jobData.getIsCreateTable())){
				DcJobTransDataLinkHdfs linkTo = new DcJobTransDataLinkHdfs();
				linkTo.setJobId(jobId);
				linkTo.preUpdate();
				linkTo.setIsCreateTable(DcConstants.DC_RESULT_FLAG_FALSE);
				linkHdfsDao.update(linkTo);
				
				//更新运行脚本
				String script = jobData.getRemarks();
				if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())){
					job.setRemarks(script.replace("--create-hive-table", ""));
				}else if(DcJobTransData.TOLINK_HBASE.equals(jobData.getToLink())){
					job.setRemarks(script.replace("--hbase-create-table", ""));
				}
			}
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
			logger.error("-->extract data from DB: runTask",e);
			taskResult.setRst_err_msg(e.getMessage());
			taskResult.setRst_flag(false);
//			throw e;	//不能throw 否则无法保存日志位置
		}
		return taskResult;
	}


	/**
	 * @方法名称: extractDbData 
	 * @实现功能: 采集Db数据(调用sqoop client1 脚本)
	 * @param jobData
	 * @return
	 * @create by peijd at 2016年12月13日 上午11:45:13
	 */
	private Result extractDbData(DcJobDb2Hdfs jobData) {
		Result res = null;
		StringBuilder metaScript = new StringBuilder(64);
		DcDataSource dataSource = dcDataSourceService.get(jobData.getFromLink());
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
		String objPath = null, bakPath = null, hdfsBakPath = null;
		if(DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink())){
			objPath = jobData.getOutputDir(); 
			bakPath = DcPropertyUtils.getProperty("hdfs.extractJob.backupDir");
			hdfsBakPath = bakPath+StringUtils.substringAfterLast(objPath, "/");		//Hdfs备份文件完整目录
			//首先判断hdfs目录上的文件是否存在,存在先删除
			FileSystem fs = null;
			try{
				//初始化
				String address = DcPropertyUtils.getProperty("hadoop.main.address");
				//出错，不知道什么原因
				fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
				if(fs.exists(new Path(jobData.getOutputDir()))){
					fs.delete(new Path(jobData.getOutputDir()),true);
				}
			}catch(Exception e){
				logger.error("-->extractDbData:", e);
			}
		}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())){
			objPath = jobData.getOutputTable(); 
			bakPath = DcPropertyUtils.getProperty("hive.extractJob.backupDB")+"."+jobData.getOutputTable();
		}
		try {
			// 连接到hdfs
			/*ssh.connect();*/
			 DcJobTransCjThread dcJobTransCjThread = new DcJobTransCjThread();
			 dcJobTransCjThread.start();
			 //返回结果以第一个线程为主， 具体执行细节需要参考日志信息。
			 dcJobTransCjThread.join();
			 dtq = dcJobTransCjThread.getDtq();
			 ssh = dtq.getSsh();
			//如果是全量采集, 先备份原有数据
			if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_WHOLE.equals(jobData.getIncrementType())){
				logger.info("----> backup data begin !");
				if(DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink())){			//备份Hdfs
					//删除备份目录
					ssh.exec(new ExecCommand("sudo -u hdfs hdfs dfs -rm -r "+hdfsBakPath+"; sudo -u hdfs hdfs dfs -mv "+objPath+" "+bakPath));
				}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())){	//备份Hive表
					metaScript.setLength(0);
					//1.删除备份表
					metaScript.append("sudo -u hdfs hive -e 'DROP TABLE IF EXISTS ").append(bakPath).append(";");
					//2.原表重命名为备份表
					metaScript.append("ALTER TABLE ").append(objPath).append(" RENAME TO ").append(bakPath).append("'");
					//3.创建原表结构  sqoop自动创建
					//metaScript.append("CREATE TABLE ").append(objPath).append(" LIKE ").append(bakPath).append("'");
					ssh.exec(new ExecCommand(metaScript.toString()));
				}
				logger.info("----> backup data end !");
			}
			//执行采集脚本
			res = ssh.exec(new ExecCommand(sqoopScript));
			//返回的永远是错误标记... 根据返回错误信息 手工判断是否执行成功
			if(checkResultErr(res)){
				//采集成功  保存元数据信息
				saveTransMetaData(jobData);
				//更新增量采集 最新记录值
				updateIncrementValue(jobData, objPath);
				
			}else{
				//采集失败  还原数据(全量采集 模式下)
				if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_WHOLE.equals(jobData.getIncrementType())){
					logger.info("----> Extract data error, restore data begin !");
					if(DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink())){			//备份Hdfs
						ssh.exec(new ExecCommand("sudo -u hdfs hdfs dfs -mv "+hdfsBakPath+" "+StringUtils.substringBeforeLast(objPath, "/")));
						
					}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())){	//备份Hive表
						ssh.exec(new ExecCommand("sudo -u hdfs hive -e 'DROP TABLE IF EXISTS "+objPath+"; ALTER TABLE "+bakPath+" RENAME TO "+objPath+"'"));
					}
					logger.info("----> restore data end !");
				}
			}
		} catch (Exception e) {
			logger.error("-->extractDbData:", e);
		}finally{
            //任务结束 关闭占用状态
        	dtq.setStatus(DcTaskQuery.SSH_STATUS_FREE);
        	DcTaskQueryUtils.dcTaskQueryList.set(dtq.getWeiZhi(),dtq);
		}
		return res;
	}
	
/*	private Result extractDbData(DcJobDb2Hdfs jobData) {
		 Result res=null;
		 DcTaskQuery dtq=null;
		try {
			 DcDataSource dataSource = dcDataSourceService.get(jobData.getFromLink());
			    //构建任务脚本,每一张表为一个任务脚本
			    BlockingQueue<Map.Entry<String,String>> sqoopScripts = buildTaskScript(jobData, dataSource);
			    //获取空闲的ssh和其他信息
			    dtq = DcTaskQueryUtils.getDcTaskQueryYes();
			    //第一个线程为守护线程 join到主进程。
			    DcJobTransCjThread dcJobTransCjThread = new DcJobTransCjThread(sqoopScripts,jobData,dtq);
			    dcJobTransCjThread.start();
			    //返回结果以第一个线程为主， 具体执行细节需要参考日志信息。
			    dcJobTransCjThread.join();
			    res = dcJobTransCjThread.getRes();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        return res;
	}*/

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
		if(StringUtils.isNotBlank(res.error_msg)){
			return res.error_msg.indexOf("completed successfully")>0;
		}
		return res.isSuccess;
	}

	/**
	 * @方法名称: updateIncrementValue 
	 * @实现功能: 更新增量记录值, 从原有数据库中查询得到
	 * @param jobData	
	 * @param objPath
	 * @create by peijd at 2016年12月14日 下午5:15:54
	 */
	private void updateIncrementValue(DcJobDb2Hdfs jobData, String objPath) {
		//全量采集  不处理
		if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_WHOLE.equals(jobData.getIncrementType())){
			return;
		}
		StringBuilder metaSql = new StringBuilder(64);
		
		//查询原表中最大值 不同数据库的不用表名 进行区分处理
		metaSql.append("SELECT MAX(").append(jobData.getIncrementField()).append(") AS MAX_VAL FROM ").append(dbHandleService.buildTableName(jobData.getFromLink(),jobData.getSchemaName(),jobData.getTableName()));
		List<Map<String, Object>> rstList = dbHandleService.queryMetaSql(jobData.getFromLink(), metaSql.toString());
		if(CollectionUtils.isNotEmpty(rstList)){
			String maxValStr = DcStringUtils.getObjValue(rstList.get(0).get("MAX_VAL"));
			//postgresql 会自动将大写字段转为小写  所以要根据小写字段再查一次
			if(StringUtils.isBlank(maxValStr)){
				maxValStr = DcStringUtils.getObjValue(rstList.get(0).get("max_val"));
			}
			if(StringUtils.isNotBlank(maxValStr)){
				DcJobTransDataLinkHdfs link = new DcJobTransDataLinkHdfs();
				link.setJobId(jobData.getId());
				link.setIncrementValue(maxValStr);
				linkHdfsDao.update(link);
			}
		}
	}

	/**
	 * @方法名称: saveTransMetaData 
	 * @实现功能: 保存转换元数据信息
	 * @param jobData
	 * @create by peijd at 2016年12月13日 上午10:53:04
	 */
	@Transactional(readOnly = false)
	public DcObjectMain saveTransMetaData(DcJobDb2Hdfs jobData) {
		//保存元数据版本数据
		DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
		DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
		/* 保存采集目标对象  */
		DcObjectMain tarMain = new DcObjectMain();
		//根据jobId 获取源对象
		DcObjectMain param = new DcObjectMain();
		param.setJobId(jobData.getId());
		param.setJobSrcFlag("Y");
		srcMain = dcObjectMainDao.get(param);
		//元数据版本设置信息
		dcObjectMainVersion.setJobId(jobData.getId());
		if(null==srcMain){
			//元数据版本状态设为增加
			dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
			tarMain.setId(IdGen.uuid());
		}else{
			//元数据版本状态设为编辑
			dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
			tarMain.setId(srcMain.getId());
		}
		tarMain.setJobId(jobData.getId());							//任务Id
		tarMain.setObjDesc(jobData.getJobDesc());					//备注信息
		tarMain.setJobType(DcObjectMain.JOB_TYPE_EXTRACT_DB);		//采集
		tarMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);		//采集目标
		tarMain.setObjCode(jobData.getJobName());					//任务编码
		/* hdfs-文件对象;  hive/hbase-数据表对象 ;  */
		if(DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink())){
			tarMain.setObjName(jobData.getOutputDir());				//文件路径
			tarMain.setObjType(tarMain.OBJ_TYPE_FOLDER);			//对象类型: 文件夹
			
			//文件对象-补充信息
			DcObjectFile file = new DcObjectFile();
			file.setId(tarMain.getId());
			file.setObjId(tarMain.getId());							//文件Id
			file.setFileName(jobData.getJobName());					//文件名称
			file.setFileUrl(jobData.getOutputDir());				//文件路径
			file.setIsStruct(DcObjectFile.FILE_ISSTRUCT_Y);			//是否结构化
			//dcMetadataStroeService.insertTableVersion(tarMain,dcObjectMainVersion,jobData);
			dcMetadataStroeService.insertTableVersion(tarMain,dcObjectMainVersion,file);
			dcMetadataStroeService.obj2MySQL(tarMain, file);

		}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink()) || DcJobTransData.TOLINK_HBASE.equals(jobData.getToLink())){
			tarMain.setObjName(jobData.getOutputTable());			//数据表名
			tarMain.setObjType(tarMain.OBJ_TYPE_TABLE);				//对象类型: 数据表 
			
			//数据表对象-补充信息
			DcObjectTable dcObjectTable = new DcObjectTable();
			dcObjectTable.setObjId(tarMain.getId());
			dcObjectTable.setId(tarMain.getId());
			String tarTableName = jobData.getOutputTable(), schemaName = "";
			System.out.println("--->" +tarTableName);
			//表名称为 schemaname.tableName
			if(tarTableName.indexOf(".")>0){
				schemaName = tarTableName.split("[.]")[0];
				tarTableName = tarTableName.split("[.]")[1];
			}
			dcObjectTable.setDbDataBase(schemaName);	//表空间
			dcObjectTable.setTableName(tarTableName);	//表名
			dcObjectTable.setTableLink(jobData.getToLink());		//数据库类别
			dcObjectTable.setDbType(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())?"HIVE":"HBASE");

			//数据量记录数
			StringBuilder metaSql = new StringBuilder(64);
			//查询原表中最大值 不同数据库的不用表名 进行区分处理
			metaSql.append("SELECT COUNT(*) AS COUNT FROM ").append(dbHandleService.buildTableName(jobData.getFromLink(),jobData.getSchemaName(),jobData.getTableName()));
			List<Map<String, Object>> rstList = dbHandleService.queryMetaSql(jobData.getFromLink(), metaSql.toString());

			if(CollectionUtils.isNotEmpty(rstList)){
				String count = DcStringUtils.getObjValue(rstList.get(0).get("COUNT"));
				dcObjectTable.setDataNum(Integer.parseInt(count));
			}

			List<DcObjectField> fields = new ArrayList<DcObjectField>();
			DcObjectField dcObjectField = null;
			//字段列表 如果是* 则根据源数据表 解析字段元数据  TODO: 如果是Hive表, 需同步添加至系统hive表/表空间信息 
			if("*".equalsIgnoreCase(jobData.getTableColumn())){
				List<Map<String, Object>> fieldList = dbHandleService.queryTableFieldList(jobData.getFromLink(), jobData.getSchemaName(), jobData.getTableName());
				for(Map<String, Object> fieldMap: fieldList){
					dcObjectField = new DcObjectField();
					dcObjectField.setFieldName(DcStringUtils.getObjValue(fieldMap.get("id")));		//字段名
					dcObjectField.setFieldDesc(DcStringUtils.getObjValue(fieldMap.get("name")));	//字段描述
					dcObjectField.setFieldType("String");											//字段类型
					dcObjectField.setBelong2Id(tarMain.getId());
					fields.add(dcObjectField);
				}
//				getQueryHiveService().updateHiveField();
				
			}else{
				String[] columns= jobData.getTableColumn().split(",");
				for(String field:columns){
					dcObjectField = new DcObjectField();
					dcObjectField.setFieldName(field);							//字段名
					dcObjectField.setFieldType("String");						//字段类型
					dcObjectField.setBelong2Id(tarMain.getId());
					fields.add(dcObjectField);
				}
			}
			dcMetadataStroeService.insertTableVersion(tarMain,dcObjectMainVersion,dcObjectTable);
			//保存目标数据对象
			dcMetadataStroeService.obj2MySQL(tarMain, dcObjectTable, fields);
		}
		return tarMain;
	}
	
/*	private DcQueryHiveService getQueryHiveService() {
		if(null==queryHiveService){
			synchronized (DcJobTransDataService.class) {
				if(null==queryHiveService){
					queryHiveService = SpringContextHolder.getBean(DcQueryHiveService.class);
				}
			}
		}
		return queryHiveService;
	}*/

	/**
	 * @方法名称: buildTaskScript 
	 * @实现功能: 构建任务执行脚本
	 * @param jobData		任务信息
	 * @param dataSource	数据源连接
	 * @return
	 * @create by peijd at 2016年12月12日 下午7:18:46
	 */
	private String buildTaskScript(DcJobDb2Hdfs jobData, DcDataSource dataSource) {
		StringBuilder taskScript = new StringBuilder(200);
		if(StringUtils.isBlank(jobData.getRemarks())){
			taskScript.append("sudo -u hdfs sqoop import");
			//设置线程数
			if(jobData.getSortNum()>0){
				taskScript.append(" -m ").append(jobData.getSortNum());
			}
			//指定数据表
			taskScript.append(" --table ").append(jobData.getTableName().toUpperCase());
			//添加列数据
			if(!"*".equalsIgnoreCase(jobData.getTableColumn())){
				taskScript.append(" --columns ").append(jobData.getTableColumn());
			}
			//指定查询sql
			if(StringUtils.isNotEmpty(jobData.getTableSql())){
				taskScript.append(" --query ").append(jobData.getTableSql());
			}
			//存储到hdfs
			if(DcJobTransData.TOLINK_HDFS.equals(jobData.getToLink())){
				//指定分区字段
				taskScript.append(" --split-by ").append(jobData.getPartitionColumn());
				//指定输出目录
				taskScript.append(" --target-dir ").append(jobData.getOutputDir());
				//指定输出格式(默认为textFile)
				if("sequence".equalsIgnoreCase(jobData.getOutputFormat())){
					taskScript.append(" --as-sequencefile ");
				}
				//指定压缩格式
				String compresFormat = jobData.getCompresFormat();
				if(!"none".equalsIgnoreCase(compresFormat)){
					taskScript.append(" -z,--compress --compression-codec ").append(compresFormat);
				}			
				//替换null值
				if(StringUtils.isNotBlank(jobData.getNullValue())){
					taskScript.append(" --null-string ").append(jobData.getNullValue());
				}
			//存储到Hive	
			}else if(DcJobTransData.TOLINK_HIVE.equals(jobData.getToLink())){
				taskScript.append(" --hive-table ").append(jobData.getOutputTable()).append(" --hive-import ");
				//是否建表
				if(DcConstants.DC_RESULT_FLAG_TRUE.equals(jobData.getIsCreateTable())){
					taskScript.append(" --create-hive-table ");
				}else if(StringUtils.isNotBlank(jobData.getPartitionField()) && StringUtils.isNotBlank(jobData.getPartitionValue())){
					taskScript.append(" --hive-partition-key '").append(jobData.getPartitionField()).append("' ");
					taskScript.append(" --hive-partition-value '").append(jobData.getPartitionValue()).append("' ");
				}
			//存储到hbase	
			}else if(DcJobTransData.TOLINK_HBASE.equals(jobData.getToLink())){
				taskScript.append(" --hbase-table ").append(jobData.getOutputTable());
				//列族
				if(StringUtils.isNotEmpty(jobData.getColumnFamily())){
					taskScript.append(" --column-family ").append(jobData.getColumnFamily());
				}
				//主键字段
				if(StringUtils.isNotEmpty(jobData.getKeyField())){
					taskScript.append(" --hbase-row-key ").append(jobData.getKeyField());
				}
				//是否建表
				if(DcConstants.DC_RESULT_FLAG_TRUE.equals(jobData.getIsCreateTable())){	
					taskScript.append(" --hbase-create-table ");
				}
			}
			//增量方式
			if(!DcJobTransDataLinkHdfs.INCREMENT_TYPE_WHOLE.equals(jobData.getIncrementType())){
				taskScript.append(" --check-column ").append(jobData.getIncrementField()).append(" --incremental ");
				//根据序列增量更新  
				if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_SEQUENCE.equals(jobData.getIncrementType())){
					taskScript.append("append ");
				}else if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_TIMESTAMP.equals(jobData.getIncrementType())){
					taskScript.append("lastmodified ");
				}
				if(StringUtils.isNotEmpty(jobData.getIncrementValue())){
					taskScript.append(" --last-value ").append(jobData.getIncrementValue());
				}
			}
		}else{
			String remarkScript = jobData.getRemarks();
			//动态设置增量更新值
			if(!DcJobTransDataLinkHdfs.INCREMENT_TYPE_WHOLE.equals(jobData.getIncrementType()) && remarkScript.indexOf(" --last-value ")>0){
				String lastVal = StringUtils.substringBetween(jobData.getRemarks(), "--last-value ", " ");
				if(DcJobTransDataLinkHdfs.INCREMENT_TYPE_SEQUENCE.equals(jobData.getIncrementType())){
					if(Integer.parseInt(lastVal) < Integer.parseInt(jobData.getIncrementValue())){
						remarkScript = remarkScript.replaceAll("--last-value "+lastVal, "--last-value "+jobData.getIncrementValue());
					}
				}
			}
			taskScript.append(remarkScript);
		}
		
		//构建数据源 连接信息
		buildDataBaseLink(jobData, taskScript, dataSource);

		logger.debug("--> sqoop task script: "+taskScript.toString());

		//变量替换 add by peijd 2017-05-18
		return engineService.decorateScript(taskScript.toString(), DcTransDataSub.TRANSLATE_ENGINE_SQOOP);
	}
	

	/**
	 * @方法名称: buildDataBaseLink 
	 * @实现功能: 构建数据源连接信息
	 * @param jobData		任务对象
	 * @param taskScript	任务脚本
	 * @param dataSource	数据源信息
	 * @create by peijd at 2016年12月13日 下午5:06:34
	 */
	public void buildDataBaseLink(DcJobDb2Hdfs jobData, StringBuilder taskScript, DcDataSource dataSource) {
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
	 * @方法名称: previewDbData 
	 * @实现功能: 预览DB数据 
	 * @param jobId
	 * @return
	 * @create by peijd at 2016年11月21日 下午1:27:25
	 */
	public List<Map<String, Object>> previewDbData(String jobId) {
		Assert.hasText(jobId);
		//构建数据对象
		DcJobDb2Hdfs jobData = buildJobData(jobId);
		Assert.notNull(jobData);
		
		//构建查询SQL
		StringBuilder tableSql = new StringBuilder(128);
		if(StringUtils.isNotEmpty(jobData.getTableColumn())){
			tableSql.append("select ").append(jobData.getTableColumn());
		
		}
		if(StringUtils.isNotEmpty(jobData.getTableName())){
			tableSql.append(" from ");
			//数据源连接对象
//			DcDataSource dataSource = dcDataSourceService.get(jobData.getFromLink());
			//数据需要连接schema
			//tableSql.append(jobData.getSchemaName()).append(".").append(jobData.getTableName()).append(" ");
			tableSql.append(dbHandleService.buildTableName(jobData.getFromLink(), jobData.getSchemaName(), jobData.getTableName())).append(" ");
		}	
		//获取返回结果 默认显示
		return dbHandleService.queryLimitMetaSql(jobData.getFromLink(),tableSql.toString(), Integer.parseInt(DcPropertyUtils.getProperty("extractdb.preview.datanum", "50")));
	}

	/**
	 * @方法名称: getJobName 
	 * @实现功能: 验证jobname不重复
	 * @param jobName
	 * @return
	 * @create by yuzh at 2016年11月25日 下午1:46:48
	 */
	public Object getJobName(String jobName) {
		return dao.getJobName(jobName);
	}

	/**
	 * @方法名称: buildList 
	 * @实现功能: TODO
	 * @param obj
	 * @return
	 * @create by peijd at 2016年12月5日 下午5:34:26
	 */
	public List<DcJobDb2Hdfs> buildList(DcJobDb2Hdfs obj) {
		return dao.buildList(obj);
	}

	/**
	 * @方法名称: buildPage 
	 * @实现功能: 构建分页信息
	 * @param page
	 * @param obj
	 * @return
	 * @create by peijd at 2016年12月5日 下午5:38:36
	 */
	public Page<DcJobDb2Hdfs> buildPage(Page<DcJobDb2Hdfs> page, DcJobDb2Hdfs obj) {
		obj.setPage(page);
		obj.getSqlMap().put("dsf", dataScopeFilter(obj.getCurrentUser(), "o", "u"));
		page.setList(dao.buildList(obj));
		return page;
	}

	/**
	 * @方法名称: getAu 
	 * @实现功能: 发起权限申请请求
	 * @param obj
	 * @create by yuzh at 2017年1月19日 上午11:20:48
	 */
	@Transactional(readOnly = false)
	public void getAu(DcJobTransData obj) throws Exception {
		DcObjectAu dcObjectAu = new DcObjectAu();
		dcObjectAu.setUserId(UserUtils.getUser().getId());
		dcObjectAu.setFileId(obj.getId());
		dcObjectAu.setStatus("未处理");
		dcObjectAu.setFrom("2");
		if(!DcStringUtils.isNotNull(dcObjectAuService.get(dcObjectAu))){
		dcObjectAuService.save(dcObjectAu);
			throw new Exception("已向管理员申请该任务操作权限，请等待管理员审核!");
		}else{
			dcObjectAuService.classify(dcObjectAu);
		}
	}


	
	/**
	 * @方法名称: updateStatus 
	 * @实现功能: 更新任务状态
	 * @param objId
	 * @param status
	 * @create by peijd at 2017年3月10日 下午1:37:42
	 */
	@Transactional(readOnly = false)
	public void updateStatus(String objId, String status){
		Assert.hasText(objId);
		status = StringUtils.isEmpty(status)? DcJobTransData.TASK_STATUS_EDIT:status;
		DcJobTransData obj = new DcJobTransData();
		obj.setId(objId);
		obj.setStatus(status);
		obj.preUpdate();
		dao.update(obj);
	}

}
