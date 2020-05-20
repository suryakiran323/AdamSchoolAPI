package com.stu.app.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class AesUtil {
	
	private AesUtil() {

	}

	private static final int KEY_SIZE = 128;
	private static final int ITERATION_COUNT = 1000;

	private static final String PASS_PHRASE = "nn/FaGhQl3YjsyUcpJl4dg==";

	public static String random(int length) {
		byte[] salt = new byte[length];
		new SecureRandom().nextBytes(salt);
		return hex(salt);
	}

	public static String base64(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public static byte[] base64(String str) {
		return Base64.decodeBase64(str);
	}

	public static String hex(byte[] bytes) {
		return Hex.encodeHexString(bytes);
	}

	public static byte[] hex(String str) {
		try {
			return Hex.decodeHex(str.toCharArray());
		} catch (DecoderException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String encrypt(String plaintext) {
		String salt = random(16);
		String iv = random(16);
		SecretKey key = generateKey(salt, PASS_PHRASE);
		byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeBase64String((iv + "::" + salt + "::" + base64(encrypted)).getBytes());
	}

	public static String decrypt(String value) {
		try {
			value = new String(java.util.Base64.getDecoder().decode(value));
			SecretKey key = generateKey(value.split("::")[1], PASS_PHRASE);
			byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, value.split("::")[0], base64(value.split("::")[2]));
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
			return cipher.doFinal(bytes);
		} catch (InvalidKeyException
				| InvalidAlgorithmParameterException
				| IllegalBlockSizeException
				| BadPaddingException
				| NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			return null;
		}
	}

	private static SecretKey generateKey(String salt, String passPhrase) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(PASS_PHRASE.toCharArray(), hex(salt), ITERATION_COUNT, KEY_SIZE);
			return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return null;
		}
	}

}
