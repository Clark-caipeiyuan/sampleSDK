package com.xixi.sdk.globals;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.LLLogWrapper;

import android.content.Context;

public class LLSdkGlobals  { 
		
    private static int _logTag = LLLogWrapper.L_TO_ALL ; 

    public static int getLogTag() {
		return _logTag;
	}

	public static void setLogTag(int logTag) {
		_logTag = logTag;
	}

	public static String getSipBaseUrl() {
		return _sip_base_url;
	}

	public static String getUPLOAD_URL() {
		return _upload_url;
	}

	public static boolean isEnableLocalExceptionHandler() {
		return _enableLocalExceptionHandler;
	}

	public static String getUserName() {
		return _userId;
	}

	public static void setSipBaseUrl(String sip_base_url) {
		_sip_base_url = sip_base_url;
	}

	public static void setUPLOAD_URL(String upload_url) {
		_upload_url = upload_url;
	}

	public static void setEnableLocalExceptionHandler(boolean enableLocalExceptionHandler) {
		LLSdkGlobals._enableLocalExceptionHandler = enableLocalExceptionHandler;
	}

	public static void setUserName(String userName) {
		LLSdkGlobals._userId = userName;
	}

	
	
	private static boolean alwaysEnable1stFloor = true ; 

	public static boolean isAlwaysEnable1stFloor() {
		return alwaysEnable1stFloor;
	}

	public static void setAlwaysEnable1stFloor(boolean alwaysEnable1stFloor) {
		LLSdkGlobals.alwaysEnable1stFloor = alwaysEnable1stFloor;
	}



	private static String  MessagePushUrl ;
	
	private static String _sip_base_url;  
	
	private static String _upload_url ; 
	
	private static boolean _enableLocalExceptionHandler = false ;   
	
	private static String _userId ; 
	
	private static String macAddress ;
	
	public static void setMessagePushUrl(String url){
		MessagePushUrl = url;
	}
	public static String getMessagePushUrl(){
		if(MessagePushUrl==null){
			MessagePushUrl = new String();
		}
		return MessagePushUrl;
	}
	
	public static String getMacAddress(){
		return macAddress ;
	}
	
	public static void setMacAddress(String macaddress){
		macAddress = macaddress ;
	}
	
	public static Context getContext() {
		return LongLakeApplication.getInstance(); 
	}

	public final static String DANIEL_TAG ="daniel::";
	
	
	private static int elevator_allowexpire_duration = 30000 ;

	public static int getElevator_allowexpire_duration() {
		return elevator_allowexpire_duration;
	}

	public static void setElevator_allowexpire_duration(int elevator_expire_duration) {
		LLSdkGlobals.elevator_allowexpire_duration = elevator_expire_duration;
	}  
	
	private static int elevator_directexpire_duration = 2000 ;

	public static int getElevator_directexpire_duration() {
		return elevator_directexpire_duration;
	}

	public static void setElevator_directexpire_duration(int elevator_directexpire_duration) {
		LLSdkGlobals.elevator_directexpire_duration = elevator_directexpire_duration;
	}
}
