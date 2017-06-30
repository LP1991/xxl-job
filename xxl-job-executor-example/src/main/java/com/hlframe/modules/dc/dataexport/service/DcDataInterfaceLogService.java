/********************** 版权声明 *************************
 * 文件: DcDataInterfaceLogService.java
 * 包名: com.hlframe.modules.dc.dataexport.service
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月04日 16:46
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataexport.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.dataexport.dao.DcDataInterfaceLogDao;
import com.hlframe.modules.dc.dataexport.entity.DcDataInterfaceLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * com.hlframe.modules.dc.dataexport.service.DcDataInterfaceLogService
 * TODO: ${DESCRIPTION}
 *
 * @author peijd
 * @create 2017-05-04 16:46
 **/
@Service
@Transactional(readOnly = true)
public class DcDataInterfaceLogService extends CrudService<DcDataInterfaceLogDao, DcDataInterfaceLog> {

    /**
     * @方法名称: insertInterfaceLog
     * @实现功能: 保存接口访问日志
     * @param log   日志对象
     * @return  void
     * @create by peijd at 2017/5/4 16:50
     */
    @Transactional(readOnly = false)
    public void insertInterfaceLog(DcDataInterfaceLog log){
        Assert.notNull(log);
        Assert.isTrue(dao.insert(log)>0);
    }

}
