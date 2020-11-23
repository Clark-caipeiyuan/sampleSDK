package com.xixi.sdk.sipmsg.params;

public class LLDeviceSipRetMsg {

	int isSuccess = -1;
	int state = -1;
	private String data;

	public int getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
