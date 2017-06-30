/********************** 版权声明 *************************
 * 文件名: job2mysqlService.java
 * 包名: com.hlframe.modules.dc.uploadtohdfs
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月5日 下午3:56:06
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveDatabaseDao;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveTableDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveDatabase;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveTable;
import com.hlframe.modules.dc.datasearch.entity.DcSearchContent;
import com.hlframe.modules.dc.datasearch.service.DcSearchContentService;
import com.hlframe.modules.dc.metadata.dao.*;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.sys.dao.UserDao;
import com.hlframe.modules.sys.entity.Office;
import com.hlframe.modules.sys.entity.User;
import com.hlframe.modules.sys.service.OfficeService;
import com.hlframe.modules.sys.utils.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.uploadtohdfs.job2mysqlService.java 
 * @职责说明: TODO 元数据存储方法整合, 需对元数据和ES数据统一封装, 根据元数据jobSrcFlag值判断是否要存储到到ES中...  
 * @创建者: yuzh
 * @创建时间: 2016年12月5日 下午3:56:06
 */
@Service
@Transactional(readOnly = false)
public class DcMetadataStroeService extends CrudService<DcObjectMainDao, DcObjectMain> {
	@Autowired
	private DcObjectTableDao dcObjectTableDao;
	@Autowired	//接口元数据
	private DcObjectInterfaceDao dcObjectIntfcDao;
	@Autowired
	private DcObjectFieldDao dcObjectFieldDao;
	@Autowired
	private DcObjectFileDao dcObjectFileDao;
	@Autowired
	private DcObjectFolderDao dcObjectFolderDao;
	@Autowired
	private DcObjectLableDao dcObjectLableDao;
	@Autowired
	private DcObjectCataRefDao dcObjectCataRefDao;
	@Autowired
	private DcSearchContentService dcSearchContentService;
	@Autowired
	private DcHiveDatabaseDao dcHiveDatabaseDao;
	@Autowired
	private DcHiveTableDao dcHiveTableDao;
	@Autowired
	private DcCommonService dcCommonService;
	@Autowired
	private DcObjectMainVersionDao dcObjectMainVersionDao;
	@Autowired
	private DcDataSourceDao dcDataSourceDao;

	@Autowired
	private UserDao userDao;

	/**
	 * @方法名称: obj2MySQL 
	 * @实现功能: 表和字段的新增修改
	 * @param dcObjectMain
	 * @param dcObjectTable
	 * @param dcObjectField
	 * @create by yuzh at 2016年12月6日 上午11:43:39
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectTable dcObjectTable, List<DcObjectField> dcObjectField){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		if (dao.getById(dcObjectMain.getId())==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dcObjectMain.setObjName(dcObjectTable.getDbDataBase()+"."+dcObjectTable.getTableName());
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();//将新建数据插入系统默认分类中
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("af7cfe6232e44a6db031da602c197827");	//添加至数据表分类
			dcObjectCataRefDao.insert(dcObjectCataRef);
			for(DcSearchContent unMarked : dcSearchContentService.getUnmark()){
				dcObjectCataRef.setCataId(unMarked.getId());
				dcObjectCataRefDao.insert(dcObjectCataRef);
			}
			//创建table信息
			dcObjectTable.preInsert();
			dcObjectTable.setObjId(id);
			dcObjectTableDao.insert(dcObjectTable);
		}else{
			dcObjectMain.preUpdate();
			dcObjectMain.setObjName(dcObjectTable.getDbDataBase()+"."+dcObjectTable.getTableName());
			dao.update(dcObjectMain);
			//更新table信息
			dcObjectTable.preUpdate();
			dcObjectTableDao.update(dcObjectTable);
		}
		

		//dcObjectFieldDao.delete(new DcObjectField(id));//在对字段进行操作前 删除原有对应字段
		dcObjectFieldDao.deleteByBelong2Id(id);//在对字段进行操作前 删除原有对应字段

		if(!CollectionUtils.isEmpty(dcObjectField)){
			//更改为批量提交方式
			for(DcObjectField field:dcObjectField){
				field.preInsert();
				field.setBelong2Id(id);
			}
			dcObjectFieldDao.batchInsert(dcObjectField);
		}
		try {
			dcCommonService.loadTableToEs(id);
		} catch (Exception e) {
			logger.error("DcCommonService.loadTableToEs()",e);
		}
	}

	/**
	 * @方法名称: obj2MySQL 
	 * @实现功能: 文件的新增修改
	 * @param dcObjectMain
	 * @param dcObjectFile
	 * @create by yuzh at 2016年12月6日 上午11:44:16
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectFile dcObjectFile){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		DcObjectMain para = new DcObjectMain();
		para.setId(dcObjectMain.getId());
		if (dao.get(para)==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("d523ac0e2a5d4d80ab64c085ca3f9da3"); //添加至 文件 分类
			dcObjectCataRefDao.insert(dcObjectCataRef);
			
			//插入table信息
			dcObjectFile.preInsert();
			dcObjectFile.setId(id);
			dcObjectFileDao.insert(dcObjectFile);
			try{
				dcCommonService.loadFileToEs(dcObjectFile.getId());
			}catch(Exception e){
				logger.error("DcCommonService.loadFileToEs()",e);
			}
		}else {
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			//更新table信息
			if (dcObjectFileDao.get(dcObjectFile) == null) {
				dcObjectFile.preInsert();
				dcObjectFile.setId(dcObjectMain.getId());
				dcObjectFileDao.insert(dcObjectFile);
			} else {
				//更新file信息
				dcObjectFile.preUpdate();
				dcObjectFileDao.update(dcObjectFile);
			}
		}
		try{
			dcCommonService.loadFileToEs(id);
		}catch(Exception e){
			logger.error("-->dcMetadataStoreService.obj2MySQL() fileId :"+id,e);
		}
	}
	
	
	/**
	 * @方法名称: obj2MySQL 
	 * @实现功能: 文件夹及文件的新增修改	TODO: 需实现文件对象批量保存, 批量load至ES peijd
	 * @param dcObjectMain
	 * @param dcObjectFolder
	 * @param dcObjectFile
	 * @param loadFile2ES	是否加载文件到ElasticSearch检索队列  add By peijd 2017-3-4
	 * @create by yuzh at 2016年12月6日 上午11:45:20
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectFolder dcObjectFolder, List<DcObjectFile> dcObjectFile, boolean loadFile2ES) {
		String id = dcObjectMain.getId();//获取主表id作为从表id
		DcObjectMain para = new DcObjectMain();
		para.setId(dcObjectMain.getId());
		if (dao.get(para)==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("ef15e415e7684e0e9cba8450603493b8");	//添加至文件夹分类
			dcObjectCataRefDao.insert(dcObjectCataRef);
			
			//插入folder信息
			dcObjectFolder.preInsert();
			dcObjectFolder.setId(id);
			dcObjectFolderDao.insert(dcObjectFolder);
		}else{
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			if(dcObjectFolderDao.get(dcObjectFolder)==null){
				dcObjectFolder.preInsert();
				dcObjectFolder.setId(dcObjectMain.getId());
				dcObjectFolderDao.insert(dcObjectFolder);
			}else{
				//更新folder信息
				dcObjectFolder.preUpdate();
				dcObjectFolderDao.update(dcObjectFolder);
			}
		}

		//获取对应文件夹
		DcObjectMain obj = new DcObjectMain();
		obj.setJobId(id);
		//p代表是子文件夹夹或文件
		obj.setJobSrcFlag("P");
		List<DcObjectMain> list = dao.findList(obj);
		for(DcObjectMain main :list){
			if(main.getObjType().equals(DcObjectMain.OBJ_TYPE_FOLDER)){
				DcObjectFolder param2 = new DcObjectFolder();
				param2.setObjId(main.getId());
				dcObjectFolderDao.delete(param2);
			}else if(main.getObjType().equals(DcObjectMain.OBJ_TYPE_FILE)){
				DcObjectFile param = new DcObjectFile();

				param.setId(main.getId());
				dcObjectFileDao.delete(param);
			}
			dao.deleteByLogic(main);
		}




		try{
			if(DcObjectMain.OBJ_TYPE_FOLDER.equals(dcObjectMain.getObjType())){
				for(DcObjectFile field:dcObjectFile){//新建文件信息
					field.preInsert();
					field.setFileBelong(id);//设置文件归属id

					if(loadFile2ES){	//load 至ElasticSearch集群
						dcObjectMain.setId(field.getId());
						//P代表所有文件夹下的文件和文件夹
						dcObjectMain.setJobSrcFlag("P");
						//obj2MySQL(dcObjectMain, field);
						if (field.getObjType().equals(DcObjectMain.OBJ_TYPE_FOLDER)) {
							List<DcObjectFile> li = new ArrayList<DcObjectFile>();
							DcObjectFolder f = new DcObjectFolder();
							f.setId(field.getObjId());
							f.setFolderUrl(field.getFileUrl());
							f.setFolderName(field.getFileName());
							f.setObjId(field.getObjId());
							f.setId(field.getId());
							dcObjectMain.setObjType(DcObjectMain.OBJ_TYPE_FOLDER);
							obj2MySQL(dcObjectMain,f,li,false);
						}else{
							dcObjectMain.setObjName(field.getFileName());
							dcObjectMain.setObjType(dcObjectMain.OBJ_TYPE_FILE);
							obj2MySQL(dcObjectMain, field,false);
						}
					}
				}
			}
		}catch (Exception e){
			logger.error(e.getMessage());
			throw new ServiceException(e);
		}
		try{
			dcCommonService.loadFolderToEs(id);
		}catch(Exception e){
			logger.error("-->DcCommonService.loadFolderToEs()",e);
		}
	}
	
	
	/**
	 * @方法名称: obj2MySQL 
	 * @实现功能: 标签新增修改
	 * @param dcObjectMain
	 * @param dcObjectLable
	 * @create by yuzh at 2016年12月6日 上午11:45:57
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectLable dcObjectLable){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		if (dao.getById(dcObjectMain.getId())==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("37e715fc0aa944fda279b5ce3b6234b4");	//添加至标签分类
			dcObjectCataRefDao.insert(dcObjectCataRef);
			
			//新建label信息
			dcObjectLable.preInsert();
			dcObjectLable.setId(id);
			dcObjectLableDao.insert(dcObjectLable);
		}else{
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			
			//更新label信息
			dcObjectLable.preUpdate();
			dcObjectLableDao.update(dcObjectLable);
		}
		try{
			dcCommonService.loadLabelToEs(dcObjectLable.getId());
		}catch(Exception e){
			logger.error("DcCommonService.loadLabelToEs()",e);
		}
	}

	/**
	 * @方法名称: obj2MySQL
	 * @实现功能: 数据库元数据新增修改
	 * @param dcObjectMain
	 * @param dcHiveDatabase
	 * @create by hgw at 2017年12月6日 上午11:45:57
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcHiveDatabase dcHiveDatabase){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		if (dao.getById(dcObjectMain.getId())==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			
			//新建database信息
			dcHiveDatabase.preInsert();
			dcHiveDatabase.setId(id);
			dcHiveDatabaseDao.insert(dcHiveDatabase);
		}else{
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			//更新database信息
			dcHiveDatabase.preUpdate();
			dcHiveDatabaseDao.update(dcHiveDatabase);
		}
	}

	/**
	 * @方法名称: deObj2MySQL
	 * @实现功能: 数据库元数据删除
	 * @param dcObjectMain
	 * @param dcHiveDatabase
	 * @create by hgw at 2017年12月6日 上午11:45:57
	 */
	public void deObj2MySQL(DcObjectMain dcObjectMain, DcHiveDatabase dcHiveDatabase){
		dao.delete(dcObjectMain);
		dcHiveDatabaseDao.deleteByLogic(dcHiveDatabase);
	}

	/**
	 * @方法名称: delObj2MySQL
	 * @实现功能: 表和字段的删除
	 * @param dcObjectMain
	 * @param dcObjectTable
	 * @param dcObjectField
	 * @create by hgw at 2017年3月11日 上午11:43:39
	 */
	public void delObj2MySQL(DcObjectMain dcObjectMain, DcObjectTable dcObjectTable, DcObjectField dcObjectField, DcObjectCataRef dcObjectCataRef, DcHiveTable dcHiveTable){
		dao.delete(dcObjectMain);
		dcObjectCataRefDao.delete(dcObjectCataRef);
	}

	/**
	 * @方法名称: obj2MySQL
	 * @实现功能: 表和字段的新增修改
	 * @param dcObjectMain
	 * @param dcObjectTable
	 * @param dcObjectField
	 * @create by yuzh at 2016年12月6日 上午11:43:39
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectTable dcObjectTable, List<DcObjectField> dcObjectField, DcHiveTable dcHiveTable){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		if (dao.getById(dcObjectMain.getId())==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dcObjectMain.setObjName(dcObjectTable.getDbDataBase()+"."+dcObjectTable.getTableName());
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();//将新建数据插入系统默认分类中
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("af7cfe6232e44a6db031da602c197827");	//添加至数据表分类
			dcObjectCataRefDao.insert(dcObjectCataRef);
			for(DcSearchContent unMarked : dcSearchContentService.getUnmark()){
				dcObjectCataRef.setCataId(unMarked.getId());
				dcObjectCataRefDao.insert(dcObjectCataRef);
			}
			
			//添加table信息
			dcObjectTable.preInsert();
			dcObjectTable.setObjId(id);
			dcObjectTableDao.insert(dcObjectTable);
			//添加hive table 信息
			dcHiveTable.preInsert();
			dcHiveTable.setId(id);
			dcHiveTableDao.insert(dcHiveTable);
		}else{
			dcObjectMain.preUpdate();
			dcObjectMain.setObjName(dcObjectTable.getDbDataBase()+"."+dcObjectTable.getTableName());
			dao.update(dcObjectMain);
			//更新table信息
			dcObjectTable.preUpdate();
			dcObjectTableDao.update(dcObjectTable);
			//更新hiveTable信息
			dcHiveTable.preUpdate();
			dcHiveTableDao.update(dcHiveTable);
		}

		dcObjectFieldDao.delete(new DcObjectField(id));//在对字段进行操作前 删除原有对应字段

		for(DcObjectField field:dcObjectField){
			field.preInsert();
			field.setBelong2Id(id);
			dcObjectFieldDao.insert(field);
		}
		try {
			dcCommonService.loadTableToEs(id);
		} catch (Exception e) {
			logger.error("DcCommonService.loadTableToEs()",e);
		}
	}
	
		/**
		 * @方法名称: findFieldList
		 * @实现功能: 根据表id查询字段信息
		 * @param  dcObjectField
		 * @create by hgw at 2017年3月24日 下午15:31:39
		 */
		 public List<DcObjectField> findFieldList(DcObjectField dcObjectField){
			 return dcObjectFieldDao.findList(dcObjectField);
		 }

	/**
	 * @方法名称: obj2MySQL
	 * @实现功能: 接口数据采集 保存元数据信息
	 * @params  [srcMain, intfc]
	 * @return  void
	 * @create by peijd at 2017/4/17 20:07
	 */
    public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectInterface intfc) {
		String id = dcObjectMain.getId();//获取主表id作为从表id
		if (dao.getById(dcObjectMain.getId())==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("4564a441e56e4d4c9d2f0fee2c0e81d7");	//添加至接口分类
			dcObjectCataRefDao.insert(dcObjectCataRef);

			//插入table信息
			intfc.preInsert();
			intfc.setId(id);
			dcObjectIntfcDao.insert(intfc);

		}else{
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			//更新table信息
			intfc.preUpdate();
			dcObjectIntfcDao.update(intfc);
		}

		try{
			//接口对象 推送至ES
			dcCommonService.loadInterfaceToEs(intfc.getId());
		}catch(Exception e){
			logger.error("DcCommonService.loadFileToEs()",e);
		}
    }

	/**
	 *创建表元数据版本信息
	 * @param dcObjectMain 新的
	 * @param dcObjectTable 新的
	 * @create by hgw
	 */
	public void insertTableVersion(DcObjectMain srcMain, DcObjectMainVersion dcObjectMainVersion, DcObjectTable srcTable){
		StringBuffer strbuf = new StringBuffer();
		DcDataSource dcDataSource = dcDataSourceDao.get(srcTable.getTableLink());
		DcObjectMain dcmain = dao.get(srcMain);
		DcObjectTable dcObjectTable = dcObjectTableDao.getTable(srcTable);
		String dbType = dcDataSource==null?"hive":dcDataSource.getServerType().replace("dc_","");
		String tablelink = dcDataSource==null?"hive":dcDataSource.getConnName();
		String objName = ("").equals(srcTable.getDbDataBase())?srcTable.getTableName():srcTable.getDbDataBase()+"."+srcTable.getTableName();
		dcObjectMainVersion.setObjName(objName);
		if(dcObjectMainVersion.JOB_TYPE_ACTIVE_ADD.equals(dcObjectMainVersion.getActive())||dcObjectTable==null){
			//元数据更改记录
			dcObjectMainVersion.setRemark("添加了一条元数据");
		}else if(dcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT.equals(dcObjectMainVersion.getActive())||dcObjectMainVersion.JOB_TYPE_ACTIVE_TASK.equals(dcObjectMainVersion.getActive())){
				//元数据更改记录
				if(!(srcTable.getDbDataBase()+"."+srcTable.getTableName()).equals(dcmain.getObjName())){
					strbuf.append("对象名"+dcmain.getObjName()+"更改为"+srcTable.getDbDataBase()+"."+srcTable.getTableName()+" ");
				}
				if(!srcMain.getObjDesc().equals(dcmain.getObjDesc())){
					strbuf.append("对象描述"+dcmain.getObjDesc()+"更改为"+srcMain.getObjDesc()+" ");
				}
				if(!srcMain.getObjCode().equals(dcmain.getObjCode())){
					strbuf.append("对象编码"+dcmain.getObjCode()+"更改为"+srcMain.getObjCode()+" ");
				}
				if(!dbType.contains(dcObjectTable.getDbType())){
					strbuf.append("数据库类别"+dcObjectTable.getDbType()+"更改为"+dbType+" ");
				}
				if(!dcObjectTable.getTableLink().equals(tablelink)){
					strbuf.append("数据库连接"+dcObjectTable.getTableLink()+"更改为"+tablelink+" ");
				}
				if(!dcObjectTable.getTableName().equals(srcTable.getTableName())){
					strbuf.append("数据表"+dcObjectTable.getTableName()+"更改为"+srcTable.getTableName()+" ");
				}
				if(!dcObjectTable.getDbDataBase().equals(srcTable.getDbDataBase())){
					strbuf.append("数据库名称"+dcObjectTable.getDbDataBase()+"更改为"+srcTable.getDbDataBase()+" ");
				}
			if("".equals(strbuf.toString())){
				strbuf.append("未对元数据进行修改");
			}
		}
		if(!"".equals(strbuf.toString())){
			dcObjectMainVersion.setRemark("修改内容:"+strbuf.toString());
		}
		//元数据版本设置信息
		//dcObjectMainVersion.setObjName(srcMain.getObjName());
		dcObjectMainVersion.setUser(UserUtils.getUser());
		dcObjectMainVersion.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
		//插入元数据版本记录 是否为原纪录N
		dcObjectMainVersion.preInsert();
		dcObjectMainVersionDao.insert(dcObjectMainVersion);
	}

	/**
	 *创建文件夹元数据版本信息
	 * @param dcObjectMain 新的
	 * @param dcObjectFolder 新的
	 * @create by hgw
	 */
	public void insertTableVersion(DcObjectMain srcMain, DcObjectMainVersion dcObjectMainVersion, DcObjectFolder srcFolder){
		StringBuffer strbuf = new StringBuffer();
		DcObjectMain dcmain = dao.get(srcMain);
		DcObjectFolder dcObjectFolder = dcObjectFolderDao.get(srcFolder);
		if(dcObjectMainVersion.JOB_TYPE_ACTIVE_ADD.equals(dcObjectMainVersion.getActive())||dcObjectFolder==null){
			//元数据更改记录
			dcObjectMainVersion.setRemark("添加了一条元数据");
		}else if(dcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT.equals(dcObjectMainVersion.getActive())||dcObjectMainVersion.JOB_TYPE_ACTIVE_TASK.equals(dcObjectMainVersion.getActive())){
			//元数据更改记录
			if(!srcMain.getObjName().equals(dcmain.getObjName())){
				strbuf.append("对象名"+dcmain.getObjName()+"更改为"+srcMain.getObjName()+" ");
			}
			if(!srcMain.getObjDesc().equals(dcmain.getObjDesc())){
				strbuf.append("对象描述"+dcmain.getObjDesc()+"更改为"+srcMain.getObjDesc()+" ");
			}
			if(StringUtils.isNotBlank(srcMain.getObjCode())){
				if(!srcMain.getObjCode().equals(dcmain.getObjCode())){
					strbuf.append("对象编码"+dcmain.getObjCode()+"更改为"+srcMain.getObjCode()+" ");
				}
			}
			if(!srcFolder.getFolderUrl().contains(dcObjectFolder.getFolderUrl())) {
				strbuf.append("文件路径" + dcObjectFolder.getFolderUrl() + "更改为" + srcFolder.getFolderUrl() + " ");
			}
			if("".equals(strbuf.toString())){
				strbuf.append("未对元数据进行修改");
			}
		}
		if(!"".equals(strbuf.toString())){
			dcObjectMainVersion.setRemark("修改内容:"+strbuf.toString());
		}
		//元数据版本设置信息
		dcObjectMainVersion.setObjName(srcMain.getObjName());
		dcObjectMainVersion.setUser(UserUtils.getUser());
		dcObjectMainVersion.setObjType(DcObjectMain.OBJ_TYPE_FOLDER);
		//插入元数据版本记录 是否为原纪录N
		dcObjectMainVersion.preInsert();
		dcObjectMainVersionDao.insert(dcObjectMainVersion);
	}

	/**
	 *创建文件元数据版本信息
	 * @param dcObjectMain 新的
	 * @param DcObjectFile 新的
	 * @create by hgw
	 */
	public void insertTableVersion(DcObjectMain srcMain, DcObjectMainVersion dcObjectMainVersion, DcObjectFile srcFile){
		StringBuffer strbuf = new StringBuffer();
		DcObjectMain dcmain = dao.get(srcMain);
		DcObjectFile dcObjectFile = dcObjectFileDao.get(srcFile);
		if(dcObjectMainVersion.JOB_TYPE_ACTIVE_ADD.equals(dcObjectMainVersion.getActive())||dcObjectFile==null){
			//元数据更改记录
			dcObjectMainVersion.setRemark("添加了一条元数据");
		}else if(dcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT.equals(dcObjectMainVersion.getActive())||dcObjectMainVersion.JOB_TYPE_ACTIVE_TASK.equals(dcObjectMainVersion.getActive())){
				//元数据更改记录
			if(dcmain==null){
				strbuf.append("对象名"+"更改为"+srcMain.getObjName()+" ");
				strbuf.append("对象描述"+"更改为"+srcMain.getObjDesc()+" ");
				strbuf.append("对象编码"+"更改为"+srcMain.getObjCode()+" ");
			}else{
				if(!srcMain.getObjName().equals(dcmain.getObjName())){
					strbuf.append("对象名"+dcmain.getObjName()+"更改为"+srcMain.getObjName()+" ");
				}
				if(!srcMain.getObjDesc().equals(dcmain.getObjDesc())){
					strbuf.append("对象描述"+dcmain.getObjDesc()+"更改为"+srcMain.getObjDesc()+" ");
				}
/*				if(!srcMain.getObjCode().equals(dcmain.getObjCode())){
					strbuf.append("对象编码"+dcmain.getObjCode()+"更改为"+srcMain.getObjCode()+" ");
				}*/
			}
				if(!srcFile.getFileUrl().equals(dcObjectFile.getFileUrl())) {
					strbuf.append("文件路径" + dcObjectFile.getFileUrl() + "更改为" + srcFile.getFileUrl() + " ");
				}
			if("".equals(strbuf.toString())){
				strbuf.append("未对元数据进行修改");
			}
		}
		if(!"".equals(strbuf.toString())){
			dcObjectMainVersion.setRemark("修改内容:"+strbuf.toString());
		}
		//元数据版本设置信息
		dcObjectMainVersion.setObjName(srcMain.getObjName());
		dcObjectMainVersion.setUser(UserUtils.getUser());
		dcObjectMainVersion.setObjType(DcObjectMain.OBJ_TYPE_FILE);
		//插入元数据版本记录 是否为原纪录N
		dcObjectMainVersion.preInsert();
		dcObjectMainVersionDao.insert(dcObjectMainVersion);
	}

	/**
	 *创建接口元数据版本信息
	 * @param DcObjectMain 新的
	 * @param DcObjectMainVersion 新的
	 * @create by hgw
	 */
	public void insertTableVersion(DcObjectMain srcMain, DcObjectMainVersion dcObjectMainVersion, DcObjectInterface srcInterface){
		StringBuffer strbuf = new StringBuffer();
		DcObjectMain dcmain = dao.get(srcMain);
		DcObjectInterface dcObjectInterface = dcObjectIntfcDao.get(srcInterface);
		if(dcObjectMainVersion.JOB_TYPE_ACTIVE_ADD.equals(dcObjectMainVersion.getActive())||srcInterface==null){
			dcObjectMainVersion.setActive(dcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
			//元数据更改记录
			dcObjectMainVersion.setRemark("添加了一条元数据");
		}else if(dcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT.equals(dcObjectMainVersion.getActive())||dcObjectMainVersion.JOB_TYPE_ACTIVE_TASK.equals(dcObjectMainVersion.getActive())){
			//元数据更改记录
			if(!srcMain.getObjName().equals(dcmain.getObjName())){
				strbuf.append("对象名"+dcmain.getObjName()+"更改为"+srcMain.getObjName()+" ");
			}
			if(!srcMain.getObjDesc().equals(dcmain.getObjDesc())){
				strbuf.append("对象描述"+dcmain.getObjDesc()+"更改为"+srcMain.getObjDesc()+" ");
			}
			if(!srcMain.getObjCode().equals(dcmain.getObjCode())){
				strbuf.append("对象编码"+dcmain.getObjCode()+"更改为"+srcMain.getObjCode()+" ");
			}
			if(dcObjectInterface!=null){

			}
			if("".equals(strbuf.toString())){
				strbuf.append("未对元数据进行修改");
			}
		}
		if(!"".equals(strbuf.toString())){
			dcObjectMainVersion.setRemark("修改内容:"+strbuf.toString());
		}
		//元数据版本设置信息
		dcObjectMainVersion.setObjName(srcMain.getObjName());
		dcObjectMainVersion.setUser(UserUtils.getUser());
		dcObjectMainVersion.setObjType(DcObjectMain.OBJ_TYPE_INTER);
		//插入元数据版本记录 是否为原纪录N
		dcObjectMainVersion.preInsert();
		dcObjectMainVersionDao.insert(dcObjectMainVersion);
	}

	/**
	 * @方法名称: obj2MySQL
	 * @实现功能: 文件的新增修改
	 * @param dcObjectMain
	 * @param dcObjectFile
	 * @param flag
	 * @create by hgw
	 */
	public void obj2MySQL(DcObjectMain dcObjectMain, DcObjectFile dcObjectFile, boolean flag){
		String id = dcObjectMain.getId();//获取主表id作为从表id
		DcObjectMain para = new DcObjectMain();
		para.setId(dcObjectMain.getId());
		if (dao.get(para)==null){//判断主表新建或更新
			dcObjectMain.preInsert();
			dcObjectMain.setId(id);
			dao.insert(dcObjectMain);
			DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
			dcObjectCataRef.setObjId(id);
			dcObjectCataRef.setCataId("d523ac0e2a5d4d80ab64c085ca3f9da3"); //添加至 文件 分类
			dcObjectCataRefDao.insert(dcObjectCataRef);

			//插入table信息
			dcObjectFile.preInsert();
			dcObjectFile.setId(id);
			dcObjectFileDao.insert(dcObjectFile);
		}else {
			dcObjectMain.preUpdate();
			dao.update(dcObjectMain);
			//更新table信息
			if (dcObjectFileDao.get(dcObjectFile) == null) {
				dcObjectFile.preInsert();
				dcObjectFile.setId(dcObjectMain.getId());
				dcObjectFileDao.insert(dcObjectFile);
			} else {
				//更新file信息
				dcObjectFile.preUpdate();
				dcObjectFileDao.update(dcObjectFile);
			}
		}
		if(flag){
			try{
				dcCommonService.loadFileToEs(id);
			}catch(Exception e){
				logger.error("-->dcMetadataStoreService.obj2MySQL() fileId :"+id,e);
			}
		}
	}

	/**
	 * @方法名称: deleteFolder
	 * @实现功能: 文件夹的删除
	 * @param dcObjectFolder
	 * @create by hgw
	 */
	public void deleteFolder(DcObjectFolder dcObjectFolder){
		dcObjectFolderDao.delete(dcObjectFolder);
	}
	/**
	 * @方法名称: deleteFile
	 * @实现功能: 文件的删除
	 * @param dcObjectFile
	 * @create by hgw
	 */
	public void deleteFile(DcObjectFile dcObjectFile){
		dcObjectFileDao.delete(dcObjectFile);
	}
}
