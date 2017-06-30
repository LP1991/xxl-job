/********************** 版权声明 *************************
 * 文件名: DcAbsDBHandle.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午3:29:04
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb.abs;

import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.linkdb.DBConnectionManager;
import com.hlframe.modules.dc.metadata.service.linkdb.DcDataBaseLinkService;
import com.hlframe.modules.dc.metadata.service.linkdb.DcTranslateFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.DcAbsDataBaseHandle.java 
 * @职责说明: 数据源连接 抽象处理类
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午3:29:04
 */
public abstract class DcAbsDataBaseHandle implements DcDataBaseLinkService {

	
	@Autowired
	protected DcTranslateFieldService transFieldService; 	//字段转换service

	//延时时间  获取连接时需考虑网络延时情况
	private long delayTime = 200;
	
	/**
	 * @类名: querySingleData 
	 * @职责说明: 查询动态Sql, 返回单个数据值
	 * @参数:@param dataSourceId
	 * @参数:@param metaSql
	 * @参数:@return
	 * @参数:@throws Exception 
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:32:10
	 */
	public String querySingleData(String dataSourceId, String metaSql) throws Exception {
		Assert.hasText(metaSql);
		Assert.hasText(dataSourceId);
		
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		// 获取数据源连接
		DBConnectionManager connMng = DBConnectionManager.getInstance();
		try {
			conn = connMng.getConnection(dataSourceId,delayTime);
			Assert.notNull(conn,"未取到数据源连接!");
			preStmt = conn.prepareStatement(metaSql);
			rs = preStmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			return null;
		} catch (Exception e) {
			logger.error("sql执行异常--->" + metaSql, e);
			throw e;
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != preStmt) {
					preStmt.close();
				}
				if (null!=conn) {
					connMng.freeConnection(dataSourceId, conn);
				}
			} catch (Exception e2) {
				logger.error("close connection",e2);
			}
		}
	}

	/**  
	 * @OverRide
	 * @类名: querySingleRow 
	 * @职责说明: 查询动态Sql, 返回单行记录
	 * @参数:@param dataSourceId
	 * @参数:@param metaSql
	 * @参数:@return
	 * @参数:@throws Exception 
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:38:10
	 */ 
	public Map<String, Object> querySingleRow(String dataSourceId, String metaSql) throws Exception {
		List<Map<String, Object>> resultList = queryMetaSql(dataSourceId, metaSql);
		Assert.notEmpty(resultList);
		return resultList.get(0);
	}

	/**
	 * @OverRide
	 * @类名: queryMetaSql 
	 * @职责说明: 执行动态sql, 返回查询结果
	 * @参数:@param dataSourceId
	 * @参数:@param metaSql
	 * @参数:@return
	 * @参数:@throws Exception 
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:45:32
	 */
	public List<Map<String, Object>> queryMetaSql(String dataSourceId, String metaSql) throws Exception {
		return queryMetaSql(dataSourceId, metaSql, null);
	}
	
	/**
	 * @方法名称: queryMetaSql 
	 * @实现功能: 执行动态查询sql
	 * @param dataSourceId
	 * @param metaSql
	 * @param key_upper_flag	key 值大小写设置 null-不处理; Y-大写; N-小写
	 * @return
	 * @throws Exception
	 * @create by peijd at 2017年2月8日 下午7:59:24
	 */
	public List<Map<String, Object>> queryMetaSql(String dataSourceId, String metaSql, Boolean key_upper_flag) throws Exception {
		Assert.hasText(metaSql);
		Assert.hasText(dataSourceId);

		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		// 获取数据源连接
		DBConnectionManager connMng = DBConnectionManager.getInstance();
		try {
			//考虑网络延时, 加200ms缓冲时间, 否则取不到conn	
			conn = connMng.getConnection(dataSourceId, delayTime);
			logger.debug("dataSourceId-->"+dataSourceId+"; executeSql:-->"+metaSql);
			Assert.notNull(conn, "未获取到对应数据源,请检查!");
			preStmt = conn.prepareStatement(metaSql);
			rs = preStmt.executeQuery();
			ResultSetMetaData rsmd = preStmt.getMetaData();

			// 取得结果集列数
			int columnCount = rsmd.getColumnCount();

			// 构造泛型结果集
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			Map<String, Object> data = null;

			String key = null;
			// 循环结果集
			while (rs.next()) {
				data = new HashMap<String, Object>();
				// 每循环一条将列名和列值存入Map
				for (int i = 1; i <= columnCount; i++) {
					if(null==key_upper_flag){
						key = rsmd.getColumnLabel(i);
					}else if(key_upper_flag){
						key = rsmd.getColumnLabel(i).toUpperCase();
					}else{
						key = rsmd.getColumnLabel(i).toLowerCase();
					}
					data.put(key, rs.getObject(rsmd.getColumnLabel(i)));
				}
				// 将整条数据的Map存入到List中
				datas.add(data);
			}
			return datas;

		} catch (Exception e) {
			logger.error("sql执行异常--->" + metaSql, e);
			throw e;
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != preStmt) {
					preStmt.close();
				}
				//释放数据源连接
				if (null!=conn) {
					connMng.freeConnection(dataSourceId, conn);
				}
			} catch (Exception e2) {
				logger.error("close connection",e2);
			}
		}
	}

	/**  
	 * @类名: queryTableDataNum 
	 * @职责说明: 查询数据表记录数
	 * @参数:@param dataSourceId
	 * @参数:@param page
	 * @参数:@return
	 * @参数:@throws Exception 
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:51:19
	 */ 
	public Integer queryTableDataNum(String dataSourceId, DcQueryPage page) throws Exception {
		Assert.hasText(dataSourceId);
		//数据表
		String tableName = page.getTableName();
		Assert.hasText(tableName);
		StringBuilder querySql = new StringBuilder(64);
		//查询Sql
		querySql.append("SELECT COUNT(1) AS TOTLENUM FROM ").append(tableName);
		if (StringUtils.isNotBlank(page.getFilter())) {
			querySql.append(" WHERE ").append(page.getFilter());
		}
		//执行sql 
		String res = querySingleData(dataSourceId, querySql.toString());
		Assert.hasText(res);
		return Integer.parseInt(res);
	}

	/**  
	 * @类名: queryTablePageData 
	 * @职责说明: 查询数据表分页数据
	 * @参数:@param dataSourceId
	 * @参数:@param page
	 * @参数:@return
	 * @参数:@throws Exception 
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:56:50
	 */ 
	public List<Map<String, Object>> queryTablePageData(String dataSourceId, DcQueryPage page) throws Exception {
		//组装分页查询SQL  不同数据库各自实现
		return queryMetaSql(dataSourceId, assembleQueryPageSql(page));
	}
	
	/** 
	 * @方法名称: assembleQueryPageSql 
	 * @职责说明: 组装分页查询SQL  在子类中实现
	 * @参数: @param page
	 * @参数: @return    
	 * @返回: String   
	 * @创建者: pjd 
	 * @创建时间: 2016年11月21日 下午3:57:39 
	 */ 
	protected abstract String assembleQueryPageSql(DcQueryPage page) throws Exception;

	public List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum)  throws Exception {
		return null;
	}

	/**
	 * Override
	 * @方法名称: runScript 
	 * @实现功能: 运行sql脚本
	 * @param dbSrcId
	 * @param script
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月2日 下午2:22:01
	 */
	public DcDataResult runScript(String dbSrcId, String script) throws Exception{
		Assert.hasText(dbSrcId);
		Assert.hasText(script);
		Connection con = null;
		Statement stmt = null;
		logger.debug("run script: --->"+script);
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
			logger.error("-->DcAbsDataBaseHandle runScript", e);
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
				logger.error("close connection", e2);
			}
		}
		return result;
	
	}
	
	/**
	 * Override
	 * @方法名称: buildTranslateSql 
	 * @实现功能: 构建数据转换脚本
	 * @param linkList
	 * @param transProcess
	 * @return
	 * @create by peijd at 2016年12月21日 下午6:42:36
	 */
	public String buildTranslateSql(List<DcObjectLink> linkList, DcTransDataSub transProcess){
		return null;
	}
	
	/**
	 * Override
	 * @方法名称: buildTableName 
	 * @实现功能: 构建表名
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @create by peijd at 2016年12月23日 下午2:05:18
	 */
	public String buildTableName(String schemaName, String tableName) {
		Assert.hasText(tableName);
		return StringUtils.isNotEmpty(schemaName)?schemaName+"."+tableName:tableName;
	}
	
	/**
	 * Override
	 * @方法名称: existTable 
	 * @实现功能: 数据表是否存在, 具体子类中实现
	 * @param id
	 * @param tableName
	 * @return
	 * @create by peijd at 2017年2月27日 下午8:12:16
	 */
	public boolean existTable(String id, String tableName) throws Exception{
		try {
			runScript(id, "SELECT 1 FROM "+tableName+" WHERE 1=0");
			return true;
		} catch (Exception e) {
			logger.error("-->existTable: ", e);
			return false;
		}
	}
	
	/**
	 * Override
	 * @方法名称: clearTableData
	 * @实现功能: 清空数据表内容
	 * @param id
	 * @param tableName
	 * @create by peijd at 2017年2月27日 下午8:17:36
	 */
	public void clearTableData(String id, String tableName) throws Exception{
		Assert.hasText(tableName);
		DcDataResult result = runScript(id, "TRUNCATE TABLE "+tableName);
		if(!result.getRst_flag()){
			throw new ServiceException(result.getRst_err_msg());
		}
	}
	
	/**
	 * Override
	 * @方法名称: copyData2TarTable 
	 * @实现功能: 将源表数据复制到目标表
	 * @param id
	 * @param srcTable
	 * @param tarTable
	 * @throws Exception
	 * @create by peijd at 2017年2月27日 下午8:27:25
	 */
	public void copyData2TarTable(String id, String srcTable, String tarTable) throws Exception{
		Assert.hasText(srcTable);
		Assert.hasText(tarTable);
		DcDataResult result = runScript(id, "INSERT INTO  "+tarTable+" SELECT * FROM "+srcTable);
		if(!result.getRst_flag()){
			throw new ServiceException(result.getRst_err_msg());
		}
	}
	
	/**
	 * Override
	 * @方法名称: createTable 
	 * @实现功能: 根据已有表 创建目标表
	 * @param id
	 * @param srcTable
	 * @param tarTable
	 * @param copyDataFlag
	 * @throws Exception
	 * @create by peijd at 2017年2月27日 下午8:29:37
	 */
	public void createTable(String id, String srcTable, String tarTable, boolean copyDataFlag) throws Exception{
		Assert.hasText(srcTable);
		Assert.hasText(tarTable);
		StringBuilder metaSql = new StringBuilder(64);
		metaSql.append(" CREATE TABLE ").append(tarTable).append(" SELECT * FROM ").append(srcTable);
		if(!copyDataFlag){
			metaSql.append(" WHERE 1=0");
		}
		DcDataResult result = runScript(id, metaSql.toString());
		if(!result.getRst_flag()){
			throw new ServiceException(result.getRst_err_msg());
		}
	}

	/**
	 * @方法名称: createTable
	 * @实现功能: 根据表名/字段创建 数据表
	 * @param connId        连接Id
	 * @param schemaName    schema
	 * @param tarName        数据表名
	 * @param fields        字段列表
	 * @param tableDesc	数据表描述
     * @throws Exception
	 * @create by peijd at 2017/4/20 21:22
	 */
	@Override
	public void createTable(String connId, String schemaName, String tarName, List<DcObjectField> fields, String tableDesc) throws Exception {
	}


	/**
	 * @方法名称: batchInsertData
	 * @实现功能: 批量插入数据表内容:
	 * @create by peijd at 2017/4/24 17:50
	 * @param connId        连接Id
	 * @param schemaName    schema
	 * @param tableName        数据表名
	 * @param fieldList        字段列表
	 * @param dataList        数据对象列表
	 */
	@Override
	public DcDataResult batchInsertData(String connId, String schemaName, String tableName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList) {
		DcDataResult result = new DcDataResult();
		//构建 批量insertSql
		StringBuilder batchSql = new StringBuilder(256).append("INSERT INTO ");
		if(StringUtils.isNotEmpty(schemaName)){
			batchSql.append(schemaName).append(".");
		}
		batchSql.append(tableName).append("(");
		Map<Integer, DcObjectField> fieldOrderMap = new HashMap<Integer, DcObjectField>();
		int idx = 1;
		//对照目标表, 生成数据对象
		for (DcObjectField data: fieldList) {
			fieldOrderMap.put(idx, data);
			batchSql.append(data.getFieldName()).append(",");
			idx ++;
		}
		batchSql.deleteCharAt(batchSql.length()-1).append(" ) VALUES (");
		for(int i=1; i<idx; i++){
			batchSql.append(i>1?",?":"?");
		}
		batchSql.append(")");

		//构建批量插入对象列表
		DBConnectionManager connMng = DBConnectionManager.getInstance();
		Connection con = null;
		PreparedStatement psts = null;
		try{
			//获取数据源连接
			con = connMng.getConnection(connId);
			Assert.notNull(con);
			//记录原来的提交标记
			boolean commitFlag = con.getAutoCommit();
			//关闭自动提交
			con.setAutoCommit(false);
			psts = con.prepareStatement(batchSql.toString());
			for(Map<String, Object> dataMap: dataList){
				//依次遍历每个字段对象
				for(int i=1; i<idx; i++){
					//根据字段类型  填充值
					if("String".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){
						psts.setString(i, String.valueOf(dataMap.get(fieldOrderMap.get(i).getRemarks())));
					}else if("float".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){
						psts.setFloat(i, (Float) dataMap.get(fieldOrderMap.get(i).getRemarks()));
					}else if("date".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){	//日期转换
						psts.setDate(i, new Date(DateUtils.parseDate(dataMap.get(fieldOrderMap.get(i).getRemarks())).getTime()));
					}else if("int".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){
						psts.setInt(i, (Integer) dataMap.get(fieldOrderMap.get(i).getRemarks()));
					}else if("long".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){
						psts.setLong(i, (Long) dataMap.get(fieldOrderMap.get(i).getRemarks()));
					}else if("double".equalsIgnoreCase(fieldOrderMap.get(i).getFieldType())){
						psts.setDouble(i, (Double) dataMap.get(fieldOrderMap.get(i).getRemarks()));
					}
				}
				psts.addBatch();
			}
			psts.executeBatch(); // 执行批量处理
			con.commit();  	// 手动提交
			con.setAutoCommit(commitFlag);	//还原提交标记
			result.setRst_flag(true);
			result.setRst_std_msg("执行成功, 批量提交"+dataList.size()+" 条记录!");
			result.setTotleNum(dataList.size());
		}catch(Exception e){
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
			logger.error("-->DcAbsDataBaseHandle batchInsertData", e);
		}finally {
			try {
				if (null != psts) {
					psts.close();
				}
				//释放数据源连接
				if (null!=con) {
					connMng.freeConnection(connId, con);
				}
			} catch (Exception e2) {
				logger.error("close connection", e2);
			}
		}
		return result;
	}

	/**
	 * @方法名称: batchUpdateData
	 * @实现功能:  批量更新数据表内容
	 * @param connId		连接Id
	 * @param schemaName	schema
	 * @param tarName		数据表名
	 * @param fieldList		字段列表
	 * @param dataList		数据列表
	 * @return  com.hlframe.modules.dc.common.dao.DcDataResult
	 * @create by peijd at 2017/5/11 10:04
	 */
	@Override
	public DcDataResult batchUpdateData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList) {
		return null;
	}
}
