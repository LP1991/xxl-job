/********************** 版权声明 *************************
 * 文件名: DcMsSqlServerHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb.impl
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午5:35:26
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
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.impl.DcMsSqlServerHandle.java 
 * @职责说明: sqlserver 数据库处理类
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午5:35:26
 */
@Service
@Transactional(readOnly = true)
public class DcMsSqlServerHandle extends DcAbsDataBaseHandle implements DcDataBaseLinkService {

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @参数:@param dataSourceId	连接源ID
	 * @参数:@param schema			连接用户
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:35:26
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception {
		return queryTableNames(dataSourceId, schema, null);
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @职责说明: 查询sqlserver数据库  当前用户的数据表  TODO:中文描述, 待处理
	 * @参数:@param dataSourceId	连接源ID
	 * @参数:@param schema			连接用户
	 * @param filter
	 * @return  convert(varchar(100), SERVERPROPERTY('a'))
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:35:26
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter)
			throws Exception {
		StringBuilder metaSql = new StringBuilder();
		//获取数据表及注释 
		metaSql.append("SELECT A.name AS TABNAME, 'TABLE' AS TABTYPE, ");
		metaSql.append("  CASE WHEN (C.value is null) THEN A.name ELSE CAST(A.name AS varchar) +'('+CAST(C.value AS varchar)+')' END AS TABCOMMENT ");
		metaSql.append("  FROM sys.tables A left JOIN sys.extended_properties C ON C.major_id = A.object_id  and minor_id=0 WHERE 1=1 ");
		/*if (StringUtils.isNotBlank(schema)) {
			metaSql.append(" AND TABLE_CATALOG = '").append(schema).append("'");
		}*/

		//增加过滤条件  变量替换
		if (StringUtils.isNotBlank(filter)) {
			metaSql.append(" AND ").append(filter.replaceAll("[{]TABLENAME[}]", "A.name"));
		}
		metaSql.append(" ORDER BY A.name");
		//返回查询结果
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: queryTableColumns 
	 * @职责说明: 查询sqlserver数据库 数据表列对象
	 * @param dataSourceId
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:35:26
	 */
	@Override
	public List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception {
		Assert.hasText(tableName);
		StringBuilder metaSql = new StringBuilder(512);
		metaSql.append("SELECT col.name AS COLNAME , ");					//列名
//		metaSql.append("  ISNULL(ep.[value], '') AS COLCOMMECT , ");		//注释
		metaSql.append("  CASE ep.[value] WHEN null THEN col.name when '' then col.name else col.name+'('+CAST (ep.[value] AS VARCHAR(500))+')' END AS COLCOMMECT, ");	//字段(注释)
		metaSql.append("  CAST (ep.[value] AS VARCHAR(500)) AS COLUMNCOMMENT, ");	//注释
		metaSql.append("  t.name AS COLTYPE , ");							//类型
		metaSql.append("  col.length AS COLLENG , ");						//长度
		metaSql.append("  CASE WHEN EXISTS ( SELECT 1 FROM dbo.sysindexes si ");
		metaSql.append("    INNER JOIN dbo.sysindexkeys sik ON si.id = sik.id AND si.indid = sik.indid ");
		metaSql.append("    INNER JOIN dbo.syscolumns sc ON sc.id = sik.id AND sc.colid = sik.colid ");
		metaSql.append("    INNER JOIN dbo.sysobjects so ON so.name = si.name AND so.xtype = 'PK' ");
		metaSql.append("    WHERE sc.id = col.id AND sc.colid = col.colid ) ");
		metaSql.append("  THEN 'Y' ELSE 'N' END AS COLKEY, ");				//是否主键
		metaSql.append("  CASE WHEN col.isnullable = 1 THEN 'Y' ELSE 'N' END AS COLISNULL ");	//是否为空
//		metaSql.append("  ISNULL(comm.text, '') AS COLDEFAULT ");			//默认值
		metaSql.append("FROM dbo.syscolumns col ");
		metaSql.append("  LEFT JOIN dbo.systypes t ON col.xtype = t.xusertype  ");
		metaSql.append("  INNER JOIN dbo.sysobjects obj ON col.id = obj.id AND obj.xtype = 'U' AND obj.status >= 0 ");
		metaSql.append("  LEFT JOIN dbo.syscomments comm ON col.cdefault = comm.id ");
		metaSql.append("  LEFT JOIN sys.extended_properties ep ON col.id = ep.major_id AND col.colid = ep.minor_id AND ep.name = 'MS_Description'");
		metaSql.append("  LEFT JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id  AND epTwo.minor_id = 0 AND epTwo.name = 'MS_Description' ");
		metaSql.append("WHERE obj.name = '").append(tableName).append("' ORDER BY col.colorder ");
		
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: buildTableKeySql 
	 * @实现功能: TODO
	 * @param tableList
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:35:26
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
	 * @create by peijd at 2016年11月21日 下午5:35:26
	 */
	@Override
	public String buildTableKeyField(List<String> tbNameList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: assembleQueryPageSql 
	 * @职责说明: 组建分页查询SQL
	 *  SELECT TOP 5 * FROM DEPTINDO_MJ WHERE DEPTID NOT IN (SELECT TOP 5 DEPTID FROM DEPTINDO_MJ ORDER BY DEPTID DESC) order by DEPTID desc
	 * @param page
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:35:26
	 */
	@Override
	protected String assembleQueryPageSql(DcQueryPage page) throws Exception {
		String tableName = page.getTableName();
		Assert.hasText(tableName);
		//唯一性字段   不可为空
		String keyField = page.getKeyField();
		Assert.hasText(keyField);
		
		//分页查询Sql  排序字段orderBySql
		StringBuilder pageSql = new StringBuilder(64), orderBySql = new StringBuilder();
		orderBySql.append(" ORDER BY ");
		if (StringUtils.isNotBlank(page.getOrderBy())) {
			orderBySql.append(page.getOrderBy());
		}else {
			orderBySql.append(keyField).append(" DESC");
		}
		
		pageSql.append("SELECT TOP ").append(page.getLastrownum()).append(page.getFieldStr());
		pageSql.append(" FROM ").append(tableName).append(" ");
		//分页记录
		if (page.getStart()>1) {
			pageSql.append(" WHERE ").append(keyField).append(" NOT IN (SELECT TOP ");
			pageSql.append(page.getFirstrownum()).append(" ").append(keyField).append(" FROM ");
			pageSql.append(tableName).append(orderBySql).append(") ");
		}
		pageSql.append(orderBySql);
		
		return pageSql.toString();
	}

	/**
	 * Override
	 * @方法名称: querySchemaList 
	 * @实现功能: 查询sqlserverSchema
	 * @param dataSourceId
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月22日 下午2:45:53
	 */
	@Override
	public List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception {
		Assert.hasText(dataSourceId);
		return queryMetaSql(dataSourceId, "SELECT Name as SCHEMANAME FROM Master..SysDatabases ORDER BY Name");
	}
	
	/**
	 * Override
	 * @方法名称: queryLimitMetaSql 
	 * @实现功能: 查询指定数量范围内的数据
	 * @param dbSrcId
	 * @param sql
	 * @param dataNum
	 * @return
	 * @create by peijd at 2016年12月23日 下午1:37:26
	 */
	@Override
	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum) throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(sql);
		Assert.isTrue(dataNum>0);
		return queryMetaSql(dbSrcId, sql.replaceFirst("select", "select top "+dataNum));
	}
	
	/**
	 * Override
	 * @方法名称: buildTableName 
	 * @实现功能: TODO
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @create by peijd at 2016年12月23日 下午2:07:33
	 */
	@Override
	public String buildTableName(String schemaName, String tableName) {
		return tableName;
	}

}
