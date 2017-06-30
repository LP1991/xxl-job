/********************** 版权声明 *************************
 * 文件: DcJobTransIntfService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service.translate
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 10:11
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.MyBeanUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.common.service.DcCommonService;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransIntfDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransIntfSrcDao;
import com.hlframe.modules.dc.dataprocess.dao.DcJobTransIntfTarDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransData;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntf;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainDao;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.dc.metadata.service.DcMetadataStroeService;
import com.hlframe.modules.dc.metadata.service.DcObjectAuService;
import com.hlframe.modules.dc.metadata.service.DcObjectLinkService;
import com.hlframe.modules.dc.metadata.service.DcObjectTableService;
import com.hlframe.modules.dc.metadata.service.linkdb.DbHandleService;
import com.hlframe.modules.dc.metadata.service.linkdb.impl.DcMysqlHandle;
import com.hlframe.modules.dc.schedule.entity.DcTaskMain;
import com.hlframe.modules.dc.schedule.service.task.DcTaskService;
import com.hlframe.modules.dc.utils.*;
import com.hlframe.modules.sys.utils.UserUtils;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * com.hlframe.modules.dc.dataprocess.service.translate.DcJobTransIntfService
 * 接口数据采集 Srevice
 *
 * @author peijd
 * @create 2017-04-17 10:11
 **/
@Service
@Transactional(readOnly = true)
public class DcJobTransIntfService extends CrudService<DcJobTransIntfDao, DcJobTransIntf> implements DcTaskService {

    @Autowired  //接口源设置
    private DcJobTransIntfSrcDao srcDao;
    @Autowired  //接口目标设置
    private DcJobTransIntfTarDao tarDao;
    @Autowired	//数据链路 Service
    private DcObjectLinkService linkService;
    @Autowired	//元数据存储Service
    private DcMetadataStroeService dcMetadataStroeService;
    @Autowired	//权限表service
    private DcObjectAuService dcObjectAuService;
    @Autowired  //数据库连接Service
    private DbHandleService dbHandleService;
    @Autowired  //元数据对象Service
    private DcObjectTableService objTableService;

    @Autowired
    private DcMysqlHandle mysqlHandle;

    @Autowired //源数据service
    private DcObjectMainDao dcObjectMainDao;

    @Autowired//ESservice
    private DcCommonService dcCommonService;

    /**
     * @方法名称: runTask
     * @实现功能: 运行采集任务
     * @params  [jobId]
     * @return  com.hlframe.modules.dc.common.dao.DcDataResult
     * @create by peijd at 2017/4/18 9:19
     */
    @Transactional(readOnly = false)
    public DcDataResult runTask(String jobId) throws Exception {
        Assert.hasText(jobId);
        DcDataResult taskResult = new DcDataResult();

        //运行任务, 解析接口对象, 保存到数据库中
        DcJobTransIntfForm formData = buildFormData(jobId);
        //获取接口返回结果
        String resultData = getIntfResultData(formData);
        //将json转为List<Map>对象
        List<Map<String, Object>> dataList = DcJsonUtils.parseJson2List(resultData);
        Assert.notEmpty(dataList);
        //获取接口目标表字段
        List<DcObjectField> fieldList = objTableService.getByTableId(jobId);
        Assert.notEmpty(fieldList);//TASK_STATUS_TEST
        //批量更新数据
        taskResult = dbHandleService.batchUpdateData(formData.getConnId(), formData.getSchemaName(), formData.getTarName(), fieldList, dataList);
        //执行成功, 记录日志, 保存状态
        DcJobTransIntf obj = new DcJobTransIntf();
        obj.setId(jobId);
        if(DcJobTransIntf.TASK_STATUS_EDIT.equals(formData.getStatus()) || DcJobTransIntf.TASK_STATUS_INIT.equals(formData.getStatus())){
            //更新为测试状态
            obj.setStatus(DcJobTransIntf.TASK_STATUS_TEST);
        }
        //记录日志
        obj.setRemarks(taskResult.getRst_flag()?taskResult.getRst_std_msg():(taskResult.getRst_err_msg().length()>1500?taskResult.getRst_err_msg().substring(0,1500)+"...":taskResult.getRst_err_msg()));
        obj.preUpdate();
        dao.update(obj);

        return taskResult;
    }

    /**
     * @方法名称: buildFormData
     * @实现功能: 构建编辑表单对象
     * @params  [jobId]
     * @return  com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm
     * @create by peijd at 2017/4/17 20:40
     */
    public  DcJobTransIntfForm buildFormData(String jobId){
        Assert.hasText(jobId);
        return dao.buildIntfForm(jobId);
    }

    /**
     * @方法名称: previewDbData
     * @实现功能: 预览采集数据
     * @params  [jobId]
     * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @create by peijd at 2017/4/18 10:08
     */
    public List<Map<String, Object>> previewDbData(String jobId) {
        Assert.hasText(jobId);
        //构建数据对象
        DcJobTransIntfForm jobData = buildFormData(jobId);
        Assert.notNull(jobData);
        Assert.hasText(jobData.getTarName());

        StringBuilder tableSql = new StringBuilder(64).append("select * from ");
        tableSql.append(dbHandleService.buildTableName(jobData.getConnId(), jobData.getSchemaName(), jobData.getTarName())).append(" ");
        //获取返回结果 默认显示
        return dbHandleService.queryLimitMetaSql(jobData.getConnId(),tableSql.toString(), Integer.parseInt(DcPropertyUtils.getProperty("extractdb.preview.datanum", "50")));
    }

    /**
     * @方法名称: checkJobName
     * @实现功能: 校验jobName是否存在
     * @params  [intf]
     * @return  com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntf
     * @create by peijd at 2017/4/18 10:13
     */
    public DcJobTransIntf checkJobName(DcJobTransIntf intf) {
        Assert.hasText(intf.getJobName());
        return dao.checkJobName(intf);
    }

    /**
     * @方法名称: getJobName
     * @实现功能: 根据名字获取对象
     * @params  [jobName]
     * @return  com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntf
     * @create by peijd at 2017/4/18 13:29
     */
    public DcJobTransIntf getJobName(String jobType, String jobName) {
        DcJobTransIntf param  = new DcJobTransIntf();
        param.setJobName(jobName);
        param.setJobType(jobType);
        return checkJobName(param);
    }


    /**
     * @方法名称: buildIntfList
     * @实现功能: 构建查询接口列表
     * @params  [intf]
     * @return  java.util.List<com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm>
     * @create by peijd at 2017/4/18 10:28
     */
    public List<DcJobTransIntfForm> buildIntfList(DcJobTransIntfForm intf) {
        return dao.buildIntfList(intf);
    }

    /**
     * @方法名称: buildPage
     * @实现功能: 构建分页对象
     * @params  [page, obj]
     * @return  com.hlframe.common.persistence.Page<com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm>
     * @create by peijd at 2017/4/18 10:30
     */
    public Page<DcJobTransIntfForm> buildPage(Page<DcJobTransIntfForm> page, DcJobTransIntfForm obj) {
        obj.setPage(page);
        obj.getSqlMap().put("dsf", dataScopeFilter(obj.getCurrentUser(), "o", "u"));
        page.setList(dao.buildIntfList(obj));
        return page;
    }

    /**
     * @方法名称: getAu
     * @实现功能: 发起权限申请
     * @params  [obj]
     * @return  void
     * @create by peijd at 2017/4/18 10:35
     */
    @Transactional(readOnly = false)
    public void getAu(DcJobTransIntf obj) throws Exception {
        DcJobTransIntf typ=dao.get(obj);
        DcObjectAu dcObjectAu = new DcObjectAu();
        dcObjectAu.setUserId(UserUtils.getUser().getId());
        dcObjectAu.setFileId(obj.getId());
        dcObjectAu.setStatus("未处理");
        if(typ.getJobType().equals(1)) {
            dcObjectAu.setFrom("7");
        }else{
            dcObjectAu.setFrom("8");
        }
        if(!DcStringUtils.isNotNull(dcObjectAuService.get(dcObjectAu))){
            dcObjectAuService.save(dcObjectAu);
            throw new Exception("已向管理员申请该任务操作权限，请等待管理员审核!");
        }else {
            dcObjectAuService.classify(dcObjectAu);
        }



    }

    /**
     * @方法名称: updateStatus
     * @实现功能: 更新任务状态
     * @params  [objId, status]
     * @return  void
     * @create by peijd at 2017/4/18 10:51
     */
    @Transactional(readOnly = false)
    public void updateStatus(String objId, String status){
        Assert.hasText(objId);
        status = StringUtils.isEmpty(status)? DcJobTransData.TASK_STATUS_EDIT:status;
        DcJobTransIntf obj = new DcJobTransIntf();
        obj.setId(objId);
        obj.setStatus(status);
        obj.preUpdate();
        dao.update(obj);
    }

    /**
     * @方法名称: initTableField
     * @实现功能: 根据接口表, 初始化目标表 以及字段:名称/类型/长度/描述/别名(remarks)
     * @param  form 表单对象
     * @return  java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @create by peijd at 2017/4/18 14:32
     */
    public Map<String, Object> initTableField(DcJobTransIntfForm form) throws Exception {
        Assert.notNull(form);
        List<Map<String,Object>> colList = null;
        //根据接口 初始化目标表字段, 初始化字段元数据
        String resultData = getIntfResultData(form);
        //开始解析字段
        List<String> parseFieldList = DcJsonUtils.parseJsonKey(resultData);
        Assert.notEmpty(parseFieldList, "未获取到接口返回结果字段信息!");

        //字段列表
        List<DcObjectField> fields = new ArrayList<DcObjectField>();
        DcObjectField dcObjectField = null;
        //如果源表已存在, 则初始化字段
        if(!form.getCreateFlag()){
            List<Map<String, Object>> fieldList = mysqlHandle.queryTableColumns(form.getConnId(), form.getSchemaName(), form.getTarName());
            Assert.notEmpty(fieldList,"未获取到数据表["+form.getTarName()+"]的字段信息!");

            for(Map<String, Object> fieldMap: fieldList){
                dcObjectField = new DcObjectField();
                dcObjectField.setFieldName(DcStringUtils.getObjValue(fieldMap.get("COLNAME")));		    //字段名
                dcObjectField.setFieldDesc(DcStringUtils.getObjValue(fieldMap.get("COLUMNCOMMENT")));	//字段描述
                dcObjectField.setFieldType(DcStringUtils.getObjValue(fieldMap.get("COLTYPE")));	        //字段类型
                dcObjectField.setFieldLeng(Integer.parseInt(DcStringUtils.getObjValue(fieldMap.get("COLLENG"),"0")));  //字段长度
                dcObjectField.setIsKey("Y".equalsIgnoreCase(DcStringUtils.getObjValue(fieldMap.get("COLKEY")))?1:0);                //是否主键
                dcObjectField.setIsNull(DcStringUtils.getObjValue(fieldMap.get("COLISNULL")));           //允许空值
                //对比填充字段
                for (String field: parseFieldList){
                    if(dcObjectField.getFieldName().equalsIgnoreCase(field.trim())){
                        dcObjectField.setRemarks(field);
                        break;
                    }
                }
                fields.add(dcObjectField);
            }

        }else{  //初始化列表字段
            for (String field: parseFieldList){
                dcObjectField = new DcObjectField();
                dcObjectField.setFieldName(field);		//字段名
                dcObjectField.setFieldDesc(field);	    //字段描述
                dcObjectField.setFieldType("String");	//字段类型
                dcObjectField.setFieldLeng(64);         //字段长度
                dcObjectField.setIsKey(0);              //是否主键
                dcObjectField.setIsNull("1");           //允许空值
                dcObjectField.setRemarks(field);
                fields.add(dcObjectField);
            }
        }
        //返回对象
        Map<String, Object> rstMap = new HashMap<String, Object>();
        rstMap.put("fieldList", fields);
        rstMap.put("intfFields", parseFieldList);
        return rstMap;
    }


    /**
     * @方法名称: saveTarTable
     * @实现功能: 保存目标表/字段 元数据, 设置对象链路关系
     * @params  [obj]
     * @return  void
     * @create by peijd at 2017/4/20 17:02
     */
    @Transactional(readOnly = false)
    public void saveTarTable(DcJobTransIntfForm form) throws Exception {
        DcObjectMainVersion dcObjectMainVersion = new DcObjectMainVersion();
        //字段列表
        String fieldsJson =  DcStringUtils.formatJsonStr(URLDecoder.decode(form.getRemarks(),"utf-8"));
        //数据表描述
        String tableDesc = form.getJobDesc();
        form = buildFormData(form.getId());
        //验证目标表是否存在
        if(form.getCreateFlag() && dbHandleService.existTable(form.getConnId(), form.getSchemaName(), form.getTarName())){
            throw new ServiceException("目标表["+form.getSchemaName()+"."+form.getTarName()+"]已存在,请检查!");
        }

        //保存元数据 包括: 采集接口/目标表/表字段信息...
        DcObjectMain srcMain = new DcObjectMain();					//元数据 主体对象
        //根据jobId 获取源对象
        DcObjectMain param = new DcObjectMain();
        param.setJobId(form.getId());
        param.setJobSrcFlag("N");
        srcMain =  dcObjectMainDao.get(param);
        if(null==srcMain){
            srcMain = new DcObjectMain();
            srcMain.setId(IdGen.uuid());
            dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
        }else{
            dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
        }
        srcMain.setJobId(form.getId());							        //任务Id
        srcMain.setJobType(DcObjectMain.JOB_TYPE_EXTRACT_INTF);	//接口采集
        srcMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_NO);		//外部数据
        srcMain.setObjCode(form.getJobName());					//任务编码
        srcMain.setObjName(form.getJobName());
        srcMain.setObjDesc(form.getJobDesc());					//备注信息
        srcMain.setObjType(DcObjectMain.OBJ_TYPE_INTER);
        //接口对象
        DcObjectInterface intfc = new DcObjectInterface();
        intfc.setId(srcMain.getId());
        intfc.setObjId(srcMain.getId());
        intfc.setIntfcParams(form.getParams());
        if(DcJobTransIntf.JOB_TYPE_SOAP.equals(form.getJobType())){    //soap webservice
            intfc.setIntfcType(DcObjectInterface.INTFC_TYPE_SOAP);
            intfc.setIntfcUrl(form.getWsPath());
            intfc.setIntfcNamespace(form.getWsNamespace());
            intfc.setIntfcMethod(form.getWsMethod());

        }else if(DcJobTransIntf.JOB_TYPE_RESTFUL.equals(form.getJobType())){    //restul webservice
            intfc.setIntfcType(DcObjectInterface.INTFC_TYPE_RESTFUL);
            intfc.setIntfcUrl(form.getRestUrl());
            intfc.setIntfcCalltype(form.getRestType());
            intfc.setIntfcContype(form.getRestContentType());
        }
        dcObjectMainVersion.setObjType(DcObjectMain.OBJ_TYPE_INTER);
        //元数据版本设置信息
        dcObjectMainVersion.setJobId(form.getId());
        dcMetadataStroeService.insertTableVersion(srcMain,dcObjectMainVersion,intfc);
        dcMetadataStroeService.obj2MySQL(srcMain, intfc);

        //目标表 元数据
        dcObjectMainVersion = new DcObjectMainVersion();
        param.setJobId(form.getId());
        param.setJobSrcFlag("Y");
        DcObjectMain tarMain = dcObjectMainDao.get(param);					//元数据 主体对象
//        srcMain =  dcObjectMainDao.get(param);
        if(null==tarMain){
            tarMain = new DcObjectMain();
            tarMain.setId(IdGen.uuid());
            dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_ADD);
        }else{
            dcObjectMainVersion.setActive(DcObjectMainVersion.JOB_TYPE_ACTIVE_EDIT);
        }
        tarMain.setJobId(form.getId());
        tarMain.setJobSrcFlag(DcObjectMain.JOB_SRC_FLAG_YES);		//外部数据
        tarMain.setJobType(DcObjectMain.JOB_TYPE_EXTRACT_INTF);	//接口采集
        tarMain.setObjCode(form.getJobName());					//任务编码
        tarMain.setObjName(form.getTarName());
        tarMain.setObjDesc(form.getJobDesc());					//备注信息
        tarMain.setObjType(DcObjectMain.OBJ_TYPE_TABLE);
        //数据表
        DcObjectTable dcObjectTable = new DcObjectTable();
        dcObjectTable.setId(tarMain.getId());                  //id
        dcObjectTable.setObjId(tarMain.getId());	        //主表Id
        dcObjectTable.setDbDataBase(form.getSchemaName());	//表空间
        dcObjectTable.setTableName(form.getTarName());	    //表名
        dcObjectTable.setTableLink(form.getConnId());		//数据库连
        dcObjectTable.setDbType("mysql");

        //数据量记录数
        StringBuilder metaSql = new StringBuilder(64);

        //查询原表中最大值 不同数据库的不用表名 进行区分处理
        metaSql.append("SELECT COUNT(*) AS COUNT FROM ").append(dbHandleService.buildTableName(form.getConnId(),form.getSchemaName(),form.getTarName()));
        List<Map<String, Object>> rstList = dbHandleService.queryMetaSql(form.getConnId(), metaSql.toString());

        if(CollectionUtils.isNotEmpty(rstList)){
            String count = DcStringUtils.getObjValue(rstList.get(0).get("COUNT"));
            dcObjectTable.setDataNum(Integer.parseInt(count));
        }

        List<DcObjectField> fields = new ArrayList<DcObjectField>();
        DcObjectField dcObjectField = null;
//        logger.debug(fieldsJson);
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> fieldList = mapper.readValue(fieldsJson, List.class);
        for(Map<String, Object> field: fieldList){
            dcObjectField = new DcObjectField();
            dcObjectField.setFieldName(DcStringUtils.getObjValue(field.get("fieldName")));							//字段名
            dcObjectField.setFieldType(DcStringUtils.getObjValue(field.get("fieldType")));						    //字段类型
            dcObjectField.setFieldLeng(Integer.parseInt(DcStringUtils.getObjValue(field.get("fieldLenth"), "12")));     //长度
            dcObjectField.setIsKey(Integer.parseInt(DcStringUtils.getObjValue(field.get("isKey"), "0")));	//是否主键
            dcObjectField.setIsNull(DcStringUtils.getObjValue(field.get("isNull"), "1"));			//允许为空
            dcObjectField.setDefaultVal(DcStringUtils.getObjValue(field.get("defaultVal")));				        //默认值
            dcObjectField.setRemarks(DcStringUtils.getObjValue(field.get("remarks")));                             //备注(源字段)
            dcObjectField.setFieldDesc(DcStringUtils.getObjValue(field.get("fieldDesc")));                         //描述
            dcObjectField.setBelong2Id(tarMain.getId());
            fields.add(dcObjectField);
        }
        //创建目标表
        if(form.getCreateFlag()){
            dbHandleService.createTable(form.getConnId(), form.getSchemaName(), form.getTarName(), fields, tableDesc);
        }

        //元数据版本设置信息
        dcObjectMainVersion.setJobId(form.getId());
        dcMetadataStroeService.insertTableVersion(srcMain,dcObjectMainVersion,dcObjectTable);
        //保存目标数据对象
        dcMetadataStroeService.obj2MySQL(tarMain, dcObjectTable, fields);
        //保存链路信息
        linkService.configObjLinkByExtractIntf(form.getId(), srcMain.getId(), tarMain.getId());
        //更改采集任务状态: 初始化
        updateStatus(form.getId(), DcJobTransIntf.TASK_STATUS_INIT);
    }

    /**
     * @方法名称: getIntfResultData
     * @实现功能: 获取接口结果数据
     * @params  [formData]
     * @return  java.lang.String
     * @create by peijd at 2017/4/18 16:50
     */
    private String getIntfResultData(DcJobTransIntfForm formData) throws Exception {
        Assert.notNull(formData);
        //接口采集源 设置
        DcJobTransIntfSrc intfSrc = new DcJobTransIntfSrc();
        MyBeanUtils.copyBean2Bean(intfSrc, formData);
        String resultData = null;
        //restful webservice 服务
        if(DcJobTransIntf.JOB_TYPE_RESTFUL.equals(formData.getJobType())) {
            if(formData.getJobName().startsWith("bd_wifi_page_")){    //以bd_wifi开头的任务(wifi分页接口数据), 通过项目定制类实现,暂且根据任务名扩展...
                resultData = BdRestWsParseUtils.getBdWifiWsPageData(intfSrc);

            }else if(formData.getJobName().startsWith("bd_wifi_")){  //以bd_wifi开头的任务(wifi接口数据), 通过项目定制类实现,暂且根据任务名扩展...
                resultData = BdRestWsParseUtils.getBdWifiWsData(intfSrc);

            }else if(formData.getJobName().startsWith("bd_park_page_")){ //以bd_park_page_开头的任务(停车场接口数据), 通过项目定制类实现,暂且根据任务名扩展...
                resultData = BdRestWsParseUtils.getBdParkWsPageData(intfSrc);
            }else{
                resultData = DcJsonUtils.getRestfulWsData(intfSrc);
            }

        }else if(DcJobTransIntf.JOB_TYPE_SOAP.equals(formData.getJobType())){
            resultData = DcSoapWsUtils.getSoapWsData(intfSrc);
        }
        Assert.hasText(resultData, "未获取到接口接口, 请检查!");
        return resultData;
    }

    /**
     * @方法名称: checkTarName
     * @实现功能: 检查表名时是否存在
     * @param tarName       目标表名
     * @param connId        数据源连接ID
     * @param schemaName    表空间
     * @param tarType       存储目标:0-mysql
     * @return
     * @create by peijd at 2017/4/19 21:47
     */
    public boolean checkTarName(String tarName, String connId, String schemaName, String tarType) {
        //如果是mysql  根据连接查询表是否存在
        if(DcJobTransIntf.TAR_TYPE_MYSQL.equals(tarType)){
            return !dbHandleService.existTable(connId, schemaName, tarName);
        }
        return false;
    }

}
