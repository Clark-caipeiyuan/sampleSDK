
package com.xixi.sdk.device;

import com.xixi.sdk.sipmsg.params.LLSipMsgDevicesCmds;
import com.xixi.sdk.sipmsg.params.LLSmartCurtain;
import com.xixi.sdk.sipmsg.params.LLSmartDoor;
import com.xixi.sdk.utils.file.IoCompletionListener1;

public abstract class LLSmartBoxCenter implements IDeviceCtrls,IDevCode {

	protected LLSmartBoxCenter() {}
	protected abstract void initAllConnectedDevices();

	final protected EnvironmentParams environment_params = new EnvironmentParams();
	// protected int temperature ;
	// protected int moisiture ;
	// protected int voc ;
	final protected LLSmartCurtain llSmartCurtain = new LLSmartCurtain();
	
    final protected LLSmartDoor llSmartDoor = new LLSmartDoor(); 
	
	protected abstract boolean getDogStatus();
	protected abstract void setDogStatus(final boolean dogStatus);
	
	public abstract String[] parsePrimaryInfo(String info);

	public abstract boolean onHandleMsg(final LLSipMsgDevicesCmds strMsg, final IoCompletionListener1<IDeviceData> io , Object context);

	public abstract static class LLXiXiCurtain {
		private int currentStatus;

		public abstract void changeToDegree(float degree);
	}

	boolean isLeftHome = false ;

	public boolean isLeftHome() {
		return isLeftHome;
	}
	public void setLeftHome(boolean isLeftHome , final IoCompletionListener1<IDeviceData> io) {
		this.isLeftHome = isLeftHome;
	} 
}
