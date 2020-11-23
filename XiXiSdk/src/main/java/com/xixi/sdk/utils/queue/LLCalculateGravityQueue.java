package com.xixi.sdk.utils.queue;

import java.util.LinkedList;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.observer.KnockDetectObserver.CurrentVal;
import com.xixi.sdk.utils.mem.LLMemoryManager;

public class LLCalculateGravityQueue extends LLRawDataGravityQueue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8113476147661274308L;
	private final static CurrentVal MIN = new CurrentVal(Float.MIN_VALUE);
	private final static CurrentVal MAX = new CurrentVal(Float.MAX_VALUE);

	CurrentVal queueMax = MIN;
	CurrentVal queueMin = MAX;

	final LLMemoryManager<CurrentVal> llm = new LLMemoryManager<CurrentVal>() {

		@Override
		protected CurrentVal allocate() {

			return new CurrentVal();
		}
	};

	public void calculateChangeRate(CurrentVal mod) {
		CurrentVal currentVal = llm.popBuffer();
		currentVal.setCurrentVal(mod.getCurrentXVal(), mod.getCurrentYVal(), mod.getCurrentZVal(), mod.getCurrentTime());
		currentVal.setMod(mod.getMod());
		offer(currentVal);
	}

	@Override
	public boolean preInsert(LinkedList<CurrentVal> q, CurrentVal cVal) {
		return true;
	}
	 
	
	int i = 1 ;
	@Override
	public void postInsert(LinkedList<CurrentVal> q, CurrentVal cVal) {
		int length = q.size();
		i++ ;
		if (length == 0) {
			return;
		}
		if (length > queueSize()) {
			CurrentVal removedata = q.poll();
			if (removedata == queueMax) { 
				queueMax = q.peek();
				for (CurrentVal currentValQ : q) {
					queueMax = queueMax.getMod() < currentValQ.getMod() ? currentValQ : queueMax;
				}
			}
			if (removedata == queueMin) {
				queueMin = q.peek();
				for (CurrentVal currentValQ : q) {
					queueMin = queueMin.getMod() > currentValQ.getMod() ? currentValQ : queueMin;
				}
			}
			llm.pushBuffer(removedata);
		}
	    queueMin = queueMin.getMod() > cVal.getMod() ? cVal : queueMin;
		queueMax = queueMax.getMod() < cVal.getMod() ? cVal : queueMax;
		changeRateTime = cVal.getCurrentTime();
//		changeRate = 0;
		changeRate = queueMax.getMod()-queueMin.getMod() ;
//		try {
//			Log.d(LongLakeApplication.DANIEL_TAG , "LGG:"+String.format("%f", queueMax.getMod()-queueMin.getMod()));
//			changeRate = queueMax.getCurrentTime() == queueMin.getCurrentTime() ? 0 : 1000 * (queueMax.getMod() - queueMin.getMod())
//					/ Math.abs(queueMax.getCurrentTime() - queueMin.getCurrentTime());
//			Log.d(LongLakeApplication.DANIEL_TAG , "LGL ___:queueMax:"+queueMax.getMod()+"_____queueMin"+queueMin.getMod()+"_____changeRate"+changeRate);
//		} catch (Exception e) {
//			changeRate = 0 ;
//		}
	}

	// private final static int queueMaxSize = 2 * (int) (1000 /
	// Frequent[FrequentType]);
	
	private final static int queueMaxSize = 32 ;
	private double changeRate;
	private long changeRateTime;

	public long getChangeRateTime() {
		return changeRateTime;
	}

	public double getChangeRate() {
		return changeRate;
	}

	@Override
	public int queueSize() {
		return queueMaxSize;
	}

}
