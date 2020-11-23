package com.xixi.sdk.utils.network;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xixi.sdk.utils.file.IoCompletionListener1;

public abstract class LLSmartConnectionRequest implements Runnable{


	private final static int ENDING_TAG_BIT = 0x100;

	private Object context;

	public abstract boolean isInterestedPck(final LLSmartConnectionRequest data , final Object bd)  ;
	private  int enableTimeout = 5000;

	public int getEnableTimeout() {
		return enableTimeout;
	}

	public void reset() {
		response_array.clear();
	}

	public void setResponse_data(byte[] buffer, int size) {
		try {
			this.response_data = new String(buffer, 0, size, "gb2312");
		} catch (UnsupportedEncodingException e) { 
			response_data = null;
			e.printStackTrace();
		}
	}

	String response_data;

	public String getResponse_data() {
		return response_data == null ? "" : response_data;
	}

	public byte[] getResponseBuffer() {
		return response_buffer;
	}

	final byte response_buffer[] = new byte[64];

	public void setEnableTimeout(int enableTimeout) {
		this.enableTimeout = enableTimeout;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	public LLSmartConnectionRequest enableEndingTagCheck(int endingTag) {
		endTag = ENDING_TAG_BIT | endingTag;
		return this;
	}

	int endTag = 0;

	public LLSmartConnectionRequest setEndTag(int endTag) {
		this.endTag = endTag;
		return this;
	}

	public boolean testingEnableEndingTag() {
		return (endTag & ENDING_TAG_BIT) != 0;
	}

	public int getEndingTag() {
		return endTag & (ENDING_TAG_BIT - 1);
	}

	final List<String> response_array = new ArrayList<String>();


	public String[] getResults() {
		return response_array.toArray(new String[0]);
	}
 
	public void setResponse_data(final List<String> l ) { 
		response_array.clear(); 
		if (l!=null) {  
			response_array.addAll(l);
		}
	}
	public void setResponse_data(final String array[]) {
		response_array.clear();
		if (array != null) {
			response_array.addAll(Arrays.asList(array));
		}
	}

	String payload_str;

	public String getPayload_str() {
		return payload_str;
	}

	public boolean isUrgentBit() {
		return urgentBit;
	}

	public byte getUrgentData() {
		return urgentData;
	}

	public LLSmartConnectionRequest(String data, boolean urgentBit, int urgentData,
			IoCompletionListener1<LLSmartConnectionRequest> ioCompletion) {
		super();
		this.payload_str = data;
		this.urgentBit = urgentBit;
		this.urgentData = (byte) urgentData;
		this.ioCompletion = ioCompletion;
	}

	boolean urgentBit;
	byte urgentData;

	IoCompletionListener1<LLSmartConnectionRequest> ioCompletion;

	public IoCompletionListener1<LLSmartConnectionRequest> getIoCompletion() {
		return ioCompletion;
	}

	@Override
	public void run() {

	}
}
