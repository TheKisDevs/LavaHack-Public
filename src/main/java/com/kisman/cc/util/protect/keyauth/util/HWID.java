package com.kisman.cc.util.protect.keyauth.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author superblaubeere27
 *
 */
public class HWID {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String getHWID() {
		return bytesToHex(generateHWID());
	}

	public static byte[] generateHWID() {
		try {
			MessageDigest hash = MessageDigest.getInstance("MD5");

			String s = System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("os.version")
					+ Runtime.getRuntime().availableProcessors() + System.getenv("PROCESSOR_IDENTIFIER")
					+ System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432")
					+ System.getenv("NUMBER_OF_PROCESSORS");
			return hash.digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new Error("Algorithm wasn't found.", e);
		}

	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
