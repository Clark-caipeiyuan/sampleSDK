package com.xixi.sdk.utils.mem;

import java.util.ArrayList;
import java.util.List;

public abstract class LLMemoryManager<T> {

	final List<T> buffer_list = new ArrayList<T>();

	public void pushBuffer(T buffer) {
		synchronized (buffer_list) {
			buffer_list.add(buffer);
		}
	}

	protected abstract T allocate();

	public T popBuffer() {

		T bb = null;
		do {
			synchronized (buffer_list) {
				if (0 < buffer_list.size()) {
					bb = buffer_list.remove(0);
				}
			}
		} while (false);

		if (bb == null) {
			bb = allocate();
		}

		return bb;
	}
}
