package com.xixi.sdk.serialpos;

import java.util.Map;
import java.util.TreeMap;

import com.xixi.sdk.CustomizedCRCConverter;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.android_serialport_api.device.service.LongLakeDataPumpService;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.IoCompletionListener3;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.content.Context;

public class SerialPosClient implements ISerialDataEnum, IoCompletionListener1<SerialPosEvent> {
	private static SerialPosClient instance;

	private LongLakeDataPumpService mService = null;

	private SerialPosClient(LongLakeDataPumpService service) {
		Log.i(LongLakeApplication.DANIEL_TAG, "Get Serials Port Client!");
		mService = service;
		service.registerAsObserver(this, true);
		openSerialPort();
	}

	public static synchronized SerialPosClient getInstance(LongLakeDataPumpService service) {
		if (instance == null) {
			instance = new SerialPosClient(service);
		}
		return instance;
	}

	public static final SerialPosClient instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("Serial Port Client not instantiated yet");
	}

	private final Map<Byte, IoCompletionListener3<byte[]>> callbackMap = new TreeMap<Byte, IoCompletionListener3<byte[]>>();

	public void sendDataViaCom(final boolean enablefloor, final byte[] requestedData,
			final IoCompletionListener1<byte[]> io) {
		if (requestedData[2] == (byte) 20) {
			io.onFinish(requestedData, Boolean.valueOf(true));
		} else if (requestedData[2] == (byte) 21) {
			io.onFinish(requestedData, Boolean.valueOf(true));
		}
	}

	private int counter = 0;

	public boolean testSendDataViaCom(final byte[] requestedData, final IoCompletionListener1<byte[]> io) {
		counter++;
		if ( counter <= 1 ) {
		 	sendDataViaCom(requestedData, io);
		} else {
			io.onFinish(requestedData, Boolean.valueOf(false));
		}
		
		return true;
	}

	public boolean sendDataViaCom(final byte[] requestedData, final IoCompletionListener1<byte[]> io) {
		try {

			requestedData[LENGTH_KEY_OFFSET] = (byte) (requestedData.length + 2); // 2
																					// for
																					// crc
			int cmd = requestedData[CMD_KEY_OFFSET];
			int c = CustomizedCRCConverter.createCRC(requestedData);

			final byte[] openBJ = new byte[requestedData.length + 2];
			System.arraycopy(requestedData, 0, openBJ, 0, requestedData.length);

			openBJ[requestedData.length] = (byte) (c & 0xFF);
			openBJ[requestedData.length + 1] = (byte) (c >> 8);

			Log.d(LongLakeApplication.DANIEL_TAG, "SB" + CustomizedCRCConverter.bytesToHexString(openBJ));

			if (io != null) {
				final Byte bb = Byte.valueOf((byte) cmd);
				IoCompletionListener3<byte[]> io1 = null;
				synchronized (callbackMap) {
					io1 = callbackMap.remove(bb);
				}
				if (io1 != null) {
					LLSDKUtils.danielAssert(false);
					UIThreadDispatcher.removeCallbacks(io1);
					io1.run();
				}

				io1 = new IoCompletionListener3<byte[]>() {

					@Override
					public void run() {
						synchronized (callbackMap) {
							callbackMap.remove(bb);
						}
						onFinish(requestedData, CmdErrorCode.valueOf(CMD_EXECUTE_TIMEOUT));
					}

					@Override
					public void onFinish(byte[] data, Object context) {
						io.onFinish(data == null ? requestedData : data, context);
					}
				};
				synchronized (callbackMap) {
					callbackMap.put(bb, io1);
				}

				UIThreadDispatcher.dispatch(io1, 3000);
			}

			mService.writeDataViaSerialPort(openBJ);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean sendDataViaCom(byte[] requestedData) {
		return sendDataViaCom(requestedData, null);
	}

	public void connect(Context ctx, Class<? extends LongLakeDataPumpService> class1) {
		mOnEventListener.onConnected(this);
	}

	public void disconnect() {
		mOnEventListener.onDisconnected(this);
	}

	private void openSerialPort() {
		if (mService != null) {
			mService.openSerialPort(SERIAL_PORT_ADDR, SERIAL_PORT_BAUDRATE);
			Log.i(LongLakeApplication.DANIEL_TAG, "Serials Port opened!");
		}
	}

	public void closeSerialPort() {
		if (mService != null) {
			mService.closeSerialPort();
		}
	}

	protected OnEventListener mOnEventListener = null;

	public interface OnEventListener {
		public void onConnected(SerialPosClient c);

		public void onDisconnected(SerialPosClient c);
	}

	public void setOnEventListener(OnEventListener l) {
		mOnEventListener = l;
	}

	private final IoCompletionListener1<Byte> callback_io = new IoCompletionListener1<Byte>() {

		@Override
		public void onFinish(Byte data, Object context) {
			IoCompletionListener3<byte[]> io = null;
			synchronized (callbackMap) {
				io = callbackMap.remove(data);
			}
			if (io != null) {
				UIThreadDispatcher.removeCallbacks(io);
				io.onFinish(null, context);
			}
		}
	};

	@Override
	public void onFinish(SerialPosEvent event, Object context) {
		SerialDataParser.preprocess(event.data, callback_io);
	}
}
