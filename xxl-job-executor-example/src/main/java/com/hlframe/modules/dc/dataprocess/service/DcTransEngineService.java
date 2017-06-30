/********************** 版权声明 *************************
 * 文件名: DcTranslateService.java
 * 包名: com.hlframe.modules.dc.dataprocess.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年1月11日 上午10:21:28
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.dataprocess.service;

import com.hlframe.common.utils.DateUtils;
import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.common.dao.DcDataResult;
import com.hlframe.modules.dc.dataprocess.entity.DcTransDataSub;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @类名: com.hlframe.modules.dc.dataprocess.service.DcTransEngineService.java 
 * @职责说明: 数据转换引擎Service
 * @创建者: peijd
 * @创建时间: 2017年1月11日 上午10:21:28
 */
@Service
@Transactional(readOnly = true)
public class DcTransEngineService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static Map<String, String> engineMap = new HashMap<String, String>();	//引擎类别
	private static Map<String, Map<String, String>> regexMap = new LinkedHashMap<String, Map<String, String>>();	//正则表达式类别 解析脚本中${}部分
	private static Map<String, String> escapeMap = new HashMap<String, String>();	//转义字符集 解析脚本中配对部分

	//加载引擎
	static{
		engineMap.put(DcTransDataSub.TRANSLATE_ENGINE_HIVE, DcPropertyUtils.getProperty("executeHive.restServer.url"));
		engineMap.put(DcTransDataSub.TRANSLATE_ENGINE_IMPALA, DcPropertyUtils.getProperty("executeImpala.restServer.url"));
		engineMap.put(DcTransDataSub.TRANSLATE_ENGINE_SPARKSQL, DcPropertyUtils.getProperty("executeSparkSql.restServer.url"));

		//注册正则表达式的时间
		regexMap.put("cur_date", getParamMap("当前日期", DateUtils.getDate()));	//当天
		regexMap.put("pre_date", getParamMap("前一天日期", DateUtils.formatDate(DateUtils.addDays(new Date(), -1),null)));	//昨天
		regexMap.put("nex_date", getParamMap("后一天日期", DateUtils.formatDate(DateUtils.addDays(new Date(), 1),null)));	//明天

		regexMap.put("cur_hour", getParamMap("当前小时", DateUtils.getDate("yyyy-MM-dd HH")));	//当前小时
		regexMap.put("pre_hour", getParamMap("前一小时", DateUtils.formatDate(DateUtils.addHours(new Date(), -1),null)));	//前一小时
		regexMap.put("nex_hour", getParamMap("后一小时", DateUtils.formatDate(DateUtils.addHours(new Date(), 1),null)));	//后一小时

		regexMap.put("cur_month", getParamMap("当前月份", DateUtils.getDate("yyyy-MM")));	//当月
		regexMap.put("pre_month", getParamMap("上一月份", DateUtils.formatDate(DateUtils.addMonths(new Date(), -1),null)));	//上一月
		regexMap.put("nex_month", getParamMap("下一月份", DateUtils.formatDate(DateUtils.addMonths(new Date(), 1),null)));	//下一月

		regexMap.put("cur_year", getParamMap("当前年份", DateUtils.getDate("yyyy")));	//当年
		regexMap.put("pre_year", getParamMap("上一年份", DateUtils.formatDate(DateUtils.addYears(new Date(), -1),null)));	//上一年
		regexMap.put("nex_year", getParamMap("下一年份", DateUtils.formatDate(DateUtils.addYears(new Date(), 1),null)));	//下一年

		regexMap.put("cur_week", getParamMap("当前周数", DateUtils.getWeek()));	//当前周数
		regexMap.put("pre_week", getParamMap("上一周数", DateUtils.formatDate(DateUtils.addWeeks(new Date(), -1),null)));	//上一周数
		regexMap.put("nex_week", getParamMap("下一周数", DateUtils.formatDate(DateUtils.addWeeks(new Date(), 1),null)));	//下一周数

		//转义字符集
		escapeMap.put("&gt;",">");
		escapeMap.put("&lt;","<");
		escapeMap.put("&amp;","&");
		escapeMap.put("&quot;","\"");
		escapeMap.put("&nbsp;"," ");
	}
	
	
	/**
	 * @方法名称: runScript 
	 * @实现功能: 运行转换脚本
	 * @param transEngine	转换引擎	
	 * @param transSql		转换脚本
	 * @return
	 * @create by peijd at 2017年1月11日 上午10:33:14
	 */
	@Transactional(readOnly = false)
	public DcDataResult runScript(String transEngine, String transSql) {
		Assert.hasText(transSql, "转换脚本不可为空!");
		//默认用hive处理
		transEngine = StringUtils.isNotEmpty(transEngine)?transEngine: DcTransDataSub.TRANSLATE_ENGINE_HIVE;
		String restServer = engineMap.get(transEngine);
		Assert.hasText(restServer, "未配置转换引擎服务!");
		
		DcDataResult result = new DcDataResult();
		try {
			//rest client
			Client client = Client.create();
			URI restUrl = new URI(restServer);
			WebResource resource = client.resource(restUrl);
			
			//构建form参数
			MultivaluedMapImpl params = new MultivaluedMapImpl();  
			params.add("metaSql", decorateScript(transSql, transEngine));
			System.out.println("-->sql:"+params.get("metaSql"));
			String resultMsg = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, params);

			Assert.isTrue("success".equalsIgnoreCase(resultMsg), resultMsg);
			result.setRst_flag(true);
			result.setRst_std_msg("执行成功!");
			
		} catch (Exception e) {
			logger.error("-->runScript", e);
			result.setRst_flag(false);
			result.setRst_err_msg(e.getMessage());
		}
		return result;
	}

	/**
	 * @方法名称: decorateScript
	 * @实现功能: 修饰转换脚本, 变量替换{param}  e.g. {current_date}
	 * @param updatesql		转换脚本
	 * @param transEngine	转换引擎
	 * @return  java.lang.String
	 * @create by peijd at 2017/5/17 17:55
	 */
	public String decorateScript(String updatesql, String transEngine) {
		//生成匹配模式的正则表达式
		String patternString = "\\$\\{(" + StringUtils.join(regexMap.keySet(), "|") + ")\\}";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(updatesql);

		//两个方法：appendReplacement, appendTail
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			matcher.appendReplacement(sb, regexMap.get(matcher.group(1)).get("val"));
		}
		matcher.appendTail(sb);

		//转义符替换
		pattern = Pattern.compile(StringUtils.join(escapeMap.keySet(), "|"));
//		matcher = pattern.matcher(" where t1.modifytime&gt;2017-05-16 and aaa&lt;'123' and a.ms&lt;&gt;10");
		matcher = pattern.matcher(sb.toString());
		sb.setLength(0);
		while(matcher.find()) {
			matcher.appendReplacement(sb, escapeMap.get(matcher.group(0)));
		}
		matcher.appendTail(sb);
		System.out.println("-->decorateScript: "+ sb.toString());
		return sb.toString();

	}

	/**
	 * @方法名称: getParamMap
	 * @实现功能: 获取
	 * @param desc
	 * @param value
	 * @return  java.util.Map<java.lang.String,java.lang.String>
	 * @create by peijd at 2017/5/18 10:52
	 */
	public static Map<String, String> getParamMap(String desc, String value){
		Map<String, String> rstMap = new HashMap<String, String>();
		Assert.hasText(desc);
		Assert.hasText(value);
		rstMap.put("desc", desc);
		rstMap.put("val", value);
		return rstMap;
	}

	/**
	 * @方法名称: getRegexMap
	 * @实现功能: 获取RegexMap
	 * @params  []
	 * @return  java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @create by peijd at 2017/5/18 11:01
	 */
	public Map<String, Map<String, String>> getRegexMap() {
		return regexMap;
	}
}
