/********************** 版权声明 *************************
 * 文件名: DcDataSourceDao.java
 * 包名: com.hlframe.modules.dc.metadata.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月7日 下午1:40:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.dao;


import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.metadata.entity.DcDataSource;

/** 
 * @类名: com.hlframe.modules.dc.metadata.dao.DcDataSourceDao.java 
 * @职责说明: 数据源连接 接入层
 * @创建者: peijd
 * @创建时间: 2016年11月7日 下午1:40:39
 */
@MyBatisDao
public interface DcDataSourceDao extends CrudDao<DcDataSource> {
    public DcDataSource getlist(String ids);

}
