package com.xixi.sdk.model;


import android.text.TextUtils;

public class LLUserInfo {

	@Override
	protected LLUserInfo clone() {

		LLUserInfo ud = new LLUserInfo(userName, userPwd, sipAddr);
		return ud;
	}

	public LLUserInfo(String userName, String userPwd, String sipAddr) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.sipAddr = sipAddr;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public String getSipAddr() {
		return sipAddr;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public void setSipAddr(String sipAddr) {
		this.sipAddr = sipAddr;
	}

	private String userName = "";
	private String userPwd = "";
	private String sipAddr = "";
	private String password_key = "" ;
	private String try_password_key ="";
	
	public String getTry_password_key() {
		return try_password_key;
	}

	void setTry_password_key(String try_password_key) {
		this.try_password_key = try_password_key;
	}

	public boolean isValidPwdKey() { 
		return !TextUtils.isEmpty(password_key);
	}
	
	public String getPassword_key() {
		return password_key;
	}

	public void setPassword_key(String password_key) {
		this.password_key = password_key;
	} 
}
