package com.xixi.sdk.model;

import java.util.ArrayList;
import java.util.List;

import com.xixi.sdk.parser.XmlParser;

public class LayoutCfg {
	private List<LLXmlNode> deviceTypes;

	public List<LLXmlNode> getDeviceTypes() {
		if (deviceTypes == null) {
			deviceTypes = new ArrayList<LLXmlNode>();
		}
		return deviceTypes;
	}

	public void setDeviceTypes(List<LLXmlNode> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}
    
	//public List<LLXmlNode> 
	//
	// return (List<LLXmlNode>)LLBuddy.getBuddies(xmlNodes);
	public static LayoutCfg parseXml(String result) {
		LayoutCfg layoutInfo = new LayoutCfg();
		LLXmlNode xmlNode = XmlParser.parseXml(result, LLBuddy.getLayoutMapInstanceCallback(), LLBuddy.getLayoutKeys());
		List<LLXmlNode> ll = LLBuddy.getBuddiesFromLayout(xmlNode);
	 	layoutInfo.setDeviceTypes(ll);
		return layoutInfo;
	}
}
