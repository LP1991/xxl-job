package com.hlframe.modules.dc.schedule.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * 监控DB，HDFS,FTP公共类
 * Created by Administrator on 2017/6/14.
 */
public class DcObjeMonitor extends DataEntity<DcObjeMonitor> {

    private static final long serialVersionUID = 1L;
    private String tid;
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
}
