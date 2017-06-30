/********************** 版权声明 *************************
 * 文件名: DcDataProcessDesignService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月21日 上午9:33:30
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.dao.DcDataProcessDesignDao;
import com.hlframe.modules.dc.dataprocess.entity.DcDataProcessDesign;
import com.hlframe.modules.dc.dataprocess.entity.DcDataProcessOutput;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.metadata.service.DcObjectLinkService;
import com.hlframe.modules.dc.metadata.service.DcObjectMainService;
import com.hlframe.modules.dc.metadata.service.DcObjectTableService;
import com.hlframe.modules.dc.metadata.service.linkdb.DcTranslateFieldService;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.DcStringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcDataProcessDesignService.java 
 * @职责说明: 数据转换过程设计 Service
 * @创建者: peijd
 * @创建时间: 2017年2月21日 上午9:33:30
 */
@Service
@Transactional(readOnly = true)
public class DcDataProcessDesignService extends CrudService<DcDataProcessDesignDao, DcDataProcessDesign> implements DcTaskService {
	
	@Autowired	//数据链路 service
	private DcObjectLinkService linkService;
	
	@Autowired	//元数据存储Service
	private DcMetadataStroeService dcMetadataStroeService;
	
	@Autowired	//元数据Service
	private DcObjectMainService objMainService;
	@Autowired	//元数据-数据表Service
	private DcObjectTableService objTableService;
	
	@Autowired	//hiveService
	private DcQueryHiveService queryHiveService;
	
	@Autowired	//字段转换Service
	private DcTranslateFieldService transFieldService;;
	
	
	/**
	 * Override
	 * @方法名称: findPage 
	 * @实现功能: 分页查询
	 * @param page
	 * @param design
	 * @return
	 * @create by peijd at 2017年2月21日 上午9:39:12
	 */
	public Page<DcDataProcessDesign> findPage(Page<DcDataProcessDesign> page, DcDataProcessDesign design) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		design.getSqlMap().put("dsf", dataScopeFilter(design.getCurrentUser(),"o","u"));
		// 设置分页参数
		design.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(design));
		return super.findPage(page, design);
	}
	
	/**
	 * Override
	 * @方法名称: save 
	 * @实现功能: 保存设计对象, 并解析得到table和field对象
	 * @param entity
	 * @create by peijd at 2017年3月6日 下午4:20:49
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(DcDataProcessDesign entity) {

		//设置记录默认状态
		if(StringUtils.isBlank(entity.getStatus()) ){
			entity.setStatus(entity.JOB_STATUS_EDIT);
		}
		super.save(entity);
		
		//解析设计对象及链路关系
		parseDesignJson(entity);
	}
	
	/**
	 * @方法名称: parseDesignJson 
	 * @实现功能: 解析设计对象, 支持解析多表到多表的链路
	 * @param entity
	 * @create by peijd at 2017年3月7日 上午9:24:49
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false)
	private void parseDesignJson(DcDataProcessDesign entity) {
		//解析设计对象
		String json = entity.getDesignJson().replaceAll("&quot;", "\"");
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(json, Map.class);
			List<Map> jsonList =  (List<Map>) map.get("nodes");
			Assert.notEmpty(jsonList, "转换对象数据结构异常!");
			
			Map<String, Object> nodeUI = null; 	//table对象
			Map<String, String> tableMap = new HashMap<String, String>();
//			Map<String, List<Map<String, Object>>> processMap = new HashMap<String, List<Map<String, Object>>>();
			String tableName = null, sortNum = null;
			DcDataProcessOutput targetTable = null;
			List<DcDataProcessOutput> outPutList = new ArrayList<DcDataProcessOutput>();
			List<Map<String, Object>> fieldMapList = null;
			//第一次全部遍历  提取表名(存储在hiveName中)
			for(Map item: jsonList){
				tableName = String.valueOf(item.get("hiveName"));
				if(tableName.indexOf(".")<0){
					tableName = "default."+tableName;
				}
				nodeUI = (Map) item.get("uiInfo");
				if("TARGET".equalsIgnoreCase(String.valueOf(nodeUI.get("stageType")))){			//输出目标对象
					tableName = DcStringUtils.getObjValue(item.get("targetName"));
					Assert.hasText(tableName, "未获取到对象名称");
					if(tableName.indexOf(".")<0){
						tableName = "default."+tableName;
					}
					sortNum = DcStringUtils.getObjValue(item.get("sortNum"));
					targetTable = new DcDataProcessOutput();
					//设置序号 需前台传入
					if(StringUtils.isNotEmpty(sortNum)){
						targetTable.setSortNum(Integer.parseInt(sortNum));
					}
					//设置表名
					targetTable.setTarTableName(tableName);
					//添加对象字段 优先获取dropData
					fieldMapList = (List<Map<String, Object>>)item.get("dropData");		//右侧转换代码
					if(CollectionUtils.isEmpty(fieldMapList)){
						fieldMapList = (List<Map<String, Object>>)item.get("one");		//左侧源表代码
						targetTable.setTarType(DcDataProcessOutput.TARTABLE_TYPE_CHECK);
						//检查是否错误输出
						List<String> inputLaneList = (List<String>) item.get("inputLanes");
						if(CollectionUtils.isNotEmpty(inputLaneList) && inputLaneList.contains("error")){
							targetTable.setCheckErrorFlag(true);
						}
						//添加过滤条件 
						targetTable.setFilterList((List<Map<String, Object>>)item.get("filter"));
					}else{
						targetTable.setTarType(DcDataProcessOutput.TARTABLE_TYPE_TRANSLATE);
						//添加源表关联关系
						targetTable.setRelateList((List<Map<String, Object>>)item.get("cognate"));
						//添加过滤条件
						targetTable.setFilterList((List<Map<String, Object>>)item.get("map_filter"));
					}
					targetTable.setFieldList(fieldMapList);
					outPutList.add(targetTable);
					
				}else if("SOURCE".equalsIgnoreCase(String.valueOf(nodeUI.get("stageType")))){	//转换源对象
					Assert.hasText(tableName, "未获取到对象名称");
					tableMap.put(String.valueOf(item.get("instanceName")), tableName);
					
//				}else if("PROCESSOR".equalsIgnoreCase(String.valueOf(nodeUI.get("stageType")))){	//处理过程
//					processMap.put(String.valueOf(item.get("instanceName")), (List<Map<String, Object>>) item.get("filter"));
				}
			}
			
			boolean firstDeleteFlag = true;
			//逐个构建输出对象  解析table/field关系
			for(DcDataProcessOutput outTable: outPutList){
				List<Map<String, Object>> fieldList = outTable.getFieldList();
				Assert.notEmpty(fieldList, "输出对象["+outTable.getTarTableName()+"]字段列表不可为空!");
				if(DcDataProcessOutput.TARTABLE_TYPE_TRANSLATE.equals(outTable.getTarType())){		//数据转换节点
					for(Map<String, Object> item: fieldList){
						//获取字段名
//						item.put("srcTable", tableMap.get(StringUtils.substringBefore(DcStringUtils.getObjValue(item.get("itemK")), ".")));
//						item.put("srcField", item.get("srcTable")+"."+StringUtils.substringAfterLast(DcStringUtils.getObjValue(item.get("itemK")), "."));
						tableName = tableMap.get(StringUtils.substringBefore(DcStringUtils.getObjValue(item.get("itemK")), "."));
						item.put("srcTable", tableName+" "+ DcStringUtils.getObjValue(item.get("instanceName")));
						item.put("srcField", DcStringUtils.getObjValue(item.get("itemK")));
						//添加源表
						outTable.addSrcTable(DcStringUtils.getObjValue(item.get("srcTable")));
					}
				}else if(DcDataProcessOutput.TARTABLE_TYPE_CHECK.equals(outTable.getTarType())){	//数据校验节点  解析时添加
//					outTable.addSrcTable();
				}
				//解析输出对象 (关联条件/输入源表及关联关系/对象序号/过滤条件), 生成转换脚本
				buildOutputScript(outTable);
				//保存元数据信息
				saveTransMetaData(entity, outTable.getTarTableName(), fieldList);
				//保存链路关系 
				linkService.configObjLinkByDesign(entity.getId(), outTable, firstDeleteFlag);
				firstDeleteFlag = false;
			}
		} catch (Exception e) {
			logger.error("--->parseDesignJson: ", e);
			throw new ServiceException(e);
		}
		
	}

	/**
	 * @方法名称: buildOutputScript 
	 * @实现功能: 构建输出脚本
	 * @param outTable
	 * @create by peijd at 2017年3月14日 下午4:35:54
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	private void buildOutputScript(DcDataProcessOutput outTable) {
		StringBuilder script  = new StringBuilder(256), fieldStr = new StringBuilder(64);
		script.append("drop table ").append(outTable.getTarTableName()).append(";");
		script.append("create table ").append(outTable.getTarTableName()).append(" (");
		
		int index = 0;
		List<Map<String, String>> checkFieldList = null;
		//构建字段列表
		for(Map<String, Object> item: outTable.getFieldList()){
			if(DcDataProcessOutput.TARTABLE_TYPE_TRANSLATE.equals(outTable.getTarType())){	//数据转换节点
				if(index>0){
					script.append(", ");
					fieldStr.append(", ");
				}
				script.append(DcStringUtils.getObjValue(item.get("fieldName"))).append(" ").append(DcStringUtils.getObjValue(item.get("fieldType")));
				//构建字段转换函数
				buildTransFieldFunc(fieldStr, item);
				
			}else if(DcDataProcessOutput.TARTABLE_TYPE_CHECK.equals(outTable.getTarType())){	//数据校验节点
				//过滤规则  通过fieldArr列表中获取
				checkFieldList = (List<Map<String, String>>) item.get("fieldArr");
				int idx = 0;
				for(Map<String, String> checkMap: checkFieldList){
					if(idx>0){
						script.append(", ");
						fieldStr.append(", ");
					}
					script.append(DcStringUtils.getObjValue(checkMap.get("fieldName"))).append(" ").append(DcStringUtils.getObjValue(checkMap.get("fieldType")));
//					fieldStr.append(DcStringUtils.getObjValue(checkMap.get("fieldName")));
					fieldStr.append(DcStringUtils.getObjValue(checkMap.get("instanceName"))+"."+ DcStringUtils.getObjValue(checkMap.get("fieldName")));
					idx++;
				}
				outTable.addSrcTable(DcStringUtils.getObjValue(item.get("hiveName")) + " "+ DcStringUtils.getObjValue(item.get("instanceName")));
			}
			index++;
		}
		script.append(");");
		//插入数据
		script.append("insert into ").append(outTable.getTarTableName()).append(" ");
		script.append("select ").append(fieldStr).append(" from ").append(outTable.getSrcTable());
		script.append(" where 1=1 ");
		//过滤条件 转换规则
		if(DcDataProcessOutput.TARTABLE_TYPE_TRANSLATE.equals(outTable.getTarType())){	//数据转换节点
			//关联关系
			if(CollectionUtils.isNotEmpty(outTable.getRelateList())){
				for(Map<String, Object> refMap: outTable.getRelateList()){
					script.append(" and ").append(refMap.get("instanceName_a")).append(".").append(refMap.get("fieldName_a")).append("=");
					script.append(refMap.get("instanceName_b")).append(".").append(refMap.get("fieldName_b"));
				}
			}
			//过滤条件
			buildFilter(script, outTable.getFilterList(), false);
			
		}else if(DcDataProcessOutput.TARTABLE_TYPE_CHECK.equals(outTable.getTarType())){	//过滤规则
			//过滤条件
			buildFilter(script, outTable.getFilterList(), outTable.getCheckErrorFlag());
		}
		//数据构建脚本
		outTable.setTarTableScript(script.toString());
		
	}

	/**
	 * @方法名称: buildTransFieldFunc 
	 * @实现功能: 构建字段转换函数
	 * @param fieldStr
	 * @param item
	 * @create by peijd at 2017年3月27日 下午5:28:51
	 */
	@SuppressWarnings("unchecked")
	private void buildTransFieldFunc(StringBuilder fieldStr, Map<String, Object> item) {
		//字段变形 
		Map<String, String> transMap = (Map<String, String>) item.get("transform_func");
		if(null!=transMap && !transMap.isEmpty() && StringUtils.isNotBlank(transMap.get("value"))){
			//构建变形函数
			DcObjectLink link = new DcObjectLink();
			link.setRelationExp(transMap.get("value"));									//函数名称
			link.setSrcObjId(DcStringUtils.getObjValue(item.get("srcField")));			//转换字段
			link.setTransParam(DcStringUtils.getObjValue(item.get("transform_value")));	//转换参数
			link.setRemarks(DcStringUtils.getObjValue(item.get("edit_input")));			//输出值
			//设置转换函数  该版本指定hive作为执行引擎
			fieldStr.append(transFieldService.translateField(DcTranslateFieldService.TRANSLATE_ENGINE_HIVE, link));
			
			//保存字段转换关系
			item.put("trans_exp", transMap.get("value"));
			item.put("trans_param", link.getTransParam());
			
		}else{
			fieldStr.append(DcStringUtils.getObjValue(item.get("srcField")));
		}
	}

	/**
	 * @方法名称: buildFilter 
	 * @实现功能: buildFilter
	 * @param script		脚本
	 * @param filterList	过滤条件列表
	 * @param errorFlag		错误日志Flag
	 * @return
	 * @create by peijd at 2017年3月27日 下午1:55:46
	 */
	@SuppressWarnings("unchecked")
	private void buildFilter(StringBuilder script, List<Map<String, Object>> filterList, Boolean errorFlag) {
		if(CollectionUtils.isEmpty(filterList)){
			return;
		}
		String fieldVal = null, operateVal = null;
		Map<String, String> fieldMap = null;
		for(Map<String, Object> item: filterList){
			fieldMap = (Map<String, String>) item.get("field");
			//字段不可为空
			if(null==fieldMap || StringUtils.isBlank(fieldMap.get("fieldName"))){
				continue;
			}
			fieldVal = String.valueOf(item.get("value"));
			operateVal = ((Map<String, String>) item.get("operate")).get("value");
			//isNull he notNull时, 参数值可以为空
			if(!"isNull".equalsIgnoreCase(operateVal) && !"notNull".equalsIgnoreCase(operateVal) && StringUtils.isBlank(fieldVal)){
				continue;
			}
			//去除字符串中的单引号
			fieldVal = fieldVal.replaceAll("'", "");	
			//解析字段
			script.append(" and ").append(errorFlag?"!":"").append(fieldMap.get("instanceName")).append(".").append(fieldMap.get("fieldName")).append(" ");
			//解析条件
			if("between".equalsIgnoreCase(operateVal) || "notBetween".equalsIgnoreCase(operateVal)){	//解析between 和 not between条件
				Assert.isTrue(fieldVal.indexOf(",")>0, "between过滤条件需设置两个参数(e.g. 1,100)");
				script.append(DcDataProcessOutput.processExpMap.get(operateVal).replaceFirst("XX", fieldVal.split(",")[0]).replaceFirst("YY", fieldVal.split(",")[1]));
				
			}else if("in".equalsIgnoreCase(operateVal) || "notIn".equalsIgnoreCase(operateVal)){	//解析in 和 notIn 条件
				if(fieldVal.indexOf(",")>0){	//如果有多个字符串, 根据,分隔解析
					fieldVal = fieldVal.replaceAll(",", "','");
				}
				script.append(DcDataProcessOutput.processExpMap.get(operateVal).replaceFirst("XX", fieldVal));
				
			}else{	//其他过滤条件
				script.append(DcDataProcessOutput.processExpMap.get(operateVal).replaceFirst("XX", fieldVal));
			}
		}
	}

	/**
	 * @方法名称: saveTransMetaData 
	 * @实现功能: 保存数据转换设计 元数据
	 * @param job		任务
	 * @param table	目标表
	 * @param fieldList	目标字段列表
	 * @create by peijd at 2017年3月7日 下午2:08:53
	 */
	private void saveTransMetaData(DcDataProcessDesign job, String table, List<Map<String, Object>> fieldList) {
		Assert.notNull(job);
		Assert.hasText(table);
		Assert.notEmpty(fieldList);
		
		//元数据-主体对象
		DcObjectMain tarMain = new DcObjectMain();
		tarMain.setId(job.getId());			
		tarMain.setJobId(job.getId());							//任务Id
		tarMain.setJobType(DcObjectMain.JOB_TYPE_PROCESS);		//处理
		tarMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);	//采集目标
		tarMain.setObjName(job.getDesignName());				//任务名称
		tarMain.setObjCode(job.getId());						//任务编码
		tarMain.setObjType(tarMain.OBJ_TYPE_TABLE);				//对象类型: 数据表 
		tarMain.setObjDesc(job.getDesignDesc());				//备注信息
		
		//数据表对象-补充信息
		DcObjectTable dcObjectTable = new DcObjectTable();
		dcObjectTable.setId(tarMain.getId());
		String tarTableName = table, schemaName = "default";
		//表名称为 schemaname.tableName
		if(table.indexOf(".")>0){
			schemaName = table.split("[.]")[0];
			tarTableName = table.split("[.]")[1];
		}
		dcObjectTable.setDbDataBase(schemaName);	//表空间
		dcObjectTable.setTableName(tarTableName);	//表名
		dcObjectTable.setTableLink("hive");			//数据库类别
		dcObjectTable.setDbType("HIVE");			//目前使用hive转换引擎
		
		List<DcObjectField> fields = new ArrayList<DcObjectField>();
		DcObjectField dcObjectField = null;
		for(Map<String, Object> field: fieldList){
			dcObjectField = new DcObjectField();
			dcObjectField.setFieldName(DcStringUtils.getObjValue(field.get("fieldName")));	//字段名
			dcObjectField.setFieldType(DcStringUtils.getObjValue(field.get("fieldType")));	//字段类型
			dcObjectField.setFieldDesc(DcStringUtils.getObjValue(field.get("fielDesc")));	//字段描述
			dcObjectField.setBelong2Id(tarMain.getId());
			fields.add(dcObjectField);
		}
		//保存目标数据对象
		dcMetadataStroeService.obj2MySQL(tarMain, dcObjectTable, fields);
		
		//同步更新至dc_hive_table/dc_hive_field TODO:
		
	}

	/**
	 * @方法名称: getDesignByName 
	 * @实现功能: 根据名称验证是否存在重复对象
	 * @param designName
	 * @return
	 * @create by peijd at 2017年2月21日 上午9:42:25
	 */
	public DcDataProcessDesign getDesignByName(String designName, String id) {
		Assert.hasText(designName);
		return dao.getDesignByName(designName, id);
	}

	/**
	 * @方法名称: runTask 
	 * @实现功能: 运行任务
	 * @param jobId
	 * @return
	 * @create by peijd at 2017年3月7日 下午7:56:01
	 */
	@Transactional(readOnly = false)
	public DcDataResult runTask(String jobId) {
		Assert.hasText(jobId);
		DcDataResult result = new DcDataResult();
		DcDataProcessDesign job = dao.get(jobId);
		Assert.notNull(job);
		try {
			
			//判断记录状态 如果是编辑状态, 则更新hive脚本
			if(DcDataProcessDesign.JOB_STATUS_EDIT.equals(job.getStatus()) || StringUtils.isBlank(job.getRemarks())){
				result.setRst_std_msg(updateDesignScript(job));
				
			}else{	//直接执行脚本
				if(StringUtils.isNotBlank(job.getRemarks())){
					//执行hive
					result.setRst_std_msg(queryHiveService.executeSql(job.getRemarks()));
				}
			}
			result.setRst_flag(true);
		} catch (Exception e) {
			logger.error("-->runTask:", e);
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
		}
		
		return result;
	}

	/**
	 * @方法名称: updateDesignScript 
	 * @实现功能: 更新生成脚本
	 * @param job
	 * @create by peijd at 2017年3月7日 下午8:50:29
	 */
	@Transactional(readOnly = false)
	private String updateDesignScript(DcDataProcessDesign job) {
		//获取任务元数据
		DcObjectTableInfo table = objTableService.buildTableInfo(job.getId());
		List<Map<String, Object>> fieldList = objTableService.getFieldsListByBelong2Id(job.getId());
		Assert.notNull(table, "数据表不存在!");
		Assert.notEmpty(fieldList, "数据表字段异常!");
		
		//字段链路列表
		DcObjectLink param = new DcObjectLink();
		param.setProcessId(job.getId());
//		param.setLinkType(DcObjectLink.LINK_TYPE_FIELD);	//字段
		List<DcObjectLink> linkList  = linkService.findList(param);
		Assert.notEmpty(linkList, "没有字段链路信息, 请检查!");
		/**解析生成hive建表脚本 数据表重复在前端判断 **/
		StringBuilder createScript = new StringBuilder(256);
		for(DcObjectLink link: linkList){
			createScript.append(link.getOutputScript());
		}
		
		//更新到数据库中
		DcDataProcessDesign updateObj = new DcDataProcessDesign();
		updateObj.setId(job.getId());
		updateObj.setRemarks(createScript.toString());
		//更新为测试状态
		if(DcDataProcessDesign.JOB_STATUS_EDIT.equals(job.getStatus())){
			updateObj.setStatus(DcDataProcessDesign.JOB_STATUS_TEST);
		}
		updateObj.preUpdate();
		dao.update(updateObj);
		
		//执行hive
		return queryHiveService.executeSql(createScript.toString());
		
	}
	
	/**
	 * Override
	 * @方法名称: delete 
	 * @实现功能: 删除转换设计
	 * @param design
	 * @create by peijd at 2017年3月9日 下午7:53:47
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(DcDataProcessDesign design) {
		Assert.hasText(design.getId());
		design = dao.get(design.getId());
		
		//删除调度任务
		if(DcDataProcessDesign.JOB_STATUS_JOB.equals(design.getStatus())){
			throw new ServiceException("该转换设计已添加调度任务, 不可删除!");
		}
		//删除对象link关系
		linkService.deleteByProcessId(design.getId());
		
		super.delete(design);
	}

	/**
	 * @方法名称: updateStatus 
	 * @实现功能: 更新业务状态
	 * @param objId
	 * @param status
	 * @create by peijd at 2017年3月10日 下午2:50:09
	 */
	@Transactional(readOnly = false)		
	public void updateStatus(String objId, String status){
		Assert.hasText(objId);
		status = StringUtils.isEmpty(status)? DcDataProcessDesign.JOB_STATUS_EDIT:status;
		DcDataProcessDesign obj = new DcDataProcessDesign();
		obj.setId(objId);
		obj.setStatus(status);
		obj.preUpdate();
		dao.update(obj);
	}
	
}
