package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartDev implements IDeviceData{
	String deviceId;
	String state;
	String data;
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public LLSmartDev(String deviceId, String state, String data) {
		super();
		this.deviceId = deviceId;
		this.state = state;
		this.data = data;
	}
}
