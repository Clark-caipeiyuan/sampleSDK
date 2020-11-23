package com.xixi.sdk.utils.file;


public interface IoCompletionListener<T> extends IoCompletionListener1<T>{
	public void onError(String errReason, final Throwable exceptionInstance) ;
}
