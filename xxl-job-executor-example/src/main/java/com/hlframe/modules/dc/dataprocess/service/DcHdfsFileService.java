/********************** 版权声明 *************************
 * 文件名: DcHdfsFileService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月23日 下午3:44:18
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.service.BaseService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StreamUtils;
import com.hlframe.modules.dc.dataprocess.entity.DcHdfsFile;
import com.hlframe.modules.dc.utils.DcFileUtils;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcHdfsFileService.java 
 * @职责说明: hdfs文件管理Service
 * @创建者: peijd
 * @创建时间: 2016年11月23日 下午3:44:18
 */
@Service
@Transactional(readOnly = true)
public class DcHdfsFileService extends BaseService {
	
	// 排除显示文件
	private static Set<String> excludeFileSet = new HashSet<String>();
	static{
		excludeFileSet.add("_SUCCESS");
	}
	
	private FileSystem fs = null; 
	
	//hdfs文件类型
	public static String HDFS_FILETYPE_FILE = "1";
	public static String HDFS_FILETYPE_FOLDER = "2";
	
	/**
	 * @方法名称: getFileSystem 
	 * @实现功能: 获取hdfs文件系统
	 * @return
	 * @throws IOException
	 * @create by peijd at 2016年11月24日 下午6:59:22
	 */
	public FileSystem getFileSystem() throws Exception{
		if(null==fs){
			//hadoop 目录
			String uri = "hdfs://"+ DcPropertyUtils.getProperty("hdfs.datanode.address");
			Configuration config = new Configuration(); 
			//默认连接用户 hdfs
			fs = FileSystem.get(URI.create(uri), config, DcPropertyUtils.getProperty("hdfs.fs.connUser", "hdfs"));
		}
		return fs;
	}
	
	
	/**
	 * @方法名称: listFiles 
	 * @实现功能: 获取指定文件夹内所有文件
	 * @param dirPath
	 * @param tmpAllFile
	 * @throws IOException
	 * @create by yuzh at 2016年11月29日 下午6:07:15
	 */
	public void listFiles(String dirPath, List<FileStatus> tmpAllFile) throws Exception {
		for (FileStatus file : getFileSystem().listStatus(new Path(dirPath))) {
			if (file.isDirectory()) {
				listFiles(dirPath + "/" + file.getPath().getName(), tmpAllFile);
			} else {
				tmpAllFile.add(file);
			}
		}
	}

	/**
	 * @方法名称: listFiles
	 * @实现功能: 获取指定文件夹内所有文件和文件夹
	 * @param dirPath
	 * @param tmpAllFile
	 * @throws IOException
	 * @create by  hgw
	 */
	public void listFilesAndDir(String dirPath, List<FileStatus> tmpAllFile) throws Exception {
		for (FileStatus file : getFileSystem().listStatus(new Path(dirPath))) {
			if (file.isDirectory()) {
				tmpAllFile.add(file);
				listFilesAndDir(dirPath + "/" + file.getPath().getName(), tmpAllFile);
			} else {
				tmpAllFile.add(file);
			}
		}
	}

	/**
	 * @方法名称: listFileByDir 
	 * @实现功能: 列出Hdfs文件列表
	 * @param dirPath
	 * @return
	 * @create by peijd at 2016年11月23日 下午3:46:16
	 * @update by yuzh at 2016年11月29日 下午6:07:15
	 */
	public List<DcHdfsFile> listFileByDir(String dirPath) {
		Assert.hasText(dirPath);
		
		 List<DcHdfsFile> fileList = new ArrayList<DcHdfsFile>();
		 List<FileStatus> tmpAllFile = new ArrayList<FileStatus>();
		try {
			//获取文件列表
			//FileStatus[] filelist = getFileSystem().listStatus(new Path(dirPath));
			listFiles(dirPath, tmpAllFile);//获取指定文件夹内所有文件信息
			DcHdfsFile file = null;
			
			for (FileStatus fileStatus: tmpAllFile) {
				//文件名称
				String fileName = fileStatus.getPath().getName();
				if(excludeFileSet.contains(fileName)){
					continue;
				}
				//添加文件对象信息
				file = new DcHdfsFile();
				file.setCreateDate(DateUtils.formatDateTime(new Date(fileStatus.getAccessTime())));	//创建时间
				file.setFileName(fileStatus.getPath().toString().substring(fileStatus.getPath().toString().indexOf(dirPath)+dirPath.length()+1));//从全路径中截取设置路径下的路径+文件名
				file.setFileSize(DcFileUtils.convertFileSize(fileStatus.getLen()));	//文件大小, 转换
				file.setGroup(fileStatus.getGroup());
				file.setPermissions(fileStatus.getPermission().toString());
				file.setUser(fileStatus.getOwner());
				file.setFilePath(fileStatus.getPath().toString());
				fileList.add(file);
			}
			
		} catch (Exception e) {
			logger.error("--->listFileByDir", e);
		}
        
		return fileList;
	}
	
	/**
	 * @方法名称: loadFileContent 
	 * @实现功能: 获取文件内容
	 * @param file
	 * @return
	 * @create by peijd at 2016年11月24日 下午5:56:40
	 * @update by yuzh at 2016年12月2日 上午10:56:40
	 */
	public void loadFileContent(DcHdfsFile file, String line) {
		Assert.hasText(file.getFilePath());
		int lineInt = 0;
		if (StringUtils.isNotBlank(line)) {
			lineInt = Integer.parseInt(line);
		}
		InputStream in = null;
		try {
			in = getFileSystem().open(new Path(file.getFilePath()));
			if (lineInt > 0) {
				file.setContent(getStr(StreamUtils.InputStreamTOString(in), lineInt));// 设置查看文件行数
			} else {
				file.setContent(StreamUtils.InputStreamTOString(in));// 不进行行数限制
			}
		} catch (Exception e) {
			logger.error("--->getfileContent", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("--->getfileContent close IO", e);
				}
			}
		}
	}
	
	/**
	 * 
	 * @方法名称: getStr 
	 * @实现功能: 截取前n行数据
	 * @param str
	 * @param n
	 * @return
	 * @create by yuzh at 2016年11月28日 下午3:20:21
	 */
	private static String getStr(String str, int n) {
		int i = 0;
		int s = 0;
		while (i++ < n) {
			s = str.indexOf("\n", s + 1);// 循环获取指定行数内的文件
			if (s == -1) {
				return str;// 行数少于指定长度则返还源文件
			}
		}
		return str.substring(0, s) + "\n...\n";// 截取后添加"..."表示未完
	}
	
	/**
	 * @方法名称: writeData2Hdfs 
	 * @实现功能: 写入数据到Hdfs系统
	 * @param filePath
	 * @param fileContent
	 * @throws Exception
	 * @create by peijd at 2016年12月5日 下午4:58:57
	 */
	public void writeData2Hdfs(String filePath, String fileContent) throws Exception{
		writeData2Hdfs(getFileSystem(), filePath, fileContent);
	}
	
	/**
	 * @方法名称: buildBizLoggerPath 
	 * @实现功能: 构建业务日志路径
	 * @param bizId			//业务Id
	 * @param loggerName	//日志名称, 默认为时间戳
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月6日 上午10:59:39
	 */
	public String buildBizLoggerPath(String bizId, String loggerName) throws Exception{
		Assert.hasText(bizId);
		//日志名称, 如果为空 则用当前时间
		loggerName = StringUtils.isNotEmpty(loggerName)?loggerName:(new Date().getTime()+"");
		StringBuilder loggerPath = new StringBuilder(64);
		//构建日志路径
		loggerPath.append(DcPropertyUtils.getProperty("hdfs.bizlog.baseDir")).append("/")
					.append(bizId).append("/").append(loggerName).append(".log");
		return loggerPath.toString();
	}
	
	/**
	 * @方法名称: writeData2Hdfs 
	 * @实现功能: 写入数据到Hdfs系统
	 * @param fs2
	 * @param filePath
	 * @param fileContent
	 * @throws Exception
	 * @create by peijd at 2016年12月5日 下午4:56:49
	 */
	public void writeData2Hdfs(FileSystem fs2, String filePath, String fileContent) throws Exception{
		Assert.hasText(fileContent);
		Assert.hasText(filePath);
		//System.setProperty("HADOOP_USER_NAME", "hdfs");
		System.out.println("-->"+System.getProperty("HADOOP_USER_NAME"));
		
		Path logPath = new Path(filePath);
		//检查目录是否存在  不存在则创建
		if(!fs2.exists(logPath.getParent())){
			fs2.mkdirs(logPath.getParent());
		}
		
		//先删除原有文件
		//deleteFile(fs2, filePath, HDFS_FILETYPE_FILE);
		//文件内容 转换为uft-8
		byte[] buff = fileContent.getBytes("utf-8");	
		//默认覆盖原有文件
		FSDataOutputStream outputStream = fs2.create(logPath);
		outputStream.write(buff, 0, buff.length);
		outputStream.close();
	}
	
	/**
	 * @方法名称: deleteFile 
	 * @实现功能: 删除Hdfs文件
	 * @param fs2
	 * @param filePath
	 * @param fileType
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月5日 下午7:26:25
	 */
	public boolean deleteFile(FileSystem fs2, String filePath,String fileType) throws Exception {
		Assert.hasText(filePath);
		return fs2.delete(new Path(filePath), HDFS_FILETYPE_FOLDER.equals(fileType));
	}
	
}
