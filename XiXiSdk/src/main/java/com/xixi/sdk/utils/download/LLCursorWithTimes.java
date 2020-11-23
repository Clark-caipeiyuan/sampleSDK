package com.xixi.sdk.utils.download;

public class LLCursorWithTimes extends LLCursor {
	int _times = 0;

	protected void onChangeOfPosition() { 
		reset() ; 
	}
	public int get_times() {
		return _times;
	}

	public void set_times(int _times) {
		this._times = _times;
	} 
	
	public void increTimesByOne() {
		this._times ++ ; 
	}

	protected void reset(){ 
		_times = 0 ; 
	}
}
