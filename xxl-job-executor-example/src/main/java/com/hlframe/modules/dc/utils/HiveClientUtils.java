/********************** 版权声明 *************************
 * 文件名: HiveClientUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月16日 上午11:11:58
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.utils.HiveClientUtils.java 
 * @职责说明: hive客户端连接管理
 * @创建者: peijd
 * @创建时间: 2016年12月16日 上午11:11:58
 */
public class HiveClientUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HiveClientUtils.class);
	//返回标记
	public static final String REST_RESULT_FLAG_SUCCESS = "T";
	public static final String REST_RESULT_FLAG_ERROR = "F";

	
	/**
	 * @方法名称: queryHiveMeteSql 
	 * @实现功能: 执行hive动态SQL , 返回json结果
	 * @param metaSql	动态SQL
	 * @param limit		返回结果数, 如果要显示所有数据, 传负数(-1)即可	
	 * @return
	 * @create by peijd at 2016年12月16日 上午11:26:06
	 */
	public static String queryHiveMeteSql(String metaSql, int limit) throws Exception {
		Assert.hasText(metaSql);
		Client client = Client.create();
		if(0==limit){
			limit = Integer.parseInt(DcPropertyUtils.getProperty("queryHive.result.dataLimit", "50"));
			metaSql += " limit "+limit;
		}
		try {
			//restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("queryHive.restServer.url"));
			WebResource resource = client.resource(u);
			
			//构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();  
			params.add("metaSql", metaSql);  
			return resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
			
		} catch (URISyntaxException e) {
			logger.error("-->queryHiveMeteSql: "+metaSql, e);
			throw e;
		}  
	}
	
	/**
	 * @方法名称: queryHiveMeteSql 
	 * @实现功能: 执行hive动态SQL , 返回json结果
	 * @param metaSql
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年12月16日 下午2:25:12
	 */
	public static String queryHiveMeteSql(String metaSql) throws Exception {
		return queryHiveMeteSql(metaSql, 0);
	}
	
	/**
	 * @方法名称: buildHiveMeteMap 
	 * @实现功能: 将hivesql执行结果, 封装为List<Map>对象
	 * @param metaSql
	 * @param limit		返回结果数	
	 * @return
	 * @create by peijd at 2016年12月16日 下午12:03:32
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> buildHiveMeteMap(String metaSql, int limit) throws Exception {
		Assert.hasText(metaSql);
		String rstJson = queryHiveMeteSql(metaSql, limit).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]").replaceAll("\\\\\"", "\"");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			if(StringUtils.isEmpty(rstJson)){
				return null;
			}
			Map<String, Object> map = mapper.readValue(rstJson, Map.class);
			if(REST_RESULT_FLAG_ERROR.equals(map.get("resultFlag"))){
				return null;
			}
			if(StringUtils.isBlank(DcStringUtils.getObjValue(map.get("std_msg")))){
				return null;
			}
			//jsonArray转换为Map列表
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) map.get("std_msg");
			return dataList;
			
		} catch (Exception e) {
			logger.error("-->buildHiveMeteMap: "+metaSql, e);
			throw e;
		}
	}


	/**
	 * @方法名称: queryTableDataNum
	 * @实现功能: 计算table数据量	TODO: 性能较低, 如果数据表有分区 partition 可通过元数据直接查询.
	 * @param tableName		数据表
	 * @param filter		过滤条件
	 * @return  int
	 * @create by peijd at 2017/5/9 9:57
	 */
	public static int queryTableDataNum(String tableName, String filter) throws Exception {
		Assert.hasText(tableName);
		String queryNumSql = "select count(1) as tablenum from "+tableName+ filter;
		List<Map<String, Object>> queryNumList = buildHiveMeteMap(queryNumSql, -1);
		Assert.notEmpty(queryNumList);
		//获取整数值
		return (int) queryNumList.get(0).get("tablenum");
	}
	
	public static List<Map<String, Object>> buildHiveMeteMap(String metaSql) throws Exception {
		return buildHiveMeteMap(metaSql, 0);
	}
	
	// 测试 by peijd
	public static void main(String[] args) {
		try {
			System.out.println(queryHiveMeteSql("select * from hl_dc_st_syncdata where id<10"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
