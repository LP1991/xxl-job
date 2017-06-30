/********************** 版权声明 *************************
 * 文件名: DcOracleHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb.impl
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午5:23:24
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
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.impl.DcOracleHandle.java 
 * @职责说明: oracle 数据库 同步处理
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午5:23:24
 */
@Service
@Transactional(readOnly = true)
public class DcOracleHandle extends DcAbsDataBaseHandle implements DcDataBaseLinkService {

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @param schema
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:25:40
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception {
		return queryTableNames(dataSourceId, schema, null);
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @param schema
	 * @param filter
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:25:40
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter) throws Exception {
		StringBuilder metaSql = new StringBuilder(128);
		
		//自建的数据表
		metaSql.append("SELECT B.TABLE_NAME AS TABNAME, B.TABLE_TYPE AS TABTYPE, ");
		metaSql.append(" 	NVL2(B.COMMENTS,B.TABLE_NAME||'('||COMMENTS||')',B.TABLE_NAME) AS TABCOMMENT ");
		metaSql.append("  FROM USER_TAB_COMMENTS B ");
		metaSql.append(" INNER JOIN  USER_TABLES A ON A.TABLE_NAME = B.TABLE_NAME ");
		if (StringUtils.isNotBlank(schema)) {
			metaSql.append(" AND A.TABLESPACE_NAME = '").append(schema.toUpperCase()).append("'");
		}
		
		//增加过滤条件  变量替换
		if (StringUtils.isNotBlank(filter)) {
			metaSql.append(" AND ").append(filter.replaceAll("[{]TABLENAME[}]", "B.TABLE_NAME"));
		}
		metaSql.append(" ORDER BY B.TABLE_NAME");
		
		//返回查询结果
		List<Map<String, Object>> rstList= queryMetaSql(dataSourceId, metaSql.toString());//自己创建表的表名
		StringBuilder grantSql = new StringBuilder(128);
		
		//被授权的table select distinct table_name, owner from user_tab_privs
		grantSql.append("SELECT DISTINCT TABLE_NAME AS TABNAME, '' AS TABTYPE, table_name AS TABCOMMENT FROM USER_TAB_PRIVS WHERE OWNER='").append(schema.toUpperCase()).append("'");
		List<Map<String,Object>> grantList = queryMetaSql(dataSourceId, grantSql.toString());//权限下的表名
		 rstList.addAll(grantList);
		return rstList;
	}

	/**
	 * Override
	 * @方法名称: queryTableColumns 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:25:40
	 */
	@Override
	public List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception {
		Assert.hasText(tableName);
		StringBuilder metaSql = new StringBuilder(64);
		/*metaSql.append("SELECT A.COLUMN_NAME as COLNAME, ");		//列名
		metaSql.append("   B.COMMENTS as COLCOMMECT, ");			//注释
		metaSql.append("   CASE WHEN C.COLUMN_NAME = A.COLUMN_NAME THEN 'Y' ELSE 'N' END AS COLKEY, ");				//是否主键
		metaSql.append("   A.DATA_LENGTH as COLLENG, ");			//长度
		metaSql.append("   A.DATA_TYPE as COLTYPE, ");				//类型
		metaSql.append("   A.NULLABLE as COLISNULL ");				//是否为空
		metaSql.append("FROM USER_TAB_COLUMNS A ");
		metaSql.append("  LEFT JOIN USER_COL_COMMENTS B ON A.COLUMN_NAME=B.COLUMN_NAME AND A.TABLE_NAME=B.TABLE_NAME ");
		metaSql.append("  LEFT JOIN (SELECT CU.COLUMN_NAME,CU.TABLE_NAME FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU ");
		metaSql.append(" 				WHERE CU.CONSTRAINT_NAME = AU.CONSTRAINT_NAME AND AU.CONSTRAINT_TYPE='P') C ");
		metaSql.append("    		 ON A.COLUMN_NAME = C.COLUMN_NAME AND C.TABLE_NAME = A.TABLE_NAME ");
		metaSql.append("WHERE a.TABLE_NAME = '").append(tableName.toUpperCase()).append("' ORDER BY A.COLUMN_ID");*/
		metaSql.setLength(0);
		metaSql.append("SELECT A.COLUMN_NAME as COLNAME,A.DATA_LENGTH as COLLENG,A.DATA_TYPE as COLTYPE,A.NULLABLE as COLISNULL, ");		//列名
		metaSql.append("   NVL2(B.COMMENTS,A.COLUMN_NAME||'('||B.COMMENTS||')',A.COLUMN_NAME) as COLCOMMECT, '' AS COLKEY  ");				//注释
		metaSql.append("   FROM all_tab_columns a  ");			
		metaSql.append("    left join  ALL_COL_COMMENTS B  ON A.COLUMN_NAME=B.COLUMN_NAME AND A.TABLE_NAME=B.TABLE_NAME ");				//是否为空
		metaSql.append("where a.TABLE_NAME='").append(tableName.toUpperCase()).append("' and a.OWNER='").append(schema.toUpperCase()).append("'");
		
		return queryMetaSql(dataSourceId, metaSql.toString());
	}

	/**
	 * Override
	 * @方法名称: buildTableKeySql 
	 * @实现功能: TODO
	 * @param tableList
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:25:40
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
	 * @create by peijd at 2016年11月21日 下午5:25:40
	 */
	@Override
	public String buildTableKeyField(List<String> tbNameList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: assembleQueryPageSql 
	 * @实现功能: TODO
	 * @param page
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月21日 下午5:25:40
	 */
	@Override
	protected String assembleQueryPageSql(DcQueryPage page) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: querySchemaList 
	 * @实现功能: 查询数据库schema列表
	 * @param dataSourceId
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月22日 下午2:32:58
	 */
	@Override
	public List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception {
		Assert.hasText(dataSourceId);
		StringBuilder metaSql = new StringBuilder(64);
		//metaSql.append("select username as SCHEMANAME from dba_users where account_status='OPEN' and default_tablespace not in ('BYMSRM','SYSTEM')");		//列名
		metaSql.append("select username as SCHEMANAME from ALL_USERS ");		//列名
		
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
	 * @create by cdd at 2016年11月25日 上午11:29:32
	 */
	@Override
	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum) throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(sql);
		sql ="select tt.*,ROWNUM from ("+sql+") tt where ROWNUM<="+dataNum;
		return queryMetaSql(dbSrcId, sql);
	}

}
