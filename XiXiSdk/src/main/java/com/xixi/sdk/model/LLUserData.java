package com.xixi.sdk.model;
import android.text.TextUtils;
public class LLUserData {

	@Override
	protected LLUserData clone() {

		LLUserData ud = new LLUserData(userName, userPwd, sipAddr, type);
		return ud;
	}
    
	public static String INITIALIZATION_STATE = "INITIALIZATION_STATE";

	public static final String VI = "VI";
	public static final String EL = "EL";
	public static final String EM= "EM";
	
	public LLUserData(String userName, String userPwd, String sipAddr, String type) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.sipAddr = sipAddr;
		this.type = type;
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
	private static boolean _19_inchDevice = false ;

	public static boolean is_19_inchDevice() {
		return _19_inchDevice;
	}

	public static void set_19_inchDevice(boolean _19_inchDevice) {
		LLUserData._19_inchDevice = _19_inchDevice;
	}

	private String userName = "";
	private String userPwd = "";
	private String sipAddr = "";
	private String password_key = "" ;
	private String type = INITIALIZATION_STATE ;

	
	public boolean isElevatorDevice() { 
		return EL.equals(type) ;
	}
	
	public boolean isElevatorSchedulerDevice(){
		return EM.equals(type);
	}
	public boolean isUnknownDevice() { 
		return !isElevatorDevice() && !isDoorMonitor() ; 
	}
	
	public boolean isDoorMonitor() { 
		return VI.equals(type);
	}
	
	public void setType(String type) {
		this.type = type;
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
