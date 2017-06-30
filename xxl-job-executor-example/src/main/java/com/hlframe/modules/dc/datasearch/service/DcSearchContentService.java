/********************** 版权声明 *************************
 * 文件名: DcSearchContentService.java
 * 包名: com.hlframe.modules.dc.datasearch.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2016年11月8日 上午10:04:49
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.datasearch.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.TreeService;
import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.dc.datasearch.dao.DcSearchContentDao;
import com.hlframe.modules.dc.datasearch.entity.DcSearchContent;
import com.hlframe.modules.dc.metadata.entity.DcObjectCataRef;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.service.DcObjectCataRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.datasearch.service.DcSearchContentService.java 
 * @职责说明: 分类明细树service
 * @创建者: yuzh
 * @创建时间: 2016年11月8日 上午10:04:49
 */
@Service
@Transactional(readOnly = true)
public class DcSearchContentService extends TreeService<DcSearchContentDao, DcSearchContent>{

	@Autowired
	private DcSearchContentDao dcSearchContentDao;
	@Autowired
	private DcObjectCataRefService dcObjectCataRefService;
	/**
	 * 
	 * @方法名称: newObject 
	 * @实现功能: 新建分类时创建默认分类
	 * @param parentId
	 * @return
	 * @create by yuzh at 2016年12月7日 下午7:37:12
	 */
	@Transactional(readOnly = false)
	public String newObject(String parentId){
		DcSearchContent newObject = new DcSearchContent();
		DcSearchContent parent = new DcSearchContent();
		parent.setId(parentId);
		newObject.preInsert();
		newObject.setCataName("未分类对象");
		newObject.setCataItemId(parentId);
		newObject.setParent(parent);
		newObject.setParentIds(parentId+",");
		dao.insert(newObject);
		return newObject.getId();
	}
/**
 * 
 * @方法名称: findAll 
 * @实现功能: 查询所有列表
 * @return
 * @create by yuzh at 2016年11月12日 下午12:04:23
 */
	public List<DcSearchContent> findAll(){
		return dcSearchContentDao.findAllList(new DcSearchContent());
	}
/**
 * 
 * @方法名称: findListBy 
 * @实现功能: 查询父级列表
 * @param dcSearchContent
 * @return
 * @create by yuzh at 2016年11月12日 下午12:04:48
 */
	@Transactional(readOnly = true)
	public List<DcSearchContent> findListBy(DcSearchContent dcSearchContent){
		return dcSearchContentDao.findByParentIdsLike(dcSearchContent);
	}
	
	@Transactional(readOnly = false)
	public void save(DcSearchContent dcSearchContent) {
		super.save(dcSearchContent);
	}
	/**
	 * 
	 * @方法名称: saveTop 
	 * @实现功能: 保存顶层目录
	 * @param dcSearchContent
	 * @create by yuzh at 2016年11月12日 下午12:05:15
	 */
	@Transactional(readOnly = false)
	public void saveTop(DcSearchContent dcSearchContent){
		DcSearchContent parent = new DcSearchContent();
		parent.setId(dcSearchContent.getCataItemId());
		dcSearchContent.setParent(parent);
		dcSearchContent.setParentIds(dcSearchContent.getCataItemId()+",");
		if(dcSearchContent.getIsNewRecord()){
			dcSearchContent.preInsert();
			dcSearchContentDao.insert(dcSearchContent);
		}else{
			dcSearchContent.preUpdate();
			dcSearchContentDao.update(dcSearchContent);
		}
	}

	/**
	 * modify by Primo 删除小类， 对应的obj需要归类到对应的未分组对象中。
	 * @param dcSearchContent
	 */
	@Transactional(readOnly = false)
	public void delete(DcSearchContent dcSearchContent,List<DcObjectMain> ids) {
//		Assert.notNull(dcSearchContent);
		List<String> list = dcSearchContentDao.getCatalogRefById(dcSearchContent.getId());
		List<DcSearchContent> unmarks = getUnmark(dcSearchContent.getId());
		if(unmarks.size()==1 && list.size()>0){
			String cataId = unmarks.get(0).getId();
			for(int i=0;i<list.size();i++){
				DcObjectMain obj = new DcObjectMain();
				obj.setId(list.get(i));
				ids.add(obj);
				DcObjectCataRef dcObjectCataRef = new DcObjectCataRef();
				dcObjectCataRef.setObjId(list.get(i));
				dcObjectCataRef.setCataId(cataId);
				dcObjectCataRef.setOldCataId(dcSearchContent.getId());
				dcObjectCataRefService.update(dcObjectCataRef);
			}
		}
//		dcObjectMainService.updateData2Es(ids);
		super.delete(dcSearchContent);
	}
	
//	public Page<DcSearchContent> findDcSearchContent(Page<DcSearchContent> page, DcSearchContent dcSearchContent) {
//		// 设置分页参数
//		dcSearchContent.setPage(page);
//	    // 执行分页查询
//		page.setList(dcSearchContentDao.findList(dcSearchContent));
//		return page;
//	}
	public Page<DcSearchContent> findDcSearchContent(Page<DcSearchContent> page, DcSearchContent dcSearchContent) {
		// 设置分页参数
		dcSearchContent.setPage(page);
	    // 执行分页查询
		page.setList(dao.findList(dcSearchContent));
		return page;
	}

	public List<DcSearchContent> getUnmark(String id) {
		return dao.getUnmark(id);
	}
	/**
	 * @方法名称: getUnmark
	 * @实现功能: 获取所有未分类子级id
	 * @create by yuzh at 2016年12月8日 上午10:52:47
	 */
	public List<DcSearchContent> getUnmark() {
		return dao.getUnmark(null);
	}
	
	/**
	 * @方法名称: findUniqueByPr
	 * @实现功能: 取出源数据分类中的分类明细的单条数据
	 * @create bao gang yuzh at 2016年12月8日 上午10:52:47
	 */
	public DcSearchContent findUniqueByPr(String propertyName,Object value,String cataItemId){
		return dao.findUniqueByPr(propertyName,value,cataItemId);
	}
}
