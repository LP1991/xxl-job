/********************** 版权声明 *************************
 * 文件名: DcHiveHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb.impl
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月15日 下午4:09:44
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb.impl;

import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.linkdb.DBConnectionManager;
import com.hlframe.modules.dc.metadata.service.linkdb.DcDataBaseLinkService;
import com.hlframe.modules.dc.metadata.service.linkdb.abs.DcAbsDataBaseHandle;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.impl.DcHiveHandle.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年12月15日 下午4:09:44
 */
public class DcHiveHandle extends DcAbsDataBaseHandle implements DcDataBaseLinkService {

	/**
	 * Override
	 * @方法名称: runScript 
	 * @实现功能: 执行动态脚本
	 * @param dbSrcId
	 * @param script
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月15日 下午4:12:51
	 */
	@Override
	public DcDataResult runScript(String dbSrcId, String script) throws Exception {
		Assert.hasText(dbSrcId);
		Assert.hasText(script);
		Connection con = null;
		Statement stmt = null;
		// 获取数据源连接
		DBConnectionManager connMng = DBConnectionManager.getInstance();
		DcDataResult result = new DcDataResult();
		try{
			con = connMng.getConnection(dbSrcId);
			Assert.notNull(con);
			stmt = con.createStatement();
			stmt.executeUpdate(script);
			
			//返回结果
			result.setRst_flag(true);
			result.setRst_std_msg("-->run sql:"+script+", success!");
		}catch(Exception e){
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
			logger.error("-->DcImpalaHandle.DcDataResult", e);
		}finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				//释放数据源连接
				if (null!=con) {
					connMng.freeConnection(dbSrcId, con);
				}
			} catch (Exception e2) {
				logger.error("close connection",e2);
			}
		}
		return result;
	}
	
	/**
	 * Override
	 * @方法名称: querySchemaList 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月15日 下午4:09:44
	 */
	@Override
	public List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: queryTableNames 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @param schema
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月15日 下午4:09:44
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
	 * @create by peijd at 2016年12月15日 下午4:09:44
	 */
	@Override
	public List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: queryTableColumns 
	 * @实现功能: TODO
	 * @param dataSourceId
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月15日 下午4:09:44
	 */
	@Override
	public List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Override
	 * @方法名称: buildTableKeySql 
	 * @实现功能: TODO
	 * @param tableList
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月15日 下午4:09:44
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
	 * @create by peijd at 2016年12月15日 下午4:09:44
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
	 * @create by peijd at 2016年12月15日 下午4:09:44
	 */
	@Override
	protected String assembleQueryPageSql(DcQueryPage page) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
