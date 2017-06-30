/********************** 版权声明 *************************
 * 文件: DcParamRegex.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月18日 13:34
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import java.io.Serializable;

/**
 * com.hlframe.modules.dc.dataprocess.entity.DcParamRegex
 * 参数对象
 *
 * @author peijd
 * @create 2017-05-18 13:34
 **/
public class DcParamRegex implements Serializable {

    private String paramName;
    private String paramUsage;
    private String paramDesc;
    private String paramValue;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamUsage() {
        return paramUsage;
    }

    public void setParamUsage(String paramUsage) {
        this.paramUsage = paramUsage;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
