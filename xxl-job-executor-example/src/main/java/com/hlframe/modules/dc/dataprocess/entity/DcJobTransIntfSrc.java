/********************** 版权声明 *************************
 * 文件: DcJobTransIntfSrc.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 11:34
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfSrc
 * 接口数据采集源配置
 *
 * @author peijd
 * @create 2017-04-17 11:34
 **/
public class DcJobTransIntfSrc extends DataEntity<DcJobTransIntfSrc> {
    /** Restful接口采集 调用方式  **/
    public static final String RESTFUL_TYPE_GET = "get";
    public static final String RESTFUL_TYPE_POST = "post";

    private  String jobId; //任务ID jobId
    private  String wsPath; //wsdl路径path ws_path
    private  String wsNamespace; //ws端口 ws_namespace
    private  String wsMethod; //ws调用方法 ws_method
    private  String restUrl; //restUrl  rest_url
    private  String restType; //rest传参方式(post/get) rest_type
    private  String restContentType; //rest参数类型 rest_contentType
    private  String params; //参数列表多个参数以&分割 params

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
}
