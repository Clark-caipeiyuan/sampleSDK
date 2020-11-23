package com.xixi.sdk.sipmsg.params;

import java.util.ArrayList;
import java.util.List;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.globals.ILiftCmdCacheOpCode;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.utils.file.IoCompletionListener1;

import android.text.TextUtils;
import android.util.Log;

public class LLLiftCmdBean implements Comparable<LLLiftCmdBean>, ILiftCmdCacheOpCode {

	private int logicalTargetFloor = -1;
	private int logicalLocationOfFloor = -1;

	private boolean serviced;

	public boolean isServiced() {
		return serviced;
	}

	public void setServiced(boolean serviced) {
		this.serviced = serviced;
	}

	public void reset() {

		logicalTargetFloor = -1;
		logicalLocationOfFloor = -1;
		serviced = false;
	}

	public int getLogicalLocationOfFloor() {
		return logicalLocationOfFloor;
	}

	private List<IoCompletionListener1<Boolean>> ios = new ArrayList<IoCompletionListener1<Boolean>>();;

	public LLLiftCmdBean(String targetFloor) {
		super();
		reset() ; 
		List<Integer> _l = new ArrayList<Integer>();
		try {
			LLSDKUtils.string2List(targetFloor, _l);
			this.logicalTargetFloor = _l.get(0);
		} catch (Exception e) {

		}
	}

	public LLLiftCmdBean(int floorBitmap) {
		super();
		logicalTargetFloor = floorBitmap;
	}

	public LLLiftCmdBean(String _logicalLocationOfFloor, String floorBitmap,
			List<IoCompletionListener1<Boolean>> ios) {
		this(_logicalLocationOfFloor , floorBitmap  , ios , false) ;
	}

	public LLLiftCmdBean(String _logicalLocationOfFloor, String floorBitmap,
			List<IoCompletionListener1<Boolean>> ios, boolean serviceTag) {
		super();
		this.logicalLocationOfFloor = LLSDKUtils.string2Floor(_logicalLocationOfFloor);
		this.logicalTargetFloor = LLSDKUtils.string2Floor(floorBitmap);
		this.serviced = serviceTag;
		this.ios.addAll(ios);
	}

	public int getTargetFloor() {
		return logicalTargetFloor;
	}

//	public int getDirection() {
//		return direction;
//	}

	public List<IoCompletionListener1<Boolean>> getOpIoList() {
		return new ArrayList<IoCompletionListener1<Boolean>>(ios);
	}


	public int hashCode() {
		return (getTargetFloor() << 16 ) | (isServiced() ? 0x8000: 0 ) | logicalLocationOfFloor ;
	}

	@Override
	public int compareTo(LLLiftCmdBean data) {
		return (int)(this.hashCode() - data.hashCode()) ;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == (LLLiftCmdBean) obj){
          return true;
		}
		if(!(obj instanceof LLLiftCmdBean)){
			return false;
		}
		return compareTo((LLLiftCmdBean)obj) == 0 ;
	}
}

