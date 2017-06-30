/********************** 版权声明 *************************
 * 文件名: DcObjectInterface.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午9:36:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.sys.entity.Office;
import com.hlframe.modules.sys.entity.User;

/**
 * @类名: com.hlframe.modules.dc.metadata.entity.DcObjectInterface.java 
 * @职责说明: 元数据-接口对象表  update by peijd 增加接口字段
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午9:36:39
 */
public class DcObjectInterface extends DataEntity<DcObjectInterface> {
    /**
     * 接口类别
     */
    public static final  String INTFC_TYPE_SOAP = "soap";
    public static final  String INTFC_TYPE_RESTFUL = "restful";

    // 接口来源- 内部发布
    public static final String INTFC_SRC_TYPE_PUBLISH = "pub2Interface";

    private String objId; // 接口id   obj_id
    private String intfcType; // 接口类型(Soap WebService; Restful WebService)   intfc_type
    private String intfcSrcId; // 元数据对象ID(通过元数据发布接口关联)  intfc_src_id
    private String intfcProtocal; // 接口协议(待用)  intfc_protocal
    private String intfcUrl; // 接口地址   intfc_url
    private String intfcNamespace; // 接口空间名称   intfc_namespace
    private String intfcUser; // 连接用户   intfc_user
    private String intfcPswd; // 连接密码   intfc_pswd
    private String intfcMethod; // 接口方法   intfc_method
    private String intfcCalltype; // 调用方式(post;get)   intfc_calltype
    private String intfcContype; // 传参方式(rest post调用时传参)   intfc_contype
    private String intfcParams; // 参数列表(多个参数以&分割)   intfc_params
    private String intfcFields; // 字段列表(*表示全部字段, 多个参数以,分割)   intfc_fields
    private String orderFields; // 排序字段(指定接口数据的排序方式)   ORDER_FIELDS

    /** 复合元数据main对象信息 用于传参以及列表分页处理  **/
    private String objCode;	//对象编码
    private String objName;	//对象名称
    private String objType;	//对象类型
    private String systemId;	//业务系统Id
    private String objDesc;	//对象描述
    private String managerPer;//责任人
    private String managerOrg;//责任部门
    private String jobId;   //任务ID
    //JOB_SRC_FLAG  是否数据中心元数据,即存储于集群环境的元数据(Y-是;N-否)
    protected String jobSrcFlag;

    private User user;//业务负责人
    private Office office;//业务部门
    protected int accre;		//权限  页面权限控制
    private String applySrc;    //我的接口数据, 数据来源
    private String applyUserId;    //我的接口数据, 用户ID


    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getIntfcType() {
        return intfcType;
    }

    public void setIntfcType(String intfcType) {
        this.intfcType = intfcType;
    }

    public String getIntfcSrcId() {
        return intfcSrcId;
    }

    public void setIntfcSrcId(String intfcSrcId) {
        this.intfcSrcId = intfcSrcId;
    }

    public String getIntfcProtocal() {
        return intfcProtocal;
    }

    public void setIntfcProtocal(String intfcProtocal) {
        this.intfcProtocal = intfcProtocal;
    }

    public String getIntfcUrl() {
        return intfcUrl;
    }

    public void setIntfcUrl(String intfcUrl) {
        this.intfcUrl = intfcUrl;
    }

    public String getIntfcNamespace() {
        return intfcNamespace;
    }

    public void setIntfcNamespace(String intfcNamespace) {
        this.intfcNamespace = intfcNamespace;
    }

    public String getIntfcUser() {
        return intfcUser;
    }

    public void setIntfcUser(String intfcUser) {
        this.intfcUser = intfcUser;
    }

    public String getIntfcPswd() {
        return intfcPswd;
    }

    public void setIntfcPswd(String intfcPswd) {
        this.intfcPswd = intfcPswd;
    }

    public String getIntfcMethod() {
        return intfcMethod;
    }

    public void setIntfcMethod(String intfcMethod) {
        this.intfcMethod = intfcMethod;
    }

    public String getIntfcCalltype() {
        return intfcCalltype;
    }

    public void setIntfcCalltype(String intfcCalltype) {
        this.intfcCalltype = intfcCalltype;
    }

    public String getIntfcContype() {
        return intfcContype;
    }

    public void setIntfcContype(String intfcContype) {
        this.intfcContype = intfcContype;
    }

    public String getIntfcParams() {
        return intfcParams;
    }

    public void setIntfcParams(String intfcParams) {
        this.intfcParams = intfcParams;
    }

    public String getIntfcFields() {
        return intfcFields;
    }

    public void setIntfcFields(String intfcFields) {
        this.intfcFields = intfcFields;
    }

    public String getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(String orderFields) {
        this.orderFields = orderFields;
    }

    public String getObjCode() {
        return objCode;
    }

    public void setObjCode(String objCode) {
        this.objCode = objCode;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getObjDesc() {
        return objDesc;
    }

    public void setObjDesc(String objDesc) {
        this.objDesc = objDesc;
    }

    public String getManagerPer() {
        return managerPer;
    }

    public void setManagerPer(String managerPer) {
        this.managerPer = managerPer;
    }

    public String getManagerOrg() {
        return managerOrg;
    }

    public void setManagerOrg(String managerOrg) {
        this.managerOrg = managerOrg;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public int getAccre() {
        return accre;
    }

    public void setAccre(int accre) {
        this.accre = accre;
    }

    public String getApplySrc() {
        return applySrc;
    }

    public void setApplySrc(String applySrc) {
        this.applySrc = applySrc;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getJobSrcFlag() {
        return jobSrcFlag;
    }

    public void setJobSrcFlag(String jobSrcFlag) {
        this.jobSrcFlag = jobSrcFlag;
    }
}
