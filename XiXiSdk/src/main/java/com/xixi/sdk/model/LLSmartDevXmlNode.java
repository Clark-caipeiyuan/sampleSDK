package com.xixi.sdk.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.xixi.sdk.factory.LLInstanceCreatorMap;
import com.xixi.sdk.factory.XmlNodeFactory;

import android.R.string;

public class LLSmartDevXmlNode extends LLLayoutXmlNode implements Serializable {

	final static LLInstanceCreatorMap map_layout_instance_callback;

	public final static String BUDDY_NAME = "phoneNums";
	
	public final static List<LLXmlNode> getBuddiesFromInfo(LLXmlNode xmlNodes) {

		try {
			return xmlNodes.getChildren().get(BUDDY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<LLXmlNode>();
	}

	final static String Buddy_Keys[][] = new String[][] { { BUDDY_NAME }, { "phoneNum" }};

	public static String[][] getBuddyKeys() {
		return Buddy_Keys;
	}
	
	static {
		map_layout_instance_callback = new LLInstanceCreatorMap();
		map_layout_instance_callback.put(BUDDY_NAME, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {
				LLSmartDevXmlNode llSmartDevXmlNode = new LLSmartDevXmlNode();
				llSmartDevXmlNode.setName(parser.getAttributeValue("", "name"));
				llSmartDevXmlNode.setId(parser.getAttributeValue("", "id"));
				return llSmartDevXmlNode;
			}
		});
		
		map_layout_instance_callback.put("phoneNum", new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {
				LLSmartNode llSmartNode = new LLSmartNode();
				llSmartNode.setUserId(parser.getAttributeValue("", "user_id"));
				return llSmartNode;
			}
		});
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String name;
	private String id;
}
