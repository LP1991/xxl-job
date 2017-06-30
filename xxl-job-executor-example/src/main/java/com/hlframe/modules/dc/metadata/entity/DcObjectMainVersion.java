package com.hlframe.modules.dc.metadata.entity;

import com.hlframe.common.persistence.DataEntity;
import com.hlframe.modules.sys.entity.User;

/**
 * 元数据版本管理对象
 * Created by hgw on 2017/6/6.
 */
public class DcObjectMainVersion extends DataEntity<DcObjectMainVersion>{
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    private String jobId;//任务id
    private String objType;//对象类型
    private String remark;//更改信息，例如修改了名字
    private String active;//动作，例如 新增，修改，添加调度等等
    private String objName;//元数据名称
    private User user;//创建用户


    public static final String JOB_TYPE_ACTIVE_ADD = "1";//添加
    public static final String JOB_TYPE_ACTIVE_EDIT = "2";//编辑
    public static final String JOB_TYPE_ACTIVE_TASK = "3";//调度
    public static final String JOB_TYPE_ACTIVE_INTERFACE = "4";//接口字段关联

}
