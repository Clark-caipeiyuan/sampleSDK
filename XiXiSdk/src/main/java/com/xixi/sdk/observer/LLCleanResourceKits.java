package com.xixi.sdk.observer;

import com.xixi.sdk.controller.LLNotifier; 

public class LLCleanResourceKits extends LLNotifier<IICleanable>{
  
	private static LLCleanResourceKits instance ; 
	public static synchronized LLCleanResourceKits getInstance() { 
		if ( instance == null ) { 
			instance = new LLCleanResourceKits() ; 
		}
		return instance ; 
	} 
	
	public static synchronized void rvInstance() { 
		instance = null ; 
	}
	 
	@Override
	protected void invoke(IICleanable t, Object[] o1) {
		 t.cleanCache(o1);
	}
	
}
