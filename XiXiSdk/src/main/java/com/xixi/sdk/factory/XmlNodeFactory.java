package com.xixi.sdk.factory;

import org.xmlpull.v1.XmlPullParser;

import com.xixi.sdk.model.LLXmlNode;

public interface XmlNodeFactory {
	public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser);
}