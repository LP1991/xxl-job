/********************** 版权声明 *************************
 * 文件名: SetNameBySourceService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：yuzh   创建时间：2017年1月19日 下午5:28:28
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.modules.dc.metadata.entity.DcObjectAu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.SetNameBySourceService.java 
 * @职责说明: TODO
 * @创建者: yuzh
 * @创建时间: 2017年1月19日 下午5:28:28
 */
@Service
@Transactional(readOnly = true)
public class SetNameBySourceService {

	@Autowired
	private DcObjectAuService dcObjectAuService;
	/*
       根据不同的任务类型 关联各任务的名称实现方法
       baog 2017/6/19
	 */
	public void setNameBySource(DcObjectAu obj) {
		if (obj.getFrom().equals("1") || obj.getFrom().equals("5") || obj.getFrom().equals("6")) {
			DcObjectAu dc = dcObjectAuService.Main(obj.getFileId());
			obj.setFileName(dc.getFileName());
		} else if (obj.getFrom().equals("2")) {
			DcObjectAu dc = dcObjectAuService.transdata(obj.getFileId());
			obj.setFileName(dc.getFileName());
		} else if (obj.getFrom().equals("7") || obj.getFrom().equals("8")) {
			DcObjectAu dc = dcObjectAuService.transintf(obj.getFileId());
			obj.setFileName(dc.getFileName());
		} else if (obj.getFrom().equals("9")) {
			DcObjectAu dc = dcObjectAuService.file(obj.getFileId());
			obj.setFileName(dc.getFileName());
		} else if (obj.getFrom().equals("10")) {
			DcObjectAu dc = dcObjectAuService.hdfs(obj.getFileId());
			obj.setFileName(dc.getFileName());
		} else if (obj.getFrom().equals("3")) {
			DcObjectAu dc = dcObjectAuService.export(obj.getFileId());
			obj.setFileName(dc.getFileName());
		}
	}
	}
