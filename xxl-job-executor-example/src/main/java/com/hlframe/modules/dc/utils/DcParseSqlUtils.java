/********************** 版权声明 *************************
 * 文件名: DcParseSqlUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月3日 下午12:53:14
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hlframe.modules.dc.dataprocess.entity.DcTransParseSqlResult;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcParseSqlUtils.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2016年12月3日 下午12:53:14
 */
public class DcParseSqlUtils {

	//解析Sql方式
	private static final String SQL_PARSE_ENGINE_HIVE = "parseHiveSql";

	/**
	 * @方法名称: parseTransSql 
	 * @实现功能: 解析转换SQL  默认Mysql处理引擎
	 * @param transSql
	 * @return
	 * @create by peijd at 2016年12月3日 下午1:39:57
	 */
	public static DcTransParseSqlResult parseTransSql(String transSql){
		return parseTransSql(JdbcConstants.MYSQL, transSql);
	}
	
	/**
	 * @方法名称: DcTransParseSqlResult 
	 * @实现功能: 解析转换SQL
	 * @param parseEngine	转换sql引擎
	 * @param transSql		转换SQL
	 * @return
	 * @create by peijd at 2016年12月3日 下午12:56:11
	 */
	public static DcTransParseSqlResult parseTransSql(String parseEngine, String transSql){
		Assert.hasText(parseEngine);
		Assert.hasText(transSql);
		
		DcTransParseSqlResult rst = new DcTransParseSqlResult();
		//解析SQL
		List<SQLStatement> stmtList = SQLUtils.parseStatements(transSql, parseEngine);
		
//		for(SQLStatement stmt: stmtList){
		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		stmtList.get(0).accept(visitor);
		// 获取表名称
		rst.setTarTable(visitor.getCurrentTable());
		// 获取操作方法名称
		rst.setOperate(visitor.getTables().toString());
		// 获取字段名称
//		rst.setTableFieldList(tableFieldList);
		System.out.println("fields : " + visitor.getColumns());
		// 获取查询条件
//		rst.setCondition(visitor.getConditions());
		System.out.println("conditions : " + visitor.getConditions());
		// GroupBy
		System.out.println("variants : " + visitor.getVariants());
//		}
		
		return rst;
	}
	
	/**
	 * @方法名称: parseHiveSql 
	 * @实现功能: 解析hive/Impala sql脚本 
	 * @param transSql	转换脚本
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月10日 上午11:54:33
	 */
	public static String parseHiveSql(String transSql) throws Exception {
		Assert.hasText(transSql);
		//调用restful服务  解析hiveSql
		Client client = Client.create();
		//restful 地址
		URI u = new URI(DcPropertyUtils.getProperty("parseSql.restServer.url")+SQL_PARSE_ENGINE_HIVE);
		WebResource resource = client.resource(u);
		
		//构建form参数
		MultivaluedMapImpl params = new MultivaluedMapImpl();  
		params.add("hiveSql", transSql); 
		
		//返回解析结果
		return resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
	}
	
	/**
	 * @方法名称: parseHiveSqlToMap 
	 * @实现功能: 将hive脚本解析到Map中
	 * @param transSql
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月10日 下午12:59:11
	 */
	public static Map<String, String> parseHiveSqlToMap(String transSql) throws Exception {
		//gson json转Map
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();  
		//返回解析结果
		return gson.fromJson(parseHiveSql(transSql), new TypeToken<Map<String, String>>(){}.getType());
	}
	
	
	/**
	 * @方法名称: preProcessSql 
	 * @实现功能: 预处理解析Sql
	 * @param parseEngine
	 * @param transSql
	 * @return
	 * @create by peijd at 2016年12月3日 下午1:13:44
	 */
	public static String preProcessSql(String parseEngine, String transSql){
		Assert.hasText(parseEngine);
		Assert.hasText(transSql);
		//impala sql预处理  去除EXTERNAL关键字, 以及)之后的字符;
		if(parseEngine.indexOf("impala")>0){
			transSql = transSql.toUpperCase().replaceAll("EXTERNAL", "");
			if(transSql.indexOf(")")>0){
				transSql = transSql.substring(0, transSql.indexOf(")")+1);
			}
		}
		return transSql;
	}
}
