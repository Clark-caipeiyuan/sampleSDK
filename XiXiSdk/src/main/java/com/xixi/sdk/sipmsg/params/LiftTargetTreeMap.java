package com.xixi.sdk.sipmsg.params;

import java.util.TreeSet;
import java.util.TreeMap;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.utils.mem.LLMemoryManager;

public class LiftTargetTreeMap extends TreeMap<Integer, TreeSet<LLLiftCmdBean>> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2552672990938402415L;

	public void printArray(int _logicalFloor ) {
		TreeSet<LLLiftCmdBean> set = get(_logicalFloor);
		if (set != null) {
			for (LLLiftCmdBean bean : set) {
				android.util.Log.i(LongLakeApplication.DANIEL_TAG, String.format("%d %d %s", bean.getLogicalLocationOfFloor(), bean.getTargetFloor(), Boolean.valueOf(bean.isServiced()).toString()));
			}
		}
	}

	protected static LLMemoryManager<TreeSet<LLLiftCmdBean>> mm = new LLMemoryManager<TreeSet<LLLiftCmdBean>>() {

		@Override
		public TreeSet<LLLiftCmdBean> allocate() {
			return new TreeSet<LLLiftCmdBean>();
		}
	};

	public int getTotalNodes() {
		int count = 0 ;
		for ( Integer keyNode: this.keySet()) {
			count += this.get(keyNode).size() ;
		}
		return count ;
	}

	public int requstSize(int floor) {
		TreeSet<LLLiftCmdBean> value = get(floor);
		return value == null ? 0 : value.size();
	}

	public boolean checkIfExistanceOfTarget(final int targetFloorInLogical) {
		TreeSet<LLLiftCmdBean> values = get(targetFloorInLogical);
		return ( values != null && values.size() != 0) ;

	}

	public boolean addToTask(LLLiftCmdBean bean){
		int key = bean.getTargetFloor();
		TreeSet<LLLiftCmdBean> values = get(key);
		if ( values != null ) {
			return values.add(bean);
		}
		else {
			addFloor(bean);
			return true ;
		}
 	}

 	public Integer getMaxFloor() {
		try {
			return this.lastKey();
		} catch (Exception e) {
		}
		return null;
	}

	public Integer getMinFloor() {
		try {
			return this.firstKey();
		} catch (Exception e) {
		}
		return null;

	}

	public void addFloor(LLLiftCmdBean key) {
		TreeSet<LLLiftCmdBean> value = get(key.getTargetFloor());
		if (value == null) {
			value = mm.popBuffer();
			super.put(key.getTargetFloor(), value);
		}
		value.add(key);
	}

	public void overideKey(int logicalFloor, final TreeSet<LLLiftCmdBean> tempSet) {
		TreeSet<LLLiftCmdBean> value = get(logicalFloor);
		value.clear();
		value.addAll(tempSet);
	}

	//
	// public void putStartFloor(LLLiftCmdBean key){
	// TreeSet<LLLiftCmdBean> value = get(key.getLogicalLocationOfFloor());
	// if(value == null){
	// value = mm.popBuffer();
	// super.put(key.getLogicalLocationOfFloor(), value) ;
	// }
	// value.add(key);
	// }
	//
	@Override
	public TreeSet<LLLiftCmdBean> remove(Object key) {
		TreeSet<LLLiftCmdBean> value = null;
		try {
			// LLSDKUtils.danielAssert(key instanceof Integer);
			value = super.remove(key);
			value.clear();
			mm.pushBuffer(value);
		} catch (Exception e) {
		}

		return value;
	}

}
