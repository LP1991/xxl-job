/********************** 版权声明 *************************
 * 文件名: proService.java
 * 包名: com.hlframe.modules.dc.metadata.service
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：hgw   创建时间：2017年3月29日 上午11:00:27
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.metadata.service;

import com.hlframe.common.persistence.Page;
import com.hlframe.modules.dc.metadata.entity.Pro;
import com.hlframe.modules.dc.utils.DcPropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
 * @类名: com.hlframe.modules.dc.metadata.service.proService.java 
 * @职责说明:配置文件service
 * @创建者: hgw
 * @创建时间: 2017年3月29日 上午11:00:27
 */
@Service
@Transactional(readOnly = true)
public class ProService {
		public Page<Pro> findProList(Page<Pro> page, Pro pros){
			List<Pro> list = new ArrayList<Pro>();//存放排序后的数据
			List<Pro> resultList = new ArrayList<Pro>();//存放最后结果
			long count= 0;
			Map<String,Pro> map = DcPropertyUtils.map;
			Pro pro = null;
			String[] arr =  
	             {  
	                "A", "B", "C", "D", "E", "F", "G", "H", "I",  
	                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"  
	               };  
			for(String va:arr){
				for(Object p :map.keySet()){
					String key = (String)p;
					 pro = map.get(key);
						 if(va.equals(key.substring(0,1).toUpperCase())){
							 list.add(pro);
						 }
				}
			}
			for(int i=0;i<list.size();i++){
				pro = list.get(i);
				if(null!=pro){
					//筛选记录 页数个数我知道
						//页数
						int pageno = page.getPageNo();
						//开始记录
						int start = (pageno-1)*(page.getPageSize());
						//结束记录
						int end = pageno*page.getPageSize();
						//取页数大于等于开始记录并小于结束记录
						if(count>=start&&count<end){
							resultList.add(pro);
							
						}
						count++;
				 }
			}
			/*for(Object p :map.keySet()){
				String key = (String)p;
				 pro = map.get(key);
				 if(null!=pro){
					//筛选记录 页数个数我知道
						//页数
						int pageno = page.getPageNo();
						//开始记录
						int start = (pageno-1)*(page.getPageSize());
						//结束记录
						int end = pageno*page.getPageSize();
						//取页数大于等于开始记录并小于结束记录
						if(count>=start&&count<end){
							 list.add(pro);
							
						}
						count++;
				 }
			}*/
			page.setCount(count);
			page.setList(resultList);
			
			return page;
		}
}
