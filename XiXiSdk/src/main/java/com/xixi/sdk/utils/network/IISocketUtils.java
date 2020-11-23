package com.xixi.sdk.utils.network;

import com.xixi.sdk.utils.file.IoCompletionListener1;

public interface IISocketUtils {
	public void sendData(final LLSmartConnectionRequest reqData,
                         final IoCompletionListener1<LLSmartConnectionRequest> ioComp);
	
	public void unregister(final LLSmartConnectionRequest reqData) ;
	

	public abstract int getPort() ;
	public abstract String getIp() ; 
	
	public abstract String combinPrefix(String str) ; 
	
	
}
