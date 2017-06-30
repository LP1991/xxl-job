/********************** 版权声明 *************************
 * 文件名: DcPropertyUtils.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2016年11月3日 下午3:30:43
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import com.hlframe.modules.dc.metadata.entity.Pro;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;


/** 
 * @类名: com.hlframe.modules.dc.utils.DcPropertyUtils.java 
 * @职责说明: 配置文件工具类
 * @创建者: peijd
 * @创建时间: 2016年11月3日 下午3:30:43
 */
public class DcPropertyUtils {
	public static final String dirPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	public static final String dcPropPath = "dc_config.properties";
	public static Map<String,Pro> map;
	
	//最后修改时间
	static long lastTime;
	
	static{
		lastTime = new File(dirPath+dcPropPath).lastModified();
		System.out.println("---->"+lastTime);
		reloadProperTies();
	}
	
	/**
	 * @方法名称: reloadProperTies 
	 * @职责说明: 重新加载配置文件
	 * @参数:     
	 * @返回: void   
	 * @创建者: pjd 
	 * @创建时间: 2016年11月3日 下午3:35:13
	 */
	public static void reloadProperTies() {
		map = readProperties(dirPath+dcPropPath);
	}
	
	/**
	 * @方法名称: getProperty 
	 * @职责说明: 读取配置文件
	 * @参数: @param propName	参数名
	 * @参数: @param defaultVal 默认值
	 * @参数: @return    
	 * @返回: String   
	 * @创建者: pjd 
	 * @创建时间: 2016年11月3日 下午3:41:10
	 */
	public static String getProperty(String propName, String defaultVal) {
		Assert.hasText(propName, "参数名不能为空!");
		//如果配置文件发生修改, 则重新加载
		if (lastTime!=new File(dirPath+dcPropPath).lastModified()) {
			reloadProperTies();
		}
		if (null!=map) {
			if(null!=map.get(propName)){
				return map.get(propName).getValue();
			}
		}
		return defaultVal;
	}
	
	/**
	 * @方法名称: getProperty 
	 * @实现功能: 获取配置文件默认属性
	 * @param propName
	 * @return
	 * @create by peijd at 2016年11月24日 下午6:46:40
	 */
	public static String getProperty(String propName) {
		Assert.hasText(propName, "参数名不能为空!");
		//如果配置文件发生修改, 则重新加载
		if (lastTime!=new File(dirPath+dcPropPath).lastModified()) {
			reloadProperTies();
		}
		if(null!=map){
			if(null!=map.get(propName)){
				return map.get(propName).getValue();
			}
		}
		return null;
	}
	
	/**
	 * @方法名称: load 
	 * @实现功能: 载入Properties 对象
	 * @param path
	 * @return
	 * @throws IOException
	 * @create 2016年11月3日 下午3:45:08
	 */
	public static Properties load(String path) throws IOException {
		if(path.startsWith("/")){
			path = path.substring(1);
		}else if(path.startsWith("file")){
			path = path.substring(5);
		}
        InputStream is = DcPropertyUtils.class.getClassLoader().getResourceAsStream(path);
//        InputStream is = this.getClass().getResource("/").getPath().getResourceAsStream(path);

		if(null==is){
			is=new FileInputStream(path);
		}
        if(null==is){
            throw new IOException(path+" file is not found!");
        }
        try {
            return load(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
	
	/**
	 * @方法名称: load 
	 * @实现功能: 根据文件流 载入Properties 对象
	 * @param is
	 * @return
	 * @throws IOException
	 * @create by peijd at 2016年11月3日 下午3:41:13
	 */
	public static Properties load(InputStream is) throws IOException {
		Properties prop = new Properties();
		prop.load(is);

		// 转码处理
		Set<Object> keyset = prop.keySet();
		Iterator<Object> iter = keyset.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String newValue = null;
			try {
				// 属性配置文件自身的编码
				newValue = new String(prop.getProperty(key).getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				newValue = prop.getProperty(key);
			}
			prop.setProperty(key, newValue);
		}

		return prop;
	}

	
	/**
	 * 
	 * @方法名称: readProperties 
	 * @实现功能: 修改配置信息
	 * @param filePath
	 * @create by hgw at 2017年3月27日 下午6:36:16
	 */
	public static void writerProperties(String filePath, String parameterName, String parameterValue){
		Properties props = new Properties();
		try{
			InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
			props.load(fis);
			OutputStream fos = new FileOutputStream(filePath);
			props.setProperty(parameterName, parameterValue);
			props.store(fos, "update "+parameterName+"value");
			reloadProperTies();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @方法名称: readProperties 
	 * @实现功能: 获取文件所有配置信息
	 * @param filePath
	 * @create by hgw at 2017年3月27日 下午6:36:16
	 */
	public static Map<String,Pro> readProperties(String filePath){
		map = new HashMap<String,Pro>();
		Properties props = new Properties();
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(filePath));
			props.load(in);
			Enumeration en = props.propertyNames();
			while(en.hasMoreElements()){
				Pro pro = new Pro();
				String key = (String)en.nextElement();
				if(key.endsWith(".desc")){
					continue;
				}
				String property = new String(props.getProperty(key).getBytes("ISO-8859-1"), "utf-8");
				pro.setKey(key);
				pro.setValue(property);
				map.put(key, pro);
			}
			en = props.propertyNames();
			while(en.hasMoreElements()){
				String key = (String)en.nextElement();
				if(key.endsWith(".desc")){
					//截取之后的名字
					String k = key.substring(0, key.lastIndexOf(".desc"));
					Pro pro = map.get(k);
					if(pro!=null){
						//获取备注
						String desc = new String(props.getProperty(key).getBytes("ISO-8859-1"), "utf-8");
						pro.setDesc(desc);
					}
					map.put(k, pro);
				}
			}
		}catch(Exception e){
			
		}finally {
            IOUtils.closeQuietly(in);
        }
		return map;
	}
	public static void main(String[] args) {
		System.out.println("-->"+DcPropertyUtils.class.getResource("/").getPath());
		System.out.println(getProperty("elasticSearch.main.url", "AA"));
		System.out.println(getProperty("etl.dbconfig.path", "BB"));

		try {
//			File proFile = new File("D:/log4j.properties");
			/*Properties p = new Properties();
			FileInputStream ferr=new FileInputStream("D:/log4j.properties");
			p.load(ferr);
			ferr.close();*/
			Properties p = load("D:/log4j.properties");
			System.out.println("--->log4j.rootLogger: "+p.get("log4j.rootLogger"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
