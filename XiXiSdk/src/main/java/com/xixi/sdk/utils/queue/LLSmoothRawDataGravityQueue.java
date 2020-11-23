package com.xixi.sdk.utils.queue;

import java.util.LinkedList;

import com.xixi.sdk.observer.KnockDetectObserver.CurrentVal;
import com.xixi.sdk.utils.mem.LLMemoryManager;

public class LLSmoothRawDataGravityQueue extends LLRawDataGravityQueue {

	float sumXVal = 0;
	float sumYVal = 0;
	float sumZVal = 0;
	private float averXVal;
	private float averYVal;
	private float averZVal;

	final LLMemoryManager<CurrentVal> llm = new LLMemoryManager<CurrentVal>() {

		@Override
		protected CurrentVal allocate() {

			return new CurrentVal();
		}
	}; 
	
	private CurrentVal averageCurrent = new CurrentVal();
	public CurrentVal getAverage() {
		return averageCurrent;
	}

	public LLSmoothRawDataGravityQueue() {

	}

	public void pushData(float currentXVal, float currentYVal, float currentZVal, long currentTime) {
		CurrentVal currentVal = llm.popBuffer();
		currentVal.setCurrentVal(currentXVal, currentYVal, currentZVal, currentTime);
		offer(currentVal);
	}

	@Override
	public boolean preInsert(LinkedList<CurrentVal> q, CurrentVal cVal) {

		return true;
	}

	@Override
	public void postInsert(LinkedList<CurrentVal> q, CurrentVal cVal) {
		if (size() == 0) { 
			return;
		}
		if (size() > queueSize()) {
			CurrentVal removedata = poll();
			sumXVal -= removedata.getCurrentXVal();
			sumYVal -= removedata.getCurrentYVal();
			sumZVal -= removedata.getCurrentZVal();
			llm.pushBuffer(removedata);
			
		}
		int length = size(); 
		sumXVal += cVal.getCurrentXVal();
		sumYVal += cVal.getCurrentYVal();
		sumZVal += cVal.getCurrentZVal();

		averXVal = sumXVal / length;
		averYVal = sumYVal / length;
		averZVal = sumZVal / length;
		averageCurrent = cVal ;
		averageCurrent.setMod(Math.sqrt(averXVal * averXVal + averYVal * averYVal + averZVal * averZVal));
	}

	@Override
	public int queueSize() {
		return 5;
	}

}
