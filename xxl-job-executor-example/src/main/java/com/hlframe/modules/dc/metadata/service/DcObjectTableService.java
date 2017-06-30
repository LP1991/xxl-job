/********************** 版权声明 *************************
 * 文件: DcObjectTableService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月06日 13:34
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.service.ServiceException;
import com.hlframe.common.utils.IdGen;
import com.hlframe.common.utils.MyBeanUtils;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.metadata.dao.DcObjectFieldDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectTableDao;
import com.hlframe.modules.dc.metadata.entity.*;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * com.hlframe.modules.dc.metadata.service.DcObjectTableService
 * 元数据-数据表对象Service
 *
 * @author peijd
 * @create 2017-05-06 13:34
 **/
@Service
@Transactional(readOnly = true)
public class DcObjectTableService extends CrudService<DcObjectTableDao, DcObjectTable> {

    @Autowired  //字段
    private DcObjectFieldDao fieldDao;

    @Autowired  //main对象Service
    private DcObjectMainService mainService;
    @Autowired  //接口对象Service
    private DcObjectIntfService intfService;

    //元数据存储Service
    private DcMetadataStroeService dcMetadataStroeService = null;
    //数据链路 Service
    private DcObjectLinkService linkService = null;

    /**
     * @方法名称: queryTableList
     * @实现功能: 查询数据表对象列表
     * @param objectMain
     * @return
     * @create by peijd at 2016年12月27日 下午3:29:53
     */
    public List<DcObjectTable> queryTableList(DcObjectMain objectMain) {
        return dao.findList(new DcObjectTable());
    }

    /**
     * @方法名称: buildTableInfoList
     * @实现功能: 构建数据表元数据 列表 待实现
     * @param param
     * @return
     * @create by peijd at 2017年3月7日 下午9:13:45
     */
    public List<DcObjectTableInfo> buildTableInfoList(DcObjectTableInfo param) {
        Assert.notNull(param);
        return dao.buildTableInfoList(param);
    }

    /**
     * @方法名称: buildTableInfo
     * @实现功能: 构建数据表对象
     * @param tableId
     * @return
     * @create by peijd at 2017年3月8日 上午9:51:38
     */
    public DcObjectTableInfo buildTableInfo(String tableId) {
        Assert.hasText(tableId);

        return dao.buildTableInfo(tableId);
    }

    /**
     * @方法名称: getTableByObjId
     * @实现功能: 根据对象Id 获取数据表对象
     * @param objId
     * @return
     * @create by peijd at 2017年1月17日 下午4:24:48
     */
    public DcObjectTable getTableByObjId(String objId) {
        Assert.hasText(objId);
        return dao.get(objId);
    }



    /**
     * @方法名称: buildUpdateTableForm
     * @实现功能: 根据表id查询字段详情
     * @param obj
     * @create by baog at 2017年3月29日 下午15:31:39
     */
    public DcObjectTable buildUpdateTableForm(DcObjectMain obj) throws Exception {
        String objId = obj.getId();
        Assert.hasText(objId);
        DcObjectTable tableMap = new DcObjectTable();

        //获取table对象
        DcObjectTable table = dao.get(objId);
        //将table的属性复制到map中
        if(table!=null){
            MyBeanUtils.copyBean2Bean(tableMap, table);
        }

        //构建objectMain
        DcObjectMain main = mainService.get(objId);
        //将obj的属性复制到map中
        MyBeanUtils.copyBean2Bean(tableMap, main);
        tableMap.setObjId(objId);
        return tableMap;
    }

    /**
     * Override
     * @方法名称: delete
     * @实现功能: 删除元数据对象 TODO: 待删除ES对象
     * @param dcObjectMain
     * @create by peijd at 2017年3月31日 下午4:54:20
     */
    @Transactional(readOnly = false)
    public void delete(DcObjectMain dcObjectMain) {
        //先删除字段
        fieldDao.deleteByBelong2Id(dcObjectMain.getId());

        DcObjectTable table = new DcObjectTable();
        table.setObjId(dcObjectMain.getId());
        dao.delete(table);

        mainService.delete(dcObjectMain);
    }


    /**
     * @方法名称: getTables4es
     * @实现功能: get the all tables with the catalog detail, using for initialize
     *        ES data
     * @return
     * @create by lp at 2017年3月8日
     */
    public List<Map<String, Object>> getTables4es(DcObjectMain obj) {
        if (null == obj) {
            obj = new DcObjectMain();
        }
        return dao.getTables4es(obj);
    }


    /**
     * @方法名称: getFieldsListByBelong2Id
     * @实现功能: 根据数据表Id 获取字段列表
     * @param tableId
     * @return
     * @create by peijd at 2017年1月17日 下午4:19:31
     */
    public List<Map<String, Object>> getFieldsListByBelong2Id(String tableId) {
        Assert.hasText(tableId);
        return fieldDao.getByBelong2Id(tableId);
    }

    /**
     * @方法名称: getByTableId
     * @实现功能: 根据数据表Id 获取字段列表
     * @params  [tableId]
     * @return  java.util.List<com.hlframe.modules.dc.metadata.entity.DcObjectField>
     * @create by peijd at 2017/4/24 19:33
     */
    public List<DcObjectField> getByTableId(String tableId) {
        Assert.hasText(tableId);
        return fieldDao.getDB2Id(tableId);
    }

    /**
     * @方法名称: getObjLinkService
     * @实现功能: 获取数据链路Service
     * @params
     * @return  com.hlframe.modules.dc.metadata.service.DcObjectLinkService
     * @create by peijd at 2017/5/4 20:34
     */
    private DcObjectLinkService getObjLinkService() {
        if(null==linkService){
            synchronized (DcObjectLinkService.class) {
                if(null==linkService){
                    linkService = SpringContextHolder.getBean(DcObjectLinkService.class);
                }
            }
        }
        return linkService;
    }


    /**
     * @方法名称: getDcMetadataStroeService
     * @实现功能: 获取元数据存储Service
     * @params  []
     * @return  com.hlframe.modules.dc.metadata.service.DcMetadataStroeService
     * @create by peijd at 2017/5/5 7:23
     */
    private DcMetadataStroeService getDcMetadataStroeService() {
        if(null==dcMetadataStroeService){
            synchronized (DcMetadataStroeService.class) {
                if(null==dcMetadataStroeService){
                    dcMetadataStroeService = SpringContextHolder.getBean(DcMetadataStroeService.class);
                }
            }
        }
        return dcMetadataStroeService;
    }
    
    /**
     * 
     * @方法名称: findTablePage 
     * @实现功能: 查询元数据分页数据
     * @param page
     * @param entity
     * @return
     * @create by hgw at 2017年5月8日 上午10:57:19
     */
    public Page<DcObjectTable> findTablePage(Page<DcObjectTable> page, DcObjectTable entity) {
		entity.setPage(page);
		page.setList(dao.findTableList(entity));
		return page;
	}

    /**
     *
     * @param
     * @return DcObjectTable
     * @实现功能: 根据表id查询数据表详情
     * *@create by hgw at 2017年5月8日 上午10:57:19
     */
    public DcObjectTable tnadmin(String tid) {
        return dao.tnadmin(tid);
    }


}
