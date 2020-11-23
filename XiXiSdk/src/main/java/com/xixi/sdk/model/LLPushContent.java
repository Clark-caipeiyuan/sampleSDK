
package com.xixi.sdk.model;

public class LLPushContent {

	public String getService_title() {
		return service_title;
	}

	public LLPushContent() {
	}

	public LLPushContent(String service_title, String service_body_des, String service_url, String service_image,
			String service_time) {
		super();
		this.service_title = service_title;
		this.service_body_des = service_body_des;
		this.service_url = service_url;
		this.service_image = service_image;
		this.service_time = service_time;

	}

	public void setService_title(String service_title) {
		this.service_title = service_title;
	}

	public String getService_body_des() {
		return service_body_des;
	}

	public void setService_body_des(String service_body_des) {
		this.service_body_des = service_body_des;
	}

	public String getService_url() {
		return service_url;
	}

	public void setService_url(String service_url) {
		this.service_url = service_url;
	}

	public String getService_image() {
		return service_image;
	}

	public void setService_image(String service_image) {
		this.service_image = service_image;
	}

	public String getService_time() {
		return service_time;
	}

	public void setService_time(String service_time) {
		this.service_time = service_time;
	}

	private String service_title;
	private String service_body_des;
	private String service_url;
	private String service_image;
	private String service_time; 

}
