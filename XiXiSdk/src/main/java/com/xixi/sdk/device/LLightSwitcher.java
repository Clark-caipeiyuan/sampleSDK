package com.xixi.sdk.device;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;

public  class LLightSwitcher {

	private final static int LIGHT_SWITCHER_NUM = 4 ; 
	private boolean _lockBits[] ; 
	
	public boolean isBusy(int pos) {
		return _lockBits[pos]; 
	}
	public void setBusyBit(int pos , boolean busyBit) { 
		_lockBits[pos] = busyBit ; 
	}
	public LLightSwitcher() { 
		_lockBits  = new boolean[LIGHT_SWITCHER_NUM] ;
		for(int i = 0 ; i < LIGHT_SWITCHER_NUM ; i++ ) { 
			_lockBits[i] = false ; 
		}
	}

	public static void test() { 
		LLightSwitcher ls = new LLightSwitcher() ; 
		String str = ls._generateRawStr() ;
		Log.i(LongLakeApplication.DANIEL_TAG, "test string:" + str ); 
		for (int i = 0 ; i < 4 ; i++ ) { 
			ls.setSwitch_one_bit(i, 1);
			str = ls._generateRawStr() ;
			Log.i(LongLakeApplication.DANIEL_TAG, "test string:" + str );
		}
		ls.setSwitch_one_bit(0, 0);
		str = ls._generateRawStr() ;
		Log.i(LongLakeApplication.DANIEL_TAG, "test string:" + str ); 
	
		str = ls.generateReqStr(1, 1) ;
		Log.i(LongLakeApplication.DANIEL_TAG, "test string:" + str + " " + ls._generateRawStr());
		ls.generateReqStr(2, 1) ;
		ls.generateReqStr(1, 0) ;
		str = ls._generateRawStr() ;
		Log.i(LongLakeApplication.DANIEL_TAG, "test string:" + str );
	}
	 
	int switch_bits = 0;
	int fake_switch_bits = 0 ; 
	
	public void synchSwitcherBits() {
		fake_switch_bits = switch_bits ; 
	}
	public void setSwitcherBits(int s_status) { 
		switch_bits = s_status ; 
	}
	public static int _setSwitch_one_bit(int target, int i, int value) {
		
		if (value == 0) {
			target = (target & (0xF - (1 << (LIGHT_SWITCHER_NUM- i - 1 ))));
		} else {
			target = (target | (1 << (LIGHT_SWITCHER_NUM- i - 1 )));
		}
		return target;
	}

	public void setSwitch_one_bit(int position, int sign) {
		Log.i(LongLakeApplication.DANIEL_TAG, String.format("set %d to %d" , position , sign));
		switch_bits = _setSwitch_one_bit(switch_bits, position, sign);
	}

	public int getSwitch_bit(int i) {
		return ((switch_bits>>(LIGHT_SWITCHER_NUM- i - 1 )) & 0x1);
	}

	public static String generateBitStr(int v) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < LIGHT_SWITCHER_NUM; i++ ) {
			sb.append(((v & 0x1) != 0) ? "1" : "0");
			v = (v >> 1);
		}
		return sb.reverse().toString();
	}

	public String generateReqStr(int position, int sign) {
		fake_switch_bits = _setSwitch_one_bit(fake_switch_bits, position, sign);
		return generateBitStr(fake_switch_bits);
	}

	private String _generateRawStr() {
		return generateBitStr(switch_bits);
	}
}