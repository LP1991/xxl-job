/********************** 版权声明 *************************
 * 文件名: DcJsonUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年1月11日 上午10:59:34
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.service.ServiceException;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcJsonUtils.java 
 * @职责说明: TODO
 * @创建者: peijd
 * @创建时间: 2017年1月11日 上午10:59:34
 */
public class DcJsonUtils {
	private final static Logger logger = LoggerFactory.getLogger(DcJsonUtils.class);
	/**
	 * @方法名称: jsonStr2Map 
	 * @实现功能: 将json字符串转换为Map
	 * @param json
	 * @return
	 * @create by peijd at 2017年1月11日 上午11:06:14
	 */
	public static Map<String, Object> jsonStr2Map(String json) throws Exception {
		Assert.hasText(json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, Map.class);
	}

	/**
	 * @方法名称: getJsonData
	 * @实现功能: 根据配置信息 获取json 字符串
	 * @params  [param]
	 * @return  java.lang.String
	 * @create by peijd at 2017/4/18 15:00
	 */
	public static String getRestfulWsData(DcJobTransIntfSrc param) throws Exception {
		Assert.hasLength(param.getRestUrl());
		Assert.hasText(param.getRestType());

		String restWsData = null;
		URI url = new URI(param.getRestUrl());
		try{
			//初始化客户端连接
			Client client = Client.create();
			WebResource resource = client.resource(url);

			//目前只考虑post/get调用方式
			if(DcJobTransIntfSrc.RESTFUL_TYPE_GET.equals(param.getRestType())){	//get方式 暂不考虑分页,项目中碰到实际数据再说
				return resource.get(String.class);

			}else if(DcJobTransIntfSrc.RESTFUL_TYPE_POST.equals(param.getRestType())){	//post方式 博地项目中存在数据分页情况
				//构建form参数
				MultivaluedMapImpl params = new MultivaluedMapImpl();
				if (DcStringUtils.isNotBlank(param.getParams())){
					for(String str: param.getParams().split("&")){
						if(str.indexOf("=")>0){
							//根据=号进行解析
							params.add(str.split("=")[0], str.split("=")[1]);
						}
					}
				}
				//rest 默认调用方式
				String contectType = DcStringUtils.isNotBlank(param.getRestContentType())?param.getRestContentType():MediaType.APPLICATION_FORM_URLENCODED;
				return  resource.type(contectType).post(String.class, params);
			}

		}catch(Exception e){
			logger.error("-->getRestfulWsData: ", e);
			throw new ServiceException("接口调用异常!");
		}
		return  restWsData;
	}



	/**
	 * @方法名称: parseJsonKey
	 * @实现功能: 解析json中key列表, 接口采集初始化数据表用
	 * @params  [json]
	 * @return  java.util.List<java.lang.String>
	 * @create by peijd at 2017/4/18 14:46
	 */
	public static List<String> parseJsonKey(String restJson) throws Exception {
		Assert.hasText(restJson);
		//将json转为List<Map>对象
		List<Map<String, Object>> dataList = parseJson2List(restJson);
		Assert.notEmpty(dataList);
		Assert.notNull(dataList.get(0));
		List<String> fieldList = new LinkedList<String>();
		//依次获取map中key字段
		for(String key: dataList.get(0).keySet()){
			fieldList.add(key);
		}
		return fieldList;
	}

	/**
	 * @方法名称: parseJson2List
	 * @实现功能: jsonArr 转为数组
	 * @params  [restJson]
	 * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @create by peijd at 2017/4/18 16:23
	 */
	public  static List<Map<String, Object>> parseJson2List(String restJson) throws Exception {
		Assert.hasText(restJson);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		ObjectMapper mapper = new ObjectMapper();
		try{
			//json转List对象
			dataList = mapper.readValue(restJson, List.class);
		}catch(JsonMappingException e){
//			转换异常 则尝试转换为map对象
			Map<String, Object> dataMap = mapper.readValue(restJson, Map.class);
			dataList.add(dataMap);
		}
		return dataList;

		/** 这里不做个性化处理, 在接口数据读取时统一转换为统一数据格式 modified by peijd
		JsonNode node = mapper.readTree(restJson);
		if (node.has("data")){
			return mapper.readValue(node.get("data").toString(), List.class);
		}else if (node.has("list")){
			return mapper.readValue(node.get("list").toString(), List.class);
		}
		return null;
		**/
	}

	/**
	 * @方法名称: object2Json
	 * @实现功能: 对象转Json 字符串
	 * @param obj
	 * @return  java.lang.String
	 * @create by peijd at 2017/5/2 16:28
	 */
	public  static String object2Json(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		try{
			return mapper.writeValueAsString(obj);
		}catch(Exception e){
			logger.error("-->object2Json: ",e);
		}
		return "";
	}
}
