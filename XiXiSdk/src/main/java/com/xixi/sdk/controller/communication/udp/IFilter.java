package com.xixi.sdk.controller.communication.udp;

public interface IFilter<T> { 
	public boolean accept(T param) ; 
}

