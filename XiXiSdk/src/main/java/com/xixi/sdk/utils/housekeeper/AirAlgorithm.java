package com.xixi.sdk.utils.housekeeper;

public class AirAlgorithm{
	 public static int[] SearchKeyData(int _row, int temp ,int windLevl , int sign ,int model) {

		            int[] buf = new int[255];
	                int chkSum = 0;
	                int len = AirBean.air_table[_row][0];
	                buf[0] = 0x30;
	                chkSum += buf[0];
	                buf[1] = 0x01;
	                chkSum += buf[1];
	                buf[2] = (_row >> 8) & 0xFF;
	                chkSum += buf[2];
	                buf[3] = _row & 0xFF;
	                chkSum += buf[3];
	                buf[4] = 0x00;//(byte) AirData.mTemperature;
	                chkSum += buf[4];
	                buf[5] = 0x00;//(byte) AirData.mWindRate;
	                chkSum += buf[5];
	                buf[6] = 0x00;//(byte) AirData.mWindDirection;
	                chkSum += buf[6];
	                buf[7] = 0x00;//(byte) AirData.mAutomaticWindDirection;
	                chkSum += buf[7];
	                buf[8] = 0x00;//(byte) AirData.mPower;
	                chkSum += buf[8];
	                buf[9] = 0x00;//(byte) AirData.mKey;
	                chkSum += buf[9];
	                buf[10] = 0x00;//(byte) AirData.mMode;
	                chkSum += buf[10];
	                buf[11] = len + 1;
	                chkSum += buf[11];
	                for (int i = 0; i < len; i++) {
	                    buf[12 + i] = AirBean.air_table[_row][i + 1];
	                    chkSum += buf[12 + i];
	                }
	                buf[len + 12] = 0xFF;
	                chkSum += buf[len + 12];
	                buf[len + 13] = chkSum;
	                len = AirBean.air_table[_row][0] + 14;
				    int[] buffs = new int[len];
//	                SetKey(col);
	                buf[4] = temp;
	                checksum += buf[4];
	                buf[5] = windLevl;
	                checksum += buf[5];
	                buf[6] = mWindDirection;
	                checksum += buf[6];
	                buf[7] = mAutomaticWindDirection;
	                checksum += buf[7];
	                buf[8] = sign;
	                checksum += buf[8];
	                buf[9] = mKey;
	                checksum += buf[9];
	                buf[10] = model;
	                checksum += buf[10];
	                System.arraycopy(buf, 0, buffs, 0, len);

	                checksum = 0;
	                int ck = 0;
	                for (int j = 0; j < buffs.length - 1; j++) {
	                    ck = ck + buffs[j];
	                }
	                if (ck > 255) {
	                    ck = cutCheckSum(ck);
	                }
	                buffs[len - 1] = ck;

	                if (buffs[len - 1] > 255) {
	                    buffs[len - 1] = cutCheckSum(buffs[len - 1]);
	                }
	         
	        return buffs;
	 }
	public static int cutCheckSum(int num) {
	   String ckString = Integer.toBinaryString(num);
	   String bString = ckString.substring(ckString.length() - 8, ckString.length());
	   return Integer.parseInt(bString, 2);
	 }

	    private static int checksum = 0;
	    private static int mPower = 0x00;
	    private static int mTemperature = 0x19;
	    private static int mWindRate = 0x01; // 01 02 03 04
	    private static int mWindDirection = 0x02; // 01 02 03
	    private static int mAutomaticWindDirection = 0x01; // 00十六进制
	    private static int mMode = 0x01; // 0x01 ~ 0x05
	    private static int mKey = 0x01;
	    private static int mState = 0x00;
}
	 