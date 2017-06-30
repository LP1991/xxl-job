/********************** 版权声明 *************************
 * 文件名: DcTransDataMain.java
 * 包名: com.hlframe.modules.dc.dataprocess.entity
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月30日 下午1:56:08
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.entity;

import com.hlframe.common.persistence.DataEntity;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.entity.DcTransDataMain.java 
 * @职责说明: 数据转换任务主表
 * @创建者: peijd
 * @创建时间: 2016年11月30日 下午1:56:08
 */
public class DcTransDataMain extends DataEntity<DcTransDataMain> {
	private static final long serialVersionUID = 1L;
	
	/** 转换Job执行策略 1-遇到异常立即终止; 2-遇到异常继续执行  **/
	public static final String TRANSJOB_STRATEGY_EXP_BREAK = "1";
	public static final String TRANSJOB_STRATEGY_EXP_GOON = "2";
	
	/**  任务状态: 0-编辑; 1-测试; 9-添加任务 **/
	public static final String TASK_STATUS_EDIT = "0";
	public static final String TASK_STATUS_TEST = "1";
	public static final String TASK_STATUS_TASK = "9";
	
	//任务信息
	private String jobName;
	private String jobDesc;
	private String jobPath;
	//输入类别
	private String inputName;
	private String inputType;
	//输出类别
	private String outputName;
	private String outputType;
	
	//转换策略(1-异常继续/2-异常终止) trans_type
	private String transType;
	
	private String status;			//记录状态 STATUS
	private String resultStatus;	//结果状态 RESULT_STATUS
	private int sortNum;
	
	// @return the jobName
	public String getJobName() {
		return jobName;
	}
	// @param jobName the jobName to set
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	// @return the jobDesc
	public String getJobDesc() {
		return jobDesc;
	}
	// @param jobDesc the jobDesc to set
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	// @return the jobPath
	public String getJobPath() {
		return jobPath;
	}
	// @param jobPath the jobPath to set
	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}
	// @return the inputName
	public String getInputName() {
		return inputName;
	}
	// @param inputName the inputName to set
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}
	// @return the inputType
	public String getInputType() {
		return inputType;
	}
	// @param inputType the inputType to set
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	// @return the outputName
	public String getOutputName() {
		return outputName;
	}
	// @param outputName the outputName to set
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	// @return the outputType
	public String getOutputType() {
		return outputType;
	}
	// @param outputType the outputType to set
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	// @return the transType
	public String getTransType() {
		return transType;
	}
	// @param transType the transType to set
	public void setTransType(String transType) {
		this.transType = transType;
	}
	// @return the status
	public String getStatus() {
		return status;
	}
	// @param status the status to set
	public void setStatus(String status) {
		this.status = status;
	}
	// @return the resultStatus
	public String getResultStatus() {
		return resultStatus;
	}
	// @param resultStatus the resultStatus to set
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	// @return the sortNum
	public int getSortNum() {
		return sortNum;
	}
	// @param sortNum the sortNum to set
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
	
}
