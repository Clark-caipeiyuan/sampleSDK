package com.xixi.sdk.sipmsg.params;

public class LLSipMsgDevicesCmds extends LLSipMsgBase implements LLSipCmds {

	public LLSipMsgDevicesCmds(String sipMsgType, String message,String id ,String op) {
		super(sipMsgType);
		this.message = message;
		this.id = id;
		this.op = op ;
	}
	private String message ;
	private String id ;
	private String op ;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public static String MESSAGE_DEVICE_CMD_TYPE = "MESSAGE_DEVICE_CMD_TYPE" ;
	
	/*public static int CMD_TurnOff_AirCondition = 0;
	public static int CMD_TurnOn_AirCondition = 1;
	public static int CMD_GET_AirConditionState = 2 ;
	public static int CMD_TempCtr_AirCondition = 3; */
}
