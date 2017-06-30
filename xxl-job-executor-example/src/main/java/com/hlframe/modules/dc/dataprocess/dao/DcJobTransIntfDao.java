package com.hlframe.modules.dc.dataprocess.dao;

import com.hlframe.common.persistence.CrudDao;
import com.hlframe.common.persistence.annotation.MyBatisDao;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntf;
import com.hlframe.modules.dc.dataprocess.entity.DcJobTransIntfForm;

import java.util.List;

/**
 * com.hlframe.modules.dc.dataprocess.dao.DcJobTransIntfDao
 * 接口数据采集 Dao
 *
 * @author peijd
 * @create 2017-04-17 11:45
 **/
@MyBatisDao
public interface DcJobTransIntfDao extends CrudDao<DcJobTransIntf> {
    /**
     * 构建编辑表单对象
     * @param jobId
     * @return
     */
    DcJobTransIntfForm buildIntfForm(String jobId);

    /**
     * 验证接口采集任务名是否存在
     * @param intf
     * @return
     */
    DcJobTransIntf checkJobName(DcJobTransIntf intf);

    /**
     * 构建接口查询列表
     * @param intf
     * @return
     */
    List<DcJobTransIntfForm> buildIntfList(DcJobTransIntfForm intf);
}
