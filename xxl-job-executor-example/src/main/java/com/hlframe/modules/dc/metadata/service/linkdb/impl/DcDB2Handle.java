/********************** 版权声明 *************************
 * 文件名: DcSqliteHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb.impl
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午5:39:17
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb.impl;

import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.linkdb.DcDataBaseLinkService;
import com.hlframe.modules.dc.metadata.service.linkdb.abs.DcAbsDataBaseHandle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.impl.DcDB2Handle.java 
 * @职责说明: IBM db2 数据库 数据处理类
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午5:39:17
 */
@Service
@Transactional(readOnly = true)
public class DcDB2Handle extends DcAbsDataBaseHandle implements DcDataBaseLinkService {

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: 查询DB2数据库  当前用户的数据表/注释
	 * @param dataSourceId
	 * @param schema
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception {
		return queryTableNames(dataSourceId, schema, null);
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: 查询DB2数据库  当前用户的数据表/注释
	 * @param dataSourceId
	 * @param schema
	 * @param filter
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter)
			throws Exception {
		StringBuilder metaSql = new StringBuilder(128);
		metaSql.append("SELECT tabname as TABNAME, ");
		metaSql.append("  CASE TYPE WHEN 'T' THEN 'TABLE' ELSE 'VIEW' END AS TABTYPE, ");
		metaSql.append("  CASE COALESCE(remarks,'') WHEN '' THEN tabname ELSE tabname||'('||remarks||')' END AS TABCOMMENT ");
		metaSql.append("  FROM syscat.tables WHERE tabschema = '").append(schema).append("'");	
		
		//增加过滤条件  变量替换
		if (StringUtils.isNotBlank(filter)) {
			metaSql.append(" AND ").append(filter);
		}
		metaSql.append(" ORDER BY tabname");
		
		//返回查询结果
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: queryTableColumns 
	 * @实现功能: 查询DB2数据库 数据表列名
	 * @param dataSourceId
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	public List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception {
		Assert.hasText(tableName);
		StringBuilder metaSql = new StringBuilder(64);
		metaSql.append("SELECT A.COLNAME as COLNAME, ");//列名
		metaSql.append("   CASE COALESCE(remarks,'') WHEN '' THEN COLNAME ELSE COLNAME||'('||remarks||')' END AS COLCOMMECT, ");//注释
		metaSql.append("   A.LENGTH as COLLENG, ");		//长度
		metaSql.append("   A.TYPENAME as COLTYPE, ");	//类型
		metaSql.append("   'N' AS COLKEY,");			//是否主键
		metaSql.append("   NULLS AS COLISNULL ");		//是否为空
		metaSql.append(" FROM syscat.COLUMNS A where tabname='").append(tableName).append("' and tabschema = '").append(schema).append("'");
		
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: buildTableKeySql 
	 * @实现功能: TODO
	 * @param tableList
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	public String buildTableKeySql(List<Map<String, String>> tableList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: buildTableKeyField 
	 * @实现功能: TODO
	 * @param tbNameList
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	public String buildTableKeyField(List<String> tbNameList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: assembleQueryPageSql 
	 * @实现功能: 组建分页查询SQL  
		select * from (select ROW_NUMBER() OVER() AS _ROWNUM, t.* FROM ADMINISTRATOR.DC_COFFEE t) where _ROWNUM > 10 and _ROWNUM <=20
	 * @param page 
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:43:43
	 */
	@Override
	protected String assembleQueryPageSql(DcQueryPage page) throws Exception {
		String tableName = page.getTableName();
		Assert.hasText(tableName);
		if(StringUtils.isNotEmpty(page.getSchema())){
			tableName = page.getSchema()+"."+tableName;
		}
		//分页查询Sql
		StringBuilder pageSql = new StringBuilder(64);
		pageSql.append("select * from ");
		
		pageSql.append("( select ROW_NUMBER() OVER() AS V_ROWNUM, ").append(page.getFieldStr()).append(" FROM ").append(tableName).append(") ");
		
		//过滤条件
		if (StringUtils.isNotBlank(page.getFilter())) {
			pageSql.append(" WHERE ").append(page.getFilter());
		}
		//排序
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			pageSql.append(" ORDER BY ").append(page.getOrderBy());
		}
		//分页记录
		if (page.getStart()>0) {
			pageSql.append(" V_ROWNUM > ").append(page.getFirstrownum()).append(" and V_ROWNUM <= ").append(page.getLimit()+page.getFirstrownum());
		}
		return pageSql.toString();
	}

	/**
	 * Override
	 * @方法名称: querySchemaList 
	 * @实现功能: 查询schema列表
	 * @param dataSourceId
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月22日 下午2:45:43
	 */
	@Override
	public List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception {
		Assert.hasText(dataSourceId);
		StringBuilder metaSql = new StringBuilder(64).append("select NAME as SCHEMANAME  from sysibm.sysschemata ");
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
	 * @throws Exception
	 * @create by peijd at 2017年2月8日 下午12:00:54
	 */
	@Override
	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum)  throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(sql);
		Assert.isTrue(dataNum>0);
		StringBuilder limitSql = new StringBuilder(256);
		limitSql.append("SELECT * FROM (select ROW_NUMBER() OVER() AS V_ROWNUM, ").append(sql.substring(6)).append(" ) WHERE V_ROWNUM <= ").append(dataNum);
		return queryMetaSql(dbSrcId, limitSql.toString());
	}
}
