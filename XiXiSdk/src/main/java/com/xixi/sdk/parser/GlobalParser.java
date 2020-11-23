package com.xixi.sdk.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class GlobalParser {
	
	static XmlPullParser xmlParser ;
	
	public static synchronized XmlPullParser getXmlParser() { 
//		if ( xmlParser == null ) 
		{ 
			xmlParser = Xml.newPullParser();
		}
		return xmlParser ; 
	}
	
	static XmlSerializer xmlSerializer ;  
	
	public static synchronized XmlSerializer getXmlSerialier() { 
		if ( xmlSerializer == null ) { 
			xmlSerializer = Xml.newSerializer();
		}
		return xmlSerializer ; 
	}
	
}
