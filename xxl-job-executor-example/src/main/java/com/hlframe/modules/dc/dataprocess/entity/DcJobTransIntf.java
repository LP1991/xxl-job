/********************** 版权声明 *************************
 * 文件: dcJobTransIntf.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年04月17日 11:29
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/**
 * com.hlframe.modules.dc.dataprocess.entity.dcJobTransIntf
 * 接口采集配置
 *
 * @author peijd
 * @create 2017-04-17 11:29
 **/
public class DcJobTransIntf extends DataEntity<DcJobTransIntf>{

    /**  采集接口类别 1-restful webservice; 2-soap webservice **/
    public static final String JOB_TYPE_RESTFUL = "1";
    public static final String JOB_TYPE_SOAP = "2";

    /**  接口数据存储目标 0-mysql; 1-hdfs; 2-hive; 3-hbase **/
    public static final String TAR_TYPE_MYSQL = "0";
    public static final String TAR_TYPE_HDFS = "1";
    public static final String TAR_TYPE_HIVE = "2";
    public static final String TAR_TYPE_HBASE = "3";

    /**  任务状态: 0-编辑; 1-测试; 2-初始化; 9-添加任务 **/
    public static final String TASK_STATUS_EDIT = "0";
    public static final String TASK_STATUS_TEST = "1";
    public static final String TASK_STATUS_INIT = "2";
    public static final String TASK_STATUS_TASK = "9";

    private String jobName; //任务名称  job_name
    private String jobDesc; //任务描述  job_desc
    private String jobType; //任务类别  job_type    接口类别(1-restful ws; 2-soap ws)
    private String tarType; //目标类别 tar_type    目标类别(0-mysql; 1-hdfs; 2-hive; 4-hbase)
    private String logDir; //任务日志  log_dir
    private String status; //任务状态  status
    private Integer sortNum; //排序  sort_num

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getTarType() {
        return tarType;
    }

    public void setTarType(String tarType) {
        this.tarType = tarType;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
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
}
