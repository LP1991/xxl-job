package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.common.service.CrudService;
import com.hlframe.modules.dc.dataprocess.dao.DcHiveFieldDao;
import com.hlframe.modules.dc.dataprocess.entity.DcHiveField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DcHiveFieldService extends CrudService<DcHiveFieldDao, DcHiveField> {

	public DcHiveField get(String id) {
		return super.get(id);
	}

	public Page<DcHiveField> findPage(Page<DcHiveField> page, DcHiveField dcHiveField) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		dcHiveField.getSqlMap().put("dsf", dataScopeFilter(dcHiveField.getCurrentUser(), "o", "u"));
		// 设置分页参数
		dcHiveField.setPage(page);
		// 执行分页查询
		page.setList(dao.findAllList(dcHiveField));
		return page;
	}

	/**
	 * @方法名称: deleteHiveField
	 * @实现功能: 删除表的字段
	 * @param id
	 * @create by cdd at 2017年1月9日 上午11:37:40
	 */
	public void deleteHiveField(String id) {
		DcHiveField dcHiveField = new DcHiveField();
		dcHiveField.setBelong2Id(id);
		super.delete(dcHiveField);
	}

	/**
	 * @方法名称: countByBelong2Id
	 * @实现功能: TODO
	 * @param belong2Id
	 * @return
	 * @create by cdd at 2017年1月12日 下午7:33:22
	 */
	public String countByBelong2Id(String belong2Id) {
		return dao.countByBelong2Id(belong2Id);
	}

	/**
	 * @方法名称: getFieldNameAndType
	 * @实现功能: TODO
	 * @param belong2Id
	 * @return
	 * @create by cdd at 2017年1月12日 下午7:42:15
	 */
	public List<Map<String, Object>> getFieldNameAndType(String belong2Id) {
		return dao.getFieldNameAndType(belong2Id);
	}

	/**
	 * @方法名称: deleteByBelong2Id
	 * @实现功能: TODO
	 * @param belong2Id
	 * @create by cdd at 2017年1月13日 下午1:54:49
	 */
	@Transactional(readOnly = false)
	public void deleteByBelong2Id(String belong2Id) {
		dao.deleteByBelong2Id(belong2Id);
	}

	// 批量插入数据
	@Transactional(readOnly = false)
	public int batchInsert(List<DcHiveField> list) {
		return dao.batchInsert(list);
	}

	/**
	 * @方法名称: findAllList
	 * @实现功能: 查询所有对象
	 * @param fieldParam
	 * @return
	 * @create by peijd at 2017年2月17日 下午4:11:34
	 */
	public List<DcHiveField> findAllList(DcHiveField fieldParam) {
		return dao.findAllList(fieldParam);
	}

}
