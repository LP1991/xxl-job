/********************** 版权声明 *************************
 * 文件名: DcObjeMonitor.java
 * 包名: com.hlframe.schedule.entity
 * 版权:	杭州华量软件  xxl-job
 * 职责:
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/7/5
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.schedule.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * 监控DB，HDFS,FTP公共类
 * Created by Administrator on 2017/6/14.
 */
public class DcObjeMonitor extends DataEntity<DcObjeMonitor> {

    private static final long serialVersionUID = 1L;
    private String banem;
    private  String tid;
    private String type;//类型
    private String retum;//返回结果
    private String abnorma;//备注
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRetum() {
        return retum;
    }

    public void setRetum(String retum) {
        this.retum = retum;
    }

    public String getAbnorma() {
        return abnorma;
    }

    public void setAbnorma(String abnorma) {
        this.abnorma = abnorma;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBanem() {
        return banem;
    }

    public void setBanem(String banem) {
        this.banem = banem;
    }


}
