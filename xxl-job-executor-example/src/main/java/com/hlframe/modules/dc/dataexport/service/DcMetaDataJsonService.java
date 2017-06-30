/********************** 版权声明 *************************
 * 文件: DcMetaDataJson.java
 * 包名: com.hlframe.modules.dc.dataexport.service
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月02日 15:21
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.service;

import com.hlframe.common.service.BaseService;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataexport.entity.DcDataInterface;
import com.hlframe.modules.dc.metadata.entity.DcObjectField;
import com.hlframe.modules.dc.metadata.entity.DcObjectTableInfo;
import com.hlframe.modules.dc.metadata.entity.linkdb.DcQueryPage;
import com.hlframe.modules.dc.metadata.service.DcObjectTableService;
import com.hlframe.modules.dc.metadata.service.linkdb.DbHandleService;
import com.hlframe.modules.dc.utils.*;
import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * com.hlframe.modules.dc.dataexport.service.DcMetaDataJsonService
 * 元数据对象转换json格式
 *
 * @author peijd
 * @create 2017-05-02 15:21
 **/
@Service
@Transactional(readOnly = true)
public class DcMetaDataJsonService extends BaseService {

    @Autowired  //元数据-Table对象 Service
    private DcObjectTableService objTableService;
    @Autowired  //数据库连接Service
    private DbHandleService dbHandleService;

    /**
     * @方法名称: convertData2Json
     * @实现功能: 将元数据对象(数据表) 转换为接口对象(Json)
     * @param   interFace   接口对象
     * @return
     * @create by peijd at 2017/5/2 15:25
     */
    @Transactional(readOnly = false)
    public DcDataResult convertData2Json(DcDataInterface interFace){
        Assert.hasText(interFace.getObjId());
        //数据表对象
        DcObjectTableInfo table = objTableService.buildTableInfo(interFace.getObjId());
        Assert.notNull(table, "未找到数据表["+interFace.getObjId()+"]!");

        //排序字段
        if(StringUtils.isBlank(interFace.getOrderField())){
            List<DcObjectField> fieldList = objTableService.getByTableId(table.getId());
            Assert.notEmpty(fieldList, "尚未初始化数据表, 请联系管理员!");
            //以第一个字段  作为排序字段
            interFace.setOrderField(fieldList.get(0).getFieldName());
        }

        if("hive".equalsIgnoreCase(table.getDbType())){ //转换hive表数据
            return buildHiveTable2Json(table, interFace);
        }else if("hbase".equalsIgnoreCase(table.getDbType())){  //转换hbase表数据
            return buildHbaseTable2Json(table, interFace);
        }else { //转换普通数据 RMSDB ,mysql
            return  buildTableData2Json(table, interFace);
        }
    }

    /**
     * @方法名称: buildTableData2Json
     * @实现功能: 将数据表对象 转换为json  数据分页/数据总量 字段排序
     * @param table
     * @param interFace
     * @return  com.hlframe.modules.dc.common.dao.DcDataResult
     * @create by peijd at 2017/5/2 16:52
     */
    private DcDataResult buildTableData2Json(DcObjectTableInfo table, DcDataInterface interFace) {
        DcDataResult result = new DcDataResult();
        try{
            //查询数据表 分页对象
            DcQueryPage page = new DcQueryPage();
            page.setTableName(dbHandleService.buildTableName(table.getTableLink(), table.getDbDataBase(), table.getTableName()));   //表名
            page.setFieldList(Arrays.asList(interFace.getTargetFields().split(",")));   //字段
            page.setFilter(interFace.getIntfParams());  //过滤条件
            page.setOrderBy(interFace.getOrderField()); //排序字段
            page.setStart(interFace.getPageNum());  //当前页码
            page.setLimit(interFace.getPageSize()); //每页数量
            //查询数据总数
            int totleNum = dbHandleService.queryTableDataNum(table.getTableLink(), page);
            Assert.isTrue(totleNum>0, "没有符合条件的数据!");
            result.setTotleNum(totleNum);

            List<Map<String, Object>> dataList = dbHandleService.queryPageData(table.getTableLink(), page);
            Assert.notEmpty(dataList, "没有返回结果数据!");

            //返回对象
            result.setRst_flag(true);
            result.setRemarks("返回查询结果: "+dataList.size()+"条记录!");
//            result.setRst_std_msg(DESUtils.getEncStr(DcJsonUtils.object2Json(dataList)));
            result.setRst_std_msg(Des.strEnc(DcJsonUtils.object2Json(dataList), interFace.getEncryptKey(), null, null));
        }catch(Exception e){
            result.setRst_flag(false);
            result.setRst_err_msg(e.getMessage());
            logger.error("-->buildTableData2Json: ", e);
        }
        return result;
    }


    /**
     * @方法名称: buildHbaseTable2Json
     * @实现功能: 将hbase表对象 转换为json  TODO: 查询指定列 的字段, 分页查询, 字段排序 暂不实现
     * @param table
     * @param interFace
     * @return  com.hlframe.modules.dc.common.dao.DcDataResult
     * @create by peijd at 2017/5/2 16:32
     */
    private DcDataResult buildHbaseTable2Json(DcObjectTableInfo table, DcDataInterface interFace) {
        DcDataResult result = new DcDataResult();
        try{
            //传递参数对象
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("tableName",table.getTableName());    //表名
            jsonParam.put("family",table.getDbDataBase());     //列族

            //查询条件  以'and'进行分隔解析
            if(DcStringUtils.isNotBlank(interFace.getIntfParams())){
                int idx = 0;
                for (String param: interFace.getIntfParams().split(" and ")) {
                    if(param.indexOf("=")>0){
                        //根据=号进行解析
                        jsonParam.put("qualifier"+idx,param.split("=")[0]);  //字段
                        jsonParam.put("columnVal"+idx,param.split("=")[1]);  //字段值
                        idx++;
                    }
                }
            }
            //查询hbase对象
            JSONObject rst = HbaseClientUtils.scanAndFilter(jsonParam);
            if(BooleanUtils.toBoolean(rst.get("rst_flag").toString())){
                result.setRst_flag(true);
                result.setRemarks(rst.get("remarks").toString());
//                result.setRst_std_msg(DESUtils.getEncStr(rst.get("rst_std_msg").toString()));
                result.setRst_std_msg(Des.strEnc(DcJsonUtils.object2Json(rst.get("rst_std_msg").toString()), interFace.getEncryptKey(), null, null));
            }else{
                result.setRst_flag(false);
                result.setRst_err_msg(rst.get("rst_err_msg").toString());
            }
        }catch(Exception e){
            result.setRst_flag(false);
            result.setRst_err_msg(e.getMessage());
            logger.error("-->buildHbaseTable2Json: ", e);
        }
        return result;
    }

    /**
     * @方法名称: buildHiveTable2Json
     * @实现功能: 将hive表对象 转换为json
     * @param table
     * @param interFace
     * @return  com.hlframe.modules.dc.common.dao.DcDataResult
     * @create by peijd at 2017/5/2 15:48
     */
    private DcDataResult buildHiveTable2Json(DcObjectTableInfo table, DcDataInterface interFace) {
        DcDataResult result = new DcDataResult();
        try{
            //表空间.数据表
            String tableName = DcStringUtils.isNotBlank(table.getDbDataBase())?table.getDbDataBase()+"."+table.getTableName():table.getTableName();

            //查询脚本
            StringBuilder metaSql = new StringBuilder(256);
            int totleNum = 0;
            //分页查询 页码>1表示分页  dc_rownum是页码字段
            if(interFace.getPageNum()>0){
                //记录总数
                totleNum = HiveClientUtils.queryTableDataNum(tableName, buildFilter(interFace.getIntfParams()));
                Assert.isTrue(totleNum>0, "没有符合条件的数据!");
                result.setTotleNum(totleNum);

                //分页 起始值/终止值
                int startNum = (interFace.getPageNum()-1)*interFace.getPageSize()+1;
                int endNum = interFace.getPageNum()*interFace.getPageSize();
                metaSql.append("select * from (select row_number() over (order by ").append(interFace.getOrderField());
                metaSql.append(") as dc_rownum , ").append(interFace.getTargetFields()).append(" from ");
                metaSql.append(tableName).append(" ").append(buildFilter(interFace.getIntfParams())).append(") t ");
                metaSql.append("where dc_rownum between ").append(startNum).append(" and ").append(endNum);

            }else{
                metaSql.append("select ").append(interFace.getTargetFields()).append(" from ");
                metaSql.append(tableName).append(" ").append(buildFilter(interFace.getIntfParams()));
                metaSql.append(" order by ").append(interFace.getOrderField());
            }
            //查询hive 对象 -1:不限制数量
            List<Map<String, Object>> dataList = HiveClientUtils.buildHiveMeteMap(metaSql.toString(), -1);
            Assert.notEmpty(dataList, "没有返回结果数据!");

            //计算数据总数
            if(totleNum==0){
                result.setTotleNum(dataList.size());
            }
            //返回对象
            result.setRst_flag(true);
            result.setRemarks("返回查询结果: "+dataList.size()+"条记录!");
            result.setRst_std_msg(Des.strEnc(DcJsonUtils.object2Json(dataList), interFace.getEncryptKey(), null, null));
        }catch(Exception e){
            result.setRst_flag(false);
            result.setRst_err_msg(e.getMessage());
            logger.error("-->buildHiveTable2Json: ", e);
        }
        return result;
    }

    /**
     * @方法名称: buildFilter
     * @实现功能: 构建过滤条件
     * @param intfParams
     * @return  java.lang.String
     * @create by peijd at 2017/5/8 21:01
     */
    private String buildFilter( String intfParams) {
        if(DcStringUtils.isBlank(intfParams)){
            return "";
        }
        StringBuilder filter = new StringBuilder(64);
        filter.append(" where 1=1 and ").append(intfParams);
        return filter.toString();
    }
}
