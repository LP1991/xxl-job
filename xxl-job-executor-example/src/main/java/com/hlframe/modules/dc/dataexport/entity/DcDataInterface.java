/********************** 版权声明 *************************
 * 文件: dcDataInterface.java
 * 包名: com.hlframe.modules.dc.dataexport.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月02日 14:40
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.entity;

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.dc.utils.DcStringUtils;

/**
 * com.hlframe.modules.dc.dataexport.entity.dcDataInterface
 * 元数据接口数据对象
 *
 * @author peijd
 * @create 2017-05-02 14:40
 **/
public class DcDataInterface extends DataEntity<DcDataInterface> {

    private static final long serialVersionUID = 1L;

    private String objId; //OBJ_ID  对象ID
    private String objType; //OBJ_TYPE  对象类型(1-数据表; 2-接口)
    private String objRange; //obj_range  访问范围(1-公有; 2-私有)
    private String intfType; //INTF_TYPE  接口类别(1-rest; 2-webservice)
    private String intfName; //INTF_NAME  接口名称
    private String intfDesc; //INTF_DESC  接口描述
    private String callType; //CALL_TYPE  接口传参方式(POST/GET)
    private String intfFields; //INTF_FIELDS 接口字段(数据表查询字段, 多个字段以,分割)
    private String contentType; //CONTENT_TYPE 接口参数字符集
    private String intfParams; //INTF_PARAMS  接口参数
    private String status; //STATUS 状态
    private String orderField; //排序字段
    private Integer sortNum; //SORT_NUM 显示顺序

    //支持 接口分页查询
    private Integer pageSize;   //每页数量
    private Integer pageNum;   //当前页码
    private String encryptKey;    // 加/解密key

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjRange() {
        return objRange;
    }

    public void setObjRange(String objRange) {
        this.objRange = objRange;
    }

    public String getIntfType() {
        return intfType;
    }

    public void setIntfType(String intfType) {
        this.intfType = intfType;
    }

    public String getIntfName() {
        return intfName;
    }

    public void setIntfName(String intfName) {
        this.intfName = intfName;
    }

    public String getIntfDesc() {
        return intfDesc;
    }

    public void setIntfDesc(String intfDesc) {
        this.intfDesc = intfDesc;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getIntfParams() {
        return intfParams;
    }

    public void setIntfParams(String intfParams) {
        this.intfParams = intfParams;
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

    public String getIntfFields() {
        return intfFields;
    }

    public void setIntfFields(String intfFields) {
        this.intfFields = intfFields;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    /**
     * 获取字段列表
     * @return
     */
    public String getTargetFields() {
        if(DcStringUtils.isBlank(intfFields)){
            return "*";
        }

        return intfFields;
    }
}
