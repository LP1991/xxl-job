/********************** 版权声明 *************************
 * 文件名: DbHandleService.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午5:04:43
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.DcDataSourceService;
import com.hlframe.modules.dc.utils.DcStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.DbHandleService.java 
 * @职责说明: 数据库连接Service  单独在配置文件(spring-context-dc.xml)中配置Bean
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午5:04:43
 */
@Service
@Transactional(readOnly = true)
public class DbHandleService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	//数据库操作类
	private Map<String, DcDataBaseLinkService>  handleServiceMap;
	
	//数据库类型
	private DcDataSourceService dcDataSourceService;



	/**
	 * @方法名称: queryschemaNameTree 
	 * @实现功能: 根据数据源连接Id 获取数据库schema列表树
	 * @param fromLinkId
	 * @return
	 * @create by peijd at 2016年11月22日 下午2:51:19
	 */
	public List<Map<String, Object>> queryschemaNameTree(String dbSrcId) {

		List<Map<String, Object>> mapList = querySchemaNameList(dbSrcId);
		//添加根节点
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", "schema");
		map.put("pId", "0");
		map.put("name", "schema");
		map.put("isParent", true);
		mapList.add(map);
		
		return mapList;
	}
	
	/**
	 * @方法名称: querySchemaNameList 
	 * @实现功能: 根据数据源连接Id 获取数据库schema列表
	 * @param dbSrcId
	 * @return
	 * @create by peijd at 2016年11月22日 下午2:52:25
	 */
	private List<Map<String, Object>> querySchemaNameList(String dbSrcId) {
		Assert.hasLength(dbSrcId);
		
		List<Map<String, Object>> mapList = Lists.newArrayList();
		try {
			//获取数据源连接
			DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
			//数据库类型
			DcDataBaseLinkService dcDataBaseLinkService = handleServiceMap.get(dataSource.getServerType().toLowerCase());
			Assert.notNull(dcDataBaseLinkService);
			//获取数据库中table列表		
			List<Map<String, Object>> list = dcDataBaseLinkService.querySchemaList(dbSrcId);
			
			Map<String, Object> map = null;
			for(Map<String, Object> item: list){
				map = Maps.newHashMap();
				map.put("id", item.get("SCHEMANAME"));
				map.put("pId", "schema");
//				map.put("pIds", item.get(""));
				map.put("name", item.get("SCHEMANAME"));
				map.put("isParent", false);
				mapList.add(map);
			}
		} catch (Exception e) {
			logger.error("--->treeData", e);
		}
		return mapList;
	}

	/**
	 * @方法名称: queryTableNameTree 
	 * @实现功能: 根据数据源连接Id 获取数据表信息
	 * @param dbSrcId	数据源连接Id
	 * @param schema	数据库schema
	 * @return
	 * @create by peijd at 2016年11月21日 下午5:07:42
	 */
	public List<Map<String, Object>> queryTableNameTree(String dbSrcId, String schema) {
		
		List<Map<String, Object>> mapList = queryTableNameList(dbSrcId, schema);
		//添加根节点
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", "table");
		map.put("pId", "0");
		map.put("name", "数据表");
		map.put("isParent", true);
		mapList.add(map);
		
		return mapList;
	}
	
	/**
	 * @方法名称: queryTableNameList 
	 * @实现功能: 获取数据库 table列表
	 * @param dbSrcId
	 * @param schema	连接schema
	 * @return
	 * @create by peijd at 2016年11月21日 下午7:36:26
	 */
	public List<Map<String, Object>> queryTableNameList(String dbSrcId, String schema) {
		Assert.hasLength(dbSrcId);
		Assert.hasLength(schema);
		
		List<Map<String, Object>> mapList = Lists.newArrayList();
		try {
			//获取数据源连接
			DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
			//数据库类型
			DcDataBaseLinkService dcDataBaseLinkService = handleServiceMap.get(dataSource.getServerType().toLowerCase());
			Assert.notNull(dcDataBaseLinkService);
			//获取数据库中table列表		
			List<Map<String, Object>> list = dcDataBaseLinkService.queryTableNames(dbSrcId, schema);
			
			Map<String, Object> map = null;
			for(Map<String, Object> item: list){
				map = Maps.newHashMap();
				map.put("id", item.get("TABNAME"));
				map.put("pId", "table");
//				map.put("pIds", item.get(""));
				map.put("name", item.get("TABCOMMENT"));
				map.put("isParent", false);
				mapList.add(map);
			}
		} catch (Exception e) {
			logger.error("--->treeData", e);
		}
		return mapList;
	}


	/**
	 * @方法名称: queryTableFieldTree 
	 * @实现功能: 查询数据表 字段Tree
	 * @param dbSrcId	数据源连接Id
	 * @param schema	连接schema
	 * @param tableName	数据表名 TODO: 去除括号
	 * @return
	 * @create by peijd at 2016年11月22日 上午11:11:15
	 */
	public List<Map<String, Object>> queryTableFieldTree(String dbSrcId, String schema, String tableName) {
		List<Map<String, Object>> mapList = queryTableFieldList(dbSrcId, schema, tableName);
		//添加根节点
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", "table");
		map.put("pId", "0");
		map.put("name", tableName);
		map.put("isParent", true);
		mapList.add(map);
		
		return mapList;
		
	}

	/**
	 * @方法名称: queryTableFieldList 
	 * @实现功能: 查询数据表 字段list
	 * @param dbSrcId	数据源连接Id
	 * @param schema	连接schema
	 * @param tableName	数据表名
	 * @return
	 * @create by peijd at 2016年11月22日 上午11:21:32
	 */
	public List<Map<String, Object>> queryTableFieldList(String dbSrcId, String schema, String tableName) {
		Assert.hasLength(dbSrcId);
		Assert.hasLength(schema);
		Assert.hasLength(tableName);
		
		//只取括号前的名称
		if(tableName.indexOf("(")>0){
			tableName = tableName.substring(0,tableName.indexOf("("));
		}
		
		List<Map<String, Object>> mapList = Lists.newArrayList();
		try {
			//获取数据源连接
			DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
			//数据库类型
			DcDataBaseLinkService dcDataBaseLinkService = handleServiceMap.get(dataSource.getServerType().toLowerCase());
			Assert.notNull(dcDataBaseLinkService);
			//获取数据库中table列表		
			List<Map<String, Object>> list = dcDataBaseLinkService.queryTableColumns(dbSrcId, schema, tableName);
			
			Map<String, Object> map = null;
			for(Map<String, Object> item: list){
				map = Maps.newHashMap();
				map.put("id", item.get("COLNAME"));
				map.put("pId", "table");
//				map.put("pIds", item.get(""));
				map.put("type", item.get("COLTYPE"));	//字段类型	
				map.put("name", item.get("COLCOMMECT"));
				map.put("isParent", false);
				mapList.add(map);
			}
		} catch (Exception e) {
			logger.error("--->treeData", e);
		}
		return mapList;
	}

	/**
	 * 
	 * @方法名称: queryMetaSql 
	 * @实现功能: 动态执行Sql
	 * @param dbSrcId
	 * @param sql 
	 * @return
	 * @create by cdd at 2016年11月24日 下午5:06:59
	 */
	public List<Map<String, Object>> queryMetaSql(String dbSrcId, String sql) {
		Assert.hasLength(dbSrcId);
		Assert.hasLength(sql);
		//获取数据源连接
		DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
		//数据库类型
		DcDataBaseLinkService dcDataBaseLinkService = handleServiceMap.get(dataSource.getServerType().toLowerCase());
		Assert.notNull(dcDataBaseLinkService);
		try {
			logger.debug("--->"+sql);
			return dcDataBaseLinkService.queryMetaSql(dbSrcId, sql);
		} catch (Exception e) {
			logger.error("--->queryMetaSql", e);
		}
		return null;
	}
	/**
	 * 
	 * @方法名称: queryLimitMetaSql 
	 * @实现功能: TODO
	 * @param dbSrcId
	 * @param sql 
	 * @return
	 * @create by cdd at 2016年11月24日 下午5:06:59
	 */
	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql,int dataNum) {
		Assert.hasLength(dbSrcId);
		Assert.hasLength(sql);
		Assert.isTrue(dataNum>0);
		//获取数据源连接
		DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
		Assert.notNull(dataSource, "数据源连接不存在或已删除!");
		//数据库类型
		DcDataBaseLinkService dcDataBaseLinkService = handleServiceMap.get(dataSource.getServerType().toLowerCase());
		Assert.notNull(dcDataBaseLinkService);

		try {
			logger.debug("--->"+sql);
			return dcDataBaseLinkService.queryLimitMetaSql(dbSrcId, sql ,dataNum);
		} catch (Exception e) {
			logger.error("--->queryMetaSql", e);
		}
		return null;
	}

	/**
	 * @方法名称: queryPageData
	 * @实现功能: 查询分页数据
	 * @param dbSrcId	数据源连接
	 * @param page	分页参数
	 * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @create by peijd at 2017/5/9 8:53
	 */
	public List<Map<String, Object>> queryPageData(String dbSrcId, DcQueryPage page) throws Exception {
		Assert.hasLength(dbSrcId);
		Assert.notNull(page);
		//获取数据源连接
		DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
		return handleServiceMap.get(dataSource.getServerType().toLowerCase()).queryTablePageData(dbSrcId, page);
	}

	/**
	 * @方法名称: queryTableDataNum
	 * @实现功能: 查询数据表数据总数
	 * @param dbSrcId	数据源连接
	 * @param page	分页参数
	 * @return  java.lang.Integer
	 * @create by peijd at 2017/5/9 9:48
	 */
	public Integer queryTableDataNum(String dbSrcId, DcQueryPage page) throws Exception {
		Assert.hasLength(dbSrcId);
		Assert.notNull(page);
		//获取数据源连接
		DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
		return handleServiceMap.get(dataSource.getServerType().toLowerCase()).queryTableDataNum(dbSrcId, page);
	}
	
	// @return the handleServiceMap
	public Map<String, DcDataBaseLinkService> getHandleServiceMap() {
		return handleServiceMap;
	}

	// @param handleServiceMap the handleServiceMap to set
	public void setHandleServiceMap(Map<String, DcDataBaseLinkService> handleServiceMap) {
		this.handleServiceMap = handleServiceMap;
	}

	// @return the dcDataSourceService
	public DcDataSourceService getDcDataSourceService() {
		return dcDataSourceService;
	}

	// @param dcDataSourceService the dcDataSourceService to set
	public void setDcDataSourceService(DcDataSourceService dcDataSourceService) {
		this.dcDataSourceService = dcDataSourceService;
	}

	/**
	 * @方法名称: buildTranslateSql 
	 * @实现功能: 构建数据转换脚本
	 * @param engineType	引擎类别
	 * @param linkList		数据对象链路关系
	 * @param transProcess	转换任务过程
	 * @return
	 * @create by peijd at 2016年12月21日 下午5:48:36
	 */
	public String buildTranslateSql(String engineType, List<DcObjectLink> linkList, DcTransDataSub transProcess) {
		Assert.hasText(engineType);
		return handleServiceMap.get(engineType.toLowerCase()).buildTranslateSql(linkList, transProcess);
	}

	/**
	 * @方法名称: buildTableName 
	 * @实现功能: 构建数据表名称  
	 * @param dbSrcId		数据源连接
	 * @param schemaName	schema
	 * @param tableName		数据表名
	 * @return
	 * @create by peijd at 2016年12月23日 下午2:02:15
	 */
	public String buildTableName(String dbSrcId, String schemaName, String tableName) {
		DcDataSource dataSource = dcDataSourceService.get(dbSrcId);
		//数据库类型
		try {
			return handleServiceMap.get(dataSource.getServerType().toLowerCase()).buildTableName(schemaName, tableName);
		} catch (Exception e) {
			logger.error("-->buildTableName ", e);
		}
		return null;
	}

	/**
	 * @方法名称: existTable
	 * @实现功能: 检查数据表是否存在
	 * @param connId			数据源连接Id
	 * @param schemaName		schema
	 * @param tableName		数据表名
	 * @return
	 * @create by peijd at 2017年4月20日 下午1:27:53
	 */
	public boolean existTable(String connId, String schemaName, String tableName) {
		Assert.hasText(connId);
		Assert.hasText(tableName);
		DcDataSource dataSrc = dcDataSourceService.get(connId);
		return existTable(dataSrc, DcStringUtils.isNotEmpty(schemaName)?schemaName+"."+tableName: tableName);
	}

	/**
	 * @方法名称: existTable 
	 * @实现功能: 检查数据表是否存在
	 * @param dataSource	数据源连接
	 * @param tableName		表名
	 * @return
	 * @create by peijd at 2017年2月27日 下午7:32:53
	 */
	public boolean existTable(DcDataSource dataSource, String tableName) {
		Assert.hasText(tableName);
		try {
			return  handleServiceMap.get(dataSource.getServerType().toLowerCase()).existTable(dataSource.getId(), tableName);
		} catch (Exception e) {
			logger.error("-->existTable ", e);
		}
		return false;
	}

	/**
	 * @方法名称: clearTableDate 
	 * @实现功能: 清空表数据
	 * @param dataSource	数据源连接
	 * @param tableName		表名
	 * @create by peijd at 2017年2月27日 下午7:34:24
	 */
	public void clearTableData(DcDataSource dataSource, String tableName) {
		Assert.hasText(tableName);
		try {
			handleServiceMap.get(dataSource.getServerType().toLowerCase()).clearTableData(dataSource.getId(), tableName);
		} catch (Exception e) {
			logger.error("-->clearTableData ", e);
		}
	}

	/**
	 * @方法名称: copyData2TarTable 
	 * @实现功能: 将源表数据复制到目标表中
	 * @param dataSource	数据源连接
	 * @param srcTable		源表
	 * @param tarTable		目标表
	 * @create by peijd at 2017年2月27日 下午7:35:19
	 */
	public void copyData2TarTable(DcDataSource dataSource, String srcTable, String tarTable) {
		Assert.hasText(srcTable);
		Assert.hasText(tarTable);
		try {
			handleServiceMap.get(dataSource.getServerType().toLowerCase()).copyData2TarTable(dataSource.getId(), srcTable, tarTable);
		} catch (Exception e) {
			logger.error("-->copyData2TarTable ", e);
		}
	}

	/**
	 * @方法名称: createTable 
	 * @实现功能: 根据源表创建目标表
	 * @param dataSource	数据源连接
	 * @param tarTable		目标表
	 * @param srcTable		源表
	 * @param copyDataFlag	是否复制数据
	 * @create by peijd at 2017年2月27日 下午7:37:50
	 */
	public void createTable(DcDataSource dataSource, String tarTable, String srcTable, boolean copyDataFlag) {
		Assert.hasText(srcTable);
		Assert.hasText(tarTable);
		try {
			handleServiceMap.get(dataSource.getServerType().toLowerCase()).createTable(dataSource.getId(), srcTable, tarTable, copyDataFlag);
		} catch (Exception e) {
			logger.error("-->createTable ", e);
		}
	}

	/**
	 * @方法名称: createTable
	 * @实现功能: 创建数据表
	 * @create by peijd at 2017/4/20 20:20
	 * @param connId        连接Id
	 * @param schemaName    schema
	 * @param tarName        数据表名
	 * @param fields        字段列表
	 * @param tableDesc		数据表描述
	 */
	public void createTable(String connId, String schemaName, String tarName, List<DcObjectField> fields, String tableDesc) throws  Exception {
		Assert.hasText(connId);
		Assert.hasText(tarName);
		Assert.hasText(schemaName);
		Assert.notEmpty(fields);

		DcDataSource dataSource = dcDataSourceService.get(connId);
		handleServiceMap.get(dataSource.getServerType().toLowerCase()).createTable(connId, schemaName, tarName, fields, tableDesc);
	}

	/**
	 * @方法名称: batchInsertData
	 * @实现功能: 批量插入数据对象
	 * @create by peijd at 2017/4/24 17:48
	 * @param connId        数据源连接
	 * @param schemaName    数据表空间
	 * @param tarName        数据表
	 * @param fieldList    字段列表
	 * @param dataList    值列表
	 */
	public DcDataResult batchInsertData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList) {
		Assert.hasText(connId);
		Assert.hasText(tarName);
//		Assert.hasText(schemaName);
		Assert.notEmpty(fieldList);
		if(!CollectionUtils.isEmpty(dataList)){
			DcDataSource dataSource = dcDataSourceService.get(connId);
			return handleServiceMap.get(dataSource.getServerType().toLowerCase()).batchInsertData(connId, schemaName, tarName, fieldList, dataList);
		}
		return null;
	}

	/**
	 * @方法名称: batchUpdateData
	 * @实现功能: 批量更新数据对象
	 * @param connId
	 * @param schemaName
	 * @param tarName
	 * @param fieldList
	 * @param dataList
	 * @return  com.hlframe.modules.dc.common.dao.DcDataResult
	 * @create by peijd at 2017/5/11 11:30
	 */
	public DcDataResult batchUpdateData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList) {
		Assert.hasText(connId);
		Assert.hasText(tarName);
		if(!CollectionUtils.isEmpty(dataList)){
			DcDataSource dataSource = dcDataSourceService.get(connId);
			return handleServiceMap.get(dataSource.getServerType().toLowerCase()).batchUpdateData(connId, schemaName, tarName, fieldList, dataList);
		}
		return null;
	}
}
