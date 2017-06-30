/********************** 版权声明 *************************
 * 文件: DcJobTransIntfTar.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 11:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfTar
 * 接口数据采集目标配置
 *
 * @author peijd
 * @create 2017-04-17 11:39
 **/
public class DcJobTransIntfTar extends DataEntity<DcJobTransIntfTar> {

    private String jobId; //jobId
    private String tarName; //目标名称 tarName
    private String connId; //数据源链接Id connId
    private String schemaName; //数据库Schema schemaName
    private String createFlag; //数据表创建标记 create_flag

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTarName() {
        return tarName;
    }

    public void setTarName(String tarName) {
        this.tarName = tarName;
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

    public String getCreateFlag() {
        return createFlag;
    }

    public void setCreateFlag(String createFlag) {
        this.createFlag = createFlag;
    }
}
