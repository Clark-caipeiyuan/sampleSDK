package com.xixi.sdk.model;

import com.google.gson.JsonObject;

public class LLSafeMsg {
	public String getUsr() {
		return usr;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public  LLSafeMsg(String usr, String msgid, String msg, String sign) {
		super();
		this.usr = usr;
		this.msgid = msgid;
		this.msg = msg;
		this.sign = sign;
	}

	
	private String usr;
	private String msgid;
	private String msg;
	private String sign;
}
