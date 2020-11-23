package com.xixi.sdk.serialpos;

import java.util.Calendar;
import java.util.Date;

import com.xixi.sdk.CustomizedCRCConverter;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.serialpos.LLCmdsSet.CmdNode;
import com.xixi.sdk.serialpos.LLCmdsSet.LL_IPreprocess;
import com.xixi.sdk.utils.file.IoCompletionListener1;

/**
 * Created by danielxie on 10/19/16.
 */

public class SerialDataParser implements ISerialDataEnum {

	public static class CmdRetKits {

		public static byte[] setButtonSwitchDelay(byte delay) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD, delay };
		}

		public static byte[] makeGeneralRetBytes(byte[] rawData, boolean bRet) {
			byte responseData[] = new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, rawData[CMD_KEY_OFFSET],
					(byte) (bRet ? 1 : 0) };
			return responseData;
		}

		public static byte[] makeSoundEnableCmd(boolean bOpenBit) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_SOUND_SWITCH_CMD_TO_CTRL_BOARD,
					(byte) (bOpenBit ? 1 : 0) };

		}

		public static byte[] makeCtrlScreenBklightCmdFirst(boolean bOpenBit) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, 0xA, (byte) (bOpenBit ? 1 : 0) };

		}

		public static byte[] makeCtrlScreenBklightCmd(boolean bOpenBit) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_LIGHT_SWITCH_CMD_TO_CTRL_BOARD,
					(byte) (bOpenBit ? 1 : 0) };

		}
        public static void enableWatchDogIo(boolean enableBit,IoCompletionListener1<byte[]> io) {
            SerialPosClient.instance().sendDataViaCom(new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0,
                    CMD_START_RESET_SWITCH_CMD_TO_CTRL_BOARD, (byte) (enableBit ? 1 : 0) },io);
            return;
        } 
		public static byte[] makeOpenDoorCmd(boolean bOpenBit) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_DOOR_CMD_TO_CTRL_BOARD, (byte) (bOpenBit ? 1 : 0) };
		}

		public static byte[] makeReadyStatusCmd() {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_DEV_READY_STATUS_CMD, 0x1 };
		}
        
		public static byte[] getElevatorStatus(){
			return new byte[]{FRAME_ALIGNMENT_HEAD_VALUE, 0 ,CMD_GET_ELEVATOR_ELEVATOR_STATUS};
		}
		public static byte[] configureRTCCmd() {
			// Called
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			int year = calendar.get(Calendar.YEAR);

			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_DEV_RTC_CONFIGURATION_CMD, (byte) (year >> 8),
					(byte) (year & 0xFF), (byte) (1 + calendar.get(Calendar.MONTH)),
					(byte) calendar.get(Calendar.DAY_OF_MONTH), (byte) calendar.get(Calendar.HOUR_OF_DAY),
					(byte) calendar.get(Calendar.MINUTE), (byte) calendar.get(Calendar.SECOND) };
		}

		public static byte[] makeOpenLedCmd(boolean bOpenBit) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_LED_SWITCH_CMD_TO_CTRL_BOARD,
					(byte) (bOpenBit ? 1 : 0) };
		}

		// 08

		public static byte[] setWatchDogTimer(byte resetTimeout, byte monitorPeriod, byte responseTimeout) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD, resetTimeout,
					monitorPeriod, responseTimeout };

		}

		public static void enableWatchDog(boolean enableBit) {
			SerialPosClient.instance().sendDataViaCom(new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0,
					CMD_START_RESET_SWITCH_CMD_TO_CTRL_BOARD, (byte) (enableBit ? 1 : 0) });
			return;
		} 
		
		
 		public static byte[] setFloorBoard(int num) {
			return new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_ELEVATOR_BOARD_NUM, (byte) num };
		}
 		
 		public static byte[] setFloorCount(int count){
 			return new byte[]{ FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_SET_ELEVATOR_FLOOR_COUNT, (byte) count};
 		}
 		
	}

	public static Object preprocess(byte[] rawData, final IoCompletionListener1<Byte> io) {

		assert (rawData.length >= FRAME_LENGTH_DEFAULT_VALUE);

		CmdNode node = LLCmdsSet.getInstance().searchByCmd(rawData[CMD_KEY_OFFSET]);
		LLSDKUtils.danielAssert(node != null);
		boolean bCrc = CustomizedCRCConverter.checkCRC(rawData);
		if (node.isCmdReq()) {
			byte responseData[] = CmdRetKits.makeGeneralRetBytes(rawData, bCrc);
			SerialPosClient.instance().sendDataViaCom(responseData);
		} else {
			if (io != null) {
				io.onFinish(rawData[CMD_KEY_OFFSET], CmdErrorCode.valueOf(bCrc , rawData[CMD_RET_OFFSET]));
			}
		}

		if (bCrc) {
			boolean bHandled = false;
			Object ob = null;
			try {
				LL_IPreprocess r = node.getR();
				bHandled = r.preprocess(rawData);
				ob = r.composeData(rawData);
				r.onMessage(ob);
			} catch (Exception e) {
				Log.e(e);
			}
			return bHandled ? null : ob;
		} else {
			Log.i(LongLakeApplication.DANIEL_TAG, "invalid cmd");
			return null;
		}
	}

	private final byte rawMessageBuffer[] = new byte[RAW_DATA_BUFFER_LENGTH];

	private int validDataLength = 0;

	ISerialInputData mSerialInputor;
	ISubmitResult mSubmitor;

	interface ISerialInputData {
		public byte[] getSerialData();
	}

	public interface ISubmitResult {
		public void submitData(byte msg[], int offset, int size);
	}

	public SerialDataParser(ISerialInputData inputData, ISubmitResult submitor) {
		mSerialInputor = inputData;
		mSubmitor = submitor;
	}

	private static void rawTest(String[] testingData) {
		SerialDataParser parser = new SerialDataParser(null, new ISubmitResult() {
			@Override
			public void submitData(byte[] msg, int offset, int size) {
				Log.i(LongLakeApplication.DANIEL_TAG, CustomizedCRCConverter.bytesToHexString(msg, size));
			}
		});

		for (int i = 0; i < testingData.length; i++) {
			byte bb[] = CustomizedCRCConverter.hexStringToByte(testingData[i]);
			parser.dumpCmd(bb, bb.length);

		}
	}

	// private static void rawTestBody(byte[][] testingData) {
	// SerialDataParser parser = new SerialDataParser(null, new ISubmitResult()
	// {
	// @Override
	// public void submitData(byte[] msg, int offset, int size) {
	// Log.i(MainApplication.DANIEL_TAG,
	// CustomizedCRCConverter.bytesToHexString(msg, size));
	// }
	// });
	//
	// for (int i = 0; i < testingData.length; i++) {
	// // Log.e("Daniel", "No " + i);
	// parser.dumpCmd(testingData[i], testingData[i].length);
	// }
	// }
	//
	// public static void test() {
	// test1();
	// while (true)
	// ;
	// }

	private void selfCopy(byte src[], int pos1, int pos2, int length) {
		System.arraycopy(src, pos2, src, pos1, length);
	}

	private static void test1() {
		// byte[][] testingData = { { 0x55 }, { 0x6 }, { 0x0, 0x30, 0x30, 0x30
		// }, { 0x55, 0x6, 0x0, 0x30, 0x30 }, { 0x30 },
		// { 0x11, 0x55, 0x6, 0x0, 0x30, 0x30, 0x30, 0x55, 0x6, 0x0, 0x30, 0x30,
		// 0x30 },
		// { 0x55, 0x6, 0x0, 0x30, 0x30, 0x30, 0x55 },
		// { 0x0a, 0x6, 0x0, 0x30, 0x30, 0x30, 0x55, 0x55, 0x55, 0x54, 0x54,
		// 0x55 },
		// { 0x6, 0x10, 0x0, 0x30, 0x30, 0x30, 0x55 }, };
		//
		// byte[][] testingData = { { 0x55 }, { 0x06, 0x00, 0x33, (byte) 0xb1,
		// (byte) 0xfc } };
		String testingData[] = { "552d07323031363a31323a30353a31323a31333a353520554152543220434f4e",
				"464947202d2d204f4b0d0a5e2e552b07323031363a31323a30353a31323a31333a353520555342",
				"20434f4e464947202d2d204f4b0d0ac30c552907323031363a31323a30353a31",
				"323a31333a3535204c454420494e4954202d2d204f4b0d0ab5685506002a7036",
				"552907323031363a31323a30353a31323a31333a3535205254432054494d4520",
				"53455445440d0a49e95506002a70365506002a703655060a01368955060c0135", };

		rawTest(testingData);
	}

	public void dumpCmd(byte[] rawData, int size) {

		if (validDataLength + size > rawMessageBuffer.length) {
			validDataLength = 0;
			return;
		}

		System.arraycopy(rawData, 0, rawMessageBuffer, validDataLength, size);
		validDataLength += size;

		// Log.d(LongLakeApplication.DANIEL_TAG , String.format("valid size %d"
		// , validDataLength));
		while (true) {

			int i = 0;
			for (i = 0; i < validDataLength; i++) {
				if (rawMessageBuffer[i] == FRAME_ALIGNMENT_HEAD_VALUE) {
					selfCopy(rawMessageBuffer, 0, i, validDataLength - i);
					break;
				}
			}

			validDataLength -= i;
			// Log.d(LongLakeApplication.DANIEL_TAG , String.format("CA %d" ,
			// validDataLength));

			if (validDataLength < 3)
				break;

			int lengthInProtocol = rawMessageBuffer[LENGTH_KEY_OFFSET];
			// insufficient data for consuming .
			int cmd = rawMessageBuffer[CMD_KEY_OFFSET];

			CmdNode node = LLCmdsSet.getInstance().searchByCmd(cmd);
			if (node != null) {

				int requestedLength = node.fixed_length() ? node.getLength() : lengthInProtocol; // rawMessageBuffer[LENGTH_KEY_OFFSET];
				// Log.d(LongLakeApplication.DANIEL_TAG,
				// String.format("NA %d %d %d", validDataLength, node.getCmd(),
				// requestedLength));
				if (requestedLength >= 5 && requestedLength < rawMessageBuffer.length) {
					if (validDataLength < requestedLength) {
						break;
					} else {  
						// Log.d(LongLakeApplication.DANIEL_TAG ,"SM");//,
						// node.getCmd() , requestedLength ));
						mSubmitor.submitData(rawMessageBuffer, 0, requestedLength);
						validDataLength -= requestedLength;
					}
					selfCopy(rawMessageBuffer, 0, requestedLength, validDataLength);
				} else {
					Log.d(LongLakeApplication.DANIEL_TAG, "INVALID");
					rawMessageBuffer[0] = 0;
				}
			} else {
				rawMessageBuffer[0] = 0;
				Log.d(LongLakeApplication.DANIEL_TAG, "INVALID cmd");
				// validDataLength = max(validDataLength - lengthInProtocol, 0);
				// selfCopy(rawMessageBuffer, 0, lengthInProtocol,
				// validDataLength);
			}

		}

	}
}
