/********************** 版权声明 *************************
 * 文件: DcObjectFileService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权: 杭州华量软件 hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年05月06日 13:41
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.MyBeanUtils;
import com.hlframe.modules.dc.metadata.dao.DcObjectFileDao;
import com.hlframe.modules.dc.metadata.dao.DcObjectFolderDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectFile;
import com.hlframe.modules.dc.metadata.entity.DcObjectFileInfo;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcObjectMainVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * com.hlframe.modules.dc.metadata.service.DcObjectFileService
 * 元数据-文件对象Service
 *
 * @author peijd
 * @create 2017-05-06 13:41
 **/

@Service
@Transactional(readOnly = true)
public class DcObjectFileService extends CrudService<DcObjectFileDao, DcObjectFile> {

    @Autowired
    private DcObjectFolderDao folderDao;
    @Autowired  //main对象Service
    private DcObjectMainService mainService;

    /**
     * @方法名称: queryFileList
     * @实现功能: 查询文件对象列表
     * @param objectMain
     * @return
     * @create by peijd at 2016年12月27日 下午3:30:44
     */
    public List<DcObjectFile> queryFileList(DcObjectMain objectMain) {
        return dao.findList(new DcObjectFile());
    }


    /**
     * @方法名称: getFileByObjId
     * @实现功能: 根据对象Id 获取数据表对象
     * @param objId
     * @return
     * @create by peijd at 2017年1月17日 下午5:10:21
     */
    public DcObjectFile getFileByObjId(String objId) {
        Assert.hasText(objId);
        return dao.get(objId);
    }

    /**
     * @方法名称: buildFileInfoList
     * @实现功能: 构建文件列表
     * @param param
     * @return
     * @create by peijd at 2017年3月8日 下午1:36:54
     */
    public List<DcObjectFileInfo> buildFileInfoList(DcObjectFileInfo param) {
        Assert.notNull(param);
        return dao.buildFileInfoList(param);
    }

    /**
     * @方法名称: buildFileInfo
     * @实现功能: 构建文件对象
     * @param fileId
     * @return
     * @create by peijd at 2017年3月8日 下午1:36:34
     */
    public DcObjectFileInfo buildFileInfo(String fileId) {
        Assert.hasText(fileId);
        return dao.buildFileInfo(fileId);
    }

    /**
     * @方法名称: buildFileInfo
     * @实现功能: 构建文件夹对象
     * @param fileId
     * @return
     * @create by peijd at 2017年3月8日 下午1:36:34
     */
    public DcObjectFileInfo buildFolderInfo(String fileId) {
        Assert.hasText(fileId);
        return folderDao.buildFolderInfo(fileId);
    }

    /**
     * @方法名称: getFiles4es
     * @实现功能: get the all files with the catalog detail, using for initialize ES
     *        data
     * @return
     * @create by lp at 2017年3月8日
     */
    public List<Map<String, Object>> getFiles4es(DcObjectMain obj) {
        if (null == obj) {
            obj = new DcObjectMain();
        }
        return dao.getFiles4es(obj);
    }



    /**
     * @方法名称: getFolder4es
     * @实现功能: get the all folders with the catalog detail, using for initialize
     *        ES data
     * @return
     * @create by lp at 2017年3月8日
     */
    public List<Map<String, Object>> getFolder4es(DcObjectMain obj) {
        if (null == obj) {
            obj = new DcObjectMain();
        }
        return folderDao.getFolders4es(obj);
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
        DcObjectFile file = new DcObjectFile();
        file.setObjId(dcObjectMain.getId());
        dao.delete(file);

        mainService.delete(dcObjectMain);

    }
    /*
    findLabelList查询文件和文件夹类容
     */
    @Transactional(readOnly = false)
    public Page<DcObjectFile> findLabelList(Page<DcObjectFile> page, DcObjectFile dcObjectFile) {
        dcObjectFile.setPage(page);
        page.setList(this.dao.dcobjectlist(dcObjectFile));
        return page;

    }
    /*
          根据ID获取单条文件或者文件夹类容
     */
    public DcObjectFile getdc(String tid) {
        return dao.getdc(tid);
    }


}
