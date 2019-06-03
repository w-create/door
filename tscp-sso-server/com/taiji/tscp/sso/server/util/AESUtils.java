package com.taiji.tscp.sso.server.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * 对称加密(AES)
 * 
 * @author Joe
 */
public class AESUtils {
	
	public static final String INIT_VECTOR = "RandomInitVector";

	/**
	 * 加密
	 * @param key 密钥
	 * @param value 加密数据
	 * @return
	 */
	public static String encrypt(String key, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return new BASE64Encoder().encode(encrypted);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * @param key 密钥
	 * @param value 解密数据
	 * @return
	 */
	public static String decrypt(String key, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(encrypted));

			return new String(original);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String key = "``11qqaazzxxccvv"; // 128 bit key
		System.out.println(encrypt(key, "123"));
		System.out.println(decrypt(key, encrypt(key, "123")));
	}
}
