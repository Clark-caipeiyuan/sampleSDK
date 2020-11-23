package com.xixi.sdk.android_serialport_api.device.service;

import java.io.IOException;

import com.xixi.sdk.CustomizedCRCConverter;
import com.xixi.sdk.android_serialport_api.LLPortAccessor;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.logger.LoggerHandlerThread;
import com.xixi.sdk.serialpos.ISerialDataEnum;
import com.xixi.sdk.serialpos.SerialDataParser;
import com.xixi.sdk.serialpos.SerialDataParser.ISubmitResult;
import com.xixi.sdk.serialpos.SerialPosEvent;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.mem.LLMemoryManager;

import android.os.Handler;

public abstract class LongLakeDataPumpService extends LLNotifier<IoCompletionListener1<SerialPosEvent>>
		implements ISerialDataEnum {

	private LLPortAccessor mSerialPort = null;
	/* Serial Pos Related - End */
	private final static int MAX_THREADS_LIMITS = 3;

	private final static int INDEX_READ = 0;
	private final static int INDEX_WRITE = 1;
	private final static int INDEX_DATA_HANDLING = 2;

	private final Handler handler[] = new Handler[MAX_THREADS_LIMITS];
	private final LoggerHandlerThread threads[] = new LoggerHandlerThread[MAX_THREADS_LIMITS];

	private SerialDataParser mParser = new SerialDataParser(null, new ISubmitResult() {

		@Override
		public void submitData(byte[] msg, int offset, int size) {
			sendData(msg, offset, size);
		}

	});

	//
	// private final List<byte[]> buffer_list = new ArrayList<byte[]>();

	final private LLMemoryManager<byte[]> memManager = new LLMemoryManager<byte[]>() {

		@Override
		public byte[] allocate() {

			return new byte[RAW_DATA_BUFFER_LENGTH];
		}

	};

	protected abstract LLPortAccessor createPortAccessor(Object[] params);

	public boolean openSerialPort(String path, int baudrate) {

		if (mSerialPort == null) {
			/* Open the serial port */
			try {
				mSerialPort = createPortAccessor(null);
				final String thread_tags[] = { "read data", "write data", "handling data" };
				for (int i = 0; i < MAX_THREADS_LIMITS; i++) {
					threads[i] = new LoggerHandlerThread(thread_tags[i]);
					threads[i].start();
					handler[i] = new Handler(threads[i].getLooper());
				}

				handler[INDEX_READ].post(new Runnable() {
					public void run() {
						try {
							final byte[] bb = memManager.popBuffer();
							final int size = mSerialPort.read(bb);
							if (size > 0) {
								handler[INDEX_DATA_HANDLING].post(new Runnable() {
									public void run() {
										Log.d(LLSdkGlobals.DANIEL_TAG,
												"RD" + CustomizedCRCConverter.bytesToHexString(bb, size));
										mParser.dumpCmd(bb, size);
										memManager.pushBuffer(bb);
									}
								});
							} else {
								memManager.pushBuffer(bb);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

						handler[INDEX_READ].post(this);
					}
				});
			} catch (Exception e) {
				Log.i(LLSdkGlobals.DANIEL_TAG, "Open serial port " + path + " failed");
			}
		}
		return mSerialPort != null;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			Log.i("ISerialPosService", "close serial port");
			mSerialPort.closeDev();

			for (int i = 0; i < MAX_THREADS_LIMITS; i++) {
				threads[i].quit();
			}

			mSerialPort = null;
		}
	}


	public void writeDataViaSerialPort(final byte[] raw_data ) {

		handler[INDEX_WRITE].post(new Runnable() {
			@Override
			public void run() {
				try {
					mSerialPort.write(raw_data);
					int cmd = raw_data[CMD_KEY_OFFSET];
					if (cmd == CMD_ELEVATOR_BOARD_NUM || cmd == CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD || cmd == CMD_START_RESET_SWITCH_CMD_TO_CTRL_BOARD) {
						Thread.sleep(500);
					} else if (cmd == CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD) {
						// Log.d(LLSdkGlobals.DANIEL_TAG
						// ,"CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD");
						Thread.sleep(1000);
					} else {
						Thread.sleep(20);
					}

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void invoke(IoCompletionListener1<SerialPosEvent> t, Object o1[]) {
		t.onFinish((SerialPosEvent) o1[0], null);
	}

	private void sendData(byte[] data, int offset, int length) {
		// Log.d(DANIEL_TAG ,"sendData" +
		// CustomizedCRCConverter.bytesToHexString(data , length));
		SerialPosEvent evt = new SerialPosEvent();
		evt.type = SerialPosEvent.EVENT_DATA;
		evt.data_length = length;
		if (length > 0) {
			evt.data = new byte[length];
			System.arraycopy(data, offset, evt.data, 0, length);
		}

		this.notifyOb(new SerialPosEvent[] { evt });
	}

}
