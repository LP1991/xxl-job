/********************** 版权声明 *************************
 * 文件名: DcUtils.java
 * 包名: com.hlframe.modules.dc.DcBeanUtils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月4日 下午1:43:25
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.SpringContextHolder;
import com.hlframe.modules.sys.dao.UserDao;
import com.hlframe.modules.sys.entity.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcBeanUtils.java 
 * @职责说明: 获取bean工具类, 不启动服务的情况下,通过该工具可以获取services,并测试其方法
 * @创建者: peijd
 * @创建时间: 2016年11月4日 下午1:43:25
 */
public class DcBeanUtils {
	
	static{
		//根据配置文件 加载bean
		new ClassPathXmlApplicationContext("spring-context.xml","spring-context-jedis.xml");
	}

	/**
	 * @方法名称: getBean 
	 * @实现功能: 根据className  获取bean对象
	 * @param className
	 * @return
	 * @create by peijd at 2016年11月4日 下午3:52:49
	 */
	public static <T> T getBean(String className){
		return SpringContextHolder.getBean(className);
	}
	
	/**
	 * @方法名称: getBean 
	 * @实现功能: 根据classType  获取bean对象
	 * @param classype
	 * @return
	 * @create by peijd at 2016年11月4日 下午3:53:19
	 */
	public static <T> T getBean(Class classype){
		return (T)SpringContextHolder.getBean(classype);
	}
	
	/**
	 * @方法名称: main 
	 * @实现功能: 测试类
	 * @param args
	 * @create by peijd at 2016年11月4日 下午3:54:45
	 */
	public static void main(String[] args) {
		//DcTaskLogService dcTaskLogService = getBean(DcTaskLogService.class);
	//	System.out.println(dcTaskLogService);
		UserDao userDao = getBean(UserDao.class);
//		UserDao userDao = getBean("userDao");
		User user = userDao.get("1");
		System.out.println("---->"+user.getName());	// 输出: ---->admin
	}
}
