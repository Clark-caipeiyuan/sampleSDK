package com.xixi.sdk.device;

import java.util.ArrayList;

public class EnvironmentParams extends ArrayList<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer get(int index) {
		Integer it = null;
		try {
			it = super.get(index);
		} catch (Exception e) {
			return 0;
		}

		return it;
	}

	public float getPM() {
		return this.get(4);
	}

	public float getTemp() {
		return this.get(2);
	}

	public float getMoisture() {
		return this.get(3);
	}

	public float getVoc() {
		return this.get(5);
	}
}