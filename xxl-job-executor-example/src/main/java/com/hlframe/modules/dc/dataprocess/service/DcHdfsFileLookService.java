package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.dataprocess.dao.DcHdfsFileLookDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHdfsFile;
import com.hlframe.modules.dc.dataprocess.entity.DcHdfsFileLook;
import com.hlframe.modules.dc.metadata.dao.DcObjectFileDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectFolderDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectFile;
import com.hlframe.modules.dc.metadata.entity.DcObjectFolder;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcObjectMainVersion;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * hdfs文件远程获取处理Service
 * @author phy
 * @version 2016-11-23
 */
@Service
@Transactional(readOnly = true)
public class DcHdfsFileLookService extends CrudService<DcHdfsFileLookDao,DcHdfsFileLook>{
	
	@Autowired
	private DcMetadataStroeService dcMetadataStroeService;
	
	@Autowired
	private DcObjectFolderDao dcObjectFolderDao;
	
	@Autowired
	private DcObjectMainDao dcObjectMainDao;
	
	@Autowired
	private DcObjectFileDao dcObjectFileDao;
	
	@Autowired
	private DcCommonService dcCommonService;

	//实现获取hdfs中的内容 params 哪个路径下的
	public Page<DcHdfsFileLook> findFtpList(Page<DcHdfsFileLook> page, DcHdfsFileLook dchdfs){
		if(dchdfs.getTempPath()==null||"".equals(dchdfs.getTempPath())){
			dchdfs.setTempPath("/");
		}
		List<DcHdfsFileLook> list = new ArrayList<DcHdfsFileLook>();
		long count= 0;
		try {
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			//原因路径配置
			//FileStatus[] filelist = fs.listStatus(new Path("/tmp/"));
			//动态路径配置
			FileStatus[] filelist = fs.listStatus(new Path(dchdfs.getTempPath()));
			for(int i = 0; i<filelist.length; i++){
				//筛选记录 页数个数我知道
				//页数
				int pageno = page.getPageNo();
				//开始记录
				int start = (pageno-1)*(page.getPageSize());
				//结束记录
				int end = pageno*page.getPageSize();
				//取页数大于等于开始记录并小于结束记录
				if(i>=start&&i<end){
					FileStatus fileStatus = filelist[i];
					DcHdfsFileLook dcHdfsFileLook = new DcHdfsFileLook();
					//得到完整路径
					String pa = fileStatus.getPath().toString();
					String pathName = pa.replaceFirst("hdfs://"+ DcPropertyUtils.getProperty("hdfs.datanode.address"), "");
					dcHdfsFileLook.setId(pathName);
					dcHdfsFileLook.setPathName(pathName);
					dcHdfsFileLook.setFolderName(fileStatus.getPath().getName());
					if(fileStatus.getLen()<1024&&fileStatus.getLen()>=0){
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen())+"Bytes");
					}else if(fileStatus.getLen()>=1024&&fileStatus.getLen()<(1024*1024)){
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen()/1024)+"KB");
						//1024字节等于1kb
					}else{
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen()/1048576)+"MB");
						//1048576字节等于1mb
					}
					dcHdfsFileLook.setPermissions(fileStatus.getPermission().toString());
					dcHdfsFileLook.setGroup(fileStatus.getGroup());
					Date date = new Date(fileStatus.getModificationTime());
					dcHdfsFileLook.setDateT(date);
					dcHdfsFileLook.setOwner(fileStatus.getOwner());
					dcHdfsFileLook.setDir(fileStatus.isDirectory());
					//是否拥有查询条件
					if(dchdfs.getFolderName()!=null&&!"".equals(dchdfs.getFolderName())){
						if(pathName.contains(dchdfs.getFolderName())){
							list.add(dcHdfsFileLook);
						}
					}else{
						list.add(dcHdfsFileLook);
					}
				}
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		page.setCount(count);
		page.setList(list);
		
		return page;
	}

//检索页面文件夹获取路径
//	baog 2017/6/1
	public List<DcHdfsFileLook> findListfile(DcHdfsFileLook dcHdfsFile){
		if(StringUtils.isBlank(dcHdfsFile.getTempPath())){
			dcHdfsFile.setTempPath("/");
		}
		List<DcHdfsFileLook> list = new ArrayList<DcHdfsFileLook>();
		try {
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			//动态路径配置
			FileStatus[] filelist = fs.listStatus(new Path(dcHdfsFile.getTempPath()));
			for(int i = 0; i<filelist.length; i++){

					FileStatus fileStatus = filelist[i];
					DcHdfsFileLook dcHdfsFileLook = new DcHdfsFileLook();
					//得到完整路径
					String pa = fileStatus.getPath().toString();
					String pathName = pa.replaceFirst("hdfs://"+ DcPropertyUtils.getProperty("hdfs.datanode.address"), "");
					dcHdfsFileLook.setId(pathName);
					dcHdfsFileLook.setPathName(pathName);
					dcHdfsFileLook.setFolderName(fileStatus.getPath().getName());
					if(fileStatus.getLen()<1024&&fileStatus.getLen()>=0){
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen())+"Bytes");
					}else if(fileStatus.getLen()>=1024&&fileStatus.getLen()<(1024*1024)){
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen()/1024)+"KB");
						//1024字节等于1kb
					}else{
						dcHdfsFileLook.setSize(String.valueOf(fileStatus.getLen()/1048576)+"MB");
						//1048576字节等于1mb
					}
					dcHdfsFileLook.setPermissions(fileStatus.getPermission().toString());
					dcHdfsFileLook.setGroup(fileStatus.getGroup());
					Date date = new Date(fileStatus.getModificationTime());
					dcHdfsFileLook.setDateT(date);
					dcHdfsFileLook.setOwner(fileStatus.getOwner());
					dcHdfsFileLook.setDir(fileStatus.isDirectory());
					//是否拥有查询条件
					if(StringUtils.isBlank(dcHdfsFile.getFolderName())){
						if(pathName.contains(dcHdfsFile.getFolderName())){
							list.add(dcHdfsFileLook);
						}
					}else{
						list.add(dcHdfsFileLook);
					}
				}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}


		return list;
	}
	//重命名
	public void rename(){
		try {
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			boolean flag = fs.rename(new Path("/input"),new Path("/inputfile"));//第一个参数改名为第二个参数 
			String result=flag?"成功":"失败"; 
			logger.debug("result of rename?"+result);//删除成功打印true 
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	//查看
	public void openFile(String filePath,HttpServletResponse response){
		try {
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			InputStream in = fs.open(new Path(filePath));//InputStream hdfs上的文件 
			OutputStream out = new BufferedOutputStream(response.getOutputStream());//下载到本地路径 以及重命名后的名字
			IOUtils.copyBytes(in, out, 4096, true);
			logger.debug("success");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	//检索查看
	public void JSopenFile(String filePath, DcHdfsFile dcHdfsFile){
		try {
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			InputStream in = fs.open(new Path(filePath));//InputStream hdfs上的文件
			InputStreamReader isr = new InputStreamReader(in,"utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer strbuf = new StringBuffer(256);
			String content = null;
			int maxLine = Integer.parseInt(DcPropertyUtils.getProperty("extractdb.preview.datanum", "20"));
			int ser=1;
			while((content = br.readLine())!=null){
				if(ser>maxLine){
					strbuf.append("...");
					break;
				}else {
					strbuf.append(content+"\n");
				}
				ser++;
			}
			dcHdfsFile.setContent(strbuf.toString());
			br.close();
			isr.close();
			in.close();
			logger.debug("success");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	//下载
	public void downloadT(String filePath,HttpServletResponse response){
		try {
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			InputStream in = fs.open(new Path(filePath));//InputStream hdfs上的文件 
			OutputStream out = new BufferedOutputStream(response.getOutputStream());//
			IOUtils.copyBytes(in, out, 4096, true); 
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	//重命名
	public boolean toReName(String oldName,String newName){
		try {
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			boolean flag = false;
			if(oldName.equals(newName)){
				flag=true;
			}else{
				flag = fs.rename(new Path(oldName),new Path(newName));//第一个参数改名为第二个参数
			}
			return flag;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		} 
	}
	
	//上传
	@Transactional(readOnly = false)
	public boolean uploads(DcHdfsFileLook dcHdfsFileLook, MultipartFile[] uploadfiles){
		try {
			//临时存储路径
			String temp = dcHdfsFileLook.getTempPath();
			if(temp.length()>1){
				temp = temp.replace(" ", "");
			}
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
				for(int i=0;i<uploadfiles.length;i++){
					if(!uploadfiles[i].isEmpty()){
						MultipartFile mFile = uploadfiles[i];
						if(!mFile.isEmpty()){
		    				String fileName = mFile.getOriginalFilename();
		    				String path = temp+fileName;
		    				if(!fs.exists(new Path(path))){
								DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
								dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
			    				//DC_OBJ_FILE File_BELONG= DC_OBJ_MAIN ID
			    				//DC_OBJ_MAIN OBJ_NAME=DC_OBJ_FILE FILE_URL HDFS:/
			    				String id = IdGen.uuid();
			    				DcObjectFolder dcObjectFolder = new DcObjectFolder();
			    				//是否是根目录
			    				if(temp.length()>1){
			    					dcObjectFolder.setFolderUrl(temp.substring(0,temp.length()-1));
			    				}else{
			    					dcObjectFolder.setFolderUrl(temp);
			    				}
			    				DcObjectFolder d = dcObjectFolderDao.getFolderUrl(dcObjectFolder);
			    				String folderid = null;
			    				if(d!=null){
			    					folderid = d.getId();
			    				}else{
				    				dcObjectFolder.setId(IdGen.uuid());
				    				//文件夹id 
				    				folderid = dcObjectFolder.getId();
				    				String folderUrl = null;
				    				String folderName = null;
				    				if(temp.length()>1){
				    					folderUrl = temp.substring(0,temp.length()-1);
				    					folderName = folderUrl.substring(folderUrl.lastIndexOf("/")+1,folderUrl.length());
				    				}else{
				    					folderUrl = temp;
				    					folderName=temp;
				    				}
				    				//文件夹 主表插入
				    				DcObjectMain ma = new DcObjectMain();
				    				ma.preInsert();
				    				ma.setId(folderid);
				    				ma.setObjName(folderName);
				    				ma.setObjType(DcObjectMain.OBJ_TYPE_FOLDER);
				    				ma.setJobType(ma.JOB_TYPE_UPLOAD);
				    				ma.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);
				    				dcObjectMainDao.insert(ma);

				    				dcObjectFolder.setFolderName(folderName);
				    				dcObjectFolder.setFolderUrl(folderUrl);
				    				dcObjectFolder.setIsStruct(0);
				    				dcObjectFolderDao.insert(dcObjectFolder);
									dcMetadataStroeService.insertTableVersion(ma,dcObjectMainVersion,dcObjectFolder);
			    				}
			    				//String fileid = IdGen.uuid();
			    				DcObjectFile dcObjectFile = new DcObjectFile();
			    				//dcObjectFile.setId(fileid);
			    				dcObjectFile.setFileUrl("hdfs://"+address+path);
			    				dcObjectFile.setFileName(fileName);
			    				dcObjectFile.setFileBelong(folderid);
			    				dcObjectFile.setIsStruct(0);
			    				DcObjectMain dcObjectMain = new DcObjectMain();
			    				dcObjectMain.setId(id);
			    				//dcObjectMain.setObjCode(fileid);
			    				dcObjectMain.setObjName(path);
			    				dcObjectMain.setObjType(DcObjectMain.OBJ_TYPE_FILE);
			    				dcObjectMain.setJobType(dcObjectMain.JOB_TYPE_UPLOAD);
			    				dcObjectMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);
								dcMetadataStroeService.insertTableVersion(dcObjectMain,dcObjectMainVersion,dcObjectFile);
			    				dcMetadataStroeService.obj2MySQL(dcObjectMain,dcObjectFile);
		    				}
		    				OutputStream out = fs.create(new Path(path));//上传位置及上传文件名 
							IOUtils.copyBytes(mFile.getInputStream(), out, 1024, true);//in输入源, out输出源头, 1024缓冲区大小 ,true 是否关闭数据流。如果是false，就在finally里关掉 
							logger.debug("success");
						}else{
							return false;
						}
					}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	//新建文件夹和文件
	@Transactional(readOnly = false)
	public boolean isDirOrFile(DcHdfsFileLook dcHdfsFileLook, String isDir2, String Content){
		try {
			//临时存储路径
			String temp = dcHdfsFileLook.getTempPath();
			if(temp.length()>1){
				temp = temp.replace(" ", "");
			}
			//创建hdfs连接
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			boolean flag = false;
			if("true".equals(isDir2)){
				DcObjectFolder dcObjectFolder = new DcObjectFolder();
				dcObjectFolder.setFolderUrl(temp+dcHdfsFileLook.getFolderName());
				DcObjectFolder d = dcObjectFolderDao.getFolderUrl(dcObjectFolder);
				String folderid = null;
				if(d==null){
    				//文件夹id 
    				folderid = IdGen.uuid();
    				dcObjectFolder.setId(folderid);
    				String folderUrl = temp+dcHdfsFileLook.getFolderName();
    				String folderName = dcHdfsFileLook.getFolderName();
    				if(folderName!=null){
    					//文件夹 主表插入
	    				DcObjectMain ma = new DcObjectMain();
	    				ma.setObjName(folderName);
	    				ma.setObjType(ma.OBJ_TYPE_FOLDER);
	    				ma.setJobType(ma.JOB_TYPE_UPLOAD);
	    				ma.setJobSrcFlag(ma.JOB_SRC_FLAG_YES);
	    				ma.preInsert();
	    				ma.setId(folderid);
	    				dcObjectMainDao.insert(ma);
	    				
    	 				dcObjectFolder.setFolderName(folderName);
        				dcObjectFolder.setFolderUrl(folderUrl);
        				dcObjectFolder.setIsStruct(0);
        				dcObjectFolderDao.insert(dcObjectFolder);

						DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
						dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
						dcMetadataStroeService.insertTableVersion(ma,dcObjectMainVersion,dcObjectFolder);
    				}
				}
				flag = fs.mkdirs(new Path(temp+dcHdfsFileLook.getFolderName()));
			}else{
				byte[] buff=Content.getBytes();//想要输入内容 
				Path path=new Path(temp+dcHdfsFileLook.getFolderName());//文件存放路径及文件名称 
				FSDataOutputStream outputStream=fs.create(path); 
				outputStream.write(buff, 0, buff.length); 
				flag=true;
			}
			return flag;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
	
	@Transactional(readOnly = false)
		public boolean remove(DcHdfsFileLook dcHdfsFileLook, String type){
			boolean flag=false;
			try {
				//创建hdfs连接
				FileSystem fs = null;
				//初始化
				String address = DcPropertyUtils.getProperty("hadoop.main.address");
				fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
				if("2".equals(type)){
					deleteAll(fs,dcHdfsFileLook.getPathName(),false);
				}else{
					deleteAll(fs,dcHdfsFileLook.getPathName(),true);
				}
				Trash trash = new Trash(fs,fs.getConf());
				flag = trash.moveToTrash(new Path(dcHdfsFileLook.getPathName()));
				if(flag){
					logger.info("Move to Trash"+dcHdfsFileLook.getPathName());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Move to trash error"+e);
				logger.error(e.getMessage());
			}
			return flag;
		}
	
	@Transactional(readOnly = false)
	public boolean deletePath(DcHdfsFileLook dcHdfsFileLook, String type) throws Exception{
		boolean flag= false;
		try {
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			//出错，不知道什么原因
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			//删除文件时删除文件记录
			if("2".equals(type)){
				deleteAll(fs,dcHdfsFileLook.getPathName(),false);
			}else{
				deleteAll(fs,dcHdfsFileLook.getPathName(),true);
			}
			flag = fs.delete(new Path(dcHdfsFileLook.getPathName()),true);//如果是删除路径 把参数换成路径即可"/a/test4" 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return flag;
	}
	
	//批量回收
	@Transactional(readOnly = false)
	public String deleteAll(String[] ids,DcHdfsFileLook dcHdfsFileLook){
		StringBuffer strbuf = new StringBuffer();
		try {
			FileSystem fs = null;
			//初始化
			String address = DcPropertyUtils.getProperty("hadoop.main.address");
			fs = FileSystem.get(new URI("hdfs://"+address),new Configuration(), "hdfs");//hdfs配置信息在dc_config中配置
			Trash trash = new Trash(fs,fs.getConf());
			//logger.info("move to trash start");
			int count=0;
			int failcount = 0 ;
			boolean b = true;
			for(int i=0;i<ids.length;i++){// /tmp /tmp/hhh
				//批量删除
				DcObjectMain dcObjectMain = dcObjectMainDao.findUniqueByProperty("OBJ_NAME",ids[i]);
				if(dcObjectMain!=null){
					String type = dcObjectMain.getObjType();
					if(DcObjectMain.OBJ_TYPE_FILE.equals(type)){
						deleteAll(fs,ids[i],false);
					}else if(DcObjectMain.OBJ_TYPE_FOLDER.equals(type)){
						deleteAll(fs,ids[i],true);
					}
				}
/*				if("2".equals(type)){
					
				}else if("5")*/
				boolean flag =  trash.moveToTrash(new Path(ids[i]));
				if(!flag){
					String folerName = ids[i].substring(ids[i].lastIndexOf("/"), ids[i].length());
					strbuf.append("回收"+folerName+"出错 ");
					if(b){
						strbuf.append(folerName);
						b=false;
					}else{
						strbuf.append(","+folerName);
					}
					failcount++;
				}else{
					count++;
				}
			}
			if(count==0){
				return "批量删除失败";
			}else if(count>0&&failcount==0){
				return "批量删除成功(恢复路径为/user/hdfs/.Trash/Current)";
			}else{
				return "批量删除成功"+count+"条,失败"+failcount+"条,其中删除("+strbuf.toString()+")出错";
			}
			//logger.info("move to trash begin");
		} catch (Exception e) {
			//logger.error(e.getMessage());
			//strbuf.append("批量删除失败");
			return "批量删除失败";
		} 
		//return strbuf.toString();
	}
	
	//通过id获取文件
	public DcObjectFile getFile(String id){
		DcObjectFile dcObjectFile = dcObjectFileDao.get(id);
		return dcObjectFile;
	}
	
	//通过id获取文件夹
	public DcObjectFolder getFolder(String id){
		DcObjectFolder dcObjectFolder = dcObjectFolderDao.get(id);
		return dcObjectFolder;
	}
	
	//递归循环删除文件记录
	@Transactional(readOnly = false)
	public void deleteAll(FileSystem fs,String path,boolean type) throws Exception{
		if(type){//是文件夹
			FileStatus[] filelist = fs.listStatus(new Path(path));
			for(int i=0;i<filelist.length;i++){
				FileStatus fileStatus = filelist[i];
				String pa = fileStatus.getPath().toString();
				String pathName = pa.replaceFirst("hdfs://"+ DcPropertyUtils.getProperty("hdfs.datanode.address"), "");
				if(fileStatus.isDirectory()){
					deleteAll(fs,pathName,fileStatus.isDirectory());
					dcObjectFolderDao.deleteName(pathName);
				}else{
					dcObjectFileDao.deleteName(pa);
				}
				DcObjectMain dcObjectMain = dcObjectMainDao.findUniqueByProperty("OBJ_NAME", pathName);
				dcObjectMainDao.deleteName(pathName);
				if(dcObjectMain!=null){
					dcCommonService.deleteEsByIndex(dcObjectMain.getId(), DcObjectMain.OBJ_TYPE_FILE);
				}
			}
			
			dcObjectMainDao.deleteName(path);
		}else{//是文件
			DcObjectMain dcObjectMain = dcObjectMainDao.findUniqueByProperty("OBJ_NAME", path);
			dcObjectMainDao.deleteName(path);
			if(dcObjectMain!=null){
				dcCommonService.deleteEsByIndex(dcObjectMain.getId(), DcObjectMain.OBJ_TYPE_FILE);
			}
		}
	}
	
}
