package com.xixi.sdk.utils.download;

public class LLCursor {

	protected void reset() {
	}

	public LLCursor() {
		this(0);
	}

	public LLCursor(int initialized) {
		position = initialized;
	}

	int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int moveToNext() {
		return _increase(1);
	}

	public int moveToPrevious() {
		return _increase(-1);
	}

	public int getCurrentPosition() {
		return _increase(0);
	}

	protected int _increase(int added) {

		position += added;
		if (added != 0) {
			reset();
		}
		return position;
	}

};
