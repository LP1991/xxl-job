/********************** 版权声明 *************************
 * 文件: DcDataInterfaceAuth.java
 * 包名: com.hlframe.modules.dc.dataexport.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月02日 14:53
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataexport.entity.DcDataInterfaceAuth
 * 元数据接口权限
 *
 * @author peijd
 * @create 2017-05-02 14:53
 **/
public class DcDataInterfaceAuth extends DataEntity<DcDataInterfaceAuth> {

    private String intfId; //INTF_ID 接口对象ID
    private String callUser;   //CALL_USER 访问用户ID
    private String callPswd;   //CALL_PSWD 访问密码
    private String callFreq;   //CALL_FREQ 访问频度

    public String getIntfId() {
        return intfId;
    }

    public void setIntfId(String intfId) {
        this.intfId = intfId;
    }

    public String getCallUser() {
        return callUser;
    }

    public void setCallUser(String callUser) {
        this.callUser = callUser;
    }

    public String getCallPswd() {
        return callPswd;
    }

    public void setCallPswd(String callPswd) {
        this.callPswd = callPswd;
    }

    public String getCallFreq() {
        return callFreq;
    }

    public void setCallFreq(String callFreq) {
        this.callFreq = callFreq;
    }
}
