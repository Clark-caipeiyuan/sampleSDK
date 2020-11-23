package com.xixi.sdk.sipmsg.params;

import android.text.TextUtils;

public class LLSipMsgBase implements LLSipCmds {
	private String sipMsgType;
	private String sessionId ; 
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	private String verifyMsg ; 
	public LLSipMsgBase(String sipMsgType) {
		this.sipMsgType = sipMsgType;
	}

	public String getSipMsgType() {
		return sipMsgType;
	}

	public LLSipMsgBase(String sipMsgType, String verifyMsg) {
	super();
	this.sipMsgType = sipMsgType;
	this.verifyMsg = verifyMsg;
}
	public void setSipMsgType(String sipMsgType) {
		this.sipMsgType = sipMsgType;
	}
	public String getVerifyMsg(){
		return verifyMsg ;
	}
	public boolean isTypeOf(String strType) {
		return TextUtils.equals(getSipMsgType(), strType);
	}
}
