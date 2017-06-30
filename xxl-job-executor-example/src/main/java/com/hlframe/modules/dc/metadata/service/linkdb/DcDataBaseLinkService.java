/********************** 版权声明 *************************
 * 文件名: DBHandleService.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月21日 下午3:23:16
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb;

import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.DcDataBaseLinkService.java
 * @职责说明: 数据库连接 统一处理类
 * @创建者: peijd
 * @创建时间: 2016年11月21日 下午3:23:16
 */
public interface DcDataBaseLinkService {

    Logger logger = LoggerFactory.getLogger(DcDataBaseLinkService.class);

    /**
     * @方法名称: querySchemaList
     * @实现功能: 查询数据库Schema列表
     * @param dataSourceId
     * @return
     * @throws Exception
     * @create by peijd at 2016年11月22日 下午2:29:49
     */
    List<Map<String, Object>> querySchemaList(String dataSourceId) throws Exception;

    /**
     * @方法名称: querySingleData
     * @职责说明: 查询动态Sql, 返回单个数据值
     * @参数: @param dataSourceId
     * @参数: @param metaSql
     * @参数: @return
     * @参数: @throws Exception
     * @返回: String
     * @创建者: pjd
     * @创建时间: 2015年12月22日 下午7:17:29
     */
    String querySingleData(String dataSourceId, String metaSql) throws Exception;

    /**
     * @方法名称: querySingleRow
     * @职责说明: 查询动态Sql, 返回单行记录
     * @参数: @param dataSourceId
     * @参数: @param metaSql
     * @参数: @return
     * @参数: @throws Exception
     * @返回: Map<String,Object>
     * @创建者: pjd
     * @创建时间: 2015年12月22日 下午7:19:31
     */
    Map<String, Object> querySingleRow(String dataSourceId, String metaSql) throws Exception;

    /**
     * @方法名称: queryMetaSql
     * @职责说明: 查询动态Sql, 返回list列表
     * @参数: @param dataSourceId
     * @参数: @param metaSql
     * @参数: @return
     * @参数: @throws Exception
     * @返回: List<Map<String,String>>
     * @创建者: pjd
     * @创建时间: 2015年12月16日 下午2:57:47
     */
    List<Map<String, Object>> queryMetaSql(String dataSourceId, String metaSql) throws Exception;

    /**
     * @方法名称: queryTableNames
     * @职责说明: 根据数据源 查询schema的所有表
     * @参数: @param dataSourceId	数据源ID
     * @参数: @param schema			schema
     * @参数: @return
     * @参数: @throws Exception
     * @返回: List<Map<String,String>>
     * @创建者: pjd
     * @创建时间: 2015年12月16日 下午3:04:47
     */
    List<Map<String, Object>> queryTableNames(String dataSourceId, String schema) throws Exception;

    /**
     * @方法名称: queryTableNames
     * @职责说明: 根据数据源 查询schema的所有表
     * @参数: @param dataSourceId	数据源ID
     * @参数: @param schema			schema
     * @参数: @param filter			过滤条件
     * @参数: @return
     * @参数: @throws Exception
     * @返回: List<Map<String,Object>>
     * @创建者: pjd
     * @创建时间: 2016年3月15日 下午7:35:56
     */
    List<Map<String, Object>> queryTableNames(String dataSourceId, String schema, String filter) throws Exception;

    /**
     * @方法名称: queryTableColumns
     * @职责说明: 查询数据表列名
     * @参数: @param dataSourceId
     * @参数: @param tableName
     * @参数: @return
     * @参数: @throws Exception
     * @返回: List<Map<String,Object>>
     * @创建者: pjd
     * @创建时间: 2015年12月16日 下午3:49:16
     */
    List<Map<String, Object>> queryTableColumns(String dataSourceId, String schema, String tableName) throws Exception;

    /**
     * @方法名称: queryTableDataNum
     * @职责说明: 查询数据表记录数
     * @参数: @param dataSourceId
     * @参数: @param page
     * @参数: @return
     * @参数: @throws Exception
     * @返回: Integer
     * @创建者: pjd
     * @创建时间: 2015年12月22日 下午6:55:53
     */
    Integer queryTableDataNum(String dataSourceId, DcQueryPage page) throws Exception;

    /**
     * @方法名称: queryTablePageData
     * @职责说明: 查询数据表分页数据
     * @参数: @param dataSourceId
     * @参数: @param page
     * @参数: @return
     * @参数: @throws Exception
     * @返回: List<Map<String,Object>>
     * @创建者: pjd
     * @创建时间: 2015年12月22日 下午6:54:13
     */
    List<Map<String, Object>> queryTablePageData(String dataSourceId, DcQueryPage page) throws Exception;

    /**
     * @方法名称: buildTableKeySql
     * @实现功能: 构建数据表主键查询SQL, 拼接多个表的主键查询SQL
     * @param tableList Map中内容为(tbName, 表名),(keyField,主键字段),(filter,过滤条件)
     * @return
     * @throws Exception
     * @create by peijd at 2016年9月13日 上午10:31:35
     */
    String buildTableKeySql(List<Map<String, String>> tableList) throws Exception;

    /**
     * @方法名称: buildTableKeyField
     * @实现功能: 查询数据表的主键字段
     * @param tbNameList 数据表名称
     * @return
     * @throws Exception
     * @create by peijd at 2016年9月13日 上午10:45:31
     */
    String buildTableKeyField(List<String> tbNameList) throws Exception;

    /**
     * @方法名称: queryLimitMetaSql
     * @实现功能:分页查询
     * @param dbSrcId
     * @param sql
     * @param dataNum
     * @return
     * @create by cdd at 2016年11月25日 上午11:34:23
     */
    List<Map<String, Object>> queryLimitMetaSql(String dbSrcId, String sql, int dataNum)  throws Exception;

    /**
     * @方法名称: runScript
     * @实现功能: 执行脚本 DDL/DML
     * @param dbSrcId	数据源连接Id
     * @param script	运行脚本
     * @return
     * @throws Exception
     * @create by peijd at 2016年12月2日 下午2:02:19
     */
    DcDataResult runScript(String dbSrcId, String script) throws Exception;

    /**
     * @方法名称: buildTranslateSql
     * @实现功能: 构建数据转换脚本
     * @param linkList
     * @param transProcess
     * @return
     * @create by peijd at 2016年12月21日 下午5:52:26
     */
    String buildTranslateSql(List<DcObjectLink> linkList, DcTransDataSub transProcess);

    /**
     * @方法名称: buildTableName
     * @实现功能: 构建数据表名称
     * @param schemaName
     * @param tableName
     * @return
     * @create by peijd at 2016年12月23日 下午2:04:04
     */
    String buildTableName(String schemaName, String tableName) throws Exception;

    /**
     * @方法名称: existTable
     * @实现功能: 是否存在源表
     * @param id
     * @param tableName
     * @return
     * @create by peijd at 2017年2月27日 下午7:54:02
     */
    boolean existTable(String id, String tableName) throws Exception;

    /**
     * @方法名称: clearTableDate
     * @实现功能: 清空目标表数据内容
     * @param id
     * @param tableName
     * @create by peijd at 2017年2月27日 下午7:54:51
     */
    void clearTableData(String id, String tableName) throws Exception;


    /**
     * @方法名称: copyData2TarTable
     * @实现功能: 将源表数据复制到目标表中
     * @param id
     * @param srcTable
     * @param tarTable
     * @create by peijd at 2017年2月27日 下午8:03:38
     */
    void copyData2TarTable(String id, String srcTable, String tarTable) throws Exception;

    /**
     * @方法名称: createTable
     * @实现功能: 根据已有表 创建目标表
     * @param id
     * @param srcTable
     * @param tarTable
     * @param copyDataFlag
     * @create by peijd at 2017年2月27日 下午8:05:41
     */
    void createTable(String id, String srcTable, String tarTable, boolean copyDataFlag) throws Exception;

    /**
     * 根据表名/字段创建 数据表
     * @param connId        连接Id
     * @param schemaName    schema
     * @param tarName        数据表名
     * @param fields        字段列表
     * @param tableDesc
     * @throws Exception
     * @create by peijd at 2017年4月20日 下午9:18:34
     */
    void createTable(String connId, String schemaName, String tarName, List<DcObjectField> fields, String tableDesc) throws Exception;

    /**
     * 批量插入数据表内容
     * @param connId
     * @param schemaName
     * @param tarName
     * @param fieldList
     * @param dataList
     */
    DcDataResult batchInsertData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList);

    /**
     * 批量更新数据表内容
     * @param connId		连接Id
     * @param schemaName	schema
     * @param tarName		数据表名
     * @param fieldList		字段列表
     * @param dataList		数据列表
     * @return
     * @create by peijd at 2017年5月11日 上午9:53:23
     */
    DcDataResult batchUpdateData(String connId, String schemaName, String tarName, List<DcObjectField> fieldList, List<Map<String, Object>> dataList);
}
