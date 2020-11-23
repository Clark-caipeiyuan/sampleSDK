package com.xixi.sdk.utils.download;

public class LLCursorWithMaximum extends LLCursor{
	
	int max  = 0;  
	public LLCursorWithMaximum(int max) {
		super(0) ; 
		this.max = max ; 
	}
	
	public boolean hitMax() { 
		return this.getCurrentPosition() >= max ; 
	}
}
