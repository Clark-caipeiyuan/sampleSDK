package com.xixi.sdk.model;

public class LLOpenDoorData {
	 private  String mac ;
	   private  String secret_key;
	   private  String mix_key ;
	   private  String pwd;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSecret_key() {
		return secret_key;
	}
	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}
	public String getMix_key() {
		return mix_key;
	}
	public void setMix_key(String mix_key) {
		this.mix_key = mix_key;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public   LLOpenDoorData(String mac, String  secret_key ,String mix_key ,String pwd){
		this.setMac(mac);
		this.setMix_key(mix_key);
		this.setSecret_key(secret_key);
		this.setPwd(pwd);
	}
}
