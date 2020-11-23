package com.xixi.sdk.model;

import java.io.Serializable;

public class LLSmartDevice extends LLLayoutXmlNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8601417256523565111L;
	public final static String CLASS_TAG_LIGHT = "light";
	public final static String CLASS_TAG_AIR_CON = "airCon";
	public final static String CLASS_TAG_CURTAIN = "curtain";
	public final static String CLASS_TAG_PROPERTY = "property";
	public final static String CLASS_TAG_COLLECTION = "light,airCon,curtain,property,LLSmartDevice";
	public final static String CLASS_TAG = "LLSmartDevice";
	
	// (new
	// StringBuilder().append(CLASS_TAG_LIGHT).append(CLASS_TAG_AIR_CON).append(CLASS_TAG_CURTAIN).append(CLASS_TAG_PROPERTY)).toString();

	private String status;

	public LLSmartDevice() { } 
	public LLSmartDevice(String status, String type) {
		super();
		this.status = status;
		this.type = type;
	}

	private String type;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static boolean isSameLight(LLSmartDevice light, LLSmartDevice light2) {
		boolean isSame = false;
		if (light.getId().equals(light2.getId())) {
			isSame = true;
		}
		return isSame;
	}

}
