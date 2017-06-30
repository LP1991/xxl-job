package com.hlframe.modules.dc.utils;

import com.hlframe.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @类名: com.hlframe.common.utils.DESUtils.java 
 * @职责说明: 使用DES加密与解密,可对byte[],String类型进行加密与解密 密文可使用String,byte[]存储
 * @创建者: peijd
 * @创建时间: 2017年1月6日 下午3:54:28
 */
public final class DESUtils {
	
	protected static Logger logger = LoggerFactory.getLogger(DESUtils.class);
	
    /**
     * 默认密钥
     */
    private static final String DEFAULT_DES_SECRET_KEY = "Hzhl@2015";
    

    private static Key key;
    public static String ecoderKey = "DES";

    static {
        initKey(DEFAULT_DES_SECRET_KEY);
    }

    /**
     * 根据参数生成KEY
     * @param strKey key
     */
    private static void initKey(String strKey) {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance(ecoderKey);
            _generator.init(new SecureRandom(strKey.getBytes()));
            key = _generator.generateKey();
            _generator = null;
        } catch (Exception e) {
        	logger.error("-->initKey", e);
        	throw new RuntimeException(e);
        }
    }

    /**
     * 加密String  明文输入,密文输出
     * 
     * @param strMing 明文
     * @return 密文
     */
    public static String getEncStr(String strMing) {
        if(StringUtils.isBlank(strMing)){
            return null;
        }
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        BASE64Encoder base64en = new BASE64Encoder();
        try {
            byteMing = strMing.getBytes("UTF8");
            byteMi = getEncCode(byteMing);
            strMi = base64en.encode(byteMi);
        } catch (Exception e) {
        	logger.error("-->getEncStr", e);
        	throw new RuntimeException(e);
        } finally {
            base64en = null;
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 解密  密文输入,明文输出
     * 解密失败返回null
     * @param strMi 密文
     * @return 明文
     */
    public static String getDesStr(String strMi) {
        if(StringUtils.isBlank(strMi)){
            return null;
        }
        BASE64Decoder base64De = new BASE64Decoder();
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = base64De.decodeBuffer(strMi);
            byteMing = getDesCode(byteMi);
            strMing = new String(byteMing, "UTF8");
        } catch (Exception e) {
        	logger.error("-->getDesStr", e);
            throw new RuntimeException("无效密文!");
        } finally {
            base64De = null;
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以byte[]明文输入,byte[]密文输出
     * 
     * @param byteS 明文
     * @return 密文
     */
    private static byte[] getEncCode(byte[] byteS) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ecoderKey);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
        	logger.error("-->getDesStr", e);
        	throw new RuntimeException(e);
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以byte[]密文输入,以byte[]明文输出
     * 
     * @param byteD 密文
     * @return 明文
     */
    private static byte[] getDesCode(byte[] byteD) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance(ecoderKey);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
        	logger.error("-->getDesCode", e);
        	throw new RuntimeException(e);
        } finally {
            cipher = null;
        }
        return byteFina;

    }
    
    /**
     * 
     * <p>功能说明：</p>
     * @param args parameters
     */
    public static void main(String[] args) {
        System.out.println(DESUtils.getEncStr("6015b0422cc84eda9ea40d7060a68624"));	//加密字符串  izpTE8WGwzw=
        System.out.println(DESUtils.getDesStr("izpTE8WGwzw="));// 解密字符串 czgrj
    }

}
