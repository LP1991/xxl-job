/********************** 版权声明 *************************
 * 文件名: DcDataResult.java
 * 包名: com.hlframe.modules.dc.common.dao
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月1日 下午9:09:41
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.common.dao;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;

/** 
 * @类名: com.hlframe.modules.dc.common.dao.DcDataResult.java 
 * @职责说明: 统一返回结果对象
 * @创建者: peijd
 * @创建时间: 2016年12月1日 下午9:09:41
 */
public class DcDataResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//结果标记
	private boolean rst_flag;
	//标准输出
	private String rst_std_msg;
	//错误输出
	private String rst_err_msg;

	//总数
	private Integer totleNum;

	//备注
	private  String remarks;
	
	// @return the rst_flag
	public boolean getRst_flag() {
		return rst_flag;
	}
	// @param rst_flag the rst_flag to set
	public void setRst_flag(boolean rst_flag) {
		this.rst_flag = rst_flag;
	}
	// @return the rst_std_msg
	public String getRst_std_msg() {
		return rst_std_msg;
	}
	// @param rst_std_msg the rst_std_msg to set
	public void setRst_std_msg(String rst_std_msg) {
		this.rst_std_msg = rst_std_msg;
	}
	// @return the rst_err_msg
	public String getRst_err_msg() {
		return rst_err_msg;
	}
	// @param rst_err_msg the rst_err_msg to set
	public void setRst_err_msg(String rst_err_msg) {
		this.rst_err_msg = rst_err_msg;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getTotleNum() {
		return totleNum;
	}

	public void setTotleNum(Integer totleNum) {
		this.totleNum = totleNum;
	}

	/**
	 * Override
	 * @方法名称: toString 
	 * @实现功能: 转换为字符串
	 * @return
	 * @create by peijd at 2017年2月17日 下午2:10:16
	 */
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try {
			json.put("rst_flag", rst_flag);
			//数据量
			if(null!=totleNum && totleNum>0){
				json.put("totleNum", totleNum);
			}else{
				json.put("totleNum", 0);
			}
			if(rst_flag){
				json.put("rst_msg",rst_std_msg);
			}else{
				json.put("rst_msg",rst_err_msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public static void main(String[] args) {
		DcDataResult rst = new DcDataResult();
		rst.setRst_flag(true);
		rst.setRst_std_msg("std_msg");
		rst.setRst_err_msg("err_msg");
		System.out.println(rst);
		rst.setRst_flag(false);
		System.out.println(rst);
	}
	
}
