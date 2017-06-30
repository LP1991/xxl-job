/********************** 版权声明 *************************
 * 文件名: DcTaskQueryUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：hgw   创建时间：2017年4月24日 上午10:26:39
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.modules.dc.schedule.entity.DcTaskQuery;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcTaskQueryUtils.java 
 * @职责说明: TODO
 * @创建者: hgw
 * @创建时间: 2017年4月24日 上午10:26:39
 */
public class DcTaskQueryUtils {
	public static List<DcTaskQuery> dcTaskQueryList;
	private static ConnBean cb;
	private static SSHExec ssh; 
	
	static{
		//第一次加载
		loadList();
	}
	
	public static void loadList(){
		try {
			dcTaskQueryList = new ArrayList<DcTaskQuery>();
			String[] arrs = DcPropertyUtils.getProperty("sqoop.client.address").split(",");//获取所有sqoop配置，按,分隔
			//String  address = DcPropertyUtils.getProperty("sqoop.client.address");
			String user = DcPropertyUtils.getProperty("sqoop.client.loginUser");
			String passwpord = DcPropertyUtils.getProperty("sqoop.client.loginPswd");
			//遍历配置的信息个数
			for(int i=0;i< arrs.length;i++){
				DcTaskQuery dtq = new DcTaskQuery();
				//根据sqoop配置信息分别获取cb
				cb = new ConnBean(arrs[i],
						user,
						passwpord);
				//使用反射机制加载ssh
			    //使用反射机制创建实体类SSHExec
			    Constructor<SSHExec> constructor = (Constructor<SSHExec>) Class.forName("net.neoremind.sshxcute.core.SSHExec").getDeclaredConstructor(ConnBean.class);
			    constructor.setAccessible(true);
			    ssh = constructor.newInstance(cb);
			    ssh.connect();
			    //设置cb
			    dtq.setCb(cb);
			    //设置ssh
			    dtq.setSsh(ssh);
			    //设置位置
			    dtq.setWeiZhi(i);
				//设置空闲状态
				dtq.setStatus(DcTaskQuery.SSH_STATUS_FREE);
				dcTaskQueryList.add(dtq);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//获取空闲DcTaskQuery 改变状态
	public static DcTaskQuery getDcTaskQueryYes(){
		for(DcTaskQuery dtq:dcTaskQueryList){
			if(DcTaskQuery.SSH_STATUS_FREE.equals(dtq.getStatus())){
				dtq.setStatus(DcTaskQuery.SSH_STATUS_EMPLOY);
				return dtq;
			}
		}
		return null;
	}
	
	//获取空闲DcTaskQuery 不改变状态
	public static DcTaskQuery getDcTaskQueryNo(){
		for(DcTaskQuery dtq:dcTaskQueryList){
			if(DcTaskQuery.SSH_STATUS_FREE.equals(dtq.getStatus())){
				return dtq;
			}
		}
		return null;
	}
}
