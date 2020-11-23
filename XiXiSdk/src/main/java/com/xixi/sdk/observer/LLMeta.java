package com.xixi.sdk.observer;

import com.xixi.sdk.controller.LLNotifier;

public abstract class LLMeta extends LLNotifier<IIDataObservable>{ 
	
	public abstract Object getData();
	public abstract Object getDataWithCloneVersion(); 
	public abstract void setData(Object ob);
	public abstract String getStoredFile(); 
	
	protected void invoke(IIDataObservable observer , Object[] param ) { 
		observer.onChangeOfContent(param[0], param[1]);
	}
	
	public void loadLocally() {
		readFromDB();
//		this.notifyOb(new Object[] { getData() , MainApplication.getInstance()});
	} 
	protected abstract void readFromDB(); 
	protected abstract void backupToDB() ; 
}
