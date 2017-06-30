/********************** 版权声明 *************************
 * 文件名: DcObjectLinkService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月10日 上午11:31:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.IdGen;
import com.hlframe.modules.dc.dataprocess.entity.DcDataProcessOutput;
import com.hlframe.modules.dc.metadata.dao.DcObjectLinkDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.neo4j.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.DcObjectLinkService.java 
 * @职责说明: 对象链路关系 service
 * @创建者: peijd
 * @创建时间: 2016年12月10日 上午11:31:22
 */
@Service
@Transactional(readOnly = true)
public class DcObjectLinkService extends CrudService<DcObjectLinkDao, DcObjectLink> {

	@Autowired	//溯源关系service
	private Neo4jService neo4jService;

	
	/**
	 * @方法名称: configObjLinkByExtract 
	 * @实现功能: 根据数据采集task 设置链路关系
	 * @param extractId		采集Task Id
	 * @param extractType	采集Task 类型
	 * @param srcObj		源对象		
	 * @param tarObj		目标对象
	 * @create by peijd at 2016年12月17日 上午10:27:19
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByExtract(String extractId, String extractType, String srcObj, String tarObj){
		Assert.hasText(extractId);
		Assert.hasText(extractType);
		Assert.hasText(srcObj);
		Assert.hasText(tarObj);
		
		DcObjectLink param = new DcObjectLink();
		param.setDataSource(extractType);
		param.setSrcObjId(srcObj);
		param.setProcessId(extractId);
		//检查对象关系 是否存在
		DcObjectLink link = dao.getByParam(param);
		if(null==link){	//新建link 
			link = param;
			if(DcObjectLink.DATA_SOURCE_EXTRACT_DB.equals(extractType)){
				link.setLinkType(DcObjectLink.LINK_TYPE_TABLE);	//数据表采集

			}else if(DcObjectLink.DATA_SOURCE_EXTRACT_INTF.equals(extractType)){
				link.setLinkType(DcObjectLink.LINK_TYPE_INTFC);	//接口采集

			}else if(DcObjectLink.DATA_SOURCE_EXTRACT_HDFS.equals(extractType)){
				link.setLinkType(DcObjectLink.LINK_TYPE_FILE);	//HDFS采集

			}else if(DcObjectLink.DATA_SOURCE_EXTRACT_FILE.equals(extractType)){
				link.setLinkType(DcObjectLink.LINK_TYPE_FILE);	//文件采集
			}

			//添加neo4j 链路
//			Neo4j neo4j = new Neo4j();
//			neo4j.setTableName(srcObj);
//			neo4j.setTarName(tarObj);
//			neo4j.setProcessId(extractId);
//			neo4j.setProcessName("数据采集("+extractType+")");
//			neo4j.setProcessDes("数据采集("+extractType+")");
//			neo4jService.queryHiveSql(neo4j);
		}
		link.setTarObjId(tarObj);
		link.setLinkJson(new StringBuilder(64).append("{from:'").append(srcObj).append("', to:'").append(tarObj).append("'}").toString());
		save(link);
		neo4jService.addLink2Neo4j(link);
	}

	/**
	 * @方法名称: configObjLinkByExtractDB 
	 * @实现功能: 根据DB采集task 设置链路关系
	 * @param extractId
	 * @param srcObj
	 * @param tarObj
	 * @create by peijd at 2016年12月17日 上午11:06:56
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByExtractDB(String extractId, String srcObj, String tarObj){
		configObjLinkByExtract(extractId, DcObjectLink.DATA_SOURCE_EXTRACT_DB, srcObj, tarObj);
	}

	/**
	 * @方法名称: configObjLinkByExtractIntf
	 * @实现功能: 接口数据采集  元数据链路
	 * @params  [extractId, srcObj, tarObj]
	 * @return  void
	 * @create by peijd at 2017/4/17 20:30
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByExtractIntf(String extractId, String srcObj, String tarObj){
		configObjLinkByExtract(extractId, DcObjectLink.DATA_SOURCE_EXTRACT_INTF, srcObj, tarObj);
	}

	/**
	 * @方法名称: configObjLinkByExtractHDFS
	 * @实现功能: HDFS文件采集  元数据链路
	 * @params  [extractId, srcObj, tarObj]
	 * @return  void
	 * @create by peijd at 2017/6/26 11:22
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByExtractHDFS(String extractId, String srcObj, String tarObj){
		configObjLinkByExtract(extractId, DcObjectLink.DATA_SOURCE_EXTRACT_HDFS, srcObj, tarObj);
	}

	/**
	 * @方法名称: configObjLinkByExtractFile
	 * @实现功能: 文件采集任务  元数据链路
	 * @params  [extractId, srcObj, tarObj]
	 * @return  void
	 * @create by peijd at 2017/6/26 11:23
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByExtractFile(String extractId, String srcObj, String tarObj){
		configObjLinkByExtract(extractId, DcObjectLink.DATA_SOURCE_EXTRACT_FILE, srcObj, tarObj);
	}

	/**
	 * @方法名称: configObjLinkByDesign  
	 * @实现功能: 根据转换设计关系 保存链路关系 table/field
	 * @param designId		设计Id
	 * @param tarTable		目标表对象
	 * @param deleteFlag 	
	 * @create by peijd at 2017年3月7日 上午9:44:27
	 */
	@Transactional(readOnly = false)
	public void configObjLinkByDesign(String designId, DcDataProcessOutput tarTable, boolean deleteFlag){
		Assert.hasText(designId);
		Assert.hasText(tarTable.getSrcTable());
		Assert.notEmpty(tarTable.getFieldList());
		
		List<DcObjectLink> linkList = new ArrayList<DcObjectLink>();
		DcObjectLink link = null;
		/**添加字段关系列表  TODO: 字段列表待解析
		for(Map<String, Object> fieldMap: tarTable.getFieldList()){
			link = new DcObjectLink();
			link.setId(IdGen.uuid());
			//数据来源-转换任务
			link.setProcessId(designId);
			link.setDataSource(DcObjectLink.DATA_SOURCE_TRANSDESIGN);
			link.setLinkType(DcObjectLink.LINK_TYPE_FIELD);
			link.setTarObjId(tarTable.getTarTableName()+"."+DcStringUtils.getObjValue(fieldMap.get("fieldName")));	//目标字段 table.field
			link.setSrcObjId(DcStringUtils.getObjValue(fieldMap.get("srcField")));		//源字段 table.field
			link.setRelationExp(DcStringUtils.getObjValue(fieldMap.get("trans_exp")));	//转换关系
			link.setTransParam(DcStringUtils.getObjValue(fieldMap.get("trans_param")));	//转换参数
			link.setLinkJson(new StringBuilder(64).append("{from:'").append(link.getSrcObjId()).append("', to:'").append(link.getTarObjId()).append("'}").toString());
			linkList.add(link);
		}**/
		
		//添加table关系列表 
		for(String tbName: tarTable.getSrcTableSet()){
			link = new DcObjectLink();
			link.setId(IdGen.uuid());
			//数据来源-转换任务
			link.setProcessId(designId);
			link.setDataSource(DcObjectLink.DATA_SOURCE_TRANSDESIGN);
			link.setLinkType(DcObjectLink.LINK_TYPE_TABLE);
			link.setSrcObjId(tbName);
			link.setTarObjId(tarTable.getTarTableName());
			link.setLinkJson(new StringBuilder(64).append("{from:'").append(tbName).append("', to:'").append(tarTable.getTarTableName()).append("'}").toString());
			
			//设置转换脚本与排序
			link.setOutputScript(tarTable.getTarTableScript());
			link.setSortNum(tarTable.getSortNum());
			linkList.add(link);
//
//			Neo4j neo4j = new Neo4j();
//			neo4j.setTableName(link.getSrcObjId());
//			neo4j.setTarName(link.getTarObjId());
//			neo4j.setProcessId(designId);
//			neo4j.setProcessName("数据转换("+link.getDataSource()+")");
//			neo4j.setProcessDes("数据转换("+link.getDataSource()+")");
//			neo4jService.queryHiveSql(neo4j);
		}
		
		if(!CollectionUtils.isEmpty(linkList)){
			if(deleteFlag){	//删除转换任务的链路关系
				dao.deleteByProcessId(designId);
			}
			dao.batchInsert(linkList);
			neo4jService.addDataList2Neo4j(linkList);
		}
	}

	/**
	 * @方法名称: deleteByProcessId 
	 * @实现功能: 根据任务Id 删除对象链路
	 * @param processId
	 * @create by peijd at 2017年3月27日 下午3:09:02
	 */
	@Transactional(readOnly = false)
	public void deleteByProcessId(String processId) {
		Assert.hasText(processId);
		dao.deleteByProcessId(processId);
	}

	@Override
	public DcObjectLink get(String id) {
		return dao.getById(id);
	}
}
