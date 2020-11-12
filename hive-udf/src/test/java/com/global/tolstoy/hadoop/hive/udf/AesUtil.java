/**
 * 
 */
package com.global.tolstoy.hadoop.hive.udf;


import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     AesUtil
 * @Description:   TODO(用一句话描述该文件做什么) 
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @since:      2020-11-06 11:55:11 AM
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2020-11-06     tolstoy           v1.0.0               修改原因
 */
public class AesUtil {
	
	
    private static final String ENCRYPTION_KEY="b14e25ae02c8e279";
    private static final String ENCRYPTION_IV="4e5Wa71fYoT7MFEX";


    /**
     * @Title: doEncryptField
     * @Description: TODO  加密
     * @param @param value 明文  
     * @return String    密文
     * @throws
     * Modification History:
     * Date         Author          Version            Description
     *---------------------------------------------------------*
     * 2020-11-10     tolstoy           v1.0.0               修改原因
     */
    public static String doEncryptField(String value) {
        try {
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Title: doDecryptField
     * @Description: TODO  解密
     * @param @param value  密文
     * @param @return    设定文件
     * @return String    明文
     * @throws
     * Modification History:
     * Date         Author          Version            Description
     *---------------------------------------------------------*
     * 2020-11-10     tolstoy           v1.0.0               修改原因
     */
    public static String doDecryptField(String value) {
        try {
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
            return new String(cipher.doFinal(Base64.getDecoder().decode(value)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static AlgorithmParameterSpec makeIv() {
        return new IvParameterSpec(ENCRYPTION_IV.getBytes(StandardCharsets.UTF_8));
    }

    private static Key makeKey() {
        try {
            byte[] key=ENCRYPTION_KEY.getBytes(StandardCharsets.US_ASCII);
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
