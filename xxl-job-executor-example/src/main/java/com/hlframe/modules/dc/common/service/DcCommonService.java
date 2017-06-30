package com.hlframe.modules.dc.common.service;

import java.io.IOException;
import java.util.*;

import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.hlframe.common.service.BaseService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcEsUtil;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;


/**
 * @类名: com.hlframe.modules.dc.common.service.DcCommonService.java 
 * @职责说明: 处理es数据
 * @创建者: cdd
 * @创建时间: 2016年12月8日 上午10:57:23
 */
@Service
@Transactional(readOnly = true)
public class DcCommonService extends BaseService{
	@Autowired
	private DcObjectCataRefService dcObjectCataRefService;
	@Autowired
	private DcObjectMainService objMainService; // obj main;
	@Autowired
	private DcObjectTableService objTableService; // obj Table;
	@Autowired
	private DcObjectFileService objFileService; // obj File/Folder;
	@Autowired
	private DcObjectIntfService objIntfService; // obj Interface;

	public static Client connectClient() {
		Client client = null;
		try {
			 client = DcEsUtil.getClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}
	/**
	 * @方法名称: initLoadDataToEs 
	 * @实现功能: 把表中的数据初始化到es上
	 * @throws Exception
	 * @create by cdd at 2016年12月7日 下午12:00:54
	 * modify 添加ObjCataRef的初始化功能
	 */
	public void initLoadDataToEs() {
		try {
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			Integer es_batchNum = Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.bat.num", "1000"));
			Client client = connectClient();
			//Todo label 属于每一个obj，一个obj可以有多个label， 每次都要添加label，  以下方法中。
			initUploadTablesToEs(client,index,es_batchNum);// 初始化将表信息导入es上
			initUploadInterfaceToEs(client,index,es_batchNum);// 初始化将interface信息导入es上
			initUploadFilesToEs(client,index,es_batchNum);// 初始化将文件信息导入es上
		    initUploadFoldersToEs(client,index,es_batchNum);// 初始化将文件夹信息导入es上  未完成！！！
			logger.info("init es success");
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void initObjCataRefToEs(Client client) throws Exception {
		//全部删除Es 上的refs Todo
		try {
			List<DcObjectCataRef> refs = dcObjectCataRefService.findList(new DcObjectCataRef());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @name: dataUpsert2Es
	 * @funciton:  用于更新obj或者插入obj数据时候，数据上传到ES上去
	 * @param
	 * @return
	 * @Create by lp at 2017/3/16 17:10
	 * @throws
	 */
	public void dataUpsert2Es(Client client,String id,String type,String index) {
		try{
			Map<String, Object> tableMap = null;
			DcObjectMain obj = new DcObjectMain();
			obj.setId(id);
			switch(type){
				case DcObjectMain.OBJ_TYPE_TABLE:
					tableMap = objTableService.getTables4es(obj).get(0);
					break;
				case DcObjectMain.OBJ_TYPE_FILE:
					tableMap = objFileService.getFiles4es(obj).get(0);
					break;
				case DcObjectMain.OBJ_TYPE_FIELD:
					break;
				case DcObjectMain.OBJ_TYPE_INTER:
					tableMap = objIntfService.getInterfaces4es(obj).get(0);
					break;
				case DcObjectMain.OBJ_TYPE_FOLDER:
					//TODO
					tableMap = objFileService.getFolder4es(obj).get(0);
					break;
				case DcObjectMain.OBJ_TYPE_DATABASE:
					//TODO
					break;
			}
			if(tableMap == null){
				return;
			}
			String objType = DcCommonUtils.objType2String(tableMap.get("objType").toString());
			XContentBuilder xContentBuilder = commonFieldProcessToJson(tableMap);
			IndexRequest indexRequest = new IndexRequest(index, objType, id)
					.source(xContentBuilder);
			UpdateRequest updateRequest = new UpdateRequest(index, objType, id)
					.doc(xContentBuilder)
					.upsert(indexRequest);
			client.update(updateRequest).get();
		}catch (Exception e){
			e.printStackTrace();
			logger.error("dataUpsert2ES error", e);
		}
	}
	/**
	 * @方法名称: loadTableToEs 
	 * @实现功能: 将表内容及字段内容传到Es上
	 * @param id
	 * @return
	 * @throws IOException 
	 * @create by cdd at 2016年12月3日 上午10:05:33
	 *  * modify by Primo at 2017/03/16
	 */
	public void loadTableToEs(String id) {
		try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			String type = DcObjectMain.OBJ_TYPE_TABLE;
			dataUpsert2Es(client,id,type,index);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadTableToEs error:",e);
		}
	}
	
	/**
	 * @方法名称: loadFileToEs
	 * @实现功能: 把文件上传到Es上
	 * @param id
	 * @return
	 * @create by cdd at 2016年12月3日 上午10:22:39
	 *  * modify by Primo at 2017/03/16
	 */
	public void loadFileToEs(String id) {
		try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			String type = DcObjectMain.OBJ_TYPE_FILE;
			dataUpsert2Es(client,id,type,index);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadFileToEs error:",e);
		}
	}

		
	/**
	 * @方法名称: loadFolderToEs
	 * @实现功能: 把文件夹信息上传到Es上
	 * @param id
	 * @return
	 * @create by cdd at 2016年12月3日 上午10:21:54
	 *  * modify by Primo at 2017/03/16
	 */
	public void loadFolderToEs(String id) {
		try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			String type = DcObjectMain.OBJ_TYPE_FOLDER;
			dataUpsert2Es(client,id,type,index);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadFolderToEs error:",e);
		}
	}


	/**
	 * @方法名称: loadInterfaceToEs
	 * @实现功能: 接口数据同步到ES
	 * @params  [id]
	 * @return  void
	 * @create by peijd at 2017/4/17 20:14
	 */
	public void loadInterfaceToEs(String id) {
		try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			String type = DcObjectMain.OBJ_TYPE_INTER;
			dataUpsert2Es(client,id,type,index);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadInterfaceToEs error:",e);
		}
	}


	/**
	 * @方法名称: loadLabelToEs
	 * @实现功能: 把标签信息上传到Es上
	 * @param id
	 * @return
	 * @throws Exception 
	 * @create by cdd at 2016年12月3日 上午10:08:58
	 * modify by Primo at 2017/03/16
	 */
	@Deprecated
	public void loadLabelToEs(String id){
		try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			String type = DcPropertyUtils.getProperty("elasticSearch.dataObj.labelType", "label");
			dataUpsert2Es(client,id,type,index);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadLabelToEs error:",e);
		}
	}


	/**
	 * @方法名称: SearchField 
	 * @实现功能: 查询字段信息
	 * @param id
	 * @return
	 * @create by cdd at 2016年12月7日 下午6:47:55
	 * TODO 此方法需要重写
	 */
	public static String SearchField(String id) {
		Assert.hasText(id);//判断id是否为空
		//List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();//这个dataList，是用来存放某表的字段信息的
		
		String fieldSql = "select * from dc_obj_field where belong2_id='" + id + "'";//此sql语句的意思是根据表的id得到其字段信息
		List<Map<String, Object>> fieldList = DcCommonUtils.queryDataBySql(fieldSql);//得到字段信息
		
		if (CollectionUtils.isNotEmpty(fieldList)) {//判断此字段列表是否为空
			//Map<String, Object> fieldMap = new HashMap<String, Object>();
			//for (int i = 0; i < fieldList.size(); i++) {
			//	fieldMap = fieldList.get(i);
			//}
			//dataList.add(fieldMap);
			return fieldList.toString();
		} else {//空则返回0
			return "0";
		}
	}
	
	/**
	 * @方法名称: findCategory 
	 * @实现功能: 根据Id发现其所在的业务分类
	 * @param id
	 * @return
	 * @create by cdd at 2017年1月24日 上午10:57:06
	 */
	public static List<Map<String, Object>> findCategory(String id){
		String sql = "SELECT m.id, GROUP_CONCAT(s.cat1 SEPARATOR ',')AS cat1,GROUP_CONCAT(s.cat2 SEPARATOR ',')AS cat2,"
				+ " GROUP_CONCAT(s.cat3 SEPARATOR ',')AS cat3 FROM `dc_obj_main` m "
				+ "LEFT JOIN `dc_obj_cata_ref` r ON m.id=r.obj_id"
				+ " LEFT JOIN (SELECT dt.id,dt.id AS cat1,NULL AS cat2,NULL AS cat3 FROM dc_cata_detail dt"
				+ " INNER JOIN dc_cata_item l0 ON dt.parent_id=l0.id UNION ALL "
				+ " SELECT dt.id,l1.id AS cat1,dt.id AS cat2,NULL AS cat3 FROM dc_cata_detail dt"
				+ " INNER JOIN dc_cata_detail l1 ON dt.PARENT_ID = l1.id "
				+ " INNER JOIN dc_cata_item l0 ON l1.parent_id=l0.id "
				+ "UNION ALL "
				+ " SELECT dt.id,l1.id AS cat1,l2.id AS cat2,dt.id AS cat3 FROM dc_cata_detail dt"
				+ " INNER JOIN dc_cata_detail l2 ON dt.PARENT_ID = l2.id "
				+ " INNER JOIN dc_cata_detail l1 ON l2.PARENT_ID = l1.id "
				+ "INNER JOIN dc_cata_item l0 ON l1.parent_id=l0.id ) s ON r.cata_id=s.id"
				+ "  where m.DEL_FLAG ='0'and m.id='"+id+"'"
				+ "GROUP BY m.id";
		List<Map<String, Object>> dataList = DcCommonUtils.queryDataBySql(sql);//queryDataBySql动态执行sql语句
		if(CollectionUtils.isNotEmpty(dataList)){//判断dataList是否为空
			return dataList;
		}else{
			return null;//为空，返回null
		}

	}
	/**
	 * @name: initUploadInterfaceToEs
	 * @funciton: initialize
	 * @param client
	 * @return
	 * @Create by lp at 2017/3/8 14:06
	 * @throws
	 */
	private void initUploadInterfaceToEs(Client client,String index,Integer es_batchNum) throws Exception {
		try {
			String type = DcPropertyUtils.getProperty("elasticSearch.dataObj.interType", "interface");
			List<Map<String, Object>> interface4es = objIntfService.getInterfaces4es(null);
			if (CollectionUtils.isNotEmpty(interface4es)) {// 判断tableList里面是否有数据
				//delete all files
				DcEsUtil.deleteType(client, index, type);

				int count = 0;
				BulkRequestBuilder bulkRequest = client.prepareBulk();// 批处理数据
				for (int i = 0; i < interface4es.size(); i++) {
					Map<String, Object> tableMap = interface4es.get(i);

//				commonFieldProcess(tableMap);
					XContentBuilder context = commonFieldProcessToJson(tableMap);
					bulkRequest.add(
							client.prepareIndex(index, type, tableMap.get("id").toString()).setSource(context)
					);
					count++;
					if (count % es_batchNum == 0) {
						bulkRequest.execute().actionGet();
					}

				}
				//flush bucket
				if (bulkRequest.numberOfActions() > 0) {
					bulkRequest.execute().actionGet();
				}
				System.out.println("插入数据量为:" + count);
				bulkRequest.execute().actionGet();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("initUploadInterfaceToEs error", e);
		}
	}
    /**
     * @name: initUploadFoldersToEs
     * @funciton: initialize
     * @param client
     * @return
     * @Create by lp at 2017/3/8 14:06 TODO
     * @throws
     */
    private void initUploadFoldersToEs(Client client,String index,Integer es_batchNum){
    	try {
			String type = DcPropertyUtils.getProperty("elasticSearch.dataObj.folderType", "folder");
			List<Map<String, Object>> files4es = objFileService.getFiles4es(null);
			if (CollectionUtils.isNotEmpty(files4es)) {// 判断tableList里面是否有数据
				//delete all files
				DcEsUtil.deleteType(client, index, type);

				int count = 0;
				BulkRequestBuilder bulkRequest = client.prepareBulk();// 批处理数据
				for (int i = 0; i < files4es.size(); i++) {
					Map<String, Object> tableMap = files4es.get(i);

//                commonFieldProcess(tableMap);
					XContentBuilder context = commonFieldProcessToJson(tableMap);
					bulkRequest.add(
							client.prepareIndex(index, type, tableMap.get("id").toString()).setSource(context)
					);
					count++;
					if (count % es_batchNum == 0) {
						bulkRequest.execute().actionGet();
					}

				}
				//flush bucket
				if (bulkRequest.numberOfActions() > 0) {
					bulkRequest.execute().actionGet();
				}
				System.out.println("插入数据量为:" + count);
				bulkRequest.execute().actionGet();
			}
		}catch(Exception e){
    		e.printStackTrace();
			logger.error("initUploadFoldersToEs error", e);
		}
    }
	/**
	 * @name: initUploadFilesToEs
	 * @funciton: initialize
	 * @param client
	 * @return
	 * @Create by lp at 2017/3/8 14:06
	 * @throws
	 */
	private void initUploadFilesToEs(Client client,String index,Integer es_batchNum){
		try {
			String type = DcPropertyUtils.getProperty("elasticSearch.dataObj.fileType", "file");
			List<Map<String, Object>> files4es = objFileService.getFiles4es(null);
			if (CollectionUtils.isNotEmpty(files4es)) {// 判断tableList里面是否有数据
				//delete all files
				DcEsUtil.deleteType(client, index, type);

				int count = 0;
				BulkRequestBuilder bulkRequest = client.prepareBulk();// 批处理数据
				for (int i = 0; i < files4es.size(); i++) {
					Map<String, Object> tableMap = files4es.get(i);

//				commonFieldProcess(tableMap);
					XContentBuilder context = commonFieldProcessToJson(tableMap);
					bulkRequest.add(
						client.prepareIndex(index, type, tableMap.get("id").toString()).setSource(context)
					);
					count++;
					if (count % es_batchNum == 0) {
						bulkRequest.execute().actionGet();
					}

				}
				//flush bucket
				if (bulkRequest.numberOfActions() > 0) {
					bulkRequest.execute().actionGet();
				}

				System.out.println("插入数据量为:" + count);
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("initUploadFilesToEs error", e);
		}
	}
	/**
	 * @name: initUploadTablesToEs
	 * @funciton: initialize
	 * @param client
	 * @return
	 * @Create by lp at 2017/3/8 14:06
	 * @throws
	 */
	private void initUploadTablesToEs(Client client,String index,Integer es_batchNum) {
		try {
			String type = DcPropertyUtils.getProperty("elasticSearch.dataObj.tableType", "table");
			List<Map<String, Object>> tables4es = objTableService.getTables4es(null);
			if (CollectionUtils.isNotEmpty(tables4es)) {// 判断tableList里面是否有数据
				//delete all
				DcEsUtil.deleteType(client, index, type);

				int count = 0;
				BulkRequestBuilder bulkRequest = client.prepareBulk();// 批处理数据
				for (int i = 0; i < tables4es.size(); i++) {
					Map<String, Object> tableMap = tables4es.get(i);

//				commonFieldProcess(tableMap);
					XContentBuilder context = commonFieldProcessToJson(tableMap);
					bulkRequest.add(
							client.prepareIndex(index, type, tableMap.get("id").toString()).setSource(context)
					);

//				printObj(tableMap);
					count++;
					if (count % es_batchNum == 0) {
						bulkRequest.execute().actionGet();
					}

				}
				//flush bucket
				if (bulkRequest.numberOfActions() > 0) {
					bulkRequest.execute().actionGet();
				}
				System.out.println("插入数据量为:" + count);
				bulkRequest.execute().actionGet();

			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("initUploadTablesToEs error", e);
		}
	}

	private XContentBuilder commonFieldProcessToJson(Map<String, Object> tableMap) {
		String[] items = new String[]{"cata","s_cata","ss_cata"};
		try {
			XContentBuilder resultJson = XContentFactory.jsonBuilder().startObject();
			//add labelData to obj  需要改造， 本方案上传的只是字符串，而非json对象
			if (DcStringUtils.isNotNull(tableMap.get("id"))) {//判断id是否为空，空则赋值为空
				tableMap.put("fieldData", SearchField(tableMap.get("id").toString()));// 获取表的相关字段信息
			} else {//若id为空，则给字段信息赋值为空
				tableMap.put("fieldData", "");
			}
			Iterator<Map.Entry<String,Object>> i$ = tableMap.entrySet().iterator();
			while(i$.hasNext()){
				Map.Entry<String,Object> entry = i$.next();
				Object value = entry.getValue();
				String key = entry.getKey();
				if("catalogsPath".equals(key)){
					if(value == null){
						for(String item : items){
							resultJson.startArray(item).endArray();
						}
					}else{
						String[] catalog = value.toString().split(";");

						List<String[]> list = new ArrayList<>();
						for(int i=0;i<catalog.length;i++){
							list.add(catalog[i].split(","));
						}
						for(int i=0;i<items.length;i++){
							resultJson.startArray(items[i]);
							for(int j=0;j<list.size();j++){
								if(list.get(j).length>i && null != list.get(j)[i] && !"".equals(list.get(j)[i])){
//									System.out.println(list.get(j)[i]);
									resultJson.startObject().field("cata_id",list.get(j)[i]).endObject();
								}
							}
							resultJson.endArray();
						}
						/*
						Map<String,List<String>> maps = new HashMap<>();
						for(String item : items){
							List<String> arr = new ArrayList<>();
							maps.put(item,arr);
						}
						for(String cataPath : catalog){
							int len_origin = cataPath.length();
							int len_replace = cataPath.replace(",","").length();
							if(len_origin-len_replace == 1){
								maps.get("cata").add(cataPath);
							}else if(len_origin-len_replace == 2){
								maps.get("s_cata").add(cataPath);
							}else if(len_origin-len_replace == 3){
								maps.get("ss_cata").add(cataPath);
							}
						}
						for(int i=0;i<items.length;i++){
							List<String> list = maps.get(items[i]);

							resultJson.startArray(items[i]);
							for(int j=0;j<list.size();j++){
								if(null != list.get(j) && !"".equals(list.get(j))){
									resultJson.startObject().field("cata_id",list.get(j)).endObject();
								}
							}
							resultJson.endArray();
						}
						*/
					}
				}else {
					resultJson.field(key, value);
				}
			}
			resultJson.endObject();
			return resultJson;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("commonFieldProcessToJson error", e);
		}
		return null;
	}
	/**
	 * @name:  commonFieldProcess
	 * @funciton: 处理公共的信息字段
	 * @param
	 * @return
	 * @Create by lp at 2017/3/9 13:14
	 * @throws
	 */
	@Deprecated
	private void commonFieldProcess(Map<String, Object> tableMap) {
		//add labelData to obj
		if (DcStringUtils.isNotNull(tableMap.get("id"))) {//判断id是否为空，空则赋值为空
			tableMap.put("fieldData", SearchField(tableMap.get("id").toString()));// 获取表的相关字段信息
		} else {//若id为空，则给字段信息赋值为空
			tableMap.put("fieldData", "");
		}
		//unpack the catalog data

		List<List<String>> catalogs = unpackCatalogData(tableMap.get("catalogsPath"));
//		List<List<Map<String, Object>>> catalogs = changeListToArray(catalogs_origin);
		if (CollectionUtils.isNotEmpty(catalogs)) {//判断catagoryList是否为空，空则赋值为空
			tableMap.put("catagory",catalogs.get(0));
			tableMap.put("s_cat", catalogs.get(1));
			tableMap.put("ss_cat", catalogs.get(2));
		} else {//若catagoryList为空，则全部赋值为空
			tableMap.put("catagory", new ArrayList<>());
			tableMap.put("s_cat", new ArrayList<>());
			tableMap.put("ss_cat", new ArrayList<>());
		}
		//delete the key of catalogsPath
		tableMap.remove("catalogsPath");
		//对更新时间的操作
		if (StringUtils.isBlank(String.valueOf(tableMap.get("updateDate")))) {//若更新时间为空，则赋值为空
			tableMap.put("updateDate","");
		}
	}

	private List<List<Map<String, Object>>> changeListToArray(List<List<String>> catalogs_origin) {
		List<List<Map<String, Object>>> result = new ArrayList<>(3);
		for(List<String> obj : catalogs_origin){
			List<Map<String,Object>> tt =  new ArrayList<>();
			for(int i=0;i<obj.size();i++){
				Map<String,Object> o = new HashMap<String,Object>();
				o.put("cata_id",obj.get(i));
				tt.add(o);
			}
			result.add(tt);
		}
		return result;
	}

	/**
	 * @name: unpackCatalogData
	 * @funciton: 解析类型
	 * @param
	 * @return
	 * @Create by lp at 2017/3/8 14:15
	 * @throws
	 */
	@Deprecated
	private List<List<String>> unpackCatalogData(Object catalogsPath) {
		List<List<String>> catalogs = new ArrayList<>();
		if(catalogsPath == null){
			for(int i=0;i<3;i++){
				List<String> s = new ArrayList<>();
				catalogs.add(s);
			}
			return catalogs;
		}
		String[] catalog = catalogsPath.toString().split(";");
		List<String[]> list = new ArrayList<>();
		for(int i=0;i<catalog.length;i++){
			list.add(catalog[i].split(","));
		}
		for(int i=0;i<3;i++){
			List<String> s = new ArrayList<>();
			for(int j =0;j<list.get(i).length;j++){
				if(list.get(j).length>i && null != list.get(j)[i] && !"".equals(list.get(j)[i])){
					s.add(list.get(i)[j]);
				}
			}
			catalogs.add(s);
		}
		return catalogs;
	}

    /**
     * @name: unpackCatalogData
     * @funciton: 解析类型
     * @param
     * @return
     * @Create by lp at 2017/3/8 14:15
     * @throws
     */
    @Deprecated
    private List<List<Map<String, Object>>> unpackCatalogDataJson(Object catalogsPath) {
        List<List<Map<String, Object>>> catalogs = new ArrayList<>();
        if(catalogsPath == null){
            for(int i=0;i<3;i++){
                List<Map<String, Object>> s = new ArrayList<>();
                Map<String, Object> cata = new HashMap<>();
                s.add(cata);
                catalogs.add(s);
            }
            return catalogs;
        }
        String[] catalog = catalogsPath.toString().split(";");
        List<String[]> list = new ArrayList<>();
        for(int i=0;i<catalog.length;i++){
            list.add(catalog[i].split(","));
        }
        for(int i=0;i<3;i++){
            List<Map<String, Object>> s = new ArrayList<>();
            for(int j =0;j<list.size();j++){
                String[] cataDetail = list.get(j);
                if(cataDetail.length>i && null != cataDetail[i] && !"".equals(cataDetail[i])){
                    Map<String, Object> cata = new HashMap<>();
                    cata.put("id",cataDetail[i]);
                    s.add(cata);
                }
            }
            catalogs.add(s);
        }
        return catalogs;
    }

    /**
     *
     * @方法名称: deleteEsByIndex
     * @实现功能: 根据索引删除数据
     * @param id
     * @param type
     * @create by hgw at 2017年4月15日 上午11:07:34
     */
    public void  deleteEsByIndex(String id,String type){
    	try {
			Client client = connectClient();
			String index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
			DcEsUtil.deleteDataToEs(client,id,type);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("loadLabelToEs error:",e);
		}
    }

}
