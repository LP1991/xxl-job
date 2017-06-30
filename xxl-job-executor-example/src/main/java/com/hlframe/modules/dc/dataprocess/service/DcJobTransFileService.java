/**
 * Copyright &copy; 2015-2020 <a href="http://www.hleast.com/">hleast</a> All rights reserved.
 */
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.PropertiesLoader;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.dataprocess.dao.DcJobHdfslogDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransFileDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransFileHdfsDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobHdfslog;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFile;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransFileHdfs;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.metadata.service.DcObjectAuService;
import com.hlframe.modules.dc.metadata.service.DcObjectLinkService;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcStringUtils;
import com.hlframe.modules.dc.utils.Des;
import com.hlframe.modules.sys.utils.UserUtils;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 采集传输文件Service
 * @author phy
 * @version 2016-11-23
 */
@Service
@Transactional(readOnly = true)
public class DcJobTransFileService extends CrudService<DcJobTransFileDao, DcJobTransFile> implements DcTaskService {

	@Autowired
	private DcJobHdfslogDao dcJobHdfslogDao;
	@Autowired
	private DcJobTransFileHdfsDao dcJobTransFileHdfsDao;
	@Autowired	//元数据存储Service
	private DcMetadataStroeService dcMetadataStroeService;
	@Autowired //源数据service
	private DcObjectMainDao dcObjectMainDao;
	@Autowired	//权限表service
	private DcObjectAuService dcObjectAuService;

	@Autowired
	private DcCommonService dcCommonService;
	@Autowired
	private DcObjectLinkService dcObjectLinkService;

	public DcJobTransFile get(String id) {
		return super.get(id);
	}

	public List<DcJobTransFile> findList(DcJobTransFile dcJobTransFile) {
		return super.findList(dcJobTransFile);
	}


	public Page<DcJobTransFile> findPage(Page<DcJobTransFile> page, DcJobTransFile dcJobTransFile) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcJobTransFile.getSqlMap().put("dsf", dataScopeFilter(dcJobTransFile.getCurrentUser(),"o","u"));
		// 设置分页参数
		dcJobTransFile.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(dcJobTransFile));
		return super.findPage(page, dcJobTransFile);
	}

	/**
	 * Override
	 * @方法名称: save
	 * @实现功能: TODO 保存文件 元数据  测试
	 * @param dcJobTransFile
	 * @create by peijd at 2017年3月4日 下午6:54:50
	 */
	@Transactional(readOnly = false)
	public void save(DcJobTransFile dcJobTransFile) {
		/*//保存元数据版本数据
		DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();*/
		//更新标记
		boolean updateFlag = StringUtils.isNotBlank(dcJobTransFile.getId());
		//设置记录默认状态
		if(StringUtils.isBlank(dcJobTransFile.getStatus()) ){
			dcJobTransFile.setStatus(dcJobTransFile.TASK_STATUS_EDIT);
		}
		super.save(dcJobTransFile);
		String jobId = StringUtils.isNotBlank(dcJobTransFile.getId())?dcJobTransFile.getId():IdGen.uuid();
		//保存从表对象  peijd
		DcJobTransFileHdfs job = dcJobTransFile.getDcJobTransFileHdfs();
		job.setJobId(dcJobTransFile.getId());
		if(updateFlag){
			dcJobTransFileHdfsDao.update(job);
		}else{
			dcJobTransFileHdfsDao.insert(job);
		}

	}

	/**
	 * Override
	 * @方法名称: delete
	 * @实现功能: 删除之前验证, 是否添加任务
	 * @param entity
	 * @create by peijd at 2017年3月10日 上午10:32:05
	 */
	@Transactional(readOnly = false)
	public void delete(DcJobTransFile entity) {

		Assert.hasText(entity.getId());
		entity = dao.get(entity.getId());
		Assert.notNull(entity);
		// 判断任务状态 如果是调度任务  则不可删除
		if(DcJobTransFile.TASK_STATUS_TASK.equals(entity.getStatus())){
			throw new ServiceException("该任务已添加调度任务, 不可删除!");
		}
		super.delete(entity);

		//删除es记录
		DcObjectMain dcObjectMain = new DcObjectMain();
		dcObjectMain.setJobId(entity.getId());
		List<DcObjectMain> dcMainList = dcObjectMainDao.findList(dcObjectMain);
		for(DcObjectMain m:dcMainList) {
			dcObjectMainDao.deleteByLogic(m);
			//Es删除对应记录
			if(m.getObjType().equals(DcObjectMain.OBJ_TYPE_FILE)){
				dcCommonService.deleteEsByIndex(m.getId(), "file");
			}else{
				dcCommonService.deleteEsByIndex(m.getId(), "folder");
			}
		}
	}

	public List<DcJobHdfslog> getHdfslogs(DcJobHdfslog dcJobHdfslog) {
		List<DcJobHdfslog> dcJobHdfslogs =dcJobHdfslogDao.findList(dcJobHdfslog);
		return dcJobHdfslogs;
	}


	/**
	 * 递归文件夹内所有文件
	 * TODO
	 */
	public static void listAllFiles(FTPClient ftpClient, String pathname, List<FTPFile> fileList, int rootLength) throws IOException  {
		String dir = null;
		if(pathname.startsWith("/")){
			dir = pathname.replaceFirst("/","./");
			dir = new String(dir.getBytes("utf-8"),"iso-8859-1");
		}
		boolean fp =  ftpClient.changeWorkingDirectory(dir);
		for (FTPFile file : ftpClient.listFiles()){
			if (file.isDirectory()){
				fileList.add(file);
				if(pathname.equals("/")){
					listAllFiles(ftpClient, pathname+file.getName(), fileList, rootLength);
					ftpClient.changeToParentDirectory();
				}else{
					listAllFiles(ftpClient, pathname+"/"+file.getName(), fileList, rootLength);
					ftpClient.changeToParentDirectory();
				}
			}else{
				if(pathname.length()>rootLength){
					file.setName((pathname+"*"+file.getName()).replace("/", "*"));
					fileList.add(file);
				}else{
					fileList.add(file);
				}
			}
		}
	}



	/**
	 * @方法名称: ftpUploadToHDFS
	 * @实现功能: 从ftp中下载文件上传至hdfs
	 * @param id
	 * @create by yuzh at 2016年11月24日 下午5:40:20
	 * @update by peijd 实现调度任务接口,
	 */
	@Transactional(readOnly = false)
	public DcDataResult runTask(String id) throws Exception {
		//初始化
		PropertiesLoader loader = new PropertiesLoader("dc_config.properties");
		String address=loader.getProperty("hadoop.main.address");
		//创建hdfs连接
		FileSystem	fs = FileSystem.get(new URI("hdfs://"+address), new Configuration(), "hdfs"); //hdfs配置信息在dc_config中配置
		DcDataResult taskResult = new DcDataResult();
		DcJobTransFileHdfs dcJobTransFileHdfs = dcJobTransFileHdfsDao.get(id);
		DcJobTransFile dcJobTransFile = dao.get(id);

		//保存元数据版本数据
		DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
		//保存文件采集源对象   元数据  hgw
		DcObjectMain objMain = new DcObjectMain();
		DcObjectMain pser= new DcObjectMain();

		String jobId = dcJobTransFile.getId();
		pser.setJobId(jobId);
		pser.setJobSrcFlag("N");
		objMain = dcObjectMainDao.get(pser);
		//元数据版本设置信息
		dcObjectMainVersion.setJobId(jobId);
		if(null==objMain){
			dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
			objMain = new DcObjectMain();
			objMain.setId(IdGen.uuid());
		}else{
			dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
		}

		//hdfs采集任务 元数据
		objMain.setObjCode(jobId);
		objMain.setObjName(dcJobTransFileHdfs.getPathname());
		objMain.setObjDesc(dcJobTransFile.getDescription());
		objMain.setObjType(objMain.OBJ_TYPE_FOLDER);
		objMain.setJobId(jobId);
		objMain.setJobType(objMain.JOB_TYPE_EXTRACT_FILE);
		objMain.setJobSrcFlag(objMain.JOB_SRC_FLAG_NO);	//FTP文件 元数据
		dcJobTransFile.setId(jobId);
		//hdfs目录信息
		DcObjectFolder objFolder = new DcObjectFolder();
		objFolder.setId(objMain.getId());
		//objFolder.setObjId(objMain.getId());
		objFolder.setFolderName(dcJobTransFileHdfs.getPathname());
		objFolder.setFolderUrl(dcJobTransFileHdfs.getPathname());
		dcMetadataStroeService.insertTableVersion(objMain,dcObjectMainVersion,objFolder);
		dcMetadataStroeService.obj2MySQL(objMain, objFolder, new ArrayList<DcObjectFile>(), false);

		String indexName = dcJobTransFile.getJobname();
		//dcJobTransFileHdfs.setPathname(dcJobTransFileHdfs.getPathname().replaceFirst("/","./"));
/*		if(dcJobTransFileHdfs.getPathname().endsWith("/")){ // / ./  ./ggg  /ggg
			if(!"./".equals(dcJobTransFileHdfs.getPathname())){
				dcJobTransFileHdfs.setPathname(dcJobTransFileHdfs.getPathname().substring(0,dcJobTransFileHdfs.getPathname().length()-1));
			}
		}*/
		if(dcJobTransFileHdfs.getPathname().endsWith("/")){
			if(!"/".equals(dcJobTransFileHdfs.getPathname())){
				dcJobTransFileHdfs.setPathname(dcJobTransFileHdfs.getPathname().substring(0,dcJobTransFileHdfs.getPathname().length()-1));
			}
		}
		DcJobHdfslog dcjobHdfslog = new DcJobHdfslog();

		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String createTime = time.format(new Date());//创建开始任务时间
		String hdfsPath = "/hldc/" + indexName + "/" + createTime;//上传目录
		String hdfsLog = "";//上传hdfs日志字段
		FTPClient ftpClient = new FTPClient();
		try {
			// 连接FTP服务器
			ftpClient.connect(dcJobTransFileHdfs.getIp(), dcJobTransFileHdfs.getPort());
			//设置utf-8编码
			ftpClient.setControlEncoding("utf-8");
			// 登录FTP服务器
			ftpClient.login(dcJobTransFileHdfs.getAccount(), Des.strDec(dcJobTransFileHdfs.getPassword()));
			// 验证FTP服务器是否登录成功
			int replyCode = ftpClient.getReplyCode(); //530用户或密码错误
			if(replyCode==530){
				hdfsLog = "账号或密码错误，请检查！\n\r";
				taskResult.setRst_flag(false);
				taskResult.setRst_err_msg(hdfsLog);
				dcjobHdfslog.setStatus("失败!");
				insertLog(dcjobHdfslog,id,createTime,hdfsPath);
				Path path = new Path(hdfsPath + "/" + createTime + ".log");// 日志文件存放路径及文件名称
				//创建日志文件
				createLog(fs,hdfsLog,path);
				return taskResult;
			}

			String p =null;
			if(dcJobTransFileHdfs.getPathname().startsWith("/")){
				p=dcJobTransFileHdfs.getPathname().replaceFirst("/","./");
			}
			boolean flag = ftpClient.changeWorkingDirectory(p);
			if(!flag){
				hdfsLog = "目录异常，目录不存在！\n\r";
				taskResult.setRst_flag(false);
				taskResult.setRst_err_msg(hdfsLog);
				dcjobHdfslog.setStatus("失败!");
				insertLog(dcjobHdfslog,id,createTime,hdfsPath);
				Path path = new Path(hdfsPath + "/" + createTime + ".log");// 日志文件存放路径及文件名称
				//创建日志文件
				createLog(fs,hdfsLog,path);
				return taskResult;
			}
			// 切换FTP目录
			//boolean f  = ftpClient.changeWorkingDirectory(dcJobTransFileHdfs.getPathname());
			//通过获取listAllFiles方法  递归获取ftp目录下所有文件信息
			List<FTPFile> allFiles = new ArrayList<FTPFile>();
			int rootLength = dcJobTransFileHdfs.getPathname().length();  //./  ./test
			listAllFiles(ftpClient, dcJobTransFileHdfs.getPathname(), allFiles,rootLength);
			/*
			 * 测试用删除根目录下所有文件
			 *
			boolean flag1 = fs.delete(new Path("/hldc"),true);//如果是删除路径 把参数换成路径即可"/a/test4"
			//第二个参数true表示递归删除，如果该文件夹下还有文件夹或者内容 ，会变递归删除，若为false则路径下有文件则会删除不成功
			//boolean flag2 = fs.delete(new Path("/log12345.log"),true);
			//boolean flag3 = fs.delete(new Path("/jdk"),true);
			System.out.println("删除文件 or 路径");
			System.out.println("delete?"+flag1);//删除成功打印true
			 */
			try {
				//上传个数
				int count = 0;
				for (FTPFile file : allFiles) {
					//如果是文件夹，直接创建文件夹
					if(file.isDirectory()){
						fs.mkdirs(new Path(hdfsPath+ "/" + file.getName()));
						continue;
					}
					count++;
					hdfsLog = hdfsLog +"-------------------\n\r"+ time.format(new Date()) + "----" + file.getName() + "----开始读取\n\r";
					ftpClient.enterLocalPassiveMode();
					//InputStream in = ftpClient.retrieveFileStream(dcJobTransFileHdfs.getPathname() + "/" + file.getName().replace("*", "/"));// 从ftp中读取文件写入流中
					String path = dcJobTransFileHdfs.getPathname() ;
					if(path.startsWith("/")){
						path = path.replaceFirst("/","./");
					}
					//boolean f  = ftpClient.changeWorkingDirectory(path);
					String s = ftpClient.printWorkingDirectory();
					System.out.println(file.getName().replace("*", "/"));
					String fileName  = file.getName().replace("*","/");

					int t =fileName.lastIndexOf("/");
					String firhh = null;
					String lashh = null;
					InputStream in = null;
					if(t!=-1){
						firhh  = fileName.substring(0,fileName.lastIndexOf("/"));
						lashh = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
						ftpClient.changeWorkingDirectory(firhh);
						in = ftpClient.retrieveFileStream(lashh);// 从ftp中读取文件写入流中
					}else{
						in = ftpClient.retrieveFileStream(file.getName().replace("*", "/"));// 从ftp中读取文件写入流中
					}
					//in = ftpClient.retrieveFileStream( file.getName().replace("*", "/"));// 从ftp中读取文件写入流中
					hdfsLog = hdfsLog + time.format(new Date()) + "----" + fileName + "----读取成功\n\r";
					hdfsLog = hdfsLog + time.format(new Date()) + "----" + fileName + "----开始上传\n\r";
					OutputStream os = fs.create(new Path(hdfsPath+ "/" + fileName));// 创建上传流
					IOUtils.copyBytes(in, os, 1024, true);// 从ftp中上传文件到HDFS, 1024缓存
					hdfsLog = hdfsLog + time.format(new Date()) + "----" + fileName + "----上传成功！\n\r";
					os.close();// 关闭流
					in.close();// 关闭流
					ftpClient.completePendingCommand();// 手动完成ftpClient服务, 不然无法再次调用ftpClient服务
				}
				//TODO: 保存目录/文件元数据信息 ftp端只存储目录元数据
				DcObjectMain tarMain = saveTransMetaData(dcJobTransFileHdfs, dcJobTransFile, hdfsPath, allFiles);
				dcObjectLinkService.configObjLinkByExtractFile(jobId,objMain.getId(),tarMain.getId());
				// 完成日志文件写入hdfs
				hdfsLog = hdfsLog + "上传完成！----共" + count + "个文件----";
				Path path = new Path(hdfsPath + "/" + createTime + ".log");// 日志文件存放路径及文件名称
				//创建日志文件
				createLog(fs,hdfsLog,path);
				taskResult.setRst_flag(true);
				taskResult.setRst_std_msg("上传完成！----共" + count + "个文件----");
				dcjobHdfslog.setStatus("成功!");

			}catch(Exception e){
				//中断情况下 写入日志文件到hdfs
				hdfsLog = hdfsLog+"操作失败，传输中断，请检查！\n\r";
				Path path = new Path(hdfsPath + "/" + createTime + ".log");// 日志文件存放路径及文件名称
				//创建日志文件
				createLog(fs,hdfsLog,path);
				taskResult.setRst_flag(false);
				taskResult.setRst_err_msg(hdfsLog+"明细: "+e.getMessage());
				dcjobHdfslog.setStatus("失败!");
			}
/*			dcjobHdfslog.setJobId(id);
			dcjobHdfslog.setUploadTime(createTime);
			dcjobHdfslog.setFullpath(hdfsPath+"/"+createTime+".log");

			dcJobHdfslogDao.insert(dcjobHdfslog);*/

			//更新任务状态
			if(DcJobTransFile.TASK_STATUS_EDIT.equals(dcJobTransFile.getStatus())){
				DcJobTransFile updateJob = new DcJobTransFile();
				updateJob.setStatus(DcJobTransFile.TASK_STATUS_TEST);
				updateJob.setId(id);
				updateJob.preUpdate();
				dao.update(updateJob);
			}


		}catch(ConnectException e){
			hdfsLog = "IP或端口连接失败，请检查！\n\r";
			taskResult.setRst_flag(false);
			taskResult.setRst_err_msg(hdfsLog);
			dcjobHdfslog.setStatus("失败!");
		}catch (UnknownHostException e){
			hdfsLog = "访问网站已经倒闭/关闭或者不存在,可以在浏览器中验证！\n\r";
			taskResult.setRst_flag(false);
			taskResult.setRst_err_msg(hdfsLog);
			dcjobHdfslog.setStatus("失败!");
		}
		catch (Exception e) {
			logger.error("-->extract file from ftp : ", e);
			taskResult.setRst_flag(false);
			taskResult.setRst_err_msg(e.getMessage());
			dcjobHdfslog.setStatus("失败!");
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.logout();
				} catch (IOException e) {
					logger.error("-->ftpClient.logout error: ", e);
				}
			}
		}
		insertLog(dcjobHdfslog,id,createTime,hdfsPath);
		Path path = new Path(hdfsPath + "/" + createTime + ".log");// 日志文件存放路径及文件名称
		//创建日志文件
		createLog(fs,hdfsLog,path);
		return taskResult;
	}



	/**
	 * @方法名称: saveTransMetaData
	 * @实现功能: 更新上传文件元数据信息
	 * @param transFile	采集配置信息
	 * @param job		job任务信息
	 * @param hdfsPath		hdfs文件配置
	 * @param allFiles	文件列表
	 * @create by peijd at 2017年3月4日 上午10:44:08
	 */
	private DcObjectMain saveTransMetaData(DcJobTransFileHdfs transFile, DcJobTransFile job, String hdfsPath, List<FTPFile> allFiles) {
		DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
		//元数据版本设置信息
		dcObjectMainVersion.setJobId(job.getId());
		dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_TASK);

		//插入目标元数据
		DcObjectMain tarMain = new DcObjectMain();
		//查询元数据
		DcObjectMain dcObjectMain = new DcObjectMain();

		//根据jobId 获取源对象
		DcObjectMain param = new DcObjectMain();
		param.setJobId(job.getId());
		param.setJobSrcFlag("Y");
		dcObjectMain = dcObjectMainDao.get(param);
		if(null==dcObjectMain){
			//元数据版本状态设为增加
			dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
			tarMain.setId(IdGen.uuid());
			dcObjectMain = new DcObjectMain();
			dcObjectMain.setId(tarMain.getId());
		}else{
				dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
				tarMain.setId(dcObjectMain.getId());
		}
		DcObjectFolder objFolder = new DcObjectFolder();
		List<DcObjectFile> fileList = new ArrayList<DcObjectFile>();

		//hdfs采集任务 元数据
		//objMain.setId(IdGen.uuid());
		//objMain.setId(job.getId());
		tarMain.setObjCode(job.getId());
		tarMain.setObjName(hdfsPath);
		tarMain.setObjDesc(job.getDescription());
		//objMain.setObjType(objMain.OBJ_TYPE_FILE);
		tarMain.setObjType(tarMain.OBJ_TYPE_FOLDER);
		tarMain.setJobId(job.getId());
		tarMain.setJobType(tarMain.JOB_TYPE_EXTRACT_FILE);
		tarMain.setJobSrcFlag(tarMain.JOB_SRC_FLAG_YES);	//hdfs文件 元数据

		//hdfs目录信息
		objFolder.setId(tarMain.getId());
		//objFolder.setObjId(objMain.getId());
		objFolder.setFolderName(StringUtils.substringAfterLast(hdfsPath, "/"));	//
		objFolder.setFolderUrl(hdfsPath);
		DcObjectFile objFile = null;
		//hdfs文件列表
		for(FTPFile file: allFiles){
			if(file.isDirectory()){
				objFile = new DcObjectFile();
				objFile.setId(IdGen.uuid());
				objFile.setFileName(file.getName());
				objFile.setFileUrl(hdfsPath+"/"+file.getName());
				objFile.setObjType(DcObjectMain.OBJ_TYPE_FOLDER);
				fileList.add(objFile);
			}else{
				objFile = new DcObjectFile();
				objFile.setId(IdGen.uuid());
				objFile.setFileName(file.getName());
				objFile.setFileUrl(hdfsPath+"/"+file.getName());
				objFile.setObjType(DcObjectMain.OBJ_TYPE_FILE);
				fileList.add(objFile);
			}
		}
		dcMetadataStroeService.insertTableVersion(tarMain,dcObjectMainVersion,objFolder);
		//保存文件元数据信息
		dcMetadataStroeService.obj2MySQL(tarMain, objFolder, fileList,true );
		return dcObjectMain;
	}

	/**
	 * @方法名称: getJobName
	 * @实现功能: 验证jobname不重复
	 * @param jobname
	 * @return
	 * @create by yuzh at 2016年11月25日 下午4:34:26
	 */
	public Object getJobName(String jobname) {
		return dao.getJobName(jobname);
	}

	/**
	 * @方法名称: updateStatus
	 * @实现功能: 更新对象状态
	 * @param objId
	 * @param status
	 * @create by peijd at 2017年3月10日 下午2:46:54
	 */
	@Transactional(readOnly = false)
	public void updateStatus(String objId, String status){
		Assert.hasText(objId);
		status = StringUtils.isEmpty(status)?DcJobTransFile.TASK_STATUS_EDIT:status;
		DcJobTransFile obj = new DcJobTransFile();
		obj.setId(objId);
		obj.setStatus(status);
		obj.preUpdate();
		dao.update(obj);
	}

	/**
	 * @实现功能: ftp树
	 * @param ip
	 * @param port
	 * @param account
	 * @param password
	 * @Author hgw data 2017 5 9
	 */
	public List<Map<String,Object>> treeData(String ip,String port,String account,String password){
		FTPClient ftpClient = new FTPClient();
		try{
			ftpClient.setDefaultPort(Integer.parseInt(port));
			ftpClient.connect(ip);
			ftpClient.setControlEncoding("utf-8");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.login(account, Des.strDec(password));
			//ftpClient.changeWorkingDirectory(dcJobTransFile.getDcJobTransFileHdfs().getPathname());
			ftpClient.enterLocalActiveMode();
			String file = ftpClient.getStatus();
			List<Map<String,Object>> ftpList=new ArrayList<Map<String,Object>>();
			dgMap(ftpClient,"/","0",true,"/",ftpList);
			return  ftpList;
		}catch(Exception e){
			return null;
		}
	}

	public void dgMap(FTPClient ftpClient, String path, String parent, boolean isDir, String gname, List<Map<String,Object>> ftpList) throws Exception{
		String dir = null; // / /dd
		//拼接文件夹路径
		if(path.equals("/")){
			path="/";
		}else{
			path=parent+gname;
			dir = parent+gname;
		}
		if(path.startsWith("/")){
			dir = path.replaceFirst("/","./");
		}
		Map<String,Object> map = null;
		if(isDir){
			map = new HashMap<String,Object>();
			map.put("id", path);
			map.put("pId", parent);
			map.put("name", gname);
			map.put("isParent", true);
			ftpList.add(map);
			FTPFile[] filelist = ftpClient.listDirectories(dir);
			for(int i=0;i<filelist.length;i++){
				FTPFile ftpFile = filelist[i];

				boolean flag = ftpFile.isDirectory();
				System.out.println(ftpFile.getName()+"___");
				dgMap(ftpClient,ftpFile.getName(),path,flag,ftpFile.getName(),ftpList);
			}
		}
	}

	//判断路径是否存在
	public boolean isDirExist(FTPClient ftpClient, String dir){
		boolean flag = false;
		try{
			//ftpClient
		}catch(Exception e){
			return flag;
		}
		return flag;
	}

	//递归所有文件夹
/*	public void getDirList(FTPClient ftpClient,String path,boolean isDir,List<String> pathList) throws Exception{
		if(isDir) {
			FTPFile[] filelist = ftpClient.listDirectories(path);
			for (int i = 0; i < filelist.length; i++) {
				FTPFile ftpFile = filelist[i];
				ftpFile.getName();
				boolean flag = ftpFile.isDirectory();
				System.out.println(ftpFile.getName() + "___");
				getDirList(ftpClient, pp, isdir);
			}
		}
	}*/
	//创建日志文件
	public void createLog(FileSystem fs,String hdfsLog,Path path) throws Exception{
		byte[] buff = hdfsLog.getBytes("utf-8");// 想要输入内容
		FSDataOutputStream outputStream = fs.create(path);
		outputStream.write(buff, 0, buff.length);
		outputStream.close();
	}

	//插入日志信息
	public void insertLog(DcJobHdfslog dcjobHdfslog,String id,String createTime,String hdfsPath){
		dcjobHdfslog.setJobId(id);
		dcjobHdfslog.setUploadTime(createTime);
		dcjobHdfslog.setFullpath(hdfsPath+"/"+createTime+".log");

		dcJobHdfslogDao.insert(dcjobHdfslog);
	}

	/**
	 * @方法名称: getAu
	 * @实现功能: 发起权限申请
	 * @params  [obj]
	 * @return  void
	 * @create by peijd at 2017/4/18 10:35
	 */
	@Transactional(readOnly = false)
	public void getAu(DcJobTransFile obj) throws Exception  {
		DcObjectAu dcObjectAu = new DcObjectAu();
		dcObjectAu.setUserId(UserUtils.getUser().getId());
		dcObjectAu.setFileId(obj.getId());
		dcObjectAu.setStatus("未处理");
		dcObjectAu.setFrom("9");
		if(!DcStringUtils.isNotNull(dcObjectAuService.get(dcObjectAu))){
			dcObjectAuService.save(dcObjectAu);
			throw new Exception("已向管理员申请该任务操作权限，请等待管理员审核!");
		}else {
			dcObjectAuService.classify(dcObjectAu);
		}
	}

}