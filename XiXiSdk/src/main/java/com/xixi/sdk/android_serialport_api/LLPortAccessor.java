package com.xixi.sdk.android_serialport_api;

import java.io.IOException;

public interface LLPortAccessor {

	public void open();

	public int read(byte[] buffer) throws IOException;

	public void write(byte[] buffer) throws IOException;

	public void closeDev();
}
