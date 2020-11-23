package com.xixi.sdk.utils.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.device.LLViliamSmartBoxCenter;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.thread.LLThreadPool;

public abstract class LLSocketClient implements IISocketUtils , LLSipCmds{

	private static LLSocketClient instance;
 
	public void sendData(final LLSmartConnectionRequest reqData,
			final IoCompletionListener1<LLSmartConnectionRequest> ioComp) {
		LLThreadPool.getInstance().dispatchRunnable(new Runnable() {
			public void run() {
				int totalLength = 0;
				Socket sock = null;
				try {
					if( getIp() == null ) return ; 
					
					sock = new Socket(getIp(), getPort());
					sock.setSoTimeout(10000);
					DataOutputStream os = new DataOutputStream(sock.getOutputStream());
					byte[] toByte = reqData.getPayload_str().getBytes();
					os.write(toByte);
					if (reqData.isUrgentBit()) {
						sock.sendUrgentData(reqData.getUrgentData());
					}
					DataInputStream is = new DataInputStream(sock.getInputStream());

					byte buffer[] = reqData.getResponseBuffer();
					do {
						int readSize = is.read(buffer, totalLength, buffer.length - totalLength);
						if (readSize == -1)
							break;
						totalLength += readSize;
						if (totalLength >= buffer.length)
							break;
						if (!reqData.testingEnableEndingTag())
							break;
						if (buffer[totalLength - 1] == reqData.getEndingTag())
							break;
					} while (true);
					if (totalLength != 0) {
						reqData.setResponse_data(buffer, totalLength);
					}
				} catch (Exception e) {
					Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
				}

				@SuppressWarnings("unchecked")
				IoCompletionListener1<LLSmartConnectionRequest> _tempIo = (IoCompletionListener1<LLSmartConnectionRequest>) (ioComp == null
						? reqData.getIoCompletion() : ioComp);
				if (_tempIo != null) {
					if (totalLength == 0) {
						if (sock != null)
							try {
								sock.close();

							} catch (Exception e) {
							}
						_tempIo.onFinish(reqData, Integer.valueOf(INVALID_TAG));
					} else {
						_tempIo.onFinish(reqData, Integer.valueOf(totalLength));

					}
				}
			}
		});
	}

	public static synchronized LLSocketClient getInstance() {
		if (instance == null) {
			instance = new LLSocketClient(){

				@Override
				public int getPort() {
					return  LLViliamSmartBoxCenter.TCP_PORT;
				}
				
				@Override
				public String getIp() { 
					return LLViliamSmartBoxCenter.getInstance().getBoxIp();
				}

				@Override
				public String combinPrefix(String str) { 
					return null;
				} 
				
			} ; 
		}
		return instance;
	}

	public static synchronized void rvInstance() {
		instance = null;
	}

	@Override
	public void unregister(LLSmartConnectionRequest reqData) {

	}

}
