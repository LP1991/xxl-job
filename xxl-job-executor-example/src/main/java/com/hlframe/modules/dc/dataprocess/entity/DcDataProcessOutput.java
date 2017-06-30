/********************** 版权声明 *************************
 * 文件名: DcDataProcessOutput.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年3月14日 下午2:33:58
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.utils.StringUtils;
import org.springframework.util.Assert;

import java.util.*;


/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcDataProcessOutput.java 
 * @职责说明: 数据转换输出对象, 不对应实体表, 中间转换过程用
 * @创建者: peijd
 * @创建时间: 2017年3月14日 下午2:33:58
 */
public class DcDataProcessOutput {
	
	/** 输出目标表类别 trans_result:转换输出; check_result:校验输出  **/
	public static final String TARTABLE_TYPE_TRANSLATE = "trans";
	public static final String TARTABLE_TYPE_CHECK = "check";
	
	//过滤条件表达式, 对应数据字典(dc_filter_express)
	public static Map<String, String> processExpMap = new HashMap<String, String>();
	static{
		processExpMap.put("like", "like '%XX%'");
		processExpMap.put("notLike", "not like '%'XX'%'");
		processExpMap.put("notIn", "not in ('XX')");
		processExpMap.put("equal", "='XX'");
		processExpMap.put("notEqual", "!='XX'");
		processExpMap.put("lessThan", "<'XX'");
		processExpMap.put("greaterThan", ">'XX'");
		processExpMap.put("isNull", "is null");
		processExpMap.put("notNull", " is not null");
		processExpMap.put("greaterEqual", ">='XX'");
		processExpMap.put("lessEqual", "<='XX'");
		processExpMap.put("in", "in ('XX')");
		processExpMap.put("between", "between 'XX' and 'YY'");
		processExpMap.put("notBetween", "notBetween 'XX' and 'YY'");
		processExpMap.put("startWith", "like 'XX%'");
		processExpMap.put("endWith", "like '%XX'");		
	}
	
	//目标数据表
	private String tarTableName;
	//排序
	private Integer sortNum;
	//源数据表列表
	private Set<String> srcTableSet;
	//字段列表
	private List<Map<String, Object>> fieldList;
	//关联列表
	private List<Map<String, Object>> relateList;
	//过滤条件列表
	private List<Map<String, Object>> filterList;
	//目标数据表脚本
	private String tarTableScript;
	//目标类别  trans_result:转换输出; check_result:校验输出
	private String tarType;
	//校验失败标记  记录校验失败输出状态
	private Boolean checkErrorFlag;

	//添加源表
	public boolean addSrcTable(String srcTable){
		Assert.hasText(srcTable);
		return getSrcTableSet().add(srcTable);
	}
	
	//获取源表
	public String getSrcTable(){
		Assert.notEmpty(srcTableSet);
		return StringUtils.join(srcTableSet.toArray(), ",");
	}
	
	// @return the srcTableList
	public Set<String> getSrcTableSet() {
		if(null==srcTableSet){
			srcTableSet = new HashSet<String>();
		}
		return srcTableSet;
	}
	// @param srcTableList the srcTableList to set
	public void setSrcTableSet(Set<String> srcTableSet) {
		this.srcTableSet = srcTableSet;
	}
	// @return the tarTableName
	public String getTarTableName() {
		return tarTableName;
	}
	// @param tarTableName the tarTableName to set
	public void setTarTableName(String tarTableName) {
		this.tarTableName = tarTableName;
	}
	// @return the tarTableScript
	public String getTarTableScript() {
		return tarTableScript;
	}
	// @param tarTableScript the tarTableScript to set
	public void setTarTableScript(String tarTableScript) {
		this.tarTableScript = tarTableScript;
	}
	// @return the sortNum
	public Integer getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	// @return the fieldList
	public List<Map<String, Object>> getFieldList() {
		return fieldList;
	}

	// @param fieldList the fieldList to set
	public void setFieldList(List<Map<String, Object>> fieldList) {
		this.fieldList = fieldList;
	}

	// @return the relateList
	public List<Map<String, Object>> getRelateList() {
		return relateList;
	}

	// @param relateList the relateList to set
	public void setRelateList(List<Map<String, Object>> relateList) {
		this.relateList = relateList;
	}

	// @return the tarType
	public String getTarType() {
		return tarType;
	}

	// @param tarType the tarType to set
	public void setTarType(String tarType) {
		this.tarType = tarType;
	}

	// @return the filterList
	public List<Map<String, Object>> getFilterList() {
		return filterList;
	}

	// @param filterList the filterList to set
	public void setFilterList(List<Map<String, Object>> filterList) {
		this.filterList = filterList;
	}

	// @return the checkErrorFlag
	public Boolean getCheckErrorFlag() {
		if(null==checkErrorFlag){
			return false;
		}
		return checkErrorFlag;
	}
	
	// @param checkErrorFlag the checkErrorFlag to set
	public void setCheckErrorFlag(Boolean checkErrorFlag) {
		this.checkErrorFlag = checkErrorFlag;
	}
	
}
