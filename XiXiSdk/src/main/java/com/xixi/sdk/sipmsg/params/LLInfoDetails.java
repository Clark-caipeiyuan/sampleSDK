package com.xixi.sdk.sipmsg.params;

public class LLInfoDetails {
	public LLInfoDetails(String ver, String content) {
		super();
		this.ver = ver;
		this.content = content;
	}
	private String ver ; 
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private String content ; 
}
