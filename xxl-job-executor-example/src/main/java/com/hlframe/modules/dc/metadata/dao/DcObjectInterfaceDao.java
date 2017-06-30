/********************** 版权声明 *************************
 * 文件名: DcObjectInterfaceDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年12月3日 上午11:27:32
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectInterface;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;

import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcObjectInterfaceDao.java 
 * @职责说明: 元数据接口Dao
 * @创建者: yuzh
 * @创建时间: 2016年12月3日 上午11:27:32
 */
@MyBatisDao
public interface DcObjectInterfaceDao extends CrudDao<DcObjectInterface> {

    List<Map<String, Object>> getInterfaces4es(DcObjectMain obj);

    /**
     * @方法名称: buildIntfInfo
     * @实现功能: 构建接口对象信息
     * @params
     * @return
     * @create by peijd at 2017/5/5 7:43
     */
    DcObjectInterface buildIntfInfo(String id);

    List<DcObjectInterface> buildTableInfoList(DcObjectInterface intf);

    /**
     * 查看我申请的数据接口
     * @param intf
     * @return
     */
    List<DcObjectInterface> findMyDataList(DcObjectInterface intf);
}
