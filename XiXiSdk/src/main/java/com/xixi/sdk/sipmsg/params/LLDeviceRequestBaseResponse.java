package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLDeviceRequestBaseResponse extends LLSipMsgBase implements IDeviceData {

	public LLDeviceRequestBaseResponse() {
		super(MESSAGE_DEVICE_CMD_TYPE);
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	private String op;
	private int errorCode;
	public static String MESSAGE_DEVICE_CMD_TYPE = "MESSAGE_DEVICE_CMD_TYPE_RESPONSE";
}
