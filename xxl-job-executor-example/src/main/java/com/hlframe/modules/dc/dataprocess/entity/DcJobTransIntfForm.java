/********************** 版权声明 *************************
 * 文件: DcJobTransIntface.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 15:25
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm
 * 接口数据采集表单对象
 *
 * @author peijd
 * @create 2017-04-17 15:25
 **/
public class DcJobTransIntfForm extends DataEntity<DcJobTransIntfForm> {
    private String jobName; //任务名称  job_name
    private String jobDesc; //任务描述  job_desc
    private String jobType; //任务类别  job_type    接口类别(1-restful ws; 2-soap ws)
    private String tarType; //目标类别 tar_type    目标类别(0-mysql; 1-hdfs; 2-hive; 4-hbase)
    private String logDir; //任务日志  log_dir
    private String status; //任务状态  status
    private Integer sortNum; //排序  sort_num

    private  String wsPath; //wsdl路径path ws_path
    private  String wsNamespace; //ws端口 ws_namespace
    private  String wsMethod; //ws调用方法 ws_method
    private  String restUrl; //restUrl  rest_url
    private  String restType; //rest传参方式(post/get) rest_type
    private  String restContentType; //rest参数类型 rest_contentType
    private  String params; //参数列表多个参数以&分割 params

    private String tarName; //目标名称 tarName
    private String connId; //数据源链接Id connId
    private String schemaName; //数据库Schema schemaName
    private Boolean createFlag; //数据表创建标记 create_flag

    /** 只读字段, 页面传参用 **/
    private Integer accre;  //操作权限

    public DcJobTransIntfForm(){}
    public DcJobTransIntfForm(DcJobTransIntf intf){
        this.id = intf.getId();
        this.jobName = intf.getJobName();
        this.jobDesc = intf.getJobDesc();
        this.jobType  = intf.getJobType();
        this.tarType = intf.getTarType();
        this.logDir = intf.getLogDir();
        this.status = intf.getStatus();
        this.sortNum = intf.getSortNum();
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getTarType() {
        return tarType;
    }

    public void setTarType(String tarType) {
        this.tarType = tarType;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getWsPath() {
        return wsPath;
    }

    public void setWsPath(String wsPath) {
        this.wsPath = wsPath;
    }

    public String getWsNamespace() {
        return wsNamespace;
    }

    public void setWsNamespace(String wsNamespace) {
        this.wsNamespace = wsNamespace;
    }

    public String getWsMethod() {
        return wsMethod;
    }

    public void setWsMethod(String wsMethod) {
        this.wsMethod = wsMethod;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestType() {
        return restType;
    }

    public void setRestType(String restType) {
        this.restType = restType;
    }

    public String getRestContentType() {
        return restContentType;
    }

    public void setRestContentType(String restContentType) {
        this.restContentType = restContentType;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getTarName() {
        return tarName;
    }

    public void setTarName(String tarName) {
        this.tarName = tarName;
    }

    public Integer getAccre() {
        return accre;
    }

    public void setAccre(Integer accre) {
        this.accre = accre;
    }


    public String getConnId() {
        return connId;
    }

    public void setConnId(String connId) {
        this.connId = connId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Boolean getCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(Boolean createFlag) {
        this.createFlag = createFlag;
    }
}
