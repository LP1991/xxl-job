/********************** 版权声明 *************************
 * 文件名: DcHiveFunction.java
 * 包名: main.java.com.hleast.bean.po
 * 版权:	杭州华量软件  gatherdata
 * 职责: 
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/5/24
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;


public class DcHiveFunction extends DataEntity<DcHiveFunction> {

    private String funcName;
    private String funcDesc;
    /**  type : 1-udf; 2-udaf; 3-udtf **/
    public static final String HIVE_FUNC_UDF = "1";
    public static final String HIVE_FUNC_UDAF = "2";
    public static final String HIVE_FUNC_UDTF = "3";
    private String funcType;
    private String jarPath;
    private String classPath;
    private String jarName;
    private String funcDemo;
    public static final String HIVE_FUNC_STATUS_UPLOAD = "1";  // 上传jar 成功
    public static final String HIVE_FUNC_STATUS_REGISTER = "2";  // 注册成功
    private String status;
    private Integer sortNum;


    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getFuncDesc() {
        return funcDesc;
    }

    public void setFuncDesc(String funcDesc) {
        this.funcDesc = funcDesc;
    }

    public String getFuncType() {
        return funcType;
    }

    public void setFuncType(String funcType) {
        this.funcType = funcType;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getFuncDemo() {
        return funcDemo;
    }

    public void setFuncDemo(String funcDemo) {
        this.funcDemo = funcDemo;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcHiveFunction that = (DcHiveFunction) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (funcName != null ? !funcName.equals(that.funcName) : that.funcName != null) return false;
        if (funcDesc != null ? !funcDesc.equals(that.funcDesc) : that.funcDesc != null) return false;
        if (funcType != null ? !funcType.equals(that.funcType) : that.funcType != null) return false;
        if (jarPath != null ? !jarPath.equals(that.jarPath) : that.jarPath != null) return false;
        if (funcDemo != null ? !funcDemo.equals(that.funcDemo) : that.funcDemo != null) return false;
        if (delFlag != null ? !delFlag.equals(that.delFlag) : that.delFlag != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (remarks != null ? !remarks.equals(that.remarks) : that.remarks != null) return false;
        if (sortNum != null ? !sortNum.equals(that.sortNum) : that.sortNum != null) return false;
        if (createBy != null ? !createBy.equals(that.createBy) : that.createBy != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (updateBy != null ? !updateBy.equals(that.updateBy) : that.updateBy != null) return false;
        if (updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (funcName != null ? funcName.hashCode() : 0);
        result = 31 * result + (funcDesc != null ? funcDesc.hashCode() : 0);
        result = 31 * result + (funcType != null ? funcType.hashCode() : 0);
        result = 31 * result + (jarPath != null ? jarPath.hashCode() : 0);
        result = 31 * result + (funcDemo != null ? funcDemo.hashCode() : 0);
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        result = 31 * result + (sortNum != null ? sortNum.hashCode() : 0);
        result = 31 * result + (createBy != null ? createBy.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateBy != null ? updateBy.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        return result;
    }
}
