package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.BaseEntity;
import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.IdGen;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveTableDao;
import com.hlframe.modules.dc.dataprocess.dao.HistoryDbNameDao;
import com.hlframe.modules.dc.dataprocess.entity.*;
import com.hlframe.modules.dc.metadata.dao.DcObjectFieldDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcObjectTable;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;
import com.hlframe.modules.sys.utils.UserUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcHiveTableService.java 
 * @职责说明: hive数据表管理 Service
 * @修改者: peijd
 * @修改时间: 2017年3月29日 上午10:40:21
 */
@Service
@Transactional(readOnly = true)
public class DcHiveTableService extends CrudService<DcHiveTableDao, DcHiveTable>{
	@Autowired
	private DcHiveFieldService dcHiveFieldService;
	@Autowired
	private DcHiveDatabaseService dcHiveDatabaseService;
	@Autowired	//数据字段dao
	private DcObjectFieldDao dcObjectFieldDao;
	
	@Autowired
	private DcHdfsFileService dcHdfsFileService;
	
	@Autowired
	private HistoryDbNameDao historyDbNameDao;
	
	@Autowired	//元数据存储Service
	private DcMetadataStroeService dcMetadataStroeService;
	
	static Client client = Client.create(); 

	public DcHiveTable get(String id) {
		DcHiveTable param = new DcHiveTable();
		param.setId(id);
		return super.get(param);
	}

	public Page<DcHiveTable> findPage(Page<DcHiveTable> page, DcHiveTable dcHiveTable) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcHiveTable.getSqlMap().put("dsf", dataScopeFilter(dcHiveTable.getCurrentUser(),"o","u"));
		dcHiveTable.setPage(page);// 设置分页参数
		page.setList(dao.findList(dcHiveTable));// 执行分页查询
		//return super.findPage(page, dcHiveTable);
		return page;
	}

	/**
	 * @方法名称: saveTableData
	 * @实现功能: 保存数据
	 * @param obj
	 * @create by cdd at 2017年1月12日 下午7:47:44
	 * @update by hgw at 2017年3月16日 下午7:47:44
	 */
	@Transactional(readOnly = false)
	public void saveTableData(DcHiveTable obj) {
		
		obj.preInsert();
		obj.setStatus(DcHiveTable.TABLE_STATUS_NEW);	//新建状态
		//加载元数据信息  主外键
		loadHiveMetaData(obj, null);
		dao.insert(obj);
		
	}

	/**
	 * @方法名称: createHiveTable 
	 * @实现功能: 创建HIVE数据表
	 * @param dcHiveTable
	 * @create by cdd at 2017年1月9日 上午11:06:34
	 * @update by peijd at 2017年3月29日 下午20:36:28  更新元数据信息, 并推送至ES集群
	 */
	@Transactional(readOnly = false)
	public String createHiveTable(DcHiveTable dcHiveTable) {
		DcHiveField param = new DcHiveField();
		param.setBelong2Id(dcHiveTable.getId());
		List<DcHiveField> fieldList = dcHiveFieldService.findList(param);
		Assert.notEmpty(fieldList, "未设置字段信息!");
		
		String sql="";
		if(CollectionUtils.isNotEmpty(fieldList)){	//判断fieldList是否为空，不为空则创建sql语句
		    sql = createSql(dcHiveTable, fieldList);//建立语句
		}
		String result = createTable(sql);
		//执行失败, 丢异常..  peijd
		if(!"success".equalsIgnoreCase(result)){
			throw new ServiceException(result);
		}
		
		//创建成功之后 更新元数据信息
		dcHiveTable.setStatus(DcHiveTable.TABLE_STATUS_INIT);
		dcHiveTable.preUpdate();
		dao.update(dcHiveTable);
		//加载hive元数据信息
		loadHiveMetaData(dcHiveTable, fieldList);
		
		//加载table内容
		loadHiveData(dcHiveTable);
		
        return "操作成功";//创建hive数据表
	}

	/**
	 * @方法名称: loadHiveMetaData 
	 * @实现功能: 加载hive表元数据
	 * @param dcHiveTable
	 * @create by peijd at 2017年3月31日 上午10:46:49
	 */
	@Transactional(readOnly = false)
	private void loadHiveMetaData(DcHiveTable dcHiveTable, List<DcHiveField> fieldList) {
		/* 把新增的数据存到dc_obj_main中 */
		DcObjectMain dcObjectMain = new DcObjectMain();
		dcObjectMain.setId(dcHiveTable.getId());
		dcObjectMain.setObjCode(dcHiveTable.getId());
		dcObjectMain.setObjName(dcHiveTable.getTableName());
		dcObjectMain.setObjType(dcObjectMain.OBJ_TYPE_TABLE);
		dcObjectMain.setObjDesc(dcHiveTable.getTableDesc());
		dcObjectMain.setJobId(dcHiveTable.getId());				//任务Id 
		dcObjectMain.setJobType(DcObjectMain.JOB_TYPE_UPLOAD);	//用户创建
		dcObjectMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);//存储于集群中
		
		/* 把新增的数据存到dc_obj_table中 */
		DcObjectTable dcObjectTable = new DcObjectTable();
		dcObjectTable.setObjId(dcObjectMain.getId());
		dcObjectTable.setTableName(dcHiveTable.getTableName());
		dcObjectTable.setDbDataBase(dcHiveTable.getTableSpace());
		dcObjectTable.setTableLink("hive");
		
		/** 添加字段 **/
		List<DcObjectField> fields = new ArrayList<DcObjectField>();
		if(CollectionUtils.isNotEmpty(fieldList)){
			int idx = 10;
			for (DcHiveField field : fieldList) {
				DcObjectField dcObjectField = new DcObjectField();//创建DcHiveField对象dcObjectField
				dcObjectField.setId(IdGen.uuid());//设置obj的id
				dcObjectField.setBelong2Id(dcObjectMain.getId());//设置obj的belong2Id
				dcObjectField.setFieldName(field.getFieldName());//设置obj的fieldName
				dcObjectField.setFieldType(field.getFieldType());//设置obj的fieldType
				dcObjectField.setFieldDesc(field.getFieldDesc());//设置obj的fieldDesc
				dcObjectField.setIsKey(0);//设置obj的isKey
				dcObjectField.setSortNum(idx);//设置obj的belong2Id
				fields.add(dcObjectField); 
				idx+=10;
			}
		}
		//更新至ES  TODO: 根据dcHiveTable的状态判断 是否需加载到ES(0,1-不加载; 9-加载)
		dcMetadataStroeService.obj2MySQL(dcObjectMain, dcObjectTable, fields);
	}

	/**
	 * @方法名称: createTable 
	 * @实现功能:创建表
	 * @param sql
	 * @return
	 * @create by cdd at 2017年1月14日 下午2:32:12
	 */
	public String createTable(String sql) {
		return executeSql(sql);
	}

	/**
	 * @方法名称: createSql
	 * @实现功能: 建立语句
	 * @param dcHiveTable
	 * @param fieldList
	 * @return
	 * @create by cdd at 2017年1月14日 下午2:28:17
	 */
	public String createSql(DcHiveTable dcHiveTable, List<DcHiveField> fieldList) {
		StringBuilder sql = new StringBuilder(128);

		sql.append("create table ");
		if ("default".equals(dcHiveTable.getTableSpace())) {// 判断表空间，若为default,可不加
			sql.append("");
		} else {
			sql.append(dcHiveTable.getTableSpace()).append(".");
		}
		sql.append(dcHiveTable.getTableName());// 表名
		sql.append("(");
		
		//拼接字段
		for (DcHiveField field : fieldList) {
			sql.append(field.getFieldName()).append(" ").append(field.getFieldType()).append(",");// 构建新建表的字段部分
		}
		sql.deleteCharAt(sql.length()-1).append(") ");
		
		//字段分隔符
		if (StringUtils.isNotBlank(dcHiveTable.getSeparatorSign())) {
			sql.append("row format delimited fields terminated by '").append(DcHiveTable.separatorMap.get(dcHiveTable.getSeparatorSign())).append("'");
		}

		return sql.toString();
	}

	/**
	 * @方法名称: deleteData 
	 * @实现功能: 删除数据
	 * @param obj
	 * @create by cdd at 2017年1月9日 上午11:14:13
	 * @update by HGW at 2017年3月16日 上午11:14:13
	 */
	@Transactional(readOnly = false)
	public String deleteData(DcHiveTable obj) {

		//删除元数据信息
//		dcObjMainService.deleteById(obj.getId());
		DcCommonUtils.delDataByField("ID", obj.getId(), "dc_obj_main");// 根据字段ID的值删除dc_obj_main表里的数据

		List<Map<String, Object>> list = dcObjectFieldDao.getByBelong2Id(obj.getId());
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				DcCommonUtils.delDataByField("ID", list.get(i).get("OBJ_ID").toString(), "dc_obj_main");// 根据字段BELONG2_ID的值删除dc_obj_field表里的数据
			}
		}
		
		//如果hive表以创建,  删除Hive中的数据表
		if(!DcHiveTable.TABLE_STATUS_NEW.equals(obj.getStatus())){
			return dropTable(obj.getTableName());
		}
		
		return "操作成功";
	}

	

	/**
	 * @方法名称: deleteHiveTable 
	 * @实现功能:删除hive数据表
	 * @param tableName
	 * @create by cdd at 2017年1月9日 上午11:06:58
	 */
	@Transactional(readOnly = false)
	public String dropTable(String tableName) {
		String sql = "drop table "+tableName;//构建sql语句
		return "success".equalsIgnoreCase(executeSql(sql))?"操作成功!":"操作失败!";
	}
	
/**
 * @方法名称: getTableName 
 * @实现功能: 检查数据库中是否存在表名
 * @param tableName
 * @return
 * @create by cdd at 2017年1月10日 下午5:25:53
 */
	public Object getTableName(String tableName) {
		return dao.getTableName(tableName);//获取表名
	}

	/**
	 * @方法名称: selectData 
	 * @实现功能:用于获取hive中数据表的数据
	 * @param obj
	 * @return
	 * @create by cdd at 2017年1月10日 上午11:44:35
	 */
	public List<Map<String,Object>> selectData(DcHiveTable obj) {
		Assert.notNull(obj);
		return selectData(obj.getTableSpace(), obj.getTableName());
	}

	/**
	 * @方法名称: selectData
	 * @实现功能: 查询hive数据表中数据
 	 * @param schemaName	主题域
	 * @param tableName		表名
	 * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @create by peijd at 2017/6/2 9:38
	 */
	public List<Map<String,Object>> selectData(String schemaName, String tableName) {
		Assert.hasText(tableName);
		StringBuilder querySql = new StringBuilder(128);
		querySql.append("select * from ").append(StringUtils.isNotBlank(schemaName) ? schemaName + "." + tableName : tableName);
		return executeQuerySql(querySql.toString(), Integer.parseInt(DcPropertyUtils.getProperty("extractdb.preview.datanum", "20")));
	}

	/**
	 * @方法名称: descTable 
	 * @实现功能: 得到hive中数据表的字段
	 * @param tableName
	 * @return
	 * @create by cdd at 2017年1月10日 下午5:25:44
	 */
	/*public List<Map<String,Object>> descTable(String tableName) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();  
		
		try {
			URI u = new URI(DcPropertyUtils.getProperty("executeHive.restServer.url"));
			//u = new URI("http://localhost:9050/jerseyHive/rest/dcHiveQuery/executeQuerySql");
			WebResource resource = client.resource(u);
			MultivaluedMapImpl params = new MultivaluedMapImpl();  
			params.add("metaSql", "desc "+tableName);  
			 String result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);  //得到的数据为json字符串
			 System.out.println(result);
			try {
				JSONArray ls = new JSONArray(result);//对数据进行解析
				 if (ls.length()>0) {  
	                 for (int i=0;i<ls.length();i++) {  
	                     JSONObject json = new JSONObject(ls.get(i).toString());  
	                     Map<String, Object> dataMap = new LinkedHashMap<String, Object>();  
	                     dataMap.put("fieldName", json.get("col_name"));  
	                     dataMap.put("fieldType", json.get("data_type")); 
	                     dataMap.put("fieldDesc", json.get("comment"));
	                     dataList.add(dataMap);  
	                     }  
	             } 
			} catch (JSONException e) {
				e.printStackTrace();
			}  
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return dataList;
	}*/

	/**
	 * @方法名称: loadData
	 * @实现功能: 导入location里面的文件内容（根据separatorSign分割的）
	 * @param location
	 * @param separatorSign
	 * @return
	 * @create by cdd at 2017年1月12日 下午12:47:35
	 */
	public List<Map<String, Object>> loadData(String location, String separatorSign) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if(StringUtils.isBlank(location)){
			return dataList;
		}
		DcHdfsFile file = new DcHdfsFile();//创建DcHdfsFile对象file
		file.setFilePath(location);//设置file的filePath
		dcHdfsFileService.loadFileContent(file, "20");//获取location的文件内容
		String fileContent = file.getContent();//获取文件内容
		if (StringUtils.isNotBlank(fileContent)) {
			String[] arr = fileContent.split("\n");//以\n分割文件内容
			int idx = 0;
			for (String line : arr) {
				Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
				for (String data : line.split(DcHiveTable.separatorMap.get(separatorSign))) {//根据separatorSign分割文件内容的一行行数据
					dataMap.put("col_" + idx, line.split(DcHiveTable.separatorMap.get(separatorSign))[idx]);//若在dc_hive_field表里没有数据，则用col_0等替换字段
					idx++;
				}
				dataList.add(dataMap);
				idx = 0;
			}
		}
		return dataList;//返回最终组合的数据
	}

/**
 * @方法名称: loadFieldData 
 * @实现功能: 导入到hive数据表中
 * @param obj
 * @return
 * @create by cdd at 2017年1月12日 下午3:09:00
 */
	public List<Map<String, Object>> loadFieldData(DcHiveTable obj) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		String count = dcHiveFieldService.countByBelong2Id(obj.getId());// 查询表id为obj.getId()在dc_hive_field中是否有数据
		if ("0".equals(count)) {// 没有数据，则导入文件中的字段名，即为col_0,col_1等
			List<Map<String, Object>> fieldList = loadData(obj.getLocation(), obj.getSeparatorSign());// 导入location里面的文件内容（根据separatorSign分割的）
			if (CollectionUtils.isNotEmpty(fieldList)) {// 判断fieldList是否为空
				Map<String, Object> fieldMap = fieldList.get(0);
				Iterator it = fieldMap.keySet().iterator();
				while (it.hasNext()) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					String key = (String) it.next();// 获取fieldList里面的key值
					// String value = fieldMap.get(key).toString();
					dataMap.put("fieldName", key);// 将key值赋值给fieldName
					dataMap.put("fieldType", "String");// 字段类型全部默认为String
					dataMap.put("fieldDesc", "");// 字段描述全部默认为空
					dataList.add(dataMap);
				}
				return dataList;
			} else {
				return null;
			}
		} else {// 如果已存在，则导入字段表中的字段
			List<Map<String, Object>> list = dcHiveFieldService.getFieldNameAndType(obj.getId());// 获取表里的字段名和字段类型
			return list;
		}

	}

/**
 * @方法名称: saveFieldData 
 * @实现功能: 保存字段信息
 * @param dcHiveField
 * @create by cdd at 2017年1月12日 下午8:07:08
 * @create by peijd at 2017年3月29日 下午8:47:12 取消关联元数据dc_object_field的操作, 在生成数据表时更新
 */
	@Transactional(readOnly = false)
    public void saveFieldData(DcHiveField dcHiveField) throws Exception{
		String remarks = DcStringUtils.formatJsonStr(dcHiveField.getRemarks());//将得到的字段信息进行整合
		List<DcHiveField> dataList = new ArrayList<DcHiveField>();
		 JSONArray ls = new JSONArray(remarks);//对String类型转换成jsonArray格式
		 if (ls.length()>0) {  
             for (int i=0;i<ls.length();i++) {  
                 JSONObject json = new JSONObject(ls.get(i).toString());  //对String类型转换成json格式
                 DcHiveField obj  = new DcHiveField();//创建DcHiveField对象obj
                 obj.setId(IdGen.uuid());//设置obj的id
                 obj.setBelong2Id(dcHiveField.getBelong2Id());//设置obj的belong2Id
                 obj.setFieldName(json.get("fieldName").toString());//设置obj的fieldName
                 obj.setFieldType(json.get("fieldType").toString());//设置obj的fieldType
                 obj.setFieldDesc(json.get("fieldDesc").toString());//设置obj的fieldDesc
                 obj.setIsKey("0");//设置obj的isKey
                 dataList.add(obj);
                 }  
         } 
			//将字段信息存到dc_hive_field数据库中，第一步，先删除所有与之相关的字段；第二步批量插入
		 DcCommonUtils.delDataByField("BELONG2_ID", dcHiveField.getBelong2Id(), "dc_hive_field");
		 dcHiveFieldService.batchInsert(dataList);
}

	/**
	 * @方法名称: loadHiveData 
	 * @实现功能:将数据导入到hive数据表中
	 * @param obj
	 * @create by cdd at 2017年1月14日 下午2:49:28
	 */
	public String loadHiveData(DcHiveTable obj) {
		String sql = "load data inpath '" + obj.getLocation() + "' into table " + obj.getTableName();	// 构建sql
		return executeSql(sql);
	}

	/**
	 * @方法名称: getHiveDatabase
	 * @实现功能: 获取hive表空间
	 * @return
	 * @create by cdd at 2017年1月14日 下午1:46:55
	 */
	public List<DcHiveDatabase> getHiveDatabase() {
		DcHiveDatabase dcHiveDatabase = new DcHiveDatabase();
		dcHiveDatabase.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);// 将delFlag设置为0
		List<DcHiveDatabase> dbList = dcHiveDatabaseService.findList(dcHiveDatabase);// 获取hive表空间
		if (CollectionUtils.isEmpty(dbList)) {// 判断dbList是否为空
			return null;
		} else {
			return dbList;
		}
	}

	/**
	 * @方法名称: loadTableData 
	 * @实现功能: 导入数据表数据
	 * @param obj
	 * @return
	 * @create by cdd at 2017年1月16日 上午9:52:19
	 */
/*	public List<Map<String, Object>> loadTableData(DcHiveTable obj) {
		// 得到表字段
		String fieldsql = "select FIELD_NAME AS 'fieldName' from dc_hive_field where BELONG2_ID = '" + obj.getId()
				+ "' ";
		List<Map<String, Object>> fieldList = DcCommonUtils.queryDataBySql(fieldsql);// 得到字段信息

		DcHdfsFile file = new DcHdfsFile();
		file.setFilePath(obj.getLocation());
		dcHdfsFileService.loadFileContent(file, "10");
		String fileContent = file.getContent();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotBlank(fileContent)) {
			String[] arr = fileContent.split("\n");

			for (int i = 0; i < fieldList.size(); i++) {
				for (String line : arr) {
					Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
					for (String data : line.split(DcHiveTable.separatorMap.get(obj.getSeparatorSign()))) {
						dataMap.put(fieldList.get(i).get("fieldName").toString(),
								line.split(DcHiveTable.separatorMap.get(obj.getSeparatorSign()))[i]);
						dataList.add(dataMap);
					}
				}
			}

		}
		return dataList;
	}*/
	
	/**
	 * @方法名称: batchInsert 
	 * @实现功能: 批量插入数据
	 * @param dataList
	 * @create by cdd at 2017年1月20日 下午4:16:48
	 */
	@Transactional(readOnly = false)
	public void batchInsert(List<DcHiveTable> dataList) {
		 dao.batchInsert(dataList);
	}

	/**
	 * @方法名称: getDataFromCatalog 
	 * @实现功能: 根据目录获取数据
	 * @param obj
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws Exception
	 * @create by cdd at 2017年1月20日 下午3:37:40
	 */
	public String getDataFromCatalog(DcHiveTable obj, boolean flag){
		String catalogPath = obj.getRemarks();
		String result = "";
		try {
			if (flag) {
				String sql = "load data inpath '" + catalogPath + "' overwrite into table " + obj.getTableName();// 构建sql
				result = executeSql(sql);
				flag = false;
			} else {
				String sql = "load data inpath '" + catalogPath + "' into table " + obj.getTableName();// 构建sql
				result = executeSql(sql);
			}
			return "success".equalsIgnoreCase(result)?"操作成功!":"操作失败!";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	}
	
	/**
	 * @方法名称: executeQuerySql
	 * @实现功能: 返回List<Map<String,Object>>数据
	 * @param sql		查询Sql
	 * @param limitNum	数据量, 小于0则查询所有数据 modified by peijd 2017-3-29
	 * @return
	 * @create by cdd at 2017年1月18日 下午2:06:43
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeQuerySql(String sql, int limitNum) {
		try {
			// restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("queryHive.restServer.url"));
			WebResource resource = client.resource(u);
			// 构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			if(0<limitNum){
				sql+=" limit "+ limitNum;
			}
			params.add("metaSql", sql);
			String result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
			
			String data = result.replace("\"[", "[").replace("]\"", "]").replace("\\", "");//对result进行变化
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(data, Map.class);//将data转成Json格式
			return (List<Map<String, Object>>) map.get("std_msg");//获取到result里面的数据，强制转化成List<Map<String, String>>格式
			
		} catch (Exception e) {//uri异常处理
			logger.error(e.getMessage());
		}
		return new ArrayList<Map<String, Object>>();
	}

	/**
	 * @方法名称: executeSql 
	 * @实现功能: 不返回List<Map<String,Object>数据
	 * @param sql
	 * @return 返回的是成功与否的标志
	 * @create by cdd at 2017年1月18日 上午10:58:34
	 */
	public String executeSql(String sql) {
		String result = "";
		try {
			// restful 地址
			URI u = new URI(DcPropertyUtils.getProperty("executeHive.restServer.url"));
			WebResource resource = client.resource(u);
			// 构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();
			params.add("metaSql", sql);
			result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);
		} catch (URISyntaxException e) {
			logger.error("--->executeSql: "+ e);
			return e.getMessage();
		}
		return result;
	}
	 
	
	/***
	 * 历史记录获取数据
	 */
	public List<Map<String,Object>> getHistoryMsg(){
		HistoryDbName historyDbName = new HistoryDbName();
		historyDbName.setHUser(UserUtils.getUser().getName());
		List<Map<String,Object>> list = historyDbNameDao.getHistoryMsg(historyDbName);
		return list;
	}
}
