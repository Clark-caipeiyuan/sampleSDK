package com.xixi.sdk.controller.xcap;

import java.util.List;

import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.utils.file.IoCompletionListener;
import com.xixi.sdk.utils.network.LLCallback;

import okhttp3.ResponseBody;

public class LLXcapRequest { 

	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String userId,
			IoCompletionListener<List<LLXmlNode>> onDataReady , Object params ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.userId = userId;
		this.onDataReady = onDataReady;
		this.params = params ; 
	}
	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String userId,String buddyId,
			IoCompletionListener<String> onStringReady , Object params ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.userId = userId;
		this.onStringReady = onStringReady;
		this.params = params ; 
		this.buddyId = buddyId;
	} 
	
	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String userId,
			LLCallback<ResponseBody> onRetHandle , long timeStamp ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.userId = userId;
		this.onRetHandle = onRetHandle;
		this.timeStamp = timeStamp ; 
	}
	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String userId,String doorId,String unitkey,
			LLCallback<ResponseBody> onRetHandle , Object params ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.userId = userId;
		this.onRetHandle = onRetHandle;
		this.params = params ; 
		this.doorId = doorId;
		this.unitkey = unitkey;
	} 
	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String userId,
			LLCallback<ResponseBody> onRetHandle , String params ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.userId = userId;
		this.onRetHandle = onRetHandle;
		this.params = params ; 
	}
	public LLXcapRequest(XCAP_REQUEST_TYPE requestType, String sipAddr, String doorId_identify,String key_expire,
			LLCallback<ResponseBody> onRetHandle , Object params ) { 
		this.requestType = requestType;
		this.sipAddr = sipAddr;
		this.doorId_identify = doorId_identify;
		this.key_expire = key_expire;
		this.onRetHandle = onRetHandle;
		this.params = params ; 
	}
	public LLXcapRequest(XCAP_REQUEST_TYPE request_TYPE ,String sipAddr ,String deviceId ,LLCallback<ResponseBody> onRetHandle){
		this.sipAddr = sipAddr;
		this.requestType = request_TYPE;
		this.deviceId = deviceId;
		this.onRetHandle = onRetHandle;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	private String deviceId;
	private String doorId_identify;
	
	private String key_expire;
	
	private long timeStamp;
	
	public long getTimeStamp(){
		return timeStamp;
	}
	
	public void setTimeStamp(long timeStamp){
		this.timeStamp = timeStamp; 
	}
	
	public String getDoorId_identify() {
		return doorId_identify;
	}
	public void setDoorId_identify(String doorId_identify) {
		this.doorId_identify = doorId_identify;
	}
	public String getKey_expire() {
		return key_expire;
	}
	public void setKey_expire(String key_expire) {
		this.key_expire = key_expire;
	}
	public LLCallback<ResponseBody> getOnRetHandle() {
		return onRetHandle;
	}

	public XCAP_REQUEST_TYPE getRequestType() {
		return requestType;
	}
	public String getSipAddr() {
		return sipAddr;
	}
	public String getUserId() {
		return userId;
	}
	public IoCompletionListener<List<LLXmlNode>> getOnDataReadyHandle() {
		return onDataReady;
	}
	public void setRequestType(XCAP_REQUEST_TYPE requestType) {
		this.requestType = requestType;
	}
	public void setSipAddr(String sipAddr) {
		this.sipAddr = sipAddr;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setOnDataReady(IoCompletionListener<List<LLXmlNode>> onDataReady) {
		this.onDataReady = onDataReady;
	} 
	private String unitkey;
	private String doorId;
	
	public String getUnitkey() {
		return unitkey;
	}
	public void setUnitkey(String unitkey) {
		this.unitkey = unitkey;
	}
	public String getDoorId() {
		return doorId;
	}
	public void setDoorId(String doorId) {
		this.doorId = doorId;
	} 
	public String getBuddyId() {
		return buddyId;
	}
	public void setBuddyId(String buddyId) {
		this.buddyId = buddyId;
	}
	
    public IoCompletionListener<String> getOnStringReady() {
		return onStringReady;
	}
	public void setOnStringReady(IoCompletionListener<String> onStringReady) {
		this.onStringReady = onStringReady;
	}

	IoCompletionListener<String> onStringReady;
	XCAP_REQUEST_TYPE requestType ;
	String sipAddr ,  userId ;
	IoCompletionListener<List<LLXmlNode >> onDataReady ; 
	Object params ;
    String buddyId;
	LLCallback<ResponseBody>  onRetHandle ; 
	public Object getParams() {
		return params;
	}

	public void setPayload(Object params) {
		this.params = params;
	} 
} 