package com.xixi.sdk.model;

public class LLBeatData {
	private static final  float DEFAULT_THRESHOLD = 1.f ; 
	private static final int WND_SIZE = 10 ; 
	private static final int BTT_THRESHOLD = 3 ; 
	
	public LLBeatData() {
		this(DEFAULT_THRESHOLD , WND_SIZE ,BTT_THRESHOLD) ;
	}
	public LLBeatData(float beatThreshold , long beatingDuration ,int BeatTimeThresholdInMinute) {
		this.beatingDuration = beatingDuration ;
		this.beatThreshold = beatThreshold ;
		this.BeatTimeThresholdInMinute = BeatTimeThresholdInMinute ;
	}
	
	private float beatThreshold = DEFAULT_THRESHOLD ; 
	private long beatingDuration = WND_SIZE;
	private int BeatTimeThresholdInMinute = BTT_THRESHOLD;
	public int getBeatTimeThresholdInMinute() {
		return BeatTimeThresholdInMinute;
	}
	public void setBeatTimeThresholdInMinute(int beatTimeThresholdInMinute) {
		BeatTimeThresholdInMinute = beatTimeThresholdInMinute;
	}
	public float getBeatThreshold() {
		return beatThreshold;
	}

	public void setBeatThreshold(float beatThreshold) {
		this.beatThreshold = beatThreshold;
	}

	public long getBeatingDuration() {
		return beatingDuration;
	}

	public void setBeatingDuration(long beatingDuration) {
		this.beatingDuration = beatingDuration;
	}
}
