package com.xixi.sdk.device;

import com.xixi.sdk.sipmsg.params.LLSmartDev;
import com.xixi.sdk.sipmsg.params.LLSmartEnvir;
import com.xixi.sdk.utils.file.IoCompletionListener1;

public abstract class LLXiXiSmartBoxCenter implements IDeviceCtrls {
	
	
	protected LLXiXiSmartBoxCenter() { 
		
	} 
	
	@Override
	public void switchOn(boolean on ,  String devName ,IoCompletionListener1<IDeviceData> io) { 
		
	}

	@Override
	public void stepOn(float flStatus,String devName) { 
		
	}

	@Override
	public void onArrivalOfData(byte[] data) { 
	}


	@Override
	public boolean startupSmartBox(IoCompletionListener1<EnvironmentParams> io) { 
		return false;
	}

	@Override
	public void shutDownSmartBox() { 
		
	}

	@Override
	public LLSmartDev getSmartDevData(String devName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LLSmartEnvir getEnvironmentData() {
		// TODO Auto-generated method stub
		return null;
	}

}
