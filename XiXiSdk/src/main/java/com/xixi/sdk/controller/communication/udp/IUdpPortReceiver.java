package com.xixi.sdk.controller.communication.udp;

public interface IUdpPortReceiver {
	public void onReceive(String peerIp, byte[] data, int length);
}
