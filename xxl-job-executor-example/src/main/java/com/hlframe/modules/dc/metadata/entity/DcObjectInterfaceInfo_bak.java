/********************** 版权声明 *************************
 * 文件: DcObjectInterfaceInfo.java
 * 包名: com.hlframe.modules.dc.metadata.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 19:37
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.entity;

/**
 * com.hlframe.modules.dc.metadata.entity.DcObjectInterfaceInfo
 * 接口数据采集 接口完整信息
 *
 * @author peijd
 * @create 2017-04-17 19:37
 **/
public class DcObjectInterfaceInfo_bak extends DcObjectMain {

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

    public String getIntfcSrcId() {
        return intfcSrcId;
    }

    public void setIntfcSrcId(String intfcSrcId) {
        this.intfcSrcId = intfcSrcId;
    }

    public String getIntfcFields() {
        return intfcFields;
    }

    public void setIntfcFields(String intfcFields) {
        this.intfcFields = intfcFields;
    }
}
