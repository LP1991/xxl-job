/********************** 版权声明 *************************
 * 文件名: DcBase64.java
 * 包名: com.hlframe.modules.dc.utils
 * 版权:	杭州华量软件  hldc_bigdata
 * 职责:	
 ********************************************************
 *
 * 创建者：peijd   创建时间：2017年2月13日 下午1:59:41
 * 文件版本：V1.0 
 *
 *******************************************************/
package com.hlframe.modules.dc.utils;

import org.springframework.util.Assert;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/** 
 * @类名: com.hlframe.modules.dc.utils.DcBase64.java 
 * @职责说明: Base64 加解密工具类
 * @创建者: peijd
 * @创建时间: 2017年2月13日 下午1:59:41
 */
public class DcBase64 {

	/**
	 * @方法名称: encrypt 
	 * @实现功能: 实现 Base64加密, 默认utf-8编码
	 * @param str
	 * @return
	 * @create by peijd at 2017年2月13日 下午2:00:48
	 */
	public static String encrypt(String str) {
		byte[] b = null;
		String s = null;
		Assert.hasText(str, "加密参数不可为空!");
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}  
  
    /**
     * @方法名称: decrypt 
     * @实现功能: 实现 Base64解密, 默认utf-8编码
     * @param s
     * @return
     * @create by peijd at 2017年2月13日 下午2:01:04
     */
	public static String decrypt(String s) {
		byte[] b = null;
		String result = null;
//		Assert.hasText(s, "解密字符串不可为空!");
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(encrypt("1"));
		System.out.println(decrypt("dGVzdDpiaXJ0aGRheQ=="));
	}
}
