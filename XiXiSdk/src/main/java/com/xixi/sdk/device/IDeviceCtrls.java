package com.xixi.sdk.device;

import com.xixi.sdk.sipmsg.params.LLSmartAir;
import com.xixi.sdk.sipmsg.params.LLSmartCurtain;
import com.xixi.sdk.sipmsg.params.LLSmartDev;
import com.xixi.sdk.sipmsg.params.LLSmartDoor;
import com.xixi.sdk.sipmsg.params.LLSmartEnvir;
import com.xixi.sdk.utils.file.IoCompletionListener1;

public interface IDeviceCtrls {
	public void switchOn(boolean on, String devName, IoCompletionListener1<IDeviceData> io);

	public void enableWatchDog(boolean on, String watchDogName, IoCompletionListener1<IDeviceData> io);

	public void stepOn(float flStatus, String devName);

	public void onArrivalOfData(byte[] data);

	public boolean startupSmartBox(IoCompletionListener1<EnvironmentParams> io);

	public void shutDownSmartBox();

	public LLSmartDev getSmartDevData(String devName);

	public LLSmartEnvir getEnvironmentData();
	
	public LLSmartAir getAirConditionData();
	
	public LLSmartCurtain getCurtainData();
	
	public LLSmartDoor getDoorData();
	
	public void setWarningDelegate(final IoCompletionListener1<String> io); 
	
	public void registerEnvironmentParams(final IoCompletionListener1<LLSmartEnvir> io) ; 
	
	public void airConditionOn(int temp, int windLevl, int model, int sign, IoCompletionListener1<IDeviceData> io);
	
	public void curtainOn(int sign, IoCompletionListener1<IDeviceData> io);
	
	public void openDoor(String pwd, final IoCompletionListener1<IDeviceData> io);
	public void setSmartDevStatusCallback(int code, final IoCompletionListener1<IDeviceData> io);
	public void unsetSmartDevStatusCallback(int code, final IoCompletionListener1<IDeviceData> io); 
}