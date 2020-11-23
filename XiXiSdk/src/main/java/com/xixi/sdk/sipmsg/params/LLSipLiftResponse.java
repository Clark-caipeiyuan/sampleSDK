package com.xixi.sdk.sipmsg.params;

public class LLSipLiftResponse extends LLSipMsgBase {

	private String errorCode;
	private String op;
	private String message ;

	public LLSipLiftResponse() {
		super(MESSAGE_LIFT_CMD_TYPE_RESPONSE);
	}
    
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}


}
