/********************** 版权声明 *************************
 * 文件名: DcObjectMainService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月7日 下午1:44:42
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.json.AjaxJson;
import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.common.utils.DateUtils;
import com.hlframe.modules.dc.metadata.dao.*;
import com.hlframe.modules.dc.metadata.entity.DcObjectAu;
import com.hlframe.modules.dc.metadata.entity.DcObjectMain;
import com.hlframe.modules.dc.utils.DcCommonUtils;
import com.hlframe.modules.dc.utils.DcEsUtil;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.hlframe.modules.dc.utils.DcStringUtils;
import com.hlframe.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

/**
 * @类名: com.hlframe.modules.dc.metadata.service.DcObjectMainService.java
 * @职责说明: 元数据对象Service
 * @创建者: peijd
 * @创建时间: 2016年11月8日 下午2:39:55
 */
@Service
@Transactional(readOnly = true)
public class DcObjectMainService extends CrudService<DcObjectMainDao, DcObjectMain> {

	@Autowired // 权限表service
	private DcObjectAuService dcObjectAuService;

	@Autowired // 数据表dao
	private DcObjectTableDao dcObjectTableDao;

    public DcObjectMain get(String id) {
        DcObjectMain param = new DcObjectMain();
        param.setId(id);
        return super.get(param);
    }

	public List<DcObjectMain> findList(DcObjectMain dcObjectMain) {
		return super.findList(dcObjectMain);
	}
	

	/**
	 * @param dcObjectMain 
	 * @实现功能: 数据权限过滤
	 * @create by yuzh at 2016年12月15日 14:30:29
	 */
	public Page<DcObjectMain> findPage(Page<DcObjectMain> page, DcObjectMain dcObjectMain, DcObjectMain ser) {
		
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcObjectMain.getSqlMap().put("dsf", dataScopeFilter(dcObjectMain.getCurrentUser(), "o", "u"));
		// 设置分页参数
		dcObjectMain.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(dcObjectMain));
		return super.findPage(page, dcObjectMain);
	}

	@Transactional(readOnly = false)
	public void save(DcObjectMain dcObjectMain) {
		boolean i = dcObjectMain.getId().isEmpty();
		super.save(dcObjectMain);
		if (i) {
			DcObjectAu obj = new DcObjectAu();
			obj.setUserId(UserUtils.getUser().getId());
			obj.setFileId(dcObjectMain.getId());
			dcObjectAuService.pass(obj);
		}
	}

	/**
	 * @方法名称: findListByCata
	 * @实现功能: 根据分类信息查询数据对象列表
	 * @param obj
	 * @return
	 * @create by peijd at 2016年11月9日 下午2:12:33
	 */
	public List<DcObjectMain> findListByCata(DcObjectMain obj) {
		return dao.findListByCata(obj);
	}

	/**
	 * @方法名称: findPageByCata
	 * @实现功能: 根据分类信息查询数据对象分页列表
	 * @param page
	 * @param obj
	 * @return
	 * @create by peijd at 2016年11月9日 下午2:41:09
	 */
	public Page<DcObjectMain> findPageByCata(Page<DcObjectMain> page, DcObjectMain obj) {
		obj.getSqlMap().put("dsf", dataScopeFilter(obj.getCurrentUser(), "o", "u"));
		obj.setPage(page);
		page.setList(dao.findListByCata(obj));
		return page;
	}
	/**
	 * @方法名称: getAu
	 * @实现功能: 发起权限申请请求
	 * @param obj
	 * @create by yuzh at 2016年11月19日 下午2:28:49
	 */
	@Transactional(readOnly = false)
	public void getAu(DcObjectMain obj) throws Exception {
		DcObjectAu dcObjectAu = new DcObjectAu();
		dcObjectAu.setUserId(UserUtils.getUser().getId());
		dcObjectAu.setFileId(obj.getId());
		dcObjectAu.setStatus("未处理");
		if (obj.getObjType().equals("数据表")) {
			dcObjectAu.setFrom("1");
		}else if(obj.getObjType().equals("文件")||obj.getObjType().equals("文件夹")){
			dcObjectAu.setFrom("5");
		}
		if (!DcStringUtils.isNotNull(dcObjectAuService.get(dcObjectAu))) {
			dcObjectAuService.save(dcObjectAu);
			throw new Exception("已向管理员申请该任务操作权限，请等待管理员审核!");
		}else {
			dcObjectAuService.classify(dcObjectAu);
		}
	}
	/**
	 * uodate obj's ref to ES
	 * 
	 * @param obj
	 */
	public void updateData2Es(DcObjectMain obj) {
		if (obj != null) {
			String[] ids = new String[1];
			ids[0] = obj.getId();
			updateData2Es(ids);
		}
	}

	/**
	 * uodate obj's ref to ES
	 * 
	 * @param objId
	 */
	public void updateData2Es(String objId) {
		if (objId != null) {
			String[] ids = new String[1];
			ids[0] = objId;
			updateData2Es(ids);
		}
	}

	/**
	 * uodate obj's ref to ES
	 * 
	 * @param objs
	 */
	public void updateData2Es(List<DcObjectMain> objs) {
		if (objs.size() > 0) {
			String[] ids = new String[objs.size()];
			for (int i = 0; i < objs.size(); i++) {
				ids[i] = objs.get(i).getId();
			}
			updateData2Es(ids);
		}
	}

	/**
	 * @方法名称: updateData2Es
	 * @实现功能: 全部的obj对象更新一遍cata属性，用于添加大类
	 * @create by lp at 2017年3月31日 下午7:25:05
	 */
	public void updateData2Es() {
		//es元数据索引
		String es_index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
		Integer es_batchNum = Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.bat.num", "1000"));
		Client client = null;
		try {
			// 获取ES client连接
			client = DcEsUtil.getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count = 0;
//获取全部obj' 对象属性
			List<Map<String,Object>> objInfo = dao.getCataInfo2EsById(null);
			for(Map<String,Object> obj : objInfo){
				if(obj == null || obj.get("objType") == null){
					continue;
				}
				//批量添加
				bulkRequest.add(client.prepareUpdate(es_index, DcCommonUtils.objType2String(obj.get("objType").toString()), obj.get("id").toString())
						.setDoc(cataFieldProcessToJson(obj)));
				count++;
//上传到ES
				if(count%es_batchNum == 0){
					logger.info("elasticSearch begin updata obj_cata_id to ES  : ["+es_batchNum + "] ");
					BulkResponse bulkResponse = bulkRequest.get();
					if (bulkResponse.hasFailures()) {
						// process failures by iterating through each bulk response item
						logger.info("elasticSearch updata obj_cata_id to ES failed : "+bulkResponse.buildFailureMessage());
					}
				}
			}
			//清理缓存
			if(bulkRequest.numberOfActions()>0){
				logger.info("elasticSearch begin updata obj_cata_id to ES  : ["+bulkRequest.numberOfActions()  + "] ");
				BulkResponse bulkResponse = bulkRequest.get();
				if (bulkResponse.hasFailures()) {
					// process failures by iterating through each bulk response item
					logger.info("elasticSearch updata obj_cata_id to ES failed : "+bulkResponse.buildFailureMessage());
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error("putDataToEs error", e);
		}
	}
	/**
	 * @方法名称: updateData2Es
	 * @实现功能: update关系信息并存储到ES里面
	 * @create by lp at 2017年3月7日 下午7:25:05
	 */
	public void updateData2Es(String[] objIds) {
		// es元数据索引
		String es_index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
		Integer es_batchNum = Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.bat.num", "1000"));
		Client client = null;
		try {
			// 获取ES client连接
			client = DcEsUtil.getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count = 0;
			if (objIds == null || objIds.length == 0) {
				logger.info("no objs need to update!");
				return;
			} else {
				for (String objId : objIds) {
					List<Map<String, Object>> objInfo = dao.getCataInfo2EsById(objId);
					if (objInfo.size() == 0) {
						continue;
					}
					bulkRequest.add(client
							.prepareUpdate(es_index,
									DcCommonUtils.objType2String(objInfo.get(0).get("objType").toString()), objId)
							.setDoc(cataFieldProcessToJson(objInfo.get(0))));
					count++;
//更新到ES
					if (count % es_batchNum == 0) {
						logger.info("elasticSearch begin updata obj_cata_id to ES  : [" + es_batchNum + "] ");
						BulkResponse bulkResponse = bulkRequest.get();
						if (bulkResponse.hasFailures()) {
							// process failures by iterating through each bulk
							// response item
							logger.info("elasticSearch updata obj_cata_id to ES failed : "
									+ bulkResponse.buildFailureMessage());
						}
					}
				}
				if (bulkRequest.numberOfActions() > 0) {
					logger.info(
							"elasticSearch begin updata obj_cata_id to ES  : [" + bulkRequest.numberOfActions() + "] ");
					BulkResponse bulkResponse = bulkRequest.get();
					if (bulkResponse.hasFailures()) {
						// process failures by iterating through each bulk
						// response item
						logger.info("elasticSearch updata obj_cata_id to ES failed : "
								+ bulkResponse.buildFailureMessage());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("putDataToEs error", e);
		}
	}

	/**
	 * @name: deleteByIds
	 * @funciton: delete Es data By objIds, batch delete
	 * @param
	 * @return
	 * @Create by lp at 2017/4/13 10:19
	 * @throws
	 */
	public void deleteByIds(String[] objIds,String type) throws Exception {
		// es元数据索引
		String es_index = DcPropertyUtils.getProperty("elasticSearch.dataObj.index", "dc_metadata");
		Integer es_batchNum = Integer.parseInt(DcPropertyUtils.getProperty("elasticSearch.bat.num", "1000"));
		Client client = null;
		try {
			// 获取ES client连接
			client = DcEsUtil.getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count = 0;
			if (objIds == null || objIds.length == 0) {
				logger.info("no objs need to delete!");
				return;
			} else {
				for (String objId : objIds) {
					List<Map<String, Object>> objInfo = dao.getCataInfo2EsById(objId);
					if (objInfo.size() == 0) {
						continue;
					}
					bulkRequest.add(client.prepareDelete(es_index, type, objId));
					count++;
//更新到ES
					if (count % es_batchNum == 0) {
						logger.info("elasticSearch begin updata obj_cata_id to ES  : [" + es_batchNum + "] ");
						BulkResponse bulkResponse = bulkRequest.get();
						if (bulkResponse.hasFailures()) {
							// process failures by iterating through each bulk
							// response item
							logger.info("elasticSearch updata obj_cata_id to ES failed : "
									+ bulkResponse.buildFailureMessage());
						}
					}
				}
				if (bulkRequest.numberOfActions() > 0) {
					logger.info(
							"elasticSearch begin updata obj_cata_id to ES  : [" + bulkRequest.numberOfActions() + "] ");
					BulkResponse bulkResponse = bulkRequest.get();
					if (bulkResponse.hasFailures()) {
						// process failures by iterating through each bulk
						// response item
						logger.info("elasticSearch updata obj_cata_id to ES failed : "
								+ bulkResponse.buildFailureMessage());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("putDataToEs error", e);
		}
	}
	/**
	 * @name: cataFieldProcessToJson @funciton: 解析字段 @param @return @Create by
	 * lp at 2017/3/15 16:42 @throws
	 */
	private XContentBuilder cataFieldProcessToJson(Map<String, Object> tableMap) {
		String[] items = new String[] { "cata", "s_cata", "ss_cata" };
		try {
			XContentBuilder resultJson = XContentFactory.jsonBuilder().startObject();
			Object value = tableMap.get("catalogsPath");

			if (value == null) {
				for (String item : items) {
					resultJson.startArray(item).endArray();
				}
			} else {
				String[] catalog = value.toString().split(";");

				List<String[]> list = new ArrayList<>();
				for (int i = 0; i < catalog.length; i++) {
					list.add(catalog[i].split(","));
				}
				for (int i = 0; i < items.length; i++) {
					resultJson.startArray(items[i]);
					for (int j = 0; j < list.size(); j++) {
						if (list.get(j).length > i && null != list.get(j)[i] && !"".equals(list.get(j)[i])) {
							System.out.println(items[i] + ":" + list.get(j)[i]);
							resultJson.startObject().field("cata_id", list.get(j)[i]).endObject();
						}
					}
					resultJson.endArray();
				}
			}
			resultJson.endObject();
			return resultJson;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @方法名称: buildHomePageStatMap
	 * @实现功能: 构建首页元数据 统计数据
	 * @return
	 * @create by peijd at 2017年3月17日 下午2:13:52
	 */
	public Map<String, String> buildHomePageStatMap() {
		Map<String, String> statMap = new HashMap<String, String>();
		// 统计查询查询sql
		List<Map<String, Object>> dataList = DcCommonUtils.queryDataBySql(
				"select obj_type, count(1) as objNum from dc_obj_main where DEL_FLAG = '0' GROUP BY obj_type");
		// Assert.notEmpty(dataList, "没有统计数据");
		int totleNum = 0, tableNum = 0, fileNum = 0, intfcNum = 0; // 元数据总数
		for (Map<String, Object> map : dataList) {
			totleNum += Integer.parseInt(String.valueOf(map.get("objNum")));
			if (DcObjectMain.OBJ_TYPE_TABLE.equals(map.get("obj_type"))) { // 数据表
				tableNum = Integer.parseInt(String.valueOf(map.get("objNum")));

			} else if (DcObjectMain.OBJ_TYPE_FILE.equals(map.get("obj_type"))) { // 文件
				fileNum = fileNum + Integer.parseInt(String.valueOf(map.get("objNum")));

			} else if (DcObjectMain.OBJ_TYPE_FOLDER.equals(map.get("obj_type"))) { // 目录也作为文件统计
				fileNum = fileNum + Integer.parseInt(String.valueOf(map.get("objNum")));

			} else if (DcObjectMain.OBJ_TYPE_INTER.equals(map.get("obj_type"))) { // 接口
				intfcNum = Integer.parseInt(String.valueOf(map.get("objNum")));
			}
		}
		// 加入总数
		statMap.put("metaData_totle", totleNum + "");
		statMap.put("metaData_table", tableNum + "");
		statMap.put("metaData_file", fileNum + "");
		statMap.put("metaData_intfc", intfcNum + "");

		// 30天前的数据累加值
		int initNum = 0;
		dataList = DcCommonUtils.queryDataBySql(
				"select count(1) as initNum from dc_obj_main where DEL_FLAG = '0' and CREATE_DATE<DATE_SUB(CURDATE(), INTERVAL 30 DAY)");
		if (CollectionUtils.isNotEmpty(dataList)) {
			initNum = Integer.parseInt(String.valueOf(dataList.get(0).get("initNum")));
		}

		Date now = new Date();
		String idxDate = null;
		StringBuilder dateArr = new StringBuilder(256), numArr = new StringBuilder(128);
		// 构建数据增长趋势(最近30天的数据)
		dataList = DcCommonUtils.queryDataBySql(
				"SELECT date_format(CREATE_DATE,'%Y-%m-%d') as createDay, count(1) as dataNum from dc_obj_main "
						+ "WHERE DEL_FLAG = '0' and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(CREATE_DATE) GROUP BY createDay order by createDay");
		if (CollectionUtils.isNotEmpty(dataList)) {
			// 日期-数据量 Map
			Map<String, Integer> dayMap = new HashMap<String, Integer>();
			for (Map<String, Object> map : dataList) {
				if (null == map.get("createDay")) {
					continue;
				}
				dayMap.put(String.valueOf(map.get("createDay")), Integer.parseInt(String.valueOf(map.get("dataNum"))));
			}

			// 30天 逐天累加
			for (int i = -30; i <= 0; i++) {
				idxDate = DateUtils.formatDate(DateUtils.addDays(now, i));
				if (dayMap.containsKey(idxDate)) {
					initNum += dayMap.get(idxDate);
				}
				dayMap.put(idxDate, initNum);
				dateArr.append(idxDate.substring(5)).append(",");
				numArr.append(initNum).append(",");
			}

		} else { // 构建空列表
			for (int i = -30; i <= 0; i++) {
				idxDate = DateUtils.formatDate(DateUtils.addDays(now, i));
				dateArr.append(idxDate.substring(5)).append(",");
				numArr.append(initNum).append(",");
			}
		}
		statMap.put("dateArr", dateArr.deleteCharAt(dateArr.length() - 1).toString());
		statMap.put("numArr", numArr.deleteCharAt(numArr.length() - 1).toString());

		return statMap;
	}

	/**
	 * @方法名称: findFieldList
	 * @实现功能: 根据表id查询申請信息
	 * @param qid
	 * @create by hgw at 2017年3月24日 下午15:31:39
	 */
	public List<DcObjectMain> quanglist(DcObjectMain qid) {
		return dao.quanglist(qid);
	}
	/**
	 * @方法名称: findFieldList
	 * @实现功能: 根据表id查询收藏信息
	 * @param sid
	 * @create by hgw at 2017年3月24日 下午15:31:39
	 */
	public List<DcObjectMain> shochanlist(DcObjectMain sid) {
		return dao.shochanlist(sid);
	}
	/**
	 * @方法名称: findFieldList
	 * @实现功能: 根据表id查询数据表详情
	 * @param tid
	 * @create by hgw at 2017年3月24日 下午15:31:39
	 */
	public DcObjectMain tnadmin(String tid) {
		return dao.tnadmin(tid);
	}


	/**
	 * @方法名称: getById
	 * @实现功能: 根据Id 获取main对象
	 * @params  [objId]
	 * @return  com.hlframe.modules.dc.metadata.entity.DcObjectMain
	 * @create by peijd at 2017/5/6 14:01
	 */
	public DcObjectMain getById(String objId) {
		Assert.hasText(objId);
		return dao.getById(objId);
	}

}
