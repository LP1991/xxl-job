package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.BaseEntity;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.dataprocess.dao.HistoryDbNameDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveDatabase;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveField;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveTable;
import com.hlframe.modules.dc.dataprocess.entity.HistoryDbName;
import com.hlframe.modules.dc.metadata.dao.DcObjectFieldDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectTableDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcObjectTable;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.metadata.service.DcObjectMainService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;
import com.hlframe.modules.sys.utils.UserUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcQueryHiveService.java 
 * @职责说明: 对sql语句的操作
 * @创建者: cdd
 * @创建时间: 2017年1月24日 上午11:37:42
 */
@Service
@Transactional(readOnly = true)
public class DcQueryHiveService {

	@Autowired
	private DcHiveTableService dcHiveTableService;
	@Autowired
	private DcHiveFieldService dcHiveFieldService;
	
	@Autowired
	private HistoryDbNameDao historyDbNameDao;
	
	@Autowired
	private DcMetadataStroeService dcMetadataStroeService;
	
	@Autowired
	private DcObjectTableDao dcObjectTableDao;
	
	@Autowired
	private DcObjectMainDao dcObjectMainDao;
	
	@Autowired
	private DcObjectFieldDao dcObjectFieldDao;
	
	@Autowired
	private DcHiveDatabaseService dcHiveDatabaseService;
	
	@Autowired
	private DcObjectMainService dcObjectMainService;

    static Client client = Client.create();
    
	protected Logger logger = LoggerFactory.getLogger(getClass());//存储日志
	
    /**
     * @方法名称: getAllHiveTableNames 
     * @实现功能: 获取某个表空间下的所有表名
     * @param dbName
     * @return
     * @create by cdd at 2017年1月17日 上午9:49:52
     */
    @Transactional(readOnly = false)
	public List<Map<String, Object>> getAllHiveTableNames(String dbName) {
		String ab = useDB(dbName);// 使用数据库 use databaseName
		logger.debug(ab);
		List<Map<String, Object>> tableList = showTables();//显示某个数据库的所有数据表名;
		return tableList;//返回数据表名列表
	}
	
    /**
     * @方法名称: showTables 
     * @实现功能: 获得数据库中的所有表名
     * @return
     * @create by cdd at 2017年1月17日 上午9:50:00
     */
	@Transactional(readOnly = false)
	public List<Map<String, Object>> showTables() {
		String sql = "show tables";//构建sql语句
		return executeQuerySql(sql);
	}

	/**
	 * @方法名称: useDB 
	 * @实现功能: 使用数据库
	 * @param dbName
	 * @return
	 * @create by cdd at 2017年1月17日 上午8:58:06
	 */
	@Transactional(readOnly = false)
	public String useDB(String dbName) {// 使用数据库
		Assert.hasText(dbName);
		return executeSql("use "+dbName);
	}

	/**
	 * @方法名称: runSql 
	 * @实现功能: 执行sql语句
	 * @param sql
	 * @param dbName
	 * @return
	 * @create by cdd at 2017年1月17日 下午6:56:03
	 * @update hgw
	 */
	@Transactional(readOnly = false)
	public List<Map<String, Object>> runSql(String sql, String dbName) {
		Assert.hasText(sql);
		Assert.hasText(dbName);
		//sql数据信息插入
		HistoryDbName historyDbName = new HistoryDbName();
		historyDbName.setHSql(sql);
		historyDbName.setHDbName(dbName);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if (StringUtils.indexOfIgnoreCase(sql,"create") >= 0 || StringUtils.indexOfIgnoreCase(sql,"use") >= 0 || StringUtils.indexOfIgnoreCase(sql,"drop") >= 0 || StringUtils.indexOfIgnoreCase(sql,"load") >= 0|| StringUtils.indexOfIgnoreCase(sql,"delete") >= 0|| StringUtils.indexOfIgnoreCase(sql,"insert") >= 0|| StringUtils.indexOfIgnoreCase(sql,"update") >= 0) {// 对创建表，使用数据库，删除数据库语句使用
			String result = executeSql(sql);//此部分不需要返回数据，只需要返回执行成功与否
			if("success".equals(result)){
				//成功之后才有表信息
				ExeSql(sql,dbName);
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("result", result);
			dataList.add(dataMap);
			historyDbName.setHType(result);
		} else if (StringUtils.indexOfIgnoreCase(sql,"select") >= 0 || StringUtils.indexOfIgnoreCase(sql,"desc") >= 0 || StringUtils.indexOfIgnoreCase(sql,"show") >= 0) {// 对有数据返回的操作。有select，desc，show
			//sql是否包含limit
			if(StringUtils.indexOfIgnoreCase(sql,"select") >= 0){
				if(!sql.toUpperCase().contains("LIMIT")){
					String count= DcPropertyUtils.getProperty("queryHive.result.count");
					sql += " limit "+count;
				}
			}
			long startTime=System.currentTimeMillis();
			dataList = executeQuerySql(sql);//返回数据
			long endTime=System.currentTimeMillis();
			float excTime=(float)(endTime-startTime)/1000;
		   logger.debug("executeQuery执行时间："+excTime+"s");
			for(int i=0;i<dataList.size();i++){
				Map<String,Object> map = new HashMap<String,Object>();
				if(map.get("result")!=null){
					historyDbName.setHType((String)map.get("result"));
					break;
				}else{
					historyDbName.setHType("success");
					break;
				}
			}
		}else{
			//其他情况
			String result = executeSql(sql);//此部分不需要返回数据，只需要返回执行成功与否
			if("success".equals(result)){
				//成功之后才有表信息
				ExeSql(sql,dbName);
			}
			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("result", result);
			dataList.add(dataMap);
			historyDbName.setHType(result);
		}
		historyDbName.setHDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		historyDbName.setHUser(UserUtils.getUser().getName());
		historyDbNameDao.insert(historyDbName);
		return dataList;
	}

	/**
	 * @方法名称: executeQuerySql
	 * @实现功能: 返回List<Map<String,Object>>数据
	 * @param sql
	 * @return
	 * @create by cdd at 2017年1月18日 下午2:06:43
	 * @update hgw
	 */
	public List<Map<String, Object>> executeQuerySql(String sql) {
		Assert.hasText(sql);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			URI u = new URI(DcPropertyUtils.getProperty("queryHive.restServer.url"));
			//URI u = new URI("http://localhost:9050/jerseyHive/rest/dcHiveQuery/executeQuerySql");
			WebResource resource = client.resource(u);
			// 构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			params.add("metaSql", sql);
			long startTime=System.currentTimeMillis();
			String result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
			long endTime=System.currentTimeMillis();
			float excTime=(float)(endTime-startTime)/1000;
			logger.debug("result执行时间："+excTime+"s");
			String data = result.replace("\"[", "[").replace("]\"", " ]").replace("\"{", "{").replace("}\"", "}").replace("\\", "");//对result进行变化
			data = jsonString(data);
			ObjectMapper mapper = new ObjectMapper();
		
			Map<String, Object> map = mapper.readValue(data, Map.class);//将data转成Json格式
			List<Map<String, Object>> jsonList = null;
			if("T".equals(map.get("resultFlag"))){
				if(map.get("std_msg")==""){
					jsonList = new ArrayList<Map<String,Object>>();
				}else{
					jsonList = (List<Map<String, Object>>) map.get("std_msg");//获取到result里面的数据，强制转化成List<Map<String, String>>格式
				}
			}
			if(CollectionUtils.isEmpty(jsonList)){
				Map<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("result", map.get("err_msg"));
				dataList.add(dataMap);
				return dataList;
			}
			//将其中的map重新装到另一个list中
				dataList.addAll(jsonList);
		} catch (Exception e) {//uri异常处理
			logger.error("-->executeQuerySql: ", e);
			throw new ServiceException(e);
		}
		return dataList;
	}
	
	/**
	 * @方法名称: executeSql 
	 * @实现功能: 不返回List<Map<String,Object>数据
	 * @param sql
	 * @return 返回的是成功与否的标志
	 * @create by cdd at 2017年1月18日 上午10:58:34
	 */
	public String executeSql(String sql) {
		Assert.hasText(sql);
		String result = "";
		try {
			// restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("executeHive.restServer.url"));
			//URI u = new URI("http://localhost:9050/jerseyHive/rest/dcHiveQuery/executeSql");
			WebResource resource = client.resource(u);
			// 构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			params.add("metaSql", sql);
			result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
		} catch (Exception e) {
			logger.error("-->executeSql: ", e);
			throw new ServiceException(e);
		}
		return result;
	}

	/**
	 * @方法名称: hiveDS 
	 * @实现功能: 同步hive中数据
	 * @create by cdd at 2017年1月18日 下午3:09:41
	 */
	@Transactional(readOnly = false)
	public void hiveDS() {
		// 对dc_hive_database的操作
		String result0 = updateHiveDataBase();//同步dc_hive_database hive数据库里面数据信息
		logger.debug(result0);
		
		// 对表的操作
	   String result1 = updateHiveTable();//同步dc_hive_table hive数据表里面数据信息
	   logger.debug(result1);
		 //字段操作
			// 对字段的操作
			//String result2 = updateHiveField();//同步dc_hive_field hive字段表里面数据信息
		 // String result2 = updateHiveField(null);
		//	logger.debug(result2);
	}

/*	*//**
	 * @方法名称: updateHiveField
	 * @实现功能: 同步数据表字段数据
	 * @create by cdd at 2017年1月18日 下午7:13:02
	 *//*
	@Transactional(readOnly = false)
	public String updateHiveField() {
		List<Map<String, Object>> tableList = getHiveData("SELECT * FROM TBLS ORDER BY DB_ID ASC"); // 获取tbls里面的所有数据
		try {
			if (CollectionUtils.isNotEmpty(tableList)) {// 判断tableList是否为空，不为空走下面
				for (int i = 0; i < tableList.size(); i++) {//循环tableList
					int tblId = Integer.parseInt(tableList.get(i).get("TBL_ID").toString()) + 1;// 获取到数据表的id
					String dbId = tableList.get(i).get("DB_ID").toString();//获取数据库Id
					String tableName = tableList.get(i).get("TBL_NAME").toString();//获取数据表名
					
					String dbName = getDBName(dbId);//根据数据库Id,获得数据库名
					
					String idsql = "select ID from dc_hive_table where TABLE_NAME='" + tableName + "' and TABLE_SPACE='"+ dbName + "'";
					String belong2Id = DcCommonUtils.getFieldByManyField(idsql);//根据表名和数据库名，查看在dc_hive_table中的id
					
					String fieldsql = "SELECT * FROM COLUMNS_V2 where CD_ID='" + tblId + "" + "'";//根据cd_id找到对应的字段信息
					List<Map<String, Object>> fieldList = getHiveData(fieldsql);
					
					List<DcHiveField> dataList = new ArrayList<DcHiveField>();
					if (CollectionUtils.isNotEmpty(fieldList)) {//判断字段列表是否为空，若为空则不进行操作
						for (int j = 0; j < fieldList.size(); j++) {//循环fieldList
							DcHiveField dcHiveField = new DcHiveField();//新建一个DcHiveField对象
							dcHiveField.setId(IdGen.uuid());//设置dcHiveField的id
							dcHiveField.setBelong2Id(belong2Id);//设置dcHiveField的belong2Id
							dcHiveField.setFieldName(fieldList.get(j).get("COLUMN_NAME").toString());;//设置dcHiveField的fieldName
							
							if (DcStringUtils.isNotNull(fieldList.get(j).get("COMMENT"))) {//判断comment是否为空,空则赋值为空
								dcHiveField.setFieldDesc(fieldList.get(j).get("COMMENT").toString());//设置dcHiveField的fieldDesc
							} else {
								dcHiveField.setFieldDesc("");//设置dcHiveField的fieldDesc
							}

							dcHiveField.setFieldType(fieldList.get(j).get("TYPE_NAME").toString());//设置dcHiveField的fieldType
							dcHiveField.setSortNum(Integer.parseInt(fieldList.get(j).get("INTEGER_IDX").toString())*10 );//设置dcHiveField的sortNum
							dcHiveField.setIsKey("0");//设置dcHiveField的是否为主键

							dataList.add(dcHiveField);//将对象驾到dataList中
						}
						//System.out.println(dataList);
						
						DcCommonUtils.delDataByField("BELONG2_ID", belong2Id, "dc_hive_field");// 清空在dc_hive_field中，所有BELONG2_ID为belong2Id值得所有数据
						// 批量插入数据到dc_hive_field中
						dcHiveFieldService.batchInsert(dataList);
						//插入元数据
					}
				}
			} else {// 若tableList为空，则写入日志中
				logger.debug("未获取到hive表字段, 请联系管理员!"); 
			}
		} catch (Exception e) {
			logger.error("-->updateHiveField: ", e.getMessage());
			throw new ServiceException(e);
		}
		return "success";
	}*/

	/**
	 * @方法名称: updateHiveField
	 * @实现功能: 同步数据表字段数据
	 * @create by cdd at 2017年1月18日 下午7:13:02
	 */
	@Transactional(readOnly = false)
	public String updateHiveField(String table) {
		List<Map<String, Object>> tableList = null;
		if(table==null){
			tableList = getHiveData("SELECT * FROM TBLS ORDER BY DB_ID ASC"); // 获取tbls里面的所有数据
		}else{
			tableList = getHiveData("SELECT * FROM TBLS WHERE TABLE="+table+"  ORDER BY DB_ID ASC"); // 获取tbls里面的所有数据
		}
		try {
			if (CollectionUtils.isNotEmpty(tableList)) {// 判断tableList是否为空，不为空走下面
				for (int i = 0; i < tableList.size(); i++) {//循环tableList     //表
					int tblId = Integer.parseInt(tableList.get(i).get("TBL_ID").toString()) + 1;// 获取到数据表的id
					String dbId = tableList.get(i).get("DB_ID").toString();//获取数据库Id
					String tableName = tableList.get(i).get("TBL_NAME").toString();//获取数据表名
					
					String dbName = getDBName(dbId);//根据数据库Id,获得数据库名
					
					String idsql = "select ID from dc_hive_table where TABLE_NAME='" + tableName + "' and TABLE_SPACE='"+ dbName + "'";
//					List<Map<String, Object>> map = DcCommonUtils.queryDataBySql(idsql);//根据表名和数据库名，查看在dc_hive_table中的id
					String belong2Id = DcCommonUtils.getFieldByManyField(idsql);//根据表名和数据库名，查看在dc_hive_table中的id
//					String belong2Id="e759ccf7758140f69d531dbe7c58e23f";
					String fieldsql = "SELECT * FROM COLUMNS_V2 where CD_ID='" + tblId + "" + "'";//根据cd_id找到对应的字段信息
					List<Map<String, Object>> fieldList = getHiveData(fieldsql);
					
					List<DcHiveField> dataList = new ArrayList<DcHiveField>();
					List<DcObjectField> dataList2 = new ArrayList<DcObjectField>();
//					List<DcObjectMain> dataList3 = new ArrayList<DcObjectMain>();
					if (CollectionUtils.isNotEmpty(fieldList)) {//判断字段列表是否为空，若为空则不进行操作
						for (int j = 0; j < fieldList.size(); j++) {//循环fieldList
							DcHiveField dcHiveField = new DcHiveField();//新建一个DcHiveField对象
							dcHiveField.setId(IdGen.uuid());//设置dcHiveField的id
							dcHiveField.setBelong2Id(belong2Id);//设置dcHiveField的belong2Id
							dcHiveField.setFieldName(fieldList.get(j).get("COLUMN_NAME").toString());;//设置dcHiveField的fieldName
							
							if (DcStringUtils.isNotNull(fieldList.get(j).get("COMMENT"))) {//判断comment是否为空,空则赋值为空
								dcHiveField.setFieldDesc(fieldList.get(j).get("COMMENT").toString());//设置dcHiveField的fieldDesc
							} else {
								dcHiveField.setFieldDesc("");//设置dcHiveField的fieldDesc
							}

							dcHiveField.setFieldType(fieldList.get(j).get("TYPE_NAME").toString());//设置dcHiveField的fieldType
							dcHiveField.setSortNum(Integer.parseInt(fieldList.get(j).get("INTEGER_IDX").toString())*10 );//设置dcHiveField的sortNum
							dcHiveField.setIsKey("0");//设置dcHiveField的是否为主键

							dataList.add(dcHiveField);//将对象驾到dataList中
							
							DcObjectField dcObjectField =new DcObjectField();
							dcObjectField.setId(dcHiveField.getId());
							dcObjectField.setBelong2Id(belong2Id);
							dcObjectField.setFieldName(fieldList.get(j).get("COLUMN_NAME").toString());
							if (DcStringUtils.isNotNull(fieldList.get(j).get("COMMENT"))) {//判断comment是否为空,空则赋值为空
								dcObjectField.setFieldDesc(fieldList.get(j).get("COMMENT").toString());//设置dcHiveField的fieldDesc
							} else {
								dcObjectField.setFieldDesc("");//设置dcHiveField的fieldDesc
							}
							dcObjectField.setFieldType(fieldList.get(j).get("TYPE_NAME").toString());
							dcObjectField.setSortNum(Integer.parseInt(fieldList.get(j).get("INTEGER_IDX").toString())*10 );//设置dcHiveField的sortNum
							dcObjectField.setIsKey(0);//设置dcHiveField的是否为主键
							dataList2.add(dcObjectField);
						}
						//System.out.println(dataList);
						List<DcObjectField> filedid = dcObjectFieldDao.getDB2Id(belong2Id);
						/*清除dc_obj_main表*/
						if (CollectionUtils.isNotEmpty(filedid)) {//判断字段列表是否为空，若为空则不进行操作
							for (int k = 0; k < filedid.size(); k++) {
								DcObjectMain delMain = new DcObjectMain();
								delMain.setId(filedid.get(k).getId());
								dcObjectMainDao.delete(delMain);
							}
						}
						DcCommonUtils.delDataByField("BELONG2_ID", belong2Id, "dc_hive_field");// 清空在dc_hive_field中，所有BELONG2_ID为belong2Id值得所有数据
						// 批量插入数据到dc_hive_field中
						dcHiveFieldService.batchInsert(dataList);
						//清除字段元数据
						DcCommonUtils.delDataByField("BELONG2_ID", belong2Id, "dc_obj_field");// 清空在dc_obj_field中，所有BELONG2_ID为belong2Id值得所有数据
						// 批量插入数据到dc_obj_field中
						dcObjectFieldDao.batchInsert(dataList2);
					}
				}
			} else {// 若tableList为空，则写入日志中
				logger.debug("未获取到hive表字段, 请联系管理员!"); 
			}
		} catch (Exception e) {
			logger.error("-->updateHiveField: ", e.getMessage());
			throw new ServiceException(e);
		}
		return "success";
	}

	/**
	 * @方法名称: updateHiveTable 
	 * @实现功能: 同步数据表数据
	 * @return 
	 * @create by cdd at 2017年1月19日 上午11:08:40
	 */
	@Transactional(readOnly = false)
	public String updateHiveTable() {
		List<Map<String, Object>> dbList = getHiveData("select * from DBS");// 获得hive里的数据库信息
		try {
			if (CollectionUtils.isNotEmpty(dbList)) {
				for (int i = 0; i < dbList.size(); i++) {
					String dbId = dbList.get(i).get("DB_ID").toString();//得到数据库Id
					String dbName = dbList.get(i).get("NAME").toString();//的到数据库名
					
					String tbsql = "SELECT *,FROM_UNIXTIME(create_time) as createTime FROM TBLS where DB_ID='" + dbId+ "' ORDER BY TBL_NAME ASC";
					List<Map<String, Object>> tableList = getHiveData(tbsql);// 根据数据库Id，找到TBLS里面有数据表
					List<DcHiveTable> dataList = new ArrayList<DcHiveTable>();
					List<DcObjectMain> dataList2 = new ArrayList<DcObjectMain>();//DcObjectMain
					List<DcObjectTable> dataList3 = new ArrayList<DcObjectTable>();//DcObjectMain
					List<DcHiveField> dataList4 = new ArrayList<DcHiveField>();
					List<DcObjectField> dataList5 = new ArrayList<DcObjectField>();
					
					if (CollectionUtils.isNotEmpty(tableList)) {//判断tableList是否为空，若为空则不进行处理
						for (int j = 0; j < tableList.size(); j++) {//循环tableList
							if (DcStringUtils.isNotNull(tableList.get(j).get("TBL_NAME"))) {//判断tab_name是否为空
								int tblId = Integer.parseInt(tableList.get(j).get("TBL_ID").toString()) ;// 获取到数据表的id
								String tableName = tableList.get(j).get("TBL_NAME").toString();//获取数据表名
								String idsql = "select ID from dc_hive_table where TABLE_NAME='" + tableName + "' and TABLE_SPACE='"+ dbName + "'";
//								List<Map<String, Object>> map = DcCommonUtils.queryDataBySql(idsql);//根据表名和数据库名，查看在dc_hive_table中的id
								String belong2Id = DcCommonUtils.getFieldByManyField(idsql);//根据表名和数据库名，查看在dc_hive_table中的id
								
									DcHiveTable dcHiveTable = new DcHiveTable();//新建DcHiveTable对象
									dcHiveTable.preInsert();
									dcHiveTable.setId(IdGen.uuid());//设置dcHiveTable的id
									dcHiveTable.setTableName(tableName);//设置dcHiveTable的tableName
									dcHiveTable.setTableDesc("");//设置dcHiveTable的tableDesc
									dcHiveTable.setTableSpace(dbName);//设置dcHiveTable的tableSpace
									dcHiveTable.setSeparatorSign("Comma(,)");//设置dcHiveTable的separatorSign
									dcHiveTable.setOwner(tableList.get(j).get("OWNER").toString());//设置dcHiveTable的owner
									
									if ("default".equals(dbName)) {//判断数据库名是否是default
										dcHiveTable.setLocation("/user/hive/warehouse/" + tableName);//设置dcHiveTable的location
									} else {
										dcHiveTable.setLocation("/user/hive/warehouse/" + dbName + ".db/" + tableName);//设置dcHiveTable的location
									}
									dcHiveTable.setTableType(tableList.get(j).get("TBL_TYPE").toString());//设置dcHiveTable的tableType
									dcHiveTable.setCreateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(tableList.get(j).get("createTime").toString()));//设置创建时间
									dcHiveTable.setStatus(DcHiveTable.TABLE_STATUS_EXPORT);
									dcHiveTable.setIsLoadData("true");
									
									dataList.add(dcHiveTable);
									//存放字段信息
									String fieldsql = "SELECT * FROM COLUMNS_V2 where CD_ID='" + tblId + "" + "'";//根据cd_id找到对应的字段信息
									List<Map<String, Object>> fieldList = getHiveData(fieldsql);
									if (CollectionUtils.isNotEmpty(fieldList)) {//判断字段列表是否为空，若为空则不进行操作
										for (int j2 = 0; j2 < fieldList.size(); j2++) {//循环fieldList
											
											DcHiveField dcHiveField = new DcHiveField();//新建一个DcHiveField对象
											dcHiveField.preInsert();
											dcHiveField.setId(IdGen.uuid());//设置dcHiveField的id
											dcHiveField.setBelong2Id(dcHiveTable.getId());//设置dcHiveField的belong2Id
											dcHiveField.setFieldName(fieldList.get(j2).get("COLUMN_NAME").toString());;//设置dcHiveField的fieldName
											
											if (DcStringUtils.isNotNull(fieldList.get(j2).get("COMMENT"))) {//判断comment是否为空,空则赋值为空
												dcHiveField.setFieldDesc(fieldList.get(j2).get("COMMENT").toString());//设置dcHiveField的fieldDesc
											} else {
												dcHiveField.setFieldDesc("");//设置dcHiveField的fieldDesc
											}

											dcHiveField.setFieldType(fieldList.get(j2).get("TYPE_NAME").toString());//设置dcHiveField的fieldType
											dcHiveField.setSortNum(Integer.parseInt(fieldList.get(j2).get("INTEGER_IDX").toString())*10 );//设置dcHiveField的sortNum
											dcHiveField.setIsKey("0");//设置dcHiveField的是否为主键

											dataList4.add(dcHiveField);//将对象驾到dataList中
											
											DcObjectField dcObjectField =new DcObjectField();
											dcObjectField.preInsert();
											dcObjectField.setId(dcHiveField.getId());
											dcObjectField.setBelong2Id(dcHiveTable.getId());
											dcObjectField.setFieldName(fieldList.get(j2).get("COLUMN_NAME").toString());
											if (DcStringUtils.isNotNull(fieldList.get(j2).get("COMMENT"))) {//判断comment是否为空,空则赋值为空
												dcObjectField.setFieldDesc(fieldList.get(j2).get("COMMENT").toString());//设置dcHiveField的fieldDesc
											} else {
												dcObjectField.setFieldDesc("");//设置dcHiveField的fieldDesc
											}
											dcObjectField.setFieldType(fieldList.get(j2).get("TYPE_NAME").toString());
											dcObjectField.setSortNum(Integer.parseInt(fieldList.get(j2).get("INTEGER_IDX").toString())*10 );//设置dcHiveField的sortNum
											dcObjectField.setIsKey(0);//设置dcHiveField的是否为主键
											dataList5.add(dcObjectField);
											
										}
									}
									
									//存放元数据
									DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
									srcMain.preInsert();
									srcMain.setId(dcHiveTable.getId());			
									srcMain.setJobId("sync_hive");							//任务Id
									srcMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);		//采集
									srcMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);		//外部数据
									srcMain.setObjCode(srcMain.getId());					//任务编码
									srcMain.setObjName(dcHiveTable.getTableSpace()+"."+dcHiveTable.getTableName());
									srcMain.setObjDesc("");					//备注信息
									srcMain.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
									dataList2.add(srcMain);
									
									DcObjectTable srcTable = new DcObjectTable(); 				//元数据 table补充
									srcTable.preInsert();
									srcTable.setId(dcHiveTable.getId());
									srcTable.setDbDataBase(dcHiveTable.getTableSpace());			//schema 名称
									srcTable.setTableName(dcHiveTable.getTableName());				//表名
									srcTable.setTableLink("");				//数据库连接
									srcTable.setDbType("hive");				//数据库类别
									srcTable.setRemarks("");
									dataList3.add(srcTable);
								
									//字段操作
									List<DcObjectField> filedid = dcObjectFieldDao.getDB2Id(belong2Id);
									/*清除dc_obj_table表*/
	/*								if (CollectionUtils.isNotEmpty(filedid)) {//判断字段列表是否为空，若为空则不进行操作
										for (int k = 0; k < filedid.size(); k++) {
											DcObjectField dcObjectField = new DcObjectField();	
											dcObjectField.setId(filedid.get(k).getId());
											dcObjectFieldDao.delete(dcObjectField);
											dcObjectFieldDao.deleteByBelong2Id(belong2Id);
										}
									}*/

							} else {//若tab_name为空，则往下走一个
								j++;
							}

						}
						
						//通过dc_hive_table获取该数据库的id 删除main表下的数据
						List<Map<String, Object>> idList = dcObjectTableDao.getByDbNameId(dbName);
						for(int j=0;j<idList.size();j++){
							if (DcStringUtils.isNotNull(idList.get(j).get("id"))) {
								String id = idList.get(j).get("id").toString();
								DcObjectMain srcMain = new DcObjectMain();
								srcMain.setId(id);
								dcObjectMainDao.delete(srcMain);
							}
						}
						//元数据管理
						if(CollectionUtils.isNotEmpty(dataList2))
						dcObjectMainDao.batchInsert(dataList2);// 在dc_obj_main表中批量新增数据
						
						if(CollectionUtils.isNotEmpty(dataList))
						dcHiveTableService.batchInsert(dataList);// 在dc_hive_table表中批量新增数据
						
						if(CollectionUtils.isNotEmpty(dataList3))
						dcObjectTableDao.batchInsert(dataList3);// 在dc_obj_table表中批量新增数据
						
						// 批量插入数据到dc_hive_field中
						if(CollectionUtils.isNotEmpty(dataList4))
						dcHiveFieldService.batchInsert(dataList4);
						//清除字段元数据
						// 批量插入数据到dc_obj_field中
						if(CollectionUtils.isNotEmpty(dataList5))
						dcObjectFieldDao.batchInsert(dataList5);
						
						dcObjectMainService.updateData2Es(dataList2);
					}
				}
			} else {// 若dbList为空，则写入日志中
				logger.debug("未获取到hive表, 请联系管理员!");
			}
		} catch (Exception e) {//异常处理，写入日志中
			logger.error("--> updateHiveTable: ", e);
			throw new ServiceException(e);
		}
		return "success";
	}

	/**
	 * @方法名称: updateHiveDataBase 
	 * @实现功能: 同步数据库数据
	 * @return 
	 * @create by cdd at 2017年1月18日 下午5:15:25
	 */
	@Transactional(readOnly = false)
	public String updateHiveDataBase() {
		List<Map<String, Object>> dbList = getHiveData("select * from DBS");// 获得hive里的数据库信息
		Assert.notEmpty(dbList, "未获取到表空间列表!");
		try {
			for (int i = 0; i < dbList.size(); i++) {// 对dbList进行循环
				String dbName = dbList.get(i).get("NAME").toString();// 获取数据库名,一般不可能为空

				String count = DcCommonUtils.getFieldByField("DATABASESPACE", dbName, "dc_hive_database","count(1) as count");// 查看这个数据库是否在dc_hive_database表里存在
				if ("0".equals(count)) {									// 0表示不存在；1表示存在，但不进行操作
					DcHiveDatabase dcHiveDatabase = new DcHiveDatabase();	// 创建一个新的DcHiveDatabase对象
					dcHiveDatabase.setId(IdGen.uuid());						// 设置dcHiveDatabase的id
					dcHiveDatabase.setDatabase(dbName);						// 设置dcHiveDatabase的数据库名
					dcHiveDatabase.setCreateDate(new Date());				// 设置dcHiveDatabase的创建时间
					dcHiveDatabase.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);	// 设置dcHiveDatabase的删除标志
					if (DcStringUtils.isNotNull(dbList.get(i).get("DESC"))) {// 判断某个数据库的描述是否为空
						dcHiveDatabase.setRemarks(dbList.get(i).get("DESC").toString());// 设置dcHiveDatabase的描述
					}

					//同步元数据
					DcObjectMain dcObjectMain = new DcObjectMain();
					dcObjectMain.setId(dcHiveDatabase.getId());
					dcObjectMain.setObjCode(dcHiveDatabase.getId());
					dcObjectMain.setObjName(dcHiveDatabase.getDatabase());
					dcObjectMain.setObjType(DcObjectMain.OBJ_TYPE_DATABASE);
					dcObjectMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);
					dcObjectMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);
					dcMetadataStroeService.obj2MySQL(dcObjectMain, dcHiveDatabase);
				}
			}
		} catch (Exception e) {
			logger.error("--> updateHiveDataBase: ", e);
			throw new ServiceException(e);
		}
		return "success";
	}

	/**
	 * @方法名称: getDBName 
	 * @实现功能: 获取数据库名
	 * @param dbId
	 * @return
	 * @create by cdd at 2017年1月24日 下午2:11:48
	 */
	public String getDBName(String dbId) {
		String dbName="";
		Connection con = null; // 连接到hive数据库
		ResultSet rs = null;
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
			con = DriverManager.getConnection("jdbc:mysql://"+ DcPropertyUtils.getProperty("hive.metadata.connect"),
					DcPropertyUtils.getProperty("hive.metadata.user","hive"),
					DcPropertyUtils.getProperty("hive.metadata.pswd","hive"));
			statement = con.createStatement();
			rs = statement.executeQuery("select NAME FROM DBS WHERE DB_ID='"+dbId+"'");
	
			while (rs.next()) { // 遍历查询结果集
				dbName= rs.getObject(1).toString();
			}
			rs.close();
			con.close();
		}  catch (Exception e) {//异常处理
			logger.error("-->getDBName: ", e);
			throw new ServiceException(e);
		}
		return dbName;
	}
	
	/**
	 * @方法名称: getHiveData
	 * @实现功能: 获取hive数据库中的数据
	 * @return
	 * @create by cdd at 2017年1月19日 上午9:29:48
	 */ 
	@Transactional(readOnly = false)
	public List<Map<String, Object>> getHiveData(String sql) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Connection con = null; // 连接到hive数据库
		
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载驱动程序
			con = DriverManager.getConnection("jdbc:mysql://"+ DcPropertyUtils.getProperty("hive.metadata.connect"),
					DcPropertyUtils.getProperty("hive.metadata.user","hive"),
					DcPropertyUtils.getProperty("hive.metadata.pswd","hive"));
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while (rs.next()) { // 遍历查询结果集
				Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					dataMap.put(rsmd.getColumnName(i), rs.getObject(i));//构建map类型
				}
				dataList.add(dataMap);
			}
			
			rs.close();
			con.close();//关闭连接
		}catch (Exception e) {// 异常处理
			logger.error("-->getHiveData: ",e);
			throw new ServiceException(e);
		}
		return dataList;
	}

	@Transactional(readOnly = false)
	public void ExeSql(String sql,String dbName){
		boolean flag = true;//操作数据库
		int one=0,two=0,three=0; //用来判断是否存在if not exists
		String[] b = sql.split(" ");//没有大写时的
		sql = sql.toUpperCase();
		String table = null;
		String[] a = sql.split(" ");
		for(int i=0;i<a.length;i++){
			if("DATABASE".equals(a[i])){//create database table4
				flag = false;//不操作
				break;
			}
			if(sql.indexOf("CREATE") >= 0 ){
				if(sql.indexOf("TABLE")>=0){
					//split sql create table 
					if("IF".equals(a[i])){
						one=1;
						continue;
					}
					if("NOT".equals(a[i])){
						two=1;
						continue;
					}
					if("EXISTS".equals(a[i])){
						three=1;
						continue;
					}
					if("CREATE".equals(a[i])){
						continue;
					}
					if("TABLE".equals(a[i])){
						continue;
					}
					if(!"".equals(a[i])){
						table = b[i].substring(0,b[i].indexOf("(")).trim();
						break;
					}
				}
			}
			if(sql.indexOf("DROP") >=0){
				if(sql.indexOf("TABLE")>=0){
					if("DATABASE".equals(a[i])){
						flag = false;//操作数据库
						break;
					}
					if("DROP".equals(a[i])){
						continue;
					}
					if("TABLE".equals(a[i])){
						continue;
					}
					if(!"".equals(a[i])){
						table = b[i];
						break;
					}
				}
			}
		}
		if(flag){
			if(sql.indexOf("CREATE")>=0){
				if(sql.indexOf("TABLE")>=0){
					if(one==1&&two==1&&three==1){
						DcObjectTable dcObjectTable = new DcObjectTable();
						dcObjectTable.setDbDataBase(dbName);
						dcObjectTable.setTableName(table);
						//根据数据库和表名获取id值
						DcObjectTable dc = dcObjectTableDao.byNameToId(dcObjectTable);
						if(dc==null){
							//表名得到
							//保存数据源对象   
							DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
							srcMain.setId(IdGen.uuid());			
							srcMain.setJobId("");							//任务Id
							srcMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);		//采集
							srcMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);		//外部数据
							srcMain.setObjCode(srcMain.getId());					//任务编码
							srcMain.setObjName(table);
							srcMain.setObjDesc("");					//备注信息
							srcMain.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
							
							DcObjectTable srcTable = new DcObjectTable(); 				//元数据 table补充
							srcTable.setId(srcMain.getId());
							srcTable.setDbDataBase(dbName);			//schema 名称
							srcTable.setTableName(table);				//表名
							srcTable.setTableLink("");				//数据库连接
							srcTable.setDbType("hive");				//数据库类别
							
							List<DcObjectField> srcFieldList = new ArrayList<DcObjectField>();	//数据表字段 暂不存储
							
							//hive表 dc_hive_table
							DcHiveTable dcHiveTable = new DcHiveTable();
							dcHiveTable.setTableName(table);
							dcHiveTable.setTableSpace(dbName);
							if ("default".equals(dbName)) {//判断数据库名是否是default
								dcHiveTable.setLocation(DcPropertyUtils.getProperty("hdfs.datanode.address") + table);//设置dcHiveTable的location
							} else {
								dcHiveTable.setLocation(DcPropertyUtils.getProperty("hdfs.datanode.address") + dbName + ".db/" + table);//设置dcHiveTable的location
							}
							dcHiveTable.setTableDesc("");
							dcHiveTable.setSeparatorSign("Comma(,)");
							dcMetadataStroeService.obj2MySQL(srcMain, srcTable, srcFieldList,dcHiveTable);
						}
					}else{
						//表名得到
						//保存数据源对象   
						DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
						srcMain.setId(IdGen.uuid());			
						srcMain.setJobId("");							//任务Id
						srcMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);		//采集
						srcMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);		//外部数据
						srcMain.setObjCode(srcMain.getId());					//任务编码
						srcMain.setObjName(table);
						srcMain.setObjDesc("");					//备注信息
						srcMain.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
						
						DcObjectTable srcTable = new DcObjectTable(); 				//元数据 table补充
						srcTable.setId(srcMain.getId());
						srcTable.setDbDataBase(dbName);			//schema 名称
						srcTable.setTableName(table);				//表名
						srcTable.setTableLink("");				//数据库连接
						srcTable.setDbType("hive");				//数据库类别
						
						List<DcObjectField> srcFieldList = new ArrayList<DcObjectField>();	//数据表字段 暂不存储
						
						//hive表 dc_hive_table
						DcHiveTable dcHiveTable = new DcHiveTable();
						dcHiveTable.setTableName(table);
						dcHiveTable.setTableSpace(dbName);
						if ("default".equals(dbName)) {//判断数据库名是否是default
							dcHiveTable.setLocation(DcPropertyUtils.getProperty("hdfs.datanode.address") + table);//设置dcHiveTable的location
						} else {
							dcHiveTable.setLocation(DcPropertyUtils.getProperty("hdfs.datanode.address") + dbName + ".db/" + table);//设置dcHiveTable的location
						}
						dcHiveTable.setTableDesc("");
						dcHiveTable.setSeparatorSign("Comma(,)");
						dcMetadataStroeService.obj2MySQL(srcMain, srcTable, srcFieldList,dcHiveTable);
					}
				}
			}else if(sql.indexOf("DROP")>=0){
				if(sql.indexOf("TABLE")>=0){
					DcObjectTable dcObjectTable = new DcObjectTable();
					dcObjectTable.setDbDataBase(dbName);
					dcObjectTable.setTableName(table);
					//根据数据库和表名获取id值
					DcObjectTable dc = dcObjectTableDao.byNameToId(dcObjectTable);
					if(dc!=null){
						//删除元数据表
						dc.setObjId(dc.getId());
						DcObjectMain dcObjectMain = new DcObjectMain();
						dcObjectMain.setId(dc.getId());
						DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();//将删除系统默认分类中
						dcObjectCataRef.setObjId(dc.getId());
						DcHiveTable dcHiveTable = new DcHiveTable();
						dcHiveTable.setId(dc.getId());
						dcMetadataStroeService.delObj2MySQL(dcObjectMain, dc, new DcObjectField(dc.getId()), dcObjectCataRef,dcHiveTable);
					}
				}
			}
		}
		//如果是数据库
		if(!flag){
			String daName = null;
			if(sql.indexOf("CREATE")>=0){
				for(int i=0;i<a.length;i++){
					if("IF".equals(a[i])){
						one=1;
						continue;
					}
					if("NOT".equals(a[i])){
						two=1;
						continue;
					}
					if("EXISTS".equals(a[i])){
						three=1;
						continue;
					}
					if("CREATE".equals(a[i])){
						continue;
					}
					if("DATABASE".equals(a[i])){
						continue;
					}
					if(!"".equals(a[i])){
						daName = b[i];
						break;
					}
				}
				//表空间操作
				DcHiveDatabase dcHiveDatabase = new DcHiveDatabase();
				dcHiveDatabase.setDatabase(daName);
				dcHiveDatabase.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);
				if(one==1&&two==1&three==1){
					DcHiveDatabase dc = dcHiveDatabaseService.byDatabaseGet(dcHiveDatabase);
					if(dc==null){
						dcHiveDatabase.preInsert();
						dcHiveDatabase.setId(IdGen.uuid());
						//dcHiveDatabaseDao.insert(dcHiveDatabase);
						DcObjectMain dcObjectMain = new DcObjectMain();
						dcObjectMain.setId(dcHiveDatabase.getId());
						dcObjectMain.setObjCode(dcHiveDatabase.getId());
						dcObjectMain.setObjName(daName);
						dcObjectMain.setObjType(DcObjectMain.OBJ_TYPE_DATABASE);
						dcObjectMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);
						dcObjectMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);
						dcMetadataStroeService.obj2MySQL(dcObjectMain,dcHiveDatabase);
					}
				}else{
					dcHiveDatabase.preInsert();
					dcHiveDatabase.setId(IdGen.uuid());
					//dcHiveDatabaseDao.insert(dcHiveDatabase);
					DcObjectMain dcObjectMain = new DcObjectMain();
					dcObjectMain.setId(dcHiveDatabase.getId());
					dcObjectMain.setObjCode(dcHiveDatabase.getId());
					dcObjectMain.setObjName(daName);
					dcObjectMain.setObjType(DcObjectMain.OBJ_TYPE_DATABASE);
					dcObjectMain.setJobType(DcObjectMain.JOB_TYPE_HIVE);
					dcObjectMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);
					dcMetadataStroeService.obj2MySQL(dcObjectMain,dcHiveDatabase);
				}
			}
			if(sql.indexOf("DROP")>=0){
				for(int i=0;i<a.length;i++){
					if("DROP".equals(a[i])){
						continue;
					}
					if("DATABASE".equals(a[i])){
						continue;
					}
					if(!"".equals(a[i])){
						daName = b[i];
						break;
					}
				}
				//表空间操作
				//根据表名和数据库删除 逻辑删除
				DcHiveDatabase dcHiveDatabase = new DcHiveDatabase();
				dcHiveDatabase.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
				dcHiveDatabase.setDatabase(daName);
				DcHiveDatabase db =  dcHiveDatabaseService.byDatabaseGet(dcHiveDatabase);
				if(db!=null){
					//dcObjectMainDao.delete(entity)
					DcObjectMain dcObjectMain = new DcObjectMain();
					dcObjectMain.setId(db.getId());
					dcMetadataStroeService.deObj2MySQL(dcObjectMain, db);
				}
			}
		}
	}
	
	/**
	 * @方法名称: checkHiveTableName 
	 * @实现功能: 判断Hive 表名是否存在, 表空间+表名 一起验证
	 * @param tableName
	 * @return
	 * @create by peijd at 2017年3月14日 上午10:56:23
	 */
	public boolean checkHiveTableName(String tableName) {
		Assert.hasText(tableName, "表名不可为空!");
		
		StringBuilder tbSql = new StringBuilder(256);
		//构建查询脚本
		tbSql.append("SELECT b.`NAME`,a.TBL_NAME,a.TBL_TYPE ");
		tbSql.append("  FROM `TBLS` a ");
		tbSql.append("  LEFT JOIN DBS b on a.DB_ID = b.DB_ID ");
		if(tableName.indexOf(".")>0){
			tbSql.append(" WHERE a.TBL_NAME='").append(tableName.split("[.]")[1]).append("' AND b.`NAME`='").append(tableName.split("[.]")[0]).append("'");
		}else{
			tbSql.append(" WHERE a.TBL_NAME='").append(tableName).append("' AND b.`NAME`='default'");
		}
		try{
			//查询hive 元数据表中记录
			return CollectionUtils.isEmpty(getHiveData(tbSql.toString()));
			
		}catch(Exception e){
			logger.error("-->checkHiveTableName:", e);
			throw new ServiceException(e);
		}
	}
	
	private  String jsonString(String s){
        char[] temp = s.toCharArray();       
        int n = temp.length;
        for(int i =0;i<n;i++){
            if(temp[i]==':'&&temp[i+1]=='"'){
                    for(int j =i+2;j<n;j++){
                        if(temp[j]=='"'){
                            if(temp[j+1]!=',' &&  temp[j+1]!='}'){
                                temp[j]='”';
                            }else if(temp[j+1]==',' ||  temp[j+1]=='}'){
                                break ;
                            }
                        }
                    }   
            }
        }       
        return new String(temp);
    }
}
