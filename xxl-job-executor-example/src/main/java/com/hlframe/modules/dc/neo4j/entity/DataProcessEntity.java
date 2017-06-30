/********************** 版权声明 *************************
 * 文件名: DataProcessEntity.java
 * 包名: com.hlframe.modules.dc.neo4j.entity
 * 版权:	杭州华量软件  hldc
 * 职责: store the process of data, use for uploading to neo4j
 ********************************************************
 *
 * 创建者：00074   创建时间：2017/4/26
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.neo4j.entity;

import java.util.List;

public class DataProcessEntity {
    public static final String NEO4J_LABEL_SOURCE = "source";
    public static final String NEO4J_LABEL_PROCESS = "process";
    public static final String NEO4J_LABEL_TARGET = "target";
    private String objId;
    private String name;
    private String label;
    private String description;
    private List<DataProcessEntity> subNodes;


    public DataProcessEntity(String objId, String name, String label, String description) {
        this.objId = objId;
        this.name = name;
        this.label = label;
        this.description = description;
    }

    public DataProcessEntity(String objId, String name, String label) {
        this.objId = objId;
        this.name = name;
        this.label = label;
    }

    public DataProcessEntity(String objId, String label) {
        this.objId = objId;
        this.label = label;
    }

    public DataProcessEntity(String objId) {
        this.objId = objId;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DataProcessEntity> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<DataProcessEntity> subNodes) {
        this.subNodes = subNodes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
