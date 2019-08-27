package com.wingo1.demo.encrypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RAS非对称加密解密
 * 
 * @author wingo1
 *
 */
public class LicenseRSAHelper {
	/**
	 * 私钥加密
	 * 
	 * @param content
	 * @param publicKey 十六进制的字符串
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content, String privateKeyHex) throws Exception {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(parseHexStr2Byte(privateKeyHex));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		Cipher cipher = Cipher.getInstance("RSA");// java默认"RSA"="RSA/ECB/PKCS1Padding"
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] bytes = cipher.doFinal(content.getBytes());
		return parseByte2HexStr(bytes);
	}

	/**
	 * 公钥解密
	 * 
	 * @param content
	 * @param publicKeyHex 十六进制的字符串
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content, String publicKeyHex) throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(parseHexStr2Byte(publicKeyHex));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte[] bytes = cipher.doFinal(parseHexStr2Byte(content));
		return new String(bytes);
	}

	public static void main(String[] args) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024);// 此方法自动生成SecureRandom，也可手动 public void initialize(int keysize, SecureRandom
											// random)
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 获取公钥，并以base64格式打印出来
		PublicKey publicKey = keyPair.getPublic();
		System.out.println("公钥：" + parseByte2HexStr(publicKey.getEncoded()));

		// 获取私钥，并以base64格式打印出来
		PrivateKey privateKey = keyPair.getPrivate();
		System.out.println("私钥：" + parseByte2HexStr(privateKey.getEncoded()));
		String content = "yes, this is a test";
		String encrypt = encrypt(content, parseByte2HexStr(privateKey.getEncoded()));
		System.out.println("--" + encrypt);
		System.out.println("---" + decrypt(encrypt, parseByte2HexStr(publicKey.getEncoded())));

	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}
