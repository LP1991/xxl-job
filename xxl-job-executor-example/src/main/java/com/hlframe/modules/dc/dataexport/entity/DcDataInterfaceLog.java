/********************** 版权声明 *************************
 * 文件: DcDataInterfaceLog.java
 * 包名: com.hlframe.modules.dc.dataexport.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月02日 14:56
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataexport.entity.DcDataInterfaceLog
 * 元数据接口日志
 *
 * @author peijd
 * @create 2017-05-02 14:56
 **/
public class DcDataInterfaceLog extends DataEntity<DcDataInterfaceLog> {

    private String intfId;  //INTF_ID   接口对象ID
    private String callBy;  //CALL_BY    调用者
    private String callParam;  //CALL_PARAM 客户端调用请求
    private String rtnFields;  //RTN_FIELDS 返回字段列表 为空则返回所有字段,多个字段以,分割
    private String clintIp;    //CLINT_IP  客户端IP
    private String startTime;   //START_TIME 调用开始时间
    private String endTime;     //END_TIME  调用结束时间
    private String rstFlag;    //RST_FLAG   调用结果标记(1-成功; 0-失败)
    private String rstMsg;      //RST_MSG 调用结果明细
    private int responseTime;   //响应时长 单位秒

    public String getIntfId() {
        return intfId;
    }

    public void setIntfId(String intfId) {
        this.intfId = intfId;
    }

    public String getCallBy() {
        return callBy;
    }

    public void setCallBy(String callBy) {
        this.callBy = callBy;
    }

    public String getCallParam() {
        return callParam;
    }

    public void setCallParam(String callParam) {
        this.callParam = callParam;
    }

    public String getClintIp() {
        return clintIp;
    }

    public void setClintIp(String clintIp) {
        this.clintIp = clintIp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRstFlag() {
        return rstFlag;
    }

    public void setRstFlag(String rstFlag) {
        this.rstFlag = rstFlag;
    }

    public String getRstMsg() {
        return rstMsg;
    }

    public void setRstMsg(String rstMsg) {
        this.rstMsg = rstMsg;
    }

    public String getRtnFields() {
        return rtnFields;
    }

    public void setRtnFields(String rtnFields) {
        this.rtnFields = rtnFields;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }
}
