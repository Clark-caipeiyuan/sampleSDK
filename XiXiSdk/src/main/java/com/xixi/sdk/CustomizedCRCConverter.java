package com.xixi.sdk;

public class CustomizedCRCConverter {

	private final static int fan_yushi[] = { // REV_CRC16 0xA001
												// //閳嚙銉㈡CRC16閳倵锟解埁鎾佹拃鐘栵拷
			0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401, 0xA001, 0x6C00, 0x7800, 0xB401, 0x5000,
			0x9C01, 0x8801, 0x4400 };

	private static int getU8(int data) {

		LLSDKUtils.danielAssert(data >= 0);
		return data & 0xFF;
	}

	// 娴ｅ簼缍�
	private static int getU16(int data) {

		LLSDKUtils.danielAssert(data >= 0);
		return data & 0xFFFF;
	}

	// 妤傛ü缍�
	private static int getHU8(int data) {
		LLSDKUtils.danielAssert(data >= 0);
		return getU8(data >> 8);

	}

	// u16 crc;
	// u8 da;

	public static int createCRC(byte[] dataStream, int length) {
		int crc = 0xFFFF;
		int da = 0;

		for (int i = 0; i < length; i++) {
			byte node = dataStream[i];
			int convertedNode = node >= 0 ? node : (node + 256);
			da = getU8(crc & 0xF);
			crc >>= 4;
			crc ^= fan_yushi[getU8(da ^ (convertedNode & 0xF))];
			crc = getU16(crc);
			da = getU8(crc & 0xF);
			crc >>= 4;
			crc ^= fan_yushi[getU8(da ^ (convertedNode >> 4))];
			crc = getU16(crc);
		}

		return crc;
	}

	public static int createCRC(byte[] dataStream) {
		return createCRC(dataStream, dataStream.length);
	}

	public static boolean checkCRC(byte[] data) {
		int calcCRC = createCRC(data, data.length - 2);
		int cc = 0;
		for (int i = data.length - 1; i >= data.length - 2; i--) {
			int _k = data[i] < 0 ? (data[i] + 256) : data[i];
			cc = (cc << 8) + _k;
		}
		return cc == calcCRC;
	}

	public static boolean checkCRC(byte[] data, byte[] crc) {

		LLSDKUtils.danielAssert(crc.length == 2);
		int calcCRC = createCRC(data);
		int cc = 0;
		for (byte b : crc) {
			cc = (cc << 8) + (b < 0 ? (b + 256) : b);
		}

		return calcCRC == cc;
	}

	public static byte[] hexStringToByte(String s) {
		int len = s.length();
		byte[] b = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return b;
	}

	public static String bytesToHexString(byte[] bytes) {
		return bytesToHexString(bytes, bytes.length);
	}

	public static String bytesToHexString(byte[] bytes, int size) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
