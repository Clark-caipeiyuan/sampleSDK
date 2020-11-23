package com.xixi.sdk.parser;

import com.xixi.sdk.model.LayoutCfg; 

public class XmlParserAdapter implements LLParserAdapter {
	private static XmlParserAdapter instance ; 
	protected XmlParserAdapter(){} 
	public static synchronized XmlParserAdapter getInstance() { 
		if ( instance == null ) { 
			instance = new XmlParserAdapter() ; 
		}
		return instance ;
	}
	
	@Override
	public LayoutCfg parseData(Object ob) {
		return LayoutCfg.parseXml((String)ob) ; 
	}
}