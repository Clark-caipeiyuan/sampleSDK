package com.xixi.sdk.controller;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;

public class LLAppStatistics {

	public static final String VOLUMEKEY = "opendoor_volumekey";
	public static final String SWIPCARD = "opendoor_swipcard";
	public static final String SCAN = "opendoor_scan";
	public static final String PASSWORD = "opendoor_password";
	public static final String KEYCASE = "opendoor_keycase";
	public static final String COMMON = "opendoor_common";
	private static LLAppStatistics instance;

	public static LLAppStatistics getInstance() {
		if (instance == null) {
			return new LLAppStatistics();
		}
		return instance;
	}

	private LLAppStatistics() {
		MobclickAgent.setDebugMode(false);
		MobclickAgent.enableEncrypt(true);
	}
	
	
	public void statisticsOnResume(Context context){
		MobclickAgent.onResume(context);
	}
	
	public void statisticsOnPause(Context context){
		MobclickAgent.onPause(context);
	}
	
	public void statisticsPageStart(){
		   MobclickAgent.onPageStart(getClass().getName());
	}
	
	public void statisticsPageEnd(){
		 MobclickAgent.onPageEnd(getClass().getName());
	}
	
	public void customEvent(Context context ,String event_id){
		MobclickAgent.onEvent(context, event_id);  
	}
}
