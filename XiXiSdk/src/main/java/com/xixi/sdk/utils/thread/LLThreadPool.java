package com.xixi.sdk.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LLThreadPool {
	
	final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
	private static LLThreadPool instance ; 
	
	public static synchronized LLThreadPool getInstance() { 
		if ( instance == null ) { 
			instance = new LLThreadPool() ; 
		}
		return instance ; 
	}
	
	public void dispatchRunnable(Runnable r) { 
		fixedThreadPool.execute(r);
	}
	
}
