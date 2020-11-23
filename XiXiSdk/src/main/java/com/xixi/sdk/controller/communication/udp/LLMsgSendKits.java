package com.xixi.sdk.controller.communication.udp;

import java.util.Collection;

import com.xixi.sdk.controller.communication.udp.LLUdpController.IDataDrainer;

public class LLMsgSendKits {
     
	public static MsgSender makeInstance() {
		return new UdpMsgSender() ; 
	}
	 
	public interface MsgSender {

		public void registerAsObserver(IDataDrainer parser, boolean tag);
		public void sendToSpecificDevice(final String peerId, final String strContent);
		public void sendToMultiDevices(final Collection<String> devices, final String strContent);
	}
 	 
	public static class UdpMsgSender implements MsgSender{
		
		@Override
		public void registerAsObserver(IDataDrainer _session, boolean tag) {
			LLUdpDataHandler.getInstance().registerAsObserver(_session, tag);
		} 
		
		@Override
		public void sendToSpecificDevice(String peerId, String strContent) { 
			LLUdpController.getInstance().send(peerId,strContent);
		}

		@Override
		public void sendToMultiDevices(Collection<String> devices, String strContent) {
			LLUdpController.getInstance().send(null,strContent);
		}
	}  
}
