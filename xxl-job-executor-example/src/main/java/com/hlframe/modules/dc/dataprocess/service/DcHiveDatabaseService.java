/********************** 版权声明 *************************
 * 文件名: DcHiveDatabaseService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2017年1月10日 下午4:09:44
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveDatabaseDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveDatabase;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveTable;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcHiveDatabaseService.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2017年1月10日 下午4:09:44
 */
@Service
@Transactional(readOnly = true)
public class DcHiveDatabaseService extends CrudService<DcHiveDatabaseDao, DcHiveDatabase>{
	@Autowired
	private DcHiveTableService dcHiveTableService;

	public String executeMetaSql(DcHiveDatabase dcHiveDatabase){
		Client client = Client.create();
		try {
			//restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("executeHive.restServer.url"));
			WebResource resource = client.resource(u);
			//构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();  
			if(StringUtils.isNotBlank(dcHiveDatabase.getRemarks())){
			params.add("metaSql","create database if not exists "+dcHiveDatabase.getDatabase()
					+ " comment '"+dcHiveDatabase.getRemarks()+"'");  
			}else{
			params.add("metaSql", "create database if not exists "+dcHiveDatabase.getDatabase());
			}
			String result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);  
			return result;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
	/**
	 * @方法名称: findDcHiveDatabase 
	 * @实现功能: TODO
	 * @param page
	 * @param dcHiveDatabase
	 * @return
	 * @create by yuzh at 2017年1月10日 下午4:56:02
	 */
	public Page<DcHiveDatabase> findDcHiveDatabase(Page<DcHiveDatabase> page, DcHiveDatabase dcHiveDatabase) {
		// 设置分页参数
		dcHiveDatabase.setPage(page);
	    // 执行分页查询
		page.setList(dao.findList(dcHiveDatabase));
		return page;
	}

	/**
	 * @方法名称: del 
	 * @实现功能: TODO
	 * @param dcHiveDatabase
	 * @create by yuzh at 2017年1月10日 下午6:37:26
	 */
	@Transactional(readOnly = false)
	public void del(DcHiveDatabase dcHiveDatabase) {
		dao.deleteByLogic(dcHiveDatabase);
	}
	
	/**
	 * @方法名称: getHiveDataBase 
	 * @实现功能: 获得数据库名
	 * @param
	 * @return
	 * @create by cdd at 2017年1月11日 下午2:10:11
	 */
	public List<DcHiveTable> getHiveDatabase(){
		DcHiveTable dcHiveTable= new DcHiveTable();
		List<DcHiveTable> dbList = dcHiveTableService.findList(dcHiveTable);

		return  dbList;
	}

	/**
	 * @方法名称: getHiveDataBase 
	 * @实现功能:根据数据库名删除数据
	 * @param dcHiveDatabase
	 * @return
	 * @create by hgw at 2017年3月10日 
	 */
/*	public void ByDatabaseDelete(DcHiveDatabase dcHiveDatabase){
		dao.ByDatabaseDelete(dcHiveDatabase);
	}*/
	
	/**
	 * @方法名称: getHiveDataBase 
	 * @实现功能:查询数据库名是否存在
	 * @param dcHiveDatabase
	 * @return
	 * @create by hgw at 2017年3月10日 
	 */
	public DcHiveDatabase byDatabaseGet(DcHiveDatabase dcHiveDatabase){
		return dao.byDatabaseGet(dcHiveDatabase);
	}
	
	/**
	 * @方法名称: getJobName 
	 * @实现功能: 验证database不重复
	 * @param jobName
	 * @return
	 * @create by yuzh at 2016年11月25日 下午1:46:48
	 */
	public Object getDatabaseName(String database) {
		// TODO Auto-generated method stub
		return dao.getDatabaseName(database);
	}
}
