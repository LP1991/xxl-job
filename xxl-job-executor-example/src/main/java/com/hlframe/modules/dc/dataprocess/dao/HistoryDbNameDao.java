package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.HistoryDbName;

import java.util.List;

/**
 * Hive历史记录
 * @author hgw
 *
 */
@MyBatisDao
public interface HistoryDbNameDao extends CrudDao<HistoryDbName>{

	List getHistoryMsg(HistoryDbName historyDbName);
}
