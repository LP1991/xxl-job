package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.metadata.dao.DcObjectMainVersionDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectMainVersion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 00052 on 2017/6/6.
 */
@Service
@Transactional(readOnly=true)
public class DcObjectMainVersionService extends CrudService<DcObjectMainVersionDao,DcObjectMainVersion>{
    /**
     * @param dcObjectMain
     * @实现功能: 数据权限过滤
     * @create by
     */
    public Page<DcObjectMainVersion> findPage(Page<DcObjectMainVersion> page, DcObjectMainVersion dcObjectMainVersion) {

        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        dcObjectMainVersion.getSqlMap().put("dsf", dataScopeFilter(dcObjectMainVersion.getCurrentUser(), "o", "u"));
        // 设置分页参数
        dcObjectMainVersion.setPage(page);
        // 执行分页查询
        page.setList(dao.findList(dcObjectMainVersion));
        return super.findPage(page, dcObjectMainVersion);
    }

    /**
     * @实现功能: 新增元数据版本数据
     * @create by hgw
     */
    @Transactional(readOnly = false)
    public void insertMain(DcObjectMainVersion dcObjectMainVersion){
        dao.insert(dcObjectMainVersion);
    }
}
