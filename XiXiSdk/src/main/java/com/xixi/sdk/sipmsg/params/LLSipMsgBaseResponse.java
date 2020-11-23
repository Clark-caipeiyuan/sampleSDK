package com.xixi.sdk.sipmsg.params;

public class LLSipMsgBaseResponse extends LLSipMsgBase{

	public LLSipMsgBaseResponse(String sipMsgType) {
		super(sipMsgType); 
	}

	private String sessionId ;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
