/********************** 版权声明 *************************
 * 文件名: DcEsUtil.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年11月3日 下午8:46:42
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.DateUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.range.InternalRange.Bucket;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcEsUtil.java 
 * @职责说明: ElasticSearch 工具类
 * @创建者: yuzh
 * @创建时间: 2016年11月3日 下午8:46:42
 */
public class DcEsUtil {
	
	static Logger logger = LoggerFactory.getLogger(DcEsUtil.class);
	static EsClientProvider provider = new EsClientProvider();
	
	static{
		provider.init();
	}
	
	/**
	 * @方法名称: getClient 
	 * @实现功能: 获取elasticSearch 连接客户端
	 * @return
	 * @throws Exception
	 * @create by peijd at 2016年11月10日 下午2:43:28
	 */
	public static Client getClient() throws Exception {
		Client client = provider.get();
    	if(null==client){
    		provider.init();
    		client = provider.get();
    	}
    	return client;
	}
	
	/**
	 * @方法名称: getEsClient 
	 * @实现功能: 创建ES客户端连接
	 * @return
	 * @create by peijd at 2016年11月4日 下午4:24:40
	 */
	public static Client getEsClient() throws Exception {
        try {
            //创建ES客户端连接
            Client client = TransportClient.builder().addPlugin(DeleteByQueryPlugin.class).build()
                    .addTransportAddress(new InetSocketTransportAddress(
                    		InetAddress.getByName(DcPropertyUtils.getProperty("elasticSearch.main.ip", "10.1.70.200")),
                    		Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.main.port", "9300"))));
            return client;
		} catch (Exception e) {
			logger.error("ElasticSearch client error!", e);
		}
        return null;
    }
	

	/**
	 * @方法名称: checkIndex 
	 * @实现功能: 检查索引是否存在
	 * @param client
	 * @param indexName  索引名称
	 * @return
	 * @create by peijd at 2016年11月5日 上午9:13:20
	 */
	public static boolean checkIndex(Client client, String indexName) throws Exception {
		IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
		IndicesExistsResponse inExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
		return inExistsResponse.isExists();
	}
	
	/**
	 * @方法名称: creataIndex 
	 * @实现功能: 创建索引库
	 * @param client
	 * @param indexName  索引名称
	 * @create by peijd at 2016年11月5日 上午9:15:52
	 */
	public static void creataIndex(Client client, String indexName) throws Exception {
		Assert.hasText(indexName);
		// 检查索引是否存在
		if (checkIndex(client, indexName)) {
			logger.info("Index[" + indexName + "] is already exists!");
			return;
		}

		// 创建索引 方法1
		CreateIndexResponse cIndexResponse = client.admin().indices().prepareCreate(indexName).setSettings("{\"analysis\":{\"analyzer\":{\"ik\":{\"tokenizer\":\"ik\"}}}}").execute().actionGet();
		// 方法2
		// CreateIndexResponse cIndexResponse = client.admin().indices().create(new CreateIndexRequest("pjd")).actionGet();
		if (cIndexResponse.isAcknowledged()) {
			logger.info("Success Create index[" + indexName + "]!");
		} else {
			logger.error("Fail to create index[" + indexName + "]!");
		}
		//新建mapping
	}
	
	/**
	 * @方法名称: deleteIndex 
	 * @实现功能: 删除索引
	 * @param client
	 * @param indexName	索引名称
	 * @create by peijd at 2016年11月5日 上午9:49:23
	 */
	public static void deleteIndex(Client client, String indexName) throws Exception {
		Assert.hasText(indexName);
		// 检查索引是否存在
		if (checkIndex(client, indexName)) {
			//删除索引
			DeleteIndexResponse deleteResponse = client.admin().indices().prepareDelete(indexName).execute().actionGet();
			if (deleteResponse.isAcknowledged()) {
				logger.info("delete index {} successfully!", indexName);
			} else {
				logger.error("Fail to delete index {}", indexName);
			}
		}
	}
	
	/**
	 * @方法名称: deleteFileById 
	 * @实现功能: 删除索引/类型上指定文件
	 * @param client
	 * @param indexName	索引名
	 * @param typeName	类型名
	 * @param fileId	文件Id
	 * @create by peijd at 2016年11月5日 上午9:24:10
	 */
	public static void deleteFileById(Client client, String indexName, String typeName, String fileId) throws Exception {
		Assert.hasText(indexName);
		Assert.hasText(typeName);
		Assert.hasText(fileId);
		
		/** 删除文件对象 id **/
		DeleteResponse dResponse = client.prepareDelete(indexName, typeName, fileId).execute().actionGet();

        if (dResponse.isFound()) {
        	logger.info("Success delete file id:{} in type:{} of index:{} !", fileId, typeName, indexName);
        } else {
        	logger.info("Fail to delete file id:{} in type:{} of index:{} !", fileId, typeName, indexName);
        }
	}
	
	/**
	 * @方法名称: deleteType 
	 * @实现功能: 删除索引的类型, 未找到该方法
	 * @param client
	 * @param indexName	索引名
	 * @param typeName	类型名
	 * @create by peijd at 2016年11月5日 上午10:11:46
	 * modify by lp at  2017年3月8日
	 * note : sudo bin/plugin install delete-by-query .
	 */
	public static void deleteType(Client client, String indexName, String typeName) throws Exception {
//		client.prepareDelete().setIndex(indexName).setType(typeName).execute().actionGet();
		String deletebyquery = "{\"query\": {\"match_all\": {}}}";
		DeleteByQueryResponse response =  new DeleteByQueryRequestBuilder(client,
				DeleteByQueryAction.INSTANCE)
				.setIndices(indexName)
				.setTypes(typeName)
				.setSource(deletebyquery)
				.execute()
				.actionGet();
	}

	/**
	 * @方法名称: deleteType
	 * @实现功能: 删除索引的类型, 未找到该方法
	 * @param client
	 * @param indexName	索引名
	 * @param typeName	类型名
	 * @create by peijd at 2016年11月5日 上午10:11:46
	 * modify by lp at  2017年3月8日
	 * note : sudo bin/plugin install delete-by-query .
	 */
	public static void deleteById(Client client, String indexName, String typeName) throws Exception {
//		client.prepareDelete().setIndex(indexName).setType(typeName).execute().actionGet();
		String deletebyquery = "{\"query\": {\"match\": { \"id\": \"2c54deca0ff84c1fbd0e3a2d2fe80747\"}}}";
		System.out.println(deletebyquery);
		DeleteByQueryResponse response =  new DeleteByQueryRequestBuilder(client,
				DeleteByQueryAction.INSTANCE)
				.setIndices(indexName)
				.setTypes(typeName)
				.setSource(deletebyquery)
				.execute()
				.actionGet();
	}

	/**
	 * @方法名称: queryPageFromSize 
	 * @实现功能: FromSize模式下获取指定分页的数据
	 * @param indexName	索引名
	 * @param typeName	类型名
	 * @param pageSize	每页数量
	 * @param pageNum	页数
	 * @param qb		查询条件
	 * @create by peijd at 2016年11月5日 上午10:05:28
	 */
	public static List<Map<String, Object>> queryPageFromSize(Client client, String indexName, String typeName, int pageSize, int pageNum, QueryBuilder qb) throws Exception {
		Assert.isTrue(pageNum > 0);
		int startNum = (pageNum - 1) * pageSize;
		// 定义每页数据
		pageSize = pageSize <= 0 ? Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.pager.num", "20"))
				: pageSize;
		// 数据总量
		// long count =
		// client.prepareCount(indexName).setTypes(TypeName).execute().actionGet().getCount();
		long count = client.prepareSearch(indexName).setTypes(typeName).execute().actionGet().getHits().totalHits();
		// 分页记录不能超过总数
		Assert.isTrue(startNum <= count);

		if (null == qb) {
			qb = QueryBuilders.matchAllQuery();
		}

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		// 分页查询 添加到列表
		SearchResponse response = client.prepareSearch(indexName).setTypes(typeName).setQuery(qb).setFrom(startNum)
				.setSize(pageSize).execute().actionGet();
		for (SearchHit sh : response.getHits().getHits()) {
			dataList.add(sh.getSource());
		}
		return dataList;
	}

	/**
	 * @方法名称: queryPageFromSize
	 * @实现功能:重写方法，加入时间，分类等
	 * @param client
	 * @param indexName
	 * @param TypeName
	 * @param pageSize
	 * @param pageNum
	 * @param qb
	 * @param timeaggregation
	 * @param catalogAgg
	 * @return
	 * @throws Exception
	 * @create by cdd at 2016年12月16日 上午9:11:24
	 *  modify by Primo at 2017年3月14日
	 */
	public static Map<String, Object> queryPageFromSize(Client client, String indexName, String TypeName, int pageSize, int pageNum, QueryBuilder qb, QueryBuilder filter,
                                                        AggregationBuilder timeaggregation, AggregationBuilder catalogAgg) throws Exception {

		Map<String, Object> tmpMap = new LinkedHashMap<>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		
		Assert.isTrue(pageNum > 0);
		int startNum = (pageNum - 1) * pageSize;

		// 定义每页数据
		pageSize = pageSize <= 0 ? Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.pager.num", "20")): pageSize;

		long count = 0 ;
		//根据类型选择不同步的type
		if(null!=TypeName && !"".equals(TypeName)){
 			String[] types = TypeName.split(",");
			count = client.prepareSearch(indexName).setTypes(types).execute().actionGet().getHits().totalHits();
		}else{
			count = client.prepareSearch(indexName).execute().actionGet().getHits().totalHits();
		}

		// 分页记录不能超过总数
		Assert.isTrue(startNum <= count);

		if (null == qb) {
			qb = QueryBuilders.matchAllQuery();
		}
		
		// 分页查询 添加到列表
		SearchResponse response = null;
        SearchRequestBuilder requestBuilder = null;

		//根据类型选择不同步的type
		if(null!=TypeName && !"".equals(TypeName)){
			String[] types = TypeName.split(",");
            requestBuilder = client.prepareSearch(indexName).setTypes(types).setQuery(qb).setPostFilter(filter);
		}else{
            requestBuilder = client.prepareSearch(indexName).setQuery(qb).setPostFilter(filter);
		}

        response = requestBuilder.addAggregation(timeaggregation).addAggregation(catalogAgg) // 聚合
                .setFrom(startNum)
                .setSize(pageSize).execute().actionGet();
		
		long totalcount  = response.getHits().totalHits();
		long pageNo = (totalcount % pageSize) == 0 ? totalcount/pageSize : (totalcount/pageSize)+1;
		tmpMap.put("pageNum", pageNo);
		tmpMap.put("count", totalcount);
		for (SearchHit sh : response.getHits().getHits()) {
			dataList.add(sh.getSource());
		}
		tmpMap.put("source", dataList);

		InternalDateRange agg = response.getAggregations().get("dateAgg");// 时间周期数据
		Map<String, Object> dateMap =  controlDateRangeData(agg);
		tmpMap.put("dateRange", dateMap);

		Nested  cataAgg = response.getAggregations().get("cataAgg");
		Terms name = cataAgg.getAggregations().get("name");
		Map<String, Object> map = controlTermsData(name);
		tmpMap.put("catalog", map);
//		controlTermsData(cataAgg);
//		System.out.println("------------------------dateMap-------------------------------");
//		Iterator<String> its = dateMap.keySet().iterator();
//		while (its.hasNext()) {
//			String key = its.next();
//			System.out.println(""+key+": "+dateMap.get(key));
//		}
//		System.out.println("-------------------------------------------------------");
//
//		System.out.println("------------------------map-------------------------------");
//		Iterator<String> it = map.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			System.out.println(""+key+": "+map.get(key));
//		}
//		System.out.println("-------------------------------------------------------");
//		System.out.println(tmpMap);
		return tmpMap;
	}
	
	/**
	 * @方法名称: controlDateRangeData 
	 * @实现功能: 处理InternalDateRange数据
	 * @param agg
	 * @return map数据
	 * @throws ParseException
	 * @create by cdd at 2016年12月16日 下午4:49:35
	 */
	public static Map<String, Object> controlDateRangeData(InternalDateRange agg) throws ParseException {
		Map<String, Object> dateMap = new LinkedHashMap<String, Object>();// 会根据得到的大小进行排列
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(Calendar.getInstance().getTimeZone());
		for (int i = 0; i < agg.getBuckets().size(); i++) { //
			Bucket a = agg.getBuckets().get(i);
			Date fromDate = sdf.parse(a.getFromAsString());// 获取开始时间
			Date toDate = sdf.parse(a.getToAsString());// 获取结束时间
			String key = dateToKey(DateUtils.getDistanceOfTwoDate(fromDate, toDate));// 两个时间之间的间隔
			long docCount = a.getDocCount(); // Doc count //每个时间范围的数量
			dateMap.put(key, docCount);
		}
		return dateMap;
	}

	private static String dateToKey(double distanceOfTwoDate) {
		if (distanceOfTwoDate<2){
			return "today";
		}else if (distanceOfTwoDate<10){
			return "oneWeek";
		}else if (distanceOfTwoDate<40){
			return "oneMonth";
		}else if (distanceOfTwoDate<100){
			return "threeMonths";
		}else if (distanceOfTwoDate<200){
			return "halfYear";
		}else if (distanceOfTwoDate<370){
			return "oneYear";
		}
		return "";
	}

	/**
	 * @方法名称: controlTermsData 
	 * @实现功能: 处理Terms数据
	 * @param terms
	 * @return map数据
	 * @create by cdd at 2016年12月16日 下午4:51:25
	 */
	public static Map<String, Object> controlTermsData(Terms terms) {
		Map<String, Object> termsMap = new LinkedHashMap<String, Object>();// 会根据得到的大小进行排列
		if (terms == null || terms.getBuckets() == null || terms.getBuckets().size() == 0) {// 对多种情况的讨论
			termsMap.put("key", (long) 0);
		} else {
			for (int i = 0; i < terms.getBuckets().size(); i++) {
				Terms.Bucket a = terms.getBuckets().get(i);
				String key = a.getKey().toString();// 获取key
				long docCount = a.getDocCount(); // 获取数量
				termsMap.put(key + "", docCount);// 组件map
			}
		}
		return termsMap;
	}
	
	/**
	 * @方法名称: queryDateFromLabel 
	 * @实现功能: TODO
	 * @param client
	 * @param property
	 * @param objType
	 * @return
	 * @create by cdd at 2016年12月16日 下午3:25:21
	 */
	public static Map<String, Object> queryDataFromSearch(Client client, String indexName, String TypeName, int pageSize, int pageNum, String searchType, QueryBuilder qb, QueryBuilder qb1) throws Exception {
		Assert.isTrue(pageNum > 0);
		int startNum = (pageNum - 1) * pageSize;
		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		// 定义每页数据
		pageSize = pageSize <= 0 ? Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.pager.num", "20"))
				: pageSize;
		// 数据总量
		//long count = client.prepareSearch(indexName).setTypes(TypeName).execute().actionGet().getHits().totalHits();
		// 分页记录不能超过总数
		//Assert.isTrue(startNum <= count);

		if (null == qb) {
			qb = QueryBuilders.matchAllQuery();
		}
		
		
		// 分页查询 添加到列表
		SearchResponse response = client.prepareSearch(indexName).setTypes(TypeName).setQuery(qb)
				.setPostFilter(qb1)
				.setFrom(startNum).setSize(pageSize).execute().actionGet();
     long count  = response.getHits().totalHits();
     long pageNo = (count % pageSize) == 0 ? count/pageSize : (count/pageSize)+1;
		dataMap.put("pageNum", pageNo);
		dataMap.put("count", count);
		for (SearchHit sh : response.getHits().getHits()) {// 数据部分
			dataList.add(sh.getSource());
		}
		dataMap.put("source", dataList);

		// Map<String, Object> HistogramMap = new LinkedHashMap<String,//时间处理方式
				// Object>();
				// Histogram agg = response.getAggregations().get("aggs"); //
				// 对于时间聚合必须使用Histogram
				// for (Histogram.Bucket entry : agg.getBuckets()) {
				// String key = (String) entry.getKeyAsString(); // bucket key
				// long docCount = entry.getDocCount(); // Doc count
				// HistogramMap.put(key+"", docCount);
				// }
				// dateMap.put("Histogram",HistogramMap);

		return dataMap;
	}

	/**
	 * @方法名称: queryPageScroll 
	 * @实现功能:  scroll模式下获取指定分页的数据  
	 * @param indexName	索引名
	 * @param typeName	类型名
	 * @param fileId	文件Id
	 * @param pageSize	每页数量
	 * @param pageNum	页数
	 * @param qb		查询条件
	 * @create by peijd at 2016年11月5日 上午10:07:55
	 */
	public static List<Map<String, Object>> queryPageScroll(Client client, String indexName, String TypeName, int pageSize, int pageNum, QueryBuilder qb) throws Exception {
		Assert.isTrue(pageNum > 0);
		int startNum = (pageNum - 1) * pageSize;
		pageSize = pageSize <= 0 ? Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.pager.num", "20")): pageSize;

		if (null == qb) { // 默认全部检索
			qb = QueryBuilders.matchAllQuery();
		}
		// scroll模式 检索
		SearchResponse response = client.prepareSearch(indexName)
				// .addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC)
				 //设置排序
				.setTypes(TypeName).setSearchType(SearchType.SCAN).setQuery(qb).setScroll(new TimeValue(60000))
				.setSize(pageSize).setFrom(startNum).execute().actionGet();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (SearchHit sh : response.getHits().getHits()) {
			dataList.add(sh.getSource());
		}
		return dataList;
	}

	/** 
	 * @方法名称: insertDataToES 
	 * @实现功能: 将数据插入到es中
	 * @param client
	 * @param json
	 * @return
	 * @create by cdd at 2016年12月2日 下午4:52:51
	 */
	public static String insertDataToES(Client client, Map map, String id, String type) {// 前面传过来json的数据
		try {
			IndexResponse response = client.prepareIndex(DcPropertyUtils.getProperty("elasticSearch.dataObj.index", ""), type, id).setSource(map).get();
			return "上传成功" + response.getId();
		} catch (Exception e) {
			// return "上传失败";
			logger.error("DcEsUtil.insertDataToES()" + e);
		}
		return null;
	}
	
	/**
	 * @方法名称: updateDataToEs
	 * @实现功能: 更新es上的数据
	 * @param client
	 * @param json
	 * @return
	 * @create by cdd at 2016年12月2日 下午4:52:51
	 */
	public static String updateDataToEs(Client client, Map map, String id, String type) {
		try {
			UpdateRequest updateRequest = new UpdateRequest(DcPropertyUtils.getProperty("elasticSearch.dataObj.index", ""), type, id).doc(map);
			client.update(updateRequest).get();
			return updateRequest.id() + "更新成功";
		} catch (Exception e) {
			return "更新失败" + e;
		}

	}

	/**
	 * @方法名称: deleteDataToEs 
	 * @实现功能: 删除es上的数据
	 * @param client
	 * @param json
	 * @return
	 * @create by cdd at 2016年12月5日 下午3:42:51
	 */
	public static String deleteDataToEs(Client client, String id, String type) {
		try {
			DeleteResponse response = client.prepareDelete(DcPropertyUtils.getProperty("elasticSearch.dataObj.index", ""), type, id).get();
			return id + "删除成功";
		} catch (Exception e) {
			return "删除失败" + e;// e.printStackTrace();
		}
	}
	
	/**
	 * @方法名称: getDataToEs
	 * @实现功能: 获取es上的数据
	 * @param client
	 * @param json
	 * @return
	 * @create by cdd at 2016年12月5日 下午2:52:51
	 */
	public static String getDataToEs(Client client, String id, String type) {
		try {
			GetResponse response = client.prepareGet(DcPropertyUtils.getProperty("elasticSearch.dataObj.index", ""), type, id).setOperationThreaded(false).get();
			return "查询成功！" + response.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "查询失败！";
		}
	}

	/**
	 * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * @param indices 索引名称；
	 * @param mappingType 索引类型
	 * @throws Exception
	 */
	public static void createCatalogMapping(Client client, String indices, String mappingType)throws Exception {
		String[] items = new String[]{"cata","s_cata","ss_cata"};
		for(String item : items){
			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject()
					.startObject(mappingType)
					.startObject("properties")
					.startObject(item).field("type","nested").startObject("properties").startObject("cata_id").field("type","string").endObject().endObject()
					.endObject()
					.startObject("objName").field("type", "string")
					.field("analyzer","ik_max_word").field("search_analyzer","ik_max_word").endObject()
					.startObject("objDesc").field("type", "string")
					.field("analyzer","ik_max_word").field("search_analyzer","ik_max_word").endObject()
					.startObject("createDate").field("type","date").field("locale", Locale.CHINA).endObject()
					.startObject("updateDate").field("type","date").field("locale", Locale.CHINA).endObject()
					.endObject()
//					.startObject("_all").field("analyzer","ik").field("search_analyzer","ik").endObject()
					.endObject();
			PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
			PutMappingResponse mappingResponse
					= client.admin().indices().putMapping(mapping).actionGet();
			if (mappingResponse.isAcknowledged()) {
				logger.info("Success modify mapping[" + mappingType+"/"+item + "]!");
			} else {
				logger.error("Fail to modify mapping[" + mappingType+"/"+item + "]!");
			}
		}

	}

	public static void resetIndex()throws Exception {
		String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
		Client client = getEsClient();
		deleteIndex(client,index);
		creataIndex(client,index);
		createCatalogMapping(client,index,"table");
		createCatalogMapping(client,index,"file");
		createCatalogMapping(client,index,"folder");
		createCatalogMapping(client,index,"interface");
	}

	public static void main(String[] args) throws Exception {
		String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
		Client client = getEsClient();
		deleteIndex(client,index);
		creataIndex(client,index);
		createCatalogMapping(client,index,"table");
		createCatalogMapping(client,index,"file");
		createCatalogMapping(client,index,"folder");
		createCatalogMapping(client,index,"interface");
//		deleteById(client,index,"table");
//		deleteIndex(client,"dc_metadata");
//		resetIndex();
	}



}
