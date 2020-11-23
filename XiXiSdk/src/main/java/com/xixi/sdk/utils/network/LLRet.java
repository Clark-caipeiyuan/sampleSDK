package com.xixi.sdk.utils.network;
import android.text.TextUtils;

public class LLRet { 
	
	private final static String SUCCESS ="success" ; 
	private final static String FAILED ="fail" ;
	public final static String SPACE_STR ="" ;
	public String getRet() {
		return ret;
	} 
	
	public static String LLGetRet(String v) { 
		return TextUtils.isEmpty(v) ? SPACE_STR : v ;
	}
	public boolean isSuccess() {  
		return !TextUtils.equals(ret , FAILED); 
	} 

	String ret = FAILED ;  
	String errorCode = SPACE_STR;
	public String getErrorCode() {
		return errorCode;
	}
} 
