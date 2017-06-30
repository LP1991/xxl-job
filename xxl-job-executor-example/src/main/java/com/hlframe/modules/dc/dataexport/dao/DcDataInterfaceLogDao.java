/********************** 版权声明 *************************
 * 文件: DcDataInterfaceLogDao.java
 * 包名: com.hlframe.modules.dc.dataexport.dao
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月04日 15:53
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataexport.entity.DcDataInterfaceLog;

/**
 * com.hlframe.modules.dc.dataexport.dao.DcDataInterfaceLogDao
 * 接口访问日志 Dao
 *
 * @author peijd
 * @create 2017-05-04 15:53
 **/
@MyBatisDao
public interface DcDataInterfaceLogDao extends CrudDao<DcDataInterfaceLog> {

}
