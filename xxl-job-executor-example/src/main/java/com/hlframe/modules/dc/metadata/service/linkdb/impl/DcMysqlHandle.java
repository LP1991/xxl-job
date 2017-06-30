/********************** 版权声明 *************************
 * 文件名: DcMysqlHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb.impl
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午5:29:08
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb.impl;

import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.linkdb.DcDataBaseLinkService;
import com.hlframe.modules.dc.metadata.service.linkdb.abs.DcAbsDataBaseHandle;
import com.hlframe.modules.dc.utils.DcStringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.impl.DcMysqlHandle.java 
 * @职责说明: Mysql 数据库处理类
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午5:29:08
 */
@Service
@Transactional(readOnly = true)
public class DcMysqlHandle extends DcAbsDataBaseHandle implements DcDataBaseLinkService {

	public static Map<String, String> fieldTypeMap = new HashMap<String,String>();	//字段类型映射关系
	public static Set<String> noLimitSet = new HashSet<String>();			//不限长度的字段类型
	static {
		fieldTypeMap.put("string","varchar");
		fieldTypeMap.put("int","int");
		fieldTypeMap.put("date","date");
		fieldTypeMap.put("datetime","datetime");
		fieldTypeMap.put("float","float");
		fieldTypeMap.put("double","double");
		fieldTypeMap.put("text","text");
		fieldTypeMap.put("char","varchar");
		fieldTypeMap.put("boolean","varchar");
		fieldTypeMap.put("timestamp","timestamp");

		noLimitSet.add("text");
		noLimitSet.add("date");
		noLimitSet.add("datetime");
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: 查询Mysql数据库  当前用户的数据表/注释
	 * @param dataSourceId
	 * @param schema
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception {
		return queryTableNames(dataSourceId, schema, null);
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: 查询Mysql数据库  当前用户的数据表/注释
	 * @param dataSourceId
	 * @param schema
	 * @param filter
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter)
			throws Exception {
		StringBuilder metaSql = new StringBuilder(128);
		metaSql.append("SELECT TABLE_NAME AS TABNAME, IF(TABLE_TYPE='BASE TABLE','TABLE','VIEW') AS TABTYPE, ");
		//注释为空时 转换
		metaSql.append("     CASE WHEN (ISNULL(TABLE_COMMENT) || LENGTH(trim(TABLE_COMMENT))<1) THEN TABLE_NAME ");
		metaSql.append("       ELSE CONCAT(TABLE_NAME,'(',TABLE_COMMENT,')') ");
		metaSql.append("     END AS TABCOMMENT ");
		metaSql.append("  FROM INFORMATION_SCHEMA.TABLES ");
		metaSql.append(" WHERE 1=1 ");
	//	metaSql.append(" AND TABLE_TYPE='BASE TABLE' ");	//只查询基本表
		if (StringUtils.isNotBlank(schema)) {
			metaSql.append(" AND TABLE_SCHEMA = '").append(schema).append("'");
		}else {
			//排除系统的schema
			metaSql.append(" AND TABLE_SCHEMA NOT IN ('information_schema','mysql','performance_schema')");
		}
		
		//增加过滤条件  变量替换
		if (StringUtils.isNotBlank(filter)) {
			metaSql.append(" AND ").append(filter.replaceAll("[{]TABLENAME[}]", "TABLE_NAME"));
		}
		metaSql.append(" ORDER BY TABLE_NAME");
		
		//返回查询结果
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: queryTableColumns 
	 * @实现功能: 查询Mysql数据库 数据表列名
	 * @param dataSourceId
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	public List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception {
		Assert.hasText(tableName);
		StringBuilder metaSql = new StringBuilder(64);
		metaSql.append("SELECT A.COLUMN_NAME as COLNAME,");			//列名
		metaSql.append("   case when (ISNULL(COLUMN_COMMENT) || LENGTH(trim(COLUMN_COMMENT))<1) then COLUMN_NAME else concat(COLUMN_NAME,'(',COLUMN_COMMENT,')') end as COLCOMMECT,");		//注释
		metaSql.append("   A.COLUMN_COMMENT as COLUMNCOMMENT,");		//注释
		metaSql.append("   A.CHARACTER_MAXIMUM_LENGTH as COLLENG,");//长度
		metaSql.append("   A.DATA_TYPE as COLTYPE,");				//类型
		metaSql.append("   CASE WHEN ('PRI'=A.COLUMN_KEY) THEN 'Y' ELSE 'N' END AS COLKEY,");				//是否主键
		metaSql.append("   CASE WHEN INSTR(IS_NULLABLE,'YES')>0 THEN 'Y' ELSE 'N' END AS COLISNULL");		//是否为空
		metaSql.append(" FROM INFORMATION_SCHEMA.COLUMNS A WHERE TABLE_NAME='").append(tableName).append("' and TABLE_SCHEMA='"+schema+"'");
		
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: buildTableKeySql 
	 * @实现功能: 构建数据表主键查询SQL, 拼接多个表的主键查询SQL
	 * @param tableList Map中内容为(tbName, 表名),(keyField,主键字段),(filter,过滤条件)
	 * @return 查询主键值的SQL  SELECT tbName, keyField, keyVal FROM tbName
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	public String buildTableKeySql(List<Map<String, String>> tableList) throws Exception {
		Assert.notEmpty(tableList);
		StringBuilder buildSql = new StringBuilder(256);
		String tableName = null, keyField = null, filter = null;
		Set<String> tbNameSet = new HashSet<String>();
		for(Map<String, String> tableMap: tableList){
			tableName = tableMap.get("tbName");		//表名
			keyField = tableMap.get("keyField");	//主键字段
			filter = tableMap.get("filter");		//过滤条件
			if(StringUtils.isBlank(tableName)){
				continue;
			}
			buildSql.append(buildSql.length()>0?" UNION ALL ":"");
			//每个表 只保留一个主键
			if(tbNameSet.add(tableName)){
				buildSql.append("SELECT '").append(tableName.toUpperCase()).append("' AS tbName, ");
			}
			//主键字段不为空 拼接主键 查询SQL
			if(StringUtils.isNotBlank(keyField)){
				buildSql.append("'").append(keyField).append("' AS keyField, CONCAT('\\\'',REPLACE(GROUP_CONCAT(");
				buildSql.append(keyField).append("),',', '\\\',\\\''),'\\\'') AS keyVal FROM ").append(tableName);
				
				//过滤条件
				if(StringUtils.isNotBlank(filter)){
					buildSql.append(" WHERE ").append(filter);
				}
			}else{
				buildSql.append("'' AS keyField, '' AS keyVal FROM DUAL");
			}
		}
		return buildSql.toString();
	}

	/**
	 * Override
	 * @方法名称: buildTableKeyField 
	 * @实现功能: 查询数据表的主键字段
	 * @param tbNameList  数据表列表
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	public String buildTableKeyField(List<String> tbNameList) throws Exception {
		if(CollectionUtils.isNotEmpty(tbNameList)){
			StringBuilder metaSql = new StringBuilder(256);
			metaSql.append("SELECT TABLE_NAME AS tbName,COLUMN_NAME AS keyField FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME IN (");
			for(String tbName: tbNameList){
				metaSql.append("'").append(tbName).append("',");
			}
			metaSql.append("'') AND 'PRI'=COLUMN_KEY");
			return metaSql.toString();
		}
		return null;
	}

	/**
	 * Override
	 * @方法名称: assembleQueryPageSql 
	 * @职责说明: 组建分页查询SQL  SELECT * FROM `APG_DEPT_OA` ORDER BY DEPTID LIMIT 0,5;
	 * @param page
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:29:08
	 */
	@Override
	protected String assembleQueryPageSql(DcQueryPage page) throws Exception {
		String tableName = page.getTableName();
		Assert.hasText(tableName);
		//分页查询Sql
		StringBuilder pageSql = new StringBuilder(64);
		pageSql.append("SELECT ").append(page.getFieldStr()).append(" FROM ").append(tableName).append(" ");
		//过滤条件
		if (StringUtils.isNotBlank(page.getFilter())) {
			pageSql.append(" WHERE ").append(page.getFilter());
		}
		//排序
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			pageSql.append(" ORDER BY ").append(page.getOrderBy());
		}
		//分页记录
		if (page.getStart()>0 && page.getFirstrownum()>0) {
			pageSql.append(" LIMIT ").append(page.getFirstrownum()).append(",").append(page.getLimit());
		}
		return pageSql.toString();
	}

	/**
	 * Override
	 * @方法名称: querySchemaList 
	 * @实现功能: 查询数据库schema
	 * @param dataSourceId 数据源Id
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月22日 下午2:30:46
	 */
	@Override
	public List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception {
		Assert.hasText(dataSourceId);
		StringBuilder metaSql = new StringBuilder(64).append("select SCHEMA_NAME as SCHEMANAME from INFORMATION_SCHEMA.SCHEMATA");
		return queryMetaSql(dataSourceId, metaSql.toString());
	}
	
	/**
	 * Override
	 * @方法名称: queryLimitMetaSql 
	 * @实现功能: 查询指定记录数
	 * @param dbSrcId
	 * @param sql
	 * @param dataNum
	 * @return
	 * @create by cdd at 2016年11月25日 上午11:27:32
	 */
	@Override
	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum)  throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(sql);
		Assert.isTrue(dataNum>0);
		return queryMetaSql(dbSrcId, sql+" limit 0,"+dataNum);
	}
	
	/**
	 * Override
	 * @方法名称: existTable 
	 * @实现功能: 判断数据表 是否存在, select TABLE_NAME,TABLE_TYPE,TABLE_SCHEMA from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'dc_exp_userdata' and TABLE_SCHEMA='test'
	 * @param dbSrcId
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2017年2月28日 下午2:14:17
	 */
	@Override
	public boolean existTable(String dbSrcId, String tableName) throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(tableName);
		StringBuilder checkSql = new StringBuilder(64);

		checkSql.append("SELECT TABLE_NAME,TABLE_TYPE,TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE ");
		if(tableName.indexOf(".")>0){
			
			checkSql.append(" TABLE_NAME = '").append(tableName.split("[.]")[1]);
			checkSql.append("' AND TABLE_SCHEMA='").append(tableName.split("[.]")[0]).append("'");
		}else{
			checkSql.append(" TABLE_NAME = '").append(tableName).append("'");
		}
		return CollectionUtils.isNotEmpty(queryMetaSql(dbSrcId, checkSql.toString()));
	}

	/**
	 * @方法名称: createTable
	 * @实现功能: 根据表名/字段创建 数据表
	 * @param connId        连接Id
	 * @param schemaName    schema
	 * @param tarName        数据表名
	 * @param fields        字段列表
	 * @param tableDesc
	 * @throws Exception
	 * @create by peijd at 2017/4/20 21:24
	 */
	@Override
	public void createTable(String connId, String schemaName, String tarName, List<DcObjectField> fields, String tableDesc) throws Exception {
		StringBuilder metaSql = new StringBuilder(256), keyField = new StringBuilder(64).append("PRIMARY KEY (");

		metaSql.append("CREATE TABLE ");
		//tableSpace
		if(StringUtils.isNotEmpty(schemaName)){
			metaSql.append("`").append(schemaName).append("`.");
		}
		//表名
		metaSql.append("`").append(tarName).append("` (");
		int idx = 0;
		for(DcObjectField field: fields){
			if (idx>0){
				metaSql.append(",");
			}
			//字段名
			metaSql.append("`").append(field.getFieldName()).append("` ");
			//字段类型
			metaSql.append(fieldTypeMap.get(field.getFieldType().toLowerCase()));
			//字段长度
			if (0<field.getFieldLeng() && !noLimitSet.contains(field.getFieldType().toLowerCase())) {
				metaSql.append("(").append(field.getFieldLeng()).append(") ");
			}else{
				metaSql.append(" ");
			}
			//是否为空
			if(!"1".equals(field.getIsNull())){
				metaSql.append("NOT NULL ");
			}
			//默认值
			metaSql.append("DEFAULT ").append(StringUtils.isNotBlank(field.getDefaultVal())?field.getDefaultVal():"NULL").append(" ");
			//字段描述
			if (DcStringUtils.isNotBlank(field.getFieldDesc())){
				metaSql.append("COMMENT '").append(field.getFieldDesc()).append("' ");
			}
			//是否主键
			if(1==field.getIsKey()){
				keyField.append(keyField.length()>14?"`,`":"`");
			}
			idx ++;
		}
		//存在主键
		if(keyField.length()>14){
			metaSql.append(",").append(keyField).append("`)");
		}else{
			metaSql.deleteCharAt(metaSql.length()-1);
		}
		metaSql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='").append(tableDesc).append("'");
		logger.info("create mysql Table: ", metaSql.toString());

		//执行建表脚本
		runScript(connId, metaSql.toString());
	}

	/**
	 * @方法名称: batchUpdateData
	 * @实现功能: 批量更新数据
	 * @param connId		连接Id
	 * @param schemaName	schema
	 * @param tarName		数据表名
	 * @param fieldList		字段列表
	 * @param dataList		数据列表
	 * @return
	 * @create by peijd at 2017/5/11 10:16
	 */
	@Override
	public DcDataResult batchUpdateData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList) {
		DcDataResult result = new DcDataResult();
		Assert.hasText(connId);
		Assert.hasText(tarName);

		try{
			Assert.notEmpty(fieldList, "字段列表不可为空!");
			Assert.notEmpty(dataList, "没有可更新的数据!");

			StringBuilder updateSql = new StringBuilder(512);
			updateSql.append("replace into ");
			//tableSpace
			if(StringUtils.isNotEmpty(schemaName)){
				updateSql.append("`").append(schemaName).append("`.");
			}
			//表名
			updateSql.append("`").append(tarName).append("` (");
			int idx = 0;
			//字段顺序Map  为保证updatesql字段顺序
			Map<Integer, DcObjectField> fieldOrderMap = new HashMap<Integer, DcObjectField>();
			for(DcObjectField field: fieldList){
				fieldOrderMap.put(idx, field);
				if (idx>0){
					updateSql.append(",");
				}
				//字段名
				updateSql.append(field.getFieldName());
				idx ++;
			}
			updateSql.append(") values ");
			//拼接字段值
			idx = 0;
			Object dataval = null;
			for(Map<String, Object> data: dataList){
				updateSql.append(idx>0?",(":"(");
				//按顺序更新
				for(int i=0; i<fieldOrderMap.size(); i++){
					dataval = data.get(fieldOrderMap.get(i).getRemarks());
					if(i>0){
						updateSql.append(",");
					}
					if(null==dataval){
						updateSql.append("null");
					}else{
						updateSql.append("'").append(DcStringUtils.getObjValue(dataval)).append("'");
					}
				}
				updateSql.append(")");
				idx++;
			}

			//提交批量脚本
			runScript(connId, updateSql.toString());
			result.setRst_flag(true);
			result.setRst_std_msg("执行成功, 批量更新"+dataList.size()+" 条记录!");
			result.setTotleNum(dataList.size());

		}catch(Exception e){
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
			logger.error("-->batchUpdateData: ", e);
		}

		return result;
	}
}
