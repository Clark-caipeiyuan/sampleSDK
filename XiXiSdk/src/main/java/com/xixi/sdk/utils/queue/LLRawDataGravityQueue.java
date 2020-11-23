package com.xixi.sdk.utils.queue;

import java.util.LinkedList;

import com.xixi.sdk.observer.KnockDetectObserver.CurrentVal;

public abstract class LLRawDataGravityQueue extends LinkedList<CurrentVal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 600590902980041901L;

	public abstract boolean preInsert(LinkedList<CurrentVal> q, CurrentVal cVal);

	public abstract void postInsert(LinkedList<CurrentVal> q, CurrentVal cVal);

	public abstract int queueSize();

	public boolean offer(CurrentVal cVal) {

		if (preInsert(this, cVal)) {
			super.offer(cVal);
			postInsert(this, cVal);
		}
		return true;
	}

}