/********************** 版权声明 *************************
 * 文件名: DcHiveFuncDao.java
 * 包名: com.hlframe.modules.dc.dataprocess.dao
 * 版权:	杭州华量软件  hldc
 * 职责: 
 ********************************************************
 *
 * 创建者：Primo  创建时间：2017/5/24
 * 文件版本：V1.0
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveFunction;

@MyBatisDao
public interface DcHiveFuncDao extends CrudDao<DcHiveFunction> {
}
