package com.xixi.sdk.sipmsg.params;

public class LLSipMsgLiftCmds extends LLSipMsgBase implements LLSipCmds {

	public LLSipMsgLiftCmds(String sipMsgType, String message,String op) {
		super(sipMsgType);
		this.message = message;
		this.op = op ;
	}
	private String message ;
	private String op ;
	
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public final static String MESSAGE_LIFT_CMD_TYPE = "MESSAGE_LIFT_CMD_TYPE" ;
}
