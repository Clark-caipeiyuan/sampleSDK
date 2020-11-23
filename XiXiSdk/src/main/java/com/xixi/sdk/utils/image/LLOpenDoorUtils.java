package com.xixi.sdk.utils.image;

import java.util.Locale;
import java.util.Random;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.model.LLOpenDoorData;
import com.xixi.sdk.utils.encryption.LLDESCrypCode;

public class LLOpenDoorUtils {
	private static String[] ENCRYPT_ARRAY = { "b", "0", "1", "3", "f", "4", "5", "6", "8", "9", "2", "7", "a", "c", "e",
			"d" };
	private static String[] DECRYPT_ARRAY = { "1", "2", "a", "3", "5", "6", "7", "b", "8", "9", "c", "0", "d", "f", "e",
			"4" };

	private final static int MIN_PWD = 5000;
	// abcdef1234567890
	// 27aced013f45689b
	public static String createNewPwd(int maxNum, int len) {
		Random pwd_generator = new Random();
		pwd_generator.setSeed(System.currentTimeMillis());
		int pwd = pwd_generator.nextInt(maxNum);
		return String.format(Locale.ENGLISH, "%0" + len + "d", pwd > MIN_PWD ? pwd : pwd + MIN_PWD );
	} 

	private static String toCryptedString(final StringBuffer str ) {
		StringBuilder sb_mixed_key = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			sb_mixed_key.append(String.valueOf(ENCRYPT_ARRAY[char2Int(c)]));
		}
		return sb_mixed_key.toString() ;
	}
	
	private static int char2Int(char c ) {  
		return c <= '9' ? (c - '0') : (c - 'a' + 10) ; 
	} 
	
	public static LLOpenDoorData getRandomOpenDoorData(String mac) {
		mac =  mac.toLowerCase() ;
		char[] keyArray = mac.toCharArray();
		StringBuffer mix_key = new StringBuffer(mac);
		StringBuffer secret_key = new StringBuffer();
		char pwdKeysIndexArray[] = createNewPwd(100000000, 8).toCharArray();
		int lastIndex = char2Int(keyArray[keyArray.length - 1]);
		int Parity = ((lastIndex & 0x1) == 0 ? 0 : -1);
		for (int i = 8; i > 0; i--) {
			int index = char2Int(pwdKeysIndexArray[i - 1]);
			mix_key.insert(i + Parity, keyArray[index]);
			secret_key.insert(0, keyArray[index]);
		}
		
		LLOpenDoorData lLOpenDoorData = new LLOpenDoorData(mac, secret_key.toString(), toCryptedString(mix_key),
				createNewPwd(10000, 4));
		return lLOpenDoorData;
	}

	public static LLOpenDoorData getOpenDoorDataFromCiphertext(String mix, byte[] ciphertext) {
		StringBuffer mix_key = new StringBuffer(mix);
		for (int i = 0; i < mix_key.length(); i++) {
			mix_key = mix_key.replace(i, i + 1, DECRYPT_ARRAY[Integer.parseInt(mix_key.charAt(i) + "", 16)]);
		}
		StringBuffer secret_key = new StringBuffer();
		int Parity = Integer.parseInt(mix_key.charAt(mix_key.length() - 1) + "", 16) % 2 == 0 ? 1 : 0;
		for (int i = 0; i < 8; i++) {
			secret_key.append(mix_key.charAt(i + Parity));
			mix_key.deleteCharAt(i + Parity);
		}
		LLOpenDoorData desData = new LLOpenDoorData(mix_key.toString(), secret_key.toString(), mix, "");
		try {
			desData.setPwd(new String(LLDESCrypCode.decrypt(ciphertext, desData.getSecret_key()), "UTF-8"));
		} catch (Exception e) {
			Log.d(LongLakeApplication.DANIEL_TAG, "exception: fail to pwd from ciphertext");
		}
		return desData;
	}

}
