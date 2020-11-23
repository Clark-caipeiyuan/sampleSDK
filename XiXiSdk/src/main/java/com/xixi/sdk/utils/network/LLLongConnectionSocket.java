package com.xixi.sdk.utils.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.device.LLViliamSmartBoxCenter;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.mem.LLBufferDescription;
import com.xixi.sdk.utils.mem.LLMemoryManager;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

public class LLLongConnectionSocket extends LLNotifier<LLSmartConnectionRequest> implements Runnable, IISocketUtils {

	Thread recvingThread;

	private static LLLongConnectionSocket instance = null;

	public static synchronized LLLongConnectionSocket getInstance() {
		if (instance == null) {
			instance = new LLLongConnectionSocket();
		}

		return instance;
	}

	public void pollingOnSocket(LLSmartBoxRequestDataEx onHandler) {

	}

	protected void printLog(String str) {
		Log.i(LongLakeApplication.DANIEL_TAG, str);
	}

	//
	// protected void onEmptyList() {
	// Log.i(LongLakeApplication.DANIEL_TAG, "empty list");
	// }

	protected LLLongConnectionSocket() {

		recvingThread = new Thread(this);

		sendingThread = new HandlerThread("sendingThread");
		sendingThread.start();
		sendingThreadHandler = new Handler(sendingThread.getLooper());

		parsingThread = new HandlerThread("parsingThread");
		parsingThread.start();
		parsingThreadHandler = new Handler(sendingThread.getLooper());

		recvingThread.start();

	}

	private final static int RAW_DATA_BUFFER_LENGTH = 128;

	final HandlerThread sendingThread;
	final HandlerThread parsingThread; // new HandlerThread("recv device
										// manager");
	final Handler parsingThreadHandler;
	final Handler sendingThreadHandler;
	// public void startTCPClient() throws IOException {
	//
	// _socketChannel = SocketChannel.open();
	// _socketChannel.configureBlocking(false);
	//
	// InetSocketAddress remote = new
	// InetSocketAddress(connectionParams.getIp(), connectionParams.getPort());
	// _socketChannel.connect(remote);
	// try {
	// while (!_socketChannel.finishConnect()) {
	// Thread.sleep(1000);
	// printLog("trying to connect");
	// }
	// } catch (Exception e) {
	// printLog("failed to connect");
	// return;
	// }
	//
	// _selector = Selector.open();
	// _socketChannel.register(_selector, SelectionKey.OP_READ |
	// SelectionKey.OP_WRITE);
	//
	// while (true) {
	//
	// if (0 == _selector.select()) {
	// _consumeData();
	// continue;
	// }
	// Set<SelectionKey> keys = _selector.selectedKeys();
	// Iterator<SelectionKey> it = keys.iterator();
	// while (it.hasNext()) {
	// SelectionKey key2 = (SelectionKey) it.next();
	// if (key2.isReadable()) {
	// printLog("client reading...");
	//// bufferRecevingBuffer.clear();
	// SocketChannel channel2 = (SocketChannel) key2.channel();
	// int len = channel2.read(bufferRecevingBuffer);
	// if (len != -1) {
	// bufferRecevingBuffer.flip();
	// printLog("client reading...flip");
	// final byte[] recvBuffer = new byte[RAW_DATA_BUFFER_LENGTH];
	// bufferRecevingBuffer.get(recvBuffer, 0, len);
	// this.notifyOb(new Object[] { new LLBufferDescription(recvBuffer, len) });
	// }
	// printLog("client reading... done");
	// } else if (key2.isWritable()) {
	// printLog("client writable...");
	// _consumeData();
	// }
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// it.remove();
	// }
	// }
	// }

	Socket _clientSock = null;

	private synchronized Socket getSocket() throws UnknownHostException, IOException ,ConnectException{
		if (_clientSock == null) {
			if (getIp() == null)
				throw new UnknownHostException("null ip");
			_clientSock = new Socket(this.getIp(), getPort());
			_clientSock.setKeepAlive(true);
			_inputStream = new DataInputStream(_clientSock.getInputStream());
			_dataOs = new DataOutputStream(_clientSock.getOutputStream());
		}
		return _clientSock;
	}

	private synchronized void destroySocket() {
		try {
			if (_clientSock != null) {
				_clientSock.close();
			}
		} catch (Exception e) {
		}

		_clientSock = null;
		try {
			if (_inputStream != null) {
				_inputStream.close();
			}
		} catch (Exception e) {
		}
		_inputStream = null;
		try {
			if (_dataOs != null) {
				_dataOs.close();
			}
		} catch (Exception e) {
		}
		_dataOs = null;
	}

	private void _tryToSleep() {
		_tryToSleep(1000);
	}

	private void _tryToSleep(int delayed) {
		try {
			Thread.sleep(delayed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	DataInputStream _inputStream = null;
	DataOutputStream _dataOs = null;

	final LLMemoryManager<byte[]> memManager = new LLMemoryManager<byte[]>() {

		@Override
		protected byte[] allocate() {
			return new byte[RAW_DATA_BUFFER_LENGTH];
		}
	};

	@Override
	public void run() {

		while (true) {
			try {
				getSocket();

				while (true) {
					rescan: do {
						final byte[] recvBuffer = (byte[]) memManager.popBuffer();
						int totalSize = 0;

						do {
							
							if (recvBuffer.length <= totalSize) {
								memManager.pushBuffer(recvBuffer);
								break rescan;
							}

							try {
								int readSize = _inputStream.read(recvBuffer, totalSize, recvBuffer.length - totalSize);
								if (readSize <= 0) {
//									android.util.Log.d(LongLakeApplication.DANIEL_TAG,"LGL___trtosleep");
									_tryToSleep(2000);
								} else {
//									android.util.Log.d(LongLakeApplication.DANIEL_TAG,"LGL___readSize:"+readSize);

									totalSize += readSize;
//									android.util.Log.d(LongLakeApplication.DANIEL_TAG,"LGL___recvBuffer:"+new String(recvBuffer,0,totalSize,"gb2312"));
//									android.util.Log.d(LongLakeApplication.DANIEL_TAG,"LGL___recvBuffer:"+(recvBuffer[totalSize - 1] == '\n'));
									if (recvBuffer[totalSize - 1] == '\n') { // '\n'
										break;
									}
								}
							} catch (Exception e) {
								memManager.pushBuffer(recvBuffer);
								throw e;
							}
						} while (true);

						LLSDKUtils.danielAssert(totalSize != 0);
						final int passedSize = totalSize;
						parsingThreadHandler.post(new Runnable() {
							public void run() {
								try {
								 	String str = new String(recvBuffer, 0, passedSize, "gb2312");
								 	Log.i(LongLakeApplication.DANIEL_TAG, "recv " + str);
									String strArray[] = str.split(",");
									if (strArray != null && strArray.length != 0) {
										for ( int i = 0 ; i < strArray.length ; i++ ) { 
											strArray[i] = strArray[i].trim() ; 
										}
										LLLongConnectionSocket.this.notifyOb(new Object[] {
												LLBufferDescription.makeValidBufferDescription(strArray) });
									}
								} catch (UnsupportedEncodingException e) {
									Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));// .printStackTrace();
								}

								memManager.pushBuffer(recvBuffer);

							}

						});
					} while (false);
				}
			} catch (Exception e) {
				Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
			}
			destroySocket();
			_tryToSleep();
		}

	}

	@Override
	public void sendData(final LLSmartConnectionRequest reqData,
			final IoCompletionListener1<LLSmartConnectionRequest> ioComp) {
			
		this.registerAsObserver(reqData, true);

		sendingThreadHandler.post(new Runnable() {
			public void run() {
				try {
					// Log.i(LongLakeApplication.DANIEL_TAG, "dispatch write
					// request");
					getSocket();
					byte[] toByte = combinPrefix(reqData.getPayload_str()).getBytes();
					_dataOs.write(toByte);
					if (reqData.isUrgentBit()) {
						_clientSock.sendUrgentData(reqData.getUrgentData());
					}

					if (reqData.getEnableTimeout() != 0) {
						UIThreadDispatcher.dispatch(reqData, reqData.getEnableTimeout());
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					;
				} catch (Exception e) {
					destroySocket();
					reqData.getIoCompletion().onFinish(reqData, LLBufferDescription.getFailureInstance());
					printLog(android.util.Log.getStackTraceString(e));
				}
			}
		});
	}

	@Override
	protected void invoke(LLSmartConnectionRequest t, Object[] o1) {
		Log.i(LongLakeApplication.DANIEL_TAG, "calling to " + t.toString());
		t.getIoCompletion().onFinish(t, o1[0]);
	}

	@Override
	public void unregister(final LLSmartConnectionRequest reqData) {
		// Log.i(LongLakeApplication.DANIEL_TAG, "try to unreg " +
		// reqData.toString());
		registerAsObserver(reqData, false);
	}

	@Override
	public int getPort() {
		return LLViliamSmartBoxCenter.getServicePort();
	}

	@Override
	public String getIp() {
		return LLViliamSmartBoxCenter.getInstance().getBoxIp();
	}

	@Override
	public String combinPrefix(String str) {
		String prefix = LLViliamSmartBoxCenter.getInstance().getHouseKeeperId(); 
		if (TextUtils.isEmpty(prefix)) { 
			return str ; 
		} 
		else { 
			return prefix + str ; 
		}
	}
}
