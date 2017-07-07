/********************** 版权声明 *************************
 * 文件名: DcJobLog.java
 * 包名: com.hlframe.task.entity
 * 版权:	杭州华量软件  xxl-job
 * 职责: 
 ********************************************************
 *
 * 创建者：Primo   创建时间：2017/7/7
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.task.entity;

import java.util.Date;

public interface DcJobLog {
    String getTaskid();

    void setTaskid(String taskid);

    String getRunid();

    void setRunid(String runid);

    Date getStartdate();

    void setStartdate(Date startdate);

    Date getEnddate();

    void setEnddate(Date enddate);

    String getStatus();

    void setStatus(String status);
}
