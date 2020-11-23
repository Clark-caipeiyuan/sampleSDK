package com.xixi.sdk.sipmsg.params;

public class LLSipLiftManageResponse extends LLSipMsgBase{

	private String op;
	private String errorCode;
	private String message ; 
	
	public String getMessage() {
		return message;
	}

    public boolean isSuccess(){
        return String.valueOf(SUCCESS_TAG).equals(errorCode) ; 
    }

	public void setMessage(String message) {
		this.message = message;
	}

	public LLSipLiftManageResponse() {
		super(MESSAGE_LIFT_MANAGE_CMD_TYPE);
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
