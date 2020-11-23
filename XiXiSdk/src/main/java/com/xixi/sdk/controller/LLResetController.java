package com.xixi.sdk.controller;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

public class LLResetController {

	private static LLResetController instance;

	public static synchronized LLResetController getInstance() {
		if (instance == null) {
			instance = new LLResetController();
		}
		return instance;
	}

	private final static int PERIODIC = 24 * 3600;
	private static final long time_8 = 8 * 3600;
	private static final long time_reboot = 3 * 3600;

	/*
	 * private long delayTime(long currentTimestamp){
	 * if(currentTimestamp<=time_16){ return time_16 - currentTimestamp ; }else{
	 * return PERIODIC-(currentTimestamp+time_8-PERIODIC); } }
	 */
	public long convertTime(long currentTime) {
		return (currentTime / 1000 + time_8) % PERIODIC;
	}

	public LLResetController() {
		long currentTimestamp = convertTime(System.currentTimeMillis());
//		Log.i(LongLakeApplication.DANIEL_TAG,
//				String.format("time %d %d", currentTimestamp, (PERIODIC - currentTimestamp + time_reboot) % PERIODIC));
		UIThreadDispatcher.dispatch(new Runnable() {
			public void run() {
				LLSDKUtils.reboot();
			}
		}, ((PERIODIC - currentTimestamp + time_reboot) % PERIODIC) * 1000);
	}

}
