
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.metadata.dao.DcObjectAuDao;
import com.hlframe.modules.dc.metadata.entity.DcObjectAu;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.metadata.entity.DcSysObjm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 
 * @类名: com.hlframe.modules.dc.metadata.service.DcObjectAuService.java 
 * @职责说明: dc权限管理
 * @创建者: yuzh
 * @创建时间: 2016年11月19日 下午2:56:37
 */
@Service
@Transactional(readOnly = true)
public class DcObjectAuService extends CrudService<DcObjectAuDao, DcObjectAu> {
	
	/**
	 * @方法名称: pass 
	 * @实现功能: 申请通过
	 * @param obj
	 * @create by yuzh at 2016年11月19日 下午4:11:31
	 */
	@Transactional(readOnly = false)
	public void pass(DcObjectAu obj) {
		obj.setStatus(DcObjectAu.STATUS_PASS);
		dao.update(obj);
		dao.pass(obj);
	}
	
	/**
	 * @方法名称: pass 
	 * @实现功能: 撤回申请
	 * @param obj
	 * @create by yuzh at 2016年11月19日 下午4:11:31
	 */
	@Transactional(readOnly = false)
	public void nopass(DcObjectAu obj) {
		obj.setStatus(DcObjectAu.STATUS_REJECT);
		dao.update(obj);
		dao.delete(obj);
	}

	/**
	 * @方法名称: getById 
	 * @实现功能: TODO
	 * @param id
	 * @create by yuzh at 2017年1月18日 下午2:08:59
	 */
	public DcObjectAu getById(String id) {
		return dao.getById(id);
	}	
	
	/**
	 * @方法名称: getAccreList 
	 * @实现功能: 获取用户-元数据对象权限
	 * @param userId
	 * @param objId
	 * @return
	 * @create by peijd at 2017年2月25日 下午1:40:56
	 */
	public List<DcSysObjm> getAccreList(String userId, String objId){
		Assert.hasText(userId+objId);
		return dao.getAccreList(userId, objId);
	}
	
	public List<DcObjectMain> queryMyDataList(DcObjectMain main){
		return dao.queryMyDataList(main);
	}

	public List<DcObjectMain> colleCtDataList(DcObjectMain ser) {
		return dao.colleCtDataList(ser);

	}

	/**
	 * @方法名称: checkIfPass
	 * @实现功能: 检查单条数据对象是否通过审核
	 * @param userId
	 * @param objId
	 * @return  boolean
	 * @create by peijd at 2017/6/2 10:40
	 */
	public boolean checkIfPass(String userId, String objId){
		Assert.hasText(userId);
		Assert.hasText(objId);
		//查询参数
		DcObjectAu param = new DcObjectAu();
		param.setFileId(objId);
		param.setUserId(userId);

		param = dao.get(param);
		if (null==param){
			return false;
		}
		return DcObjectAu.STATUS_PASS.equals(param.getStatus());
	}
	/*
	 根据不同的id查询不同表所对应权限的名称
	 baog 2017/6/19
	 */
	public DcObjectAu Main (String mid){
	 return dao.Main(mid);

	}
	public DcObjectAu transdata(String tid){
		return  dao.transdata(tid);
	}
	public DcObjectAu transintf(String sid){
		return dao.transintf(sid);
	}
	public DcObjectAu file(String fid){
		return dao.file(fid);
	}
	public DcObjectAu hdfs(String hid){
		return  dao.hdfs(hid);
	}
 public DcObjectAu export(String eid){
		return  dao.export(eid);
 }

 /*
   返回权限状态
    baog 2017/6/19
  */
	@Transactional(readOnly = false)
 public void classify(DcObjectAu dcObjectAu)throws Exception {
		DcObjectAu ser=dao.get(dcObjectAu);
		if(ser.getStatus().equals("已撤回")){
			throw new Exception("管理员已撤回您的操作权限");
		
		}else if(ser.getStatus().equals("未处理")){
			throw new Exception("请等待管理员审批");
		}

	}
}
