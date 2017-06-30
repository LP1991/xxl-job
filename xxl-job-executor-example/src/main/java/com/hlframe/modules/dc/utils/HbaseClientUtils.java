/********************** 版权声明 *************************
 * 文件名: HbaseClientUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月13日 下午3:42:23
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.*;

/** 
 * @类名: com.hlframe.modules.dc.utils.HbaseClientUtils.java 
 * @职责说明: Hbase 客户端工具类
 * @创建者: peijd
 * @创建时间: 2017年2月13日 下午3:42:23
 */
public class HbaseClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(HbaseClientUtils.class);
	
	//hbase rest服务地址
	private static String restServer = DcPropertyUtils.getProperty("hbase.rest.server");
	//scanner 批处理数
	private static int scanBatch = Integer.parseInt(DcPropertyUtils.getProperty("hbase.scanner.batch", "100"));
	
	/**
	 * @方法名称: buildHbaseMeteMap 
	 * @实现功能: 动态获取hbase数据表的内容
	 * @param tableName
	 * @return
	 * @create by peijd at 2017年2月13日 下午3:58:10
	 */
	public static List<Map<String, Object>> buildHbaseMeteMap(String tableName){
		Assert.hasText(tableName, "表名不可为空!");
		
		List<Map<String, Object>> dataList = null;
		String scannerId = getScanHeader(tableName);
		
		Assert.hasText(scannerId);
		dataList = builtScanResults(tableName, scannerId);		
		return dataList;
	}
	
	/**
	 * @方法名称: getScanHeader 
	 * @实现功能: 根据hbase-rest-api 获取Hbase scanner-id
	 * @param tableName	表名
	 * @return
	 * @create by peijd at 2017年2月13日 下午4:17:14
	 */
	private static String getScanHeader(String tableName) {
		try {
			HttpResponse<String> response = Unirest.put(restServer+"/"+tableName+"/scanner")
					  .header("accept", "text/xml")
					  .header("content-type", "text/xml")
					  .header("cache-control", "no-cache")
					  .body("<Scanner batch=\""+scanBatch+"\"> <filter>{\"type\": \"PrefixFilter\", \"value\": \"\"}</filter> </Scanner>")
					  .asString();
//			System.out.println("--->status:"+response.getStatus());	
//			Assert.isTrue("201".equals(response.getStatus()), "返回结果异常，请检查请求参数格式");
//			System.out.println(response.getHeaders()); //{Location=[http://10.1.20.138:20550/dc_sys_log/scanner/14869693188133080391]}
			String result = response.getHeaders().getFirst("Location");
			Assert.hasText(result, "返回结果异常，请检查请求参数格式");
			return result.substring(result.lastIndexOf("/")+1);
		} catch (UnirestException e) {
//			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * @方法名称: builtScanResults 
	 * @实现功能: 构建hbase scanner查询结果数据
	 * @param tableName	数据表名
	 * @param scannerId scanId
	 * @return
	 * @create by peijd at 2017年2月13日 下午4:28:32
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Map<String, Object>> builtScanResults(String tableName, String scannerId) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			String result = null;
			HttpResponse<String> response = Unirest.get(restServer+"/"+tableName+"/scanner/"+scannerId)
					  .header("accept", "application/json")
					  .header("content-type", "application/json")
					  .header("cache-control", "no-cache")
					  .asString();
//			System.out.println("--->status:"+response.getStatus());	
			if(null!=response){
				result = response.getBody();
//				System.out.println("--->body: "+result);
			}
			Assert.hasText(result, "返回结果异常，请检查请求表名及参数格式！");	//status: 200
			
			/* 返回解析结果  */
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(result, Map.class);
			List<Map<String, String>> jsonList =  (List<Map<String, String>>) map.get("Row");
			List<Map<String, String>> dataList = null;
			Map<String, Object> dataMap = null;
			
			for(Map item: jsonList){
				dataMap = new LinkedHashMap<String, Object>();
				dataMap.put("key", DcBase64.decrypt(DcStringUtils.getObjValue(item.get("key"))));
				dataList = (List<Map<String, String>>) item.get("Cell");
				for(Map data: dataList){
					dataMap.put(DcBase64.decrypt(DcStringUtils.getObjValue(data.get("column"))), DcBase64.decrypt(DcStringUtils.getObjValue(data.get("$"))));
				}
				resultList.add(dataMap);
			}
			
			//对集合结果排序
			Collections.sort(resultList, new Comparator<Map<String, Object>>(){
				@Override
				public int compare(Map<String, Object> m1, Map<String, Object> m2) {
					return m2.size() - m1.size();
				}
			});
			
		} catch (Exception e) {
			logger.error(e.getMessage());
//			e.printStackTrace();
			
		}finally{	//删除scanner
			delScanHeader(tableName, scannerId);
		}
		return resultList;
	}
	
	/**
	 * @方法名称: delScanHeader 
	 * @实现功能: 删除scanner
	 * @param tableName
	 * @param scannerId
	 * @create by peijd at 2017年2月13日 下午4:31:29
	 */
	private static void delScanHeader(String tableName, String scannerId) {
		try {
			HttpResponse<String> response =  Unirest.get(restServer+"/"+tableName+"/scanner/"+scannerId)
					  .header("accept", "text/xml")
					  .header("content-type", "text/xml")
					  .header("cache-control", "no-cache")
					  .asString();
			logger.debug("--->删除scannerId: "+scannerId+", status: "+response.getStatus());	//status:200
		} catch (UnirestException e) {
			logger.error(e.getMessage());
//			e.printStackTrace();
		}
	}

	/**
	 * @方法名称: scanAndFilter
	 * @实现功能: 根据过滤条件 查询
	 * @params  [param]
	 * @return  org.codehaus.jettison.json.JSONObject
	 * @create by peijd at 2017/5/8 21:06
	 */
	public static JSONObject scanAndFilter(JSONObject param){
		Client client = Client.create();
		try {
			//restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("hbase.restServer.url")+"/scanAndfilter");

			logger.debug("-->"+param.toString());
			WebResource resource = client.resource(u);
			//构建form参数
			ClientResponse response = resource
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, param);

			//获取返回结果
			return response.getEntity(JSONObject.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
}
