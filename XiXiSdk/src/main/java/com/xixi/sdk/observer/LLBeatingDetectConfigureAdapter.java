package com.xixi.sdk.observer;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.observer.KnockDetectObserver.KnowDetectConfigureAdapter;

import android.content.Context;

public class LLBeatingDetectConfigureAdapter implements KnowDetectConfigureAdapter {

	private static LLBeatingDetectConfigureAdapter instance;

	public void setBeatThreshold(float beatMaxNumber) {
		this.beatingThreshold = beatMaxNumber;
	}
	
	public void setBeatingDuration(long beatingDuration) {
		this.beatingDuration = beatingDuration;
	}

	public void setBeatingDurationAndBeatThreshould(float beatMaxNumber , long beatingDuration){
		this.beatingThreshold = beatMaxNumber;
		this.beatingDuration = beatingDuration;
	}
	
	public static synchronized LLBeatingDetectConfigureAdapter getInstance() {
		if (instance == null) {
			instance = new LLBeatingDetectConfigureAdapter();
		}
		return instance;
	}

	float beatingThreshold = 1.f;
	long beatingDuration = 5000;

	public Context getContext() {
		return LongLakeApplication.getInstance();
	}
 
	@Override
	public float getBeatingThreshold() {
		return beatingThreshold ;
	}

	@Override
	public long getBeatDuration() {
		return beatingDuration;
	}
}
