/********************** 版权声明 *************************
 * 文件名: DvTranslateFieldService.java
 * 包名: com.hlframe.modules.dc.metadata.service.linkdb
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年12月21日 下午7:32:22
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service.linkdb;

import com.hlframe.common.utils.StringUtils;
import com.hlframe.modules.dc.metadata.entity.DcObjectLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.linkdb.DcTranslateFieldService.java 
 * @职责说明: 数据库字段转换规则Service 根据不同的函数, 转换不同的执行引擎脚本
 * @创建者: peijd
 * @创建时间: 2016年12月21日 下午7:32:22
 */
@Service
@Transactional(readOnly = false)
public class DcTranslateFieldService {
	
	/** 数据库转换引擎  **/
	public static final String TRANSLATE_ENGINE_IMPALA = "impala";
	public static final String TRANSLATE_ENGINE_HIVE = "hive";
	public static final String TRANSLATE_ENGINE_SPARKSQL = "sparksql";
	public static final String TRANSLATE_ENGINE_MYSQL = "mysql";
	
	
	/**
	 * @方法名称: translateField 
	 * @实现功能: 字段统一转换方法
	 * @param engineType	转换引擎
	 * @param link			字段链路关系
	 * @return
	 * @create by peijd at 2016年12月22日 上午9:55:29
	 */
	public String translateField(String engineType, DcObjectLink link){
		//remarks 作为转换函数的默认输出值, 如果有值则直接返回  peijd
		if(StringUtils.isNotBlank(link.getRemarks())){
			return "'"+link.getRemarks()+"'";
		}
		if(StringUtils.isEmpty(link.getSrcObjId())){
			return "";
		}
		/** 字符串处理函数 **/
		//空值替换
		if("str_replaceNull".equalsIgnoreCase(link.getRelationExp())) return strReplaceNull(engineType, link);
		//字段拼接
		if("str_concat".equalsIgnoreCase(link.getRelationExp())) return strConcat(engineType, link);
		//字符串截取
		if("str_substr".equalsIgnoreCase(link.getRelationExp())) return strSubstr(engineType, link);
		//字符串聚合
		if("str_group_concat".equalsIgnoreCase(link.getRelationExp())) return strGroupConcat(engineType, link);
		//字符串集合查找函数，返回一个逗号分隔的字符串中指定字符串第一次出现的位置
		if("str_find_in_set".equalsIgnoreCase(link.getRelationExp())) return strFindInSet(engineType, link);
		//字符串查找
		if("str_instr".equalsIgnoreCase(link.getRelationExp())) return strInstr(engineType, link);
		//带分隔符的连接函数
		if("str_concat_ws".equalsIgnoreCase(link.getRelationExp())) return strConcatWs(engineType, link);
		//字符串首字母大写
		if("str_initcap".equalsIgnoreCase(link.getRelationExp())) return strInitcap(engineType, link);
		//字符串转小写
		if("str_lowercase".equalsIgnoreCase(link.getRelationExp())) return str2Lowercase(engineType, link);
		//字符串转大写
		if("str_uppercase".equalsIgnoreCase(link.getRelationExp())) return str2Uppercase(engineType, link);
		//字符串长度值获取
		if("str_getLength".equalsIgnoreCase(link.getRelationExp())) return strGetLength(engineType, link);
		//字符串反转
		if("str_reverse".equalsIgnoreCase(link.getRelationExp())) return strReverse(engineType, link);
		//字符串去除空格
		if("str_trim".equalsIgnoreCase(link.getRelationExp())) return strTrim(engineType, link);
		//字符串左侧补足
		if("str_lpad".equalsIgnoreCase(link.getRelationExp())) return strLpad(engineType, link);
		//字符串右侧补足
		if("str_rpad".equalsIgnoreCase(link.getRelationExp())) return strRpad(engineType, link);
		//字符串重复N次值
		if("str_repeat".equalsIgnoreCase(link.getRelationExp())) return strRepeat(engineType, link);
		//正则表达式提取函数   
		if("str_regexp_extract".equalsIgnoreCase(link.getRelationExp())) return strRegexpExtract(engineType, link);
		//正则表达式 替换结果 
		if("str_regexp_replace".equalsIgnoreCase(link.getRelationExp())) return strRegexpReplace(engineType, link);
		//正则表达式 查找匹配结果
		if("str_regexp_like".equalsIgnoreCase(link.getRelationExp())) return strRegexpLike(engineType, link);
		//正则表达式 分隔取值函数  
		if("str_split_part".equalsIgnoreCase(link.getRelationExp())) return strSplitPart(engineType, link);
		//获取json中的字段 
		if("str_get_json_object".equalsIgnoreCase(link.getRelationExp())) return strGetJsonObject(engineType, link);
		
		/** 日期处理函数 **/
		//获取当前时间
		if("date_nowDate".equalsIgnoreCase(link.getRelationExp())) return dateNowDate(engineType, link);
		//获取时间戳中的日期部分
		if("date_fatchDate".equalsIgnoreCase(link.getRelationExp())) return dateFatchDate(engineType, link);
		//获取日期中的年份
		if("date_fatchYear".equalsIgnoreCase(link.getRelationExp())) return dateFatchYear(engineType, link);
		//获取日期中的月份
		if("date_fatchMonth".equalsIgnoreCase(link.getRelationExp())) return dateFatchMonth(engineType, link);
		//获取日期中的日期部分
		if("date_fatchDay".equalsIgnoreCase(link.getRelationExp())) return dateFatchDay(engineType, link);
		//获取日期中的小时部分
		if("date_fatchHour".equalsIgnoreCase(link.getRelationExp())) return dateFatchHour(engineType, link);
		//获取日期中的分钟部分
		if("date_fatchMinute".equalsIgnoreCase(link.getRelationExp())) return dateFatchMinute(engineType, link);
		//获取日期中的秒钟部分
		if("date_fatchSecond".equalsIgnoreCase(link.getRelationExp())) return dateFatchSecond(engineType, link);
		//获取日期中的日期部分 范围为从 'Sunday' 到 'Saturday'
		if("date_fatchDayname".equalsIgnoreCase(link.getRelationExp())) return dateFatchDayname(engineType, link);
		//返回字符串日期时间中的日期部分，并转换为该日期在一周中对应的序号。范围为 1 (Sunday) 到 7 (Saturday)
		if("date_fatchDayofweek".equalsIgnoreCase(link.getRelationExp())) return dateFatchDayOfWeek(engineType, link);
		//返回日期所在的周(1-53)
		if("date_fatchWeekOfYear".equalsIgnoreCase(link.getRelationExp())) return dateFatchWeekOfYear(engineType, link);
		//日期增加天数
		if("date_addDays".equalsIgnoreCase(link.getRelationExp())) return dateAddDays(engineType, link);
		//日期减少天数
		if("date_subDays".equalsIgnoreCase(link.getRelationExp())) return dateSubDays(engineType, link);
		//获取两个日期间隔天数
		if("date_betweenDays".equalsIgnoreCase(link.getRelationExp())) return dateBetweenDays(engineType, link);
		
		
		/** 数值处理函数 **/
		//获取数字的绝对值
		if("num_getAbs".equalsIgnoreCase(link.getRelationExp())) return numGetAbs(engineType, link);
		//获取数字的二进制值
		if("num_getBin".equalsIgnoreCase(link.getRelationExp())) return numGetBinary(engineType, link);
		//获取数字的十六进制值
		if("num_getHex".equalsIgnoreCase(link.getRelationExp())) return numGetHex(engineType, link);
		//获取小于该数字的最大整数
		if("num_getFloor".equalsIgnoreCase(link.getRelationExp())) return numGetFloor(engineType, link);
		//获取大于该数字的最小整数
		if("num_getCeil".equalsIgnoreCase(link.getRelationExp())) return numGetCeil(engineType, link);
		//获取该数字的四舍五入值  
		if("num_getRound".equalsIgnoreCase(link.getRelationExp())) return numGetRound(engineType, link);
		//获取随机数  
		if("num_getRand".equalsIgnoreCase(link.getRelationExp())) return numGetRand(engineType, link);
		
		
		/** 其他处理函数 **/
		//获取表达式列表(数字,时间,)的最小值
		if("com_getLeastVal".equalsIgnoreCase(link.getRelationExp())) return getLeastVal(engineType, link);
		//获取表达式列表(数字,时间,)的最大值
		if("com_getGreatestVal".equalsIgnoreCase(link.getRelationExp())) return getGreatestVal(engineType, link);
		//类型转换
		if("com_castType".equalsIgnoreCase(link.getRelationExp())) return castType(engineType, link);
		//根据条件转换
		if("com_case_when".equalsIgnoreCase(link.getRelationExp())) return caseWhen(engineType, link);
		
		return link.getSrcObjId();
	}
	
	
	
	/**
	 * @方法名称: strReplaceNull 
	 * @实现功能: 字符串空值替换
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午9:21:13
	 */
	public String strReplaceNull(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" isnull(").append(link.getSrcObjId()).append(") as ");
		}
		if(TRANSLATE_ENGINE_MYSQL.equals(engineType)){
			builder.append(" ifnull(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	/**
	 * @方法名称: strTrim 
	 * @实现功能: 去除字符串两端空格符
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:33:27
	 */
	public String strTrim(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" trim(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strRpad 
	 * @实现功能: 字符串右侧补足
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:01:51
	 */
	public String strRpad(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" rpad(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strLpad 
	 * @实现功能: 字符串左侧补足
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:40:51
	 */
	public String strLpad(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" lpad(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strRepeat 
	 * @实现功能: 获取字符串重复N次的值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:10:08
	 */
	public String strRepeat(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" repeat(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strRegexpExtract 
	 * @实现功能: 通过正则表达式 提取结果数据
	 * @param engineType	
	 * @param link
	 * @return
	 * <p>e.g. 
	 * <li>regexp_extract('abcdef123ghi456jkl','.*(\\d+)',1) ----> 456
	 * <li>regexp_extract('AbcdBCdefGHI','.*([[:lower:]]+)',1) ----> def
	 * <li>regexp_extract('AbcdBCdefGHI','.*([[:lower:]]+).*',1) ----> bcd
	 * @create by peijd at 2016年12月22日 下午3:11:54
	 */
	public String strRegexpExtract(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" regexp_extract(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strRegexpReplace 
	 * @实现功能: 通过正则表达式 替换结果数据
	 * @param engineType
	 * @param link
	 * @return
	 * <p> e.g.
	 * <li>	regexp_replace('aaabbbaaa','b+','xyz');  ---->aaaxyzaaa
	 * <li>	regexp_replace('aaabbbaaa','(b+)','<\\1>'); ----> aaa<bbb>aaa
	 * <li>	regexp_replace('123-456-789','[^[:digit:]]',''); ----> 123456789
	 * @create by peijd at 2016年12月22日 下午3:15:02
	 */
	public String strRegexpReplace(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" regexp_replace(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	/**
	 * @方法名称: strRegexpReplace 
	 * @实现功能: 通过正则表达式 查找匹配结果
	 * @param engineType
	 * @param link
	 * @return
	 * <p> e.g.
	 * <li>	regexp_like('foo','f');  ----> true
	 * <li>	regexp_like('foo','F');  ----> false
	 * <li>	regexp_like('foo','F','i'); ----> true (-i 忽略大小写)
	 * <li>	regexp_like('foo','f$'); ----> false
	 * <li>	regexp_like('foo','o$'); ----> true
	 * <li>	regexp_like('foooooobar','fo+b'); ----> true
	 * <li>	regexp_like('foooooobar','fx*y*o*b'); ----> true
	 * @create by peijd at 2016年12月22日 下午3:15:02
	 */
	public String strRegexpLike(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系	hive没有该函数
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType)){
			builder.append(" regexp_like(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * 
	 * @方法名称: strSplitPart 
	 * @实现功能: 分隔取值函数
	 * @param engineType
	 * @param link
	 * @return
	 * <p> e.g.
	 * <li>	split_part('x,y,z',',',1);  ----> x
	 * <li>	split_part('x,y,z',',',3);  ----> z
	 * <li>	split_part('one***two***three','***',2); ----> two
	 * @create by peijd at 2016年12月22日 下午3:32:58
	 */
	public String strSplitPart(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系 hive 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType)){
			builder.append(" split_part(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}else if(TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" split(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strGetJsonObject 
	 * @实现功能: 获取json对象中的值
	 * @param engineType
	 * @param link 
	 * @return
	 * <p> e.g.
	 * select  get_json_object('
	 * 		{
	 * 		 "store": {"fruit":\[{"weight":8,"type":"apple"},{"weight":9,"type":"pear"}],
	 * 		 "bicycle":{"price":19.95,"color":"red"} 
	 * 		},
	 * 		"email":"amy@only_for_json_udf_test.net",
	 * 		"owner":"amy"
	 * 		}
	 * 	','$.owner') from dual;
	 * 	--> amy 
	 * @create by peijd at 2017年1月11日 下午7:46:30
	 */
	public String strGetJsonObject(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		// hive 获取json中对象 
		if(TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" get_json_object(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strReverse 
	 * @实现功能: 字符串反转
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:07:38
	 */
	public String strReverse(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" reverse(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strGetLength 
	 * @实现功能: 字符串 获取长度值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:30:58
	 */
	public String strGetLength(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType)  || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" length(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}

	/**
	 * @方法名称: str2Lowercase 
	 * @实现功能: 字符串转小写
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月21日 下午7:49:23
	 */
	public String str2Lowercase(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" lower(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: str2Uppercase 
	 * @实现功能: 字符串转大写
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午9:21:13
	 */
	public String str2Uppercase(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		//字段转换关系
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" upper(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}


	/**
	 * @方法名称: strConcat 
	 * @实现功能: 字符串拼接
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午10:19:10
	 */
	public String strConcat(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" concat(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strSubstr 
	 * @实现功能: 字符串截取
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:36:28
	 */
	public String strSubstr(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" substr(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strGroupConcat 
	 * @实现功能: 字符串聚合函数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:51:13
	 */
	public String strGroupConcat(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType)){
			builder.append(" group_concat(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strFindInSet 
	 * @实现功能: 字符串集合查找, 返回一个逗号分隔的字符串中指定字符串第一次出现的位置
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:52:28
	 */
	public String strFindInSet(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" find_in_set(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strInstr 
	 * @实现功能: 字符串查找函数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:47:43
	 */
	public String strInstr(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" instr(").append(link.getSrcObjId()).append(",").append(link.getTransParam()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strConcatWs 
	 * @实现功能: 带分隔符的连接函数，使用分隔符 sep 把第二个以后的参数连接到一起(注，如没有第三个参数 b，则返回 a 的值)
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:55:42
	 */
	public String strConcatWs(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" concat_ws(").append(link.getTransParam()).append(",").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: strInitcap 
	 * @实现功能: 字符串 首字符大写
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午3:49:35
	 */
	public String strInitcap(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_MYSQL.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" initcap(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateNowDate 
	 * @实现功能: 获取当前时间
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:20:38
	 */
	public String dateNowDate(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		//hive 不支持
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType)){
			builder.append("now() as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchDate 
	 * @实现功能: 时间字符串中获取日期部分  to_date('2016-05-16 12:05:38') → 2016-05-16
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:22:11
	 */
	public String dateFatchDate(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" to_date(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchYear 
	 * @实现功能: 获取日期中的年份
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午10:21:31
	 */
	public String dateFatchYear(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" year(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchMonth 
	 * @实现功能: 获取日期中的月份
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午10:51:45
	 */
	public String dateFatchMonth(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" month(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchMonth 
	 * @实现功能: 获取字符串日期时间中的日期部分
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:14:48
	 */
	public String dateFatchDay(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" day(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchHour 
	 * @实现功能: 获取字符串日期时间中的小时部分
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:15:41
	 */
	public String dateFatchHour(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" hour(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchMinute 
	 * @实现功能: 获取字符串日期时间中的分钟部分
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:15:41
	 */
	public String dateFatchMinute(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" minute(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	/**
	 * @方法名称: dateFatchSecond 
	 * @实现功能: 获取字符串日期时间中的秒部分
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:15:41
	 */
	public String dateFatchSecond(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" second(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchDayname 
	 * @实现功能: 获取日期中的日期部分
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:11:32
	 */
	public String dateFatchDayname(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType)){
			builder.append(" dayname(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchDayofweek 
	 * @实现功能: 返回字符串日期时间中的日期部分，并转换为该日期在一周中对应的序号。范围为 1 (Sunday) 到 7 (Saturday)
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:13:26
	 */
	public String dateFatchDayOfWeek(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType)){
			builder.append(" dayofweek(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateFatchWeekOfYear 
	 * @实现功能: 获取日期所在的周
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:30:55
	 */
	public String dateFatchWeekOfYear(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" weekofyear(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateAddDays 
	 * @实现功能: 日期增加天数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午1:35:54
	 */
	public String dateAddDays(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" date_add(").append(link.getSrcObjId()).append(", ").append(Integer.parseInt(link.getTransParam())).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateSubDays 
	 * @实现功能: 日期减少天数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午1:43:12
	 */
	public String dateSubDays(String engineType, DcObjectLink link){
		Assert.hasText(link.getTransParam());
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" date_sub(").append(link.getSrcObjId()).append(", ").append(Integer.parseInt(link.getTransParam())).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: dateBetweenDays 
	 * @实现功能: 获取两个日期之间的天数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:09:00
	 */
	public String dateBetweenDays(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" datediff(").append(link.getSrcObjId()).append(", '").append(link.getTransParam()).append("') as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetAbs 
	 * @实现功能: 获取数字的绝对值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:35:37
	 */
	public String numGetAbs(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" abs(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetBinary 
	 * @实现功能: 获取数字的二进制表示
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:39:10
	 */
	public String numGetBinary(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" bin(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetHex 
	 * @实现功能: 获取数字的十六进制表示
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:48:19
	 */
	public String numGetHex(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" hex(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetFloor 
	 * @实现功能: 获取数字向下取整值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:46:40
	 */
	public String numGetFloor(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" floor(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetCeil 
	 * @实现功能: 获取数字向上取整值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:34:35
	 */
	public String numGetCeil(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" ceil(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetRound 
	 * @实现功能: 四舍五入取值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2017年1月11日 下午7:34:10
	 */
	public String numGetRound(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" round(").append(link.getSrcObjId());
			if(StringUtils.isNotEmpty(link.getTransParam())){
				builder.append(",").append(link.getTransParam());
			}
			builder.append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: numGetRand 
	 * @实现功能: 获取随机数
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2017年1月11日 下午7:39:52
	 */
	public String numGetRand(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" rand(").append(link.getSrcObjId());
			if(StringUtils.isNotEmpty(link.getTransParam())){
				builder.append(",").append(link.getTransParam());
			}
			builder.append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: getLeastVal 
	 * @实现功能: 获取列表中的最小值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 上午11:59:06
	 */
	public String getLeastVal(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" least(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: getGreatestVal 
	 * @实现功能: 获取列表中的最大值
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午2:33:34
	 */
	public String getGreatestVal(String engineType, DcObjectLink link){
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" greatest(").append(link.getSrcObjId()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: castType 
	 * @实现功能: 类型转换
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午1:27:56
	 */
	public String castType(String engineType, DcObjectLink link){
		Assert.hasText(link.getTarObjType(), "字段转换类型不可为空!");
		StringBuilder builder = new StringBuilder(); 
		if(TRANSLATE_ENGINE_IMPALA.equals(engineType) || TRANSLATE_ENGINE_HIVE.equals(engineType)){
			builder.append(" cast(").append(link.getSrcObjId()).append(" as ").append(link.getTarObjType()).append(") as ");
		}
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: caseWhen 
	 * @实现功能: 按条件进行转换 	link.TransParam 对象: 'a'→1,'b'→2,'c'→3,default→0   case col_1 when 'a' then 1 when 'b' then 2 when 'c' then 3 else 0 end 
	 * @param engineType
	 * @param link
	 * @return
	 * @create by peijd at 2016年12月22日 下午4:00:37
	 */
	public String caseWhen(String engineType, DcObjectLink link){
		String param = link.getTransParam();
		Assert.hasText(param);
		StringBuilder builder = new StringBuilder(64).append(" (case ").append(link.getSrcObjId()); 
		
		for(String item: param.split(",")){
			if("default".equalsIgnoreCase(item.split("→")[0])){
				builder.append(" else ").append(item.split("→")[1]).append(" end) as") ;
			}else{
				builder.append(" when ").append(item.split("→")[0]).append(" then ").append(item.split("→")[1]) ;
			}
		}
		
		//拼接目标字段
		return appendTargetField(builder, link.getTarObjId(), link.getSrcObjId());
	}
	
	/**
	 * @方法名称: appendTargetField 
	 * @实现功能: 拼接目标字段
	 * @param builder
	 * @param tarObjId
	 * @param srcObjId
	 * @create by peijd at 2016年12月22日 上午10:11:14
	 */
	private String appendTargetField(StringBuilder builder, String tarObjId, String srcObjId) {
		//拼接目标字段
		return builder.append(StringUtils.substringAfterLast(StringUtils.defaultIfBlank(tarObjId, srcObjId), ".")).toString();
	}
	
}
