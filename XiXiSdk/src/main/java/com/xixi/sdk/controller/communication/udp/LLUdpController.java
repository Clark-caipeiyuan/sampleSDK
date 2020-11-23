package com.xixi.sdk.controller.communication.udp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLNetworkListener;
import com.xixi.sdk.controller.LLNetworkReachable;
import com.xixi.sdk.logger.Log;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

public class LLUdpController implements LLNetworkListener {

	private static LLUdpController instance;

	public static class LLUdpMsgBody implements IMsgParser {

		public byte[] getBb() {
			return bb;
		}

		public LLUdpMsgBody(int length, byte[] data, String peerIp) {
			super();
			bb = new byte[length];
			System.arraycopy(data, 0, bb, 0, length);
			this.peerIp = peerIp;
		}

		byte[] bb;
		String peerIp;

		String mac;

		public void setMac(String mac) {
			this.mac = mac;
		}

		@Override
		public String getPeer() {
			return peerIp;
		}

		@Override
		public String getMsgAsText() {
			try {
				return new String(bb, 0, bb.length, "utf-8");
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}

		@Override
		public String getMac() {
			return mac;
		}

	}

	public static interface IDataDrainer {
		public void onReceive(LLUdpMsgBody msgBody);
	}

	final IUdpPortReceiver default_delegate = new IUdpPortReceiver() {

		@Override
		public void onReceive(String peerIp, byte[] data, int length) {
		}

	};

	IUdpPortReceiver receiverDelegate = default_delegate;

	public void setReceiverDelegate(IUdpPortReceiver receiverDelegate) {
		this.receiverDelegate = receiverDelegate == null ? default_delegate : receiverDelegate;
	}

	public static synchronized LLUdpController getInstance() {
		if (instance == null) {
			instance = new LLUdpController();
		}
		return instance;
	}

	final byte[] mReceiveBuffer = new byte[1024 * 2];
	private DatagramSocket mSenderUdpPort;
	private int port;
	InetAddress targetAddrInet;
	private boolean isElevatorCenter = false;

	public void startDeamonAsElevatorCenter() {
		this.isElevatorCenter = true;
		initAllAddr();
		initUdp(10851, broadcastAddr);
	}

	public void startDeamonAsElevatorController() {
		initAllAddr();
		initUdp(10851, null);
	}

	private void closeSocket() {
		if (mSenderUdpPort != null) {
			try {
				mSenderUdpPort.close();
			} catch (Exception e) {
			}
			mSenderUdpPort = null;
		}

	}

	public void initUdp(int port, String targetAddrAsString) {
		this.port = port;
		closeSocket();

		try {
			mSenderUdpPort = new DatagramSocket(port);
			if (targetAddrAsString != null) {
				targetAddrInet = InetAddress.getByName(targetAddrAsString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		startPollingOnUdpPort();
	}

	Handler mReceiveHandler;
	HandlerThread mReceiveThread;

	Handler mSendeHandler;
	HandlerThread mSendThread;

	private LLUdpController() {
		LLNetworkReachable.getInstance().registerAsObserver(this, true);
		mSendThread = new HandlerThread("udp send");
		mSendThread.start();
		mSendeHandler = new Handler(mSendThread.getLooper());

	}

	String localAddr = "";
	String broadcastAddr = "";

	void startPollingOnUdpPort() {
		if (mReceiveHandler == null) {
			mReceiveThread = new HandlerThread("udp receive");
			mReceiveThread.start();
			mReceiveHandler = new Handler(mReceiveThread.getLooper());
		}
		mReceiveHandler.post(new Runnable() {
			public void run() {
				if (mSenderUdpPort == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
					}
				} else {
					try {
						DatagramPacket p1 = new DatagramPacket(mReceiveBuffer, mReceiveBuffer.length);
						mSenderUdpPort.receive(p1);
						String remoteAddr = LLNetworkReachable.toIpString(p1.getAddress().getAddress());
						if (!TextUtils.equals(localAddr, remoteAddr) && p1.getLength() != 0) {
							receiverDelegate.onReceive(remoteAddr, p1.getData(), p1.getLength());
						}
					} catch (Exception e) {
						//Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
					}
				}
				mReceiveHandler.post(this);
			}
		});
	}

	public void send(final String _targetIp, final String data) {
		mSendeHandler.post(new Runnable() {

			@Override
			public void run() {
				if (mSenderUdpPort == null) {
					initUdp(port, null);
				}
				int msg_length = data.length();
				byte[] message = data.getBytes();

				InetAddress _targetAddrInet;
				try {
					if (_targetIp == null) {
						_targetAddrInet = targetAddrInet;
					} else {
						_targetAddrInet = InetAddress.getByName(_targetIp);
					}
					DatagramPacket p = new DatagramPacket(message, msg_length, _targetAddrInet, port);
					mSenderUdpPort.send(p);
				} catch (IOException e) {
					//Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
					closeSocket();
				}
			}
		});
	}

	private void initAllAddr() {
		List<String> l = LLNetworkReachable.getAddrsInfo();
		localAddr = l.get(0);
		broadcastAddr = l.get(1);
	}

	@Override
	public void onStatusOfNetwork(boolean isReachable, boolean isWifiOnly) {
		if(isReachable){
			if (isElevatorCenter) {
				startDeamonAsElevatorCenter();
			} else {
				startDeamonAsElevatorController();
			}
		}
	}
}
