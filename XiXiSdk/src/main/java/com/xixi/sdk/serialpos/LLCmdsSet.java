package com.xixi.sdk.serialpos;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.mem.LLMemoryManager;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

public class LLCmdsSet implements ISerialDataEnum {

	public static void test() {
		/*
		 * LLCmdsSet.searchByCmd(CMD_KEYBOARD_FROM_CTRLBOARD);
		 * LLCmdsSet.searchByCmd(CMD_DOOR_CMD_TO_CTRL_BOARD);
		 * LLCmdsSet.searchByCmd(CMD_PAD_SWITCH_CMD_FROM_CTRL_BOARD);
		 * LLCmdsSet.searchByCmd(CMD_START_RESET_SWITCH_CMD_FROM_CTRL_BOARD);
		 */}

	private static LLCmdsSet instance;

	public LLCmdsSet() {
	}

	public synchronized static LLCmdsSet getInstance() {
		if (instance == null) {
			instance = new LLCmdsSet();
		}
		return instance;
	}

	public interface LLProcessKeyData {
		public void processKeyData(byte key, boolean b);
	}

	public interface LLProcessDoorData {
		public void processDooEvent(boolean b);
	}

	public interface LLProcessCardData {
		public void icCardEvent(String cardId);
	}

	private static LLProcessKeyData defalut_keyData = new LLProcessKeyData() {

		@Override
		public void processKeyData(byte key, boolean b) {
		}
	};
	
	private static LLProcessDoorData defalut_doorData = new LLProcessDoorData() {
		@Override
		public void processDooEvent(boolean b) {
		}
	};

	private static LLProcessCardData defalut_cardData = new LLProcessCardData() {
		@Override
		public void icCardEvent(String cardId) {
		}
	};

	private static LLProcessDoorData doorData = defalut_doorData ; 
	private static LLProcessCardData cardData = defalut_cardData ; 
	private static LLProcessKeyData keyData = defalut_keyData;
	
	public static void setProcessCardData(LLProcessCardData cardData1) {
		cardData = cardData1 == null ? defalut_cardData : cardData1;
	} 
	
	public static void setProcessDoorData(LLProcessDoorData doorData1) {
		doorData = doorData1 == null ? defalut_doorData : doorData1;
	}
	
	public static void setProcessKeyData(LLProcessKeyData keyData1) {
		keyData = keyData1 == null ? defalut_keyData : keyData1;
	}
	public interface LL_IPreprocess {
		Object composeData(byte data[]);

		boolean preprocess(byte data[]);

		void onMessage(Object data);
	}

	private final static LL_IPreprocess const_ret_ls = new LL_IPreprocess() {

		@Override
		public Object composeData(byte[] data) {
			return String.valueOf(data[3]);
		}

		@Override
		public boolean preprocess(byte[] data) {
			return true;
		}

		@Override
		public void onMessage(Object data) {

		}

	};

	public interface LLProcessElevatorData {
		public void processElevatorDirection(int direction);

		public void processElevatorFloor(int floor);
	}

	private static LLProcessElevatorData default_elevator = new LLProcessElevatorData() {

		@Override
		public void processElevatorDirection(int direction) {

		}

		@Override
		public void processElevatorFloor(int floor) {

		}
	};

	static LLProcessElevatorData elevatorDelegate = default_elevator;

	public static void setProcessElevatorFloor(LLProcessElevatorData data) {
		elevatorDelegate = data == null ? default_elevator : data;
	}

	private final static LL_IPreprocess elevatorDirectionHandler = new LL_IPreprocess() {

		@Override
		public boolean preprocess(byte[] data) {

//			elevatorDelegate.processElevatorDirection(data[3]);
			return true;
		}

		@Override
		public void onMessage(Object data) {

		}

		@Override
		public Object composeData(byte[] data) {

			return null;
		}
	};
	private final static LL_IPreprocess elevatorStatus = new LL_IPreprocess() {

		@Override
		public boolean preprocess(byte[] data) {
			elevatorDelegate.processElevatorFloor(data[4]);
			return true;
		}

		@Override
		public void onMessage(Object data) {

		}

		@Override
		public Object composeData(byte[] data) {

			return null;
		}
	};

	private final static LL_IPreprocess elevatorFloorHandler = new LL_IPreprocess() {

		@Override
		public boolean preprocess(byte[] data) {

			elevatorDelegate.processElevatorFloor(data[3]);
			return true;
		}

		@Override
		public void onMessage(Object data) {

		}

		@Override
		public Object composeData(byte[] data) {

			return null;
		}
	};

	// final static String CONST_PERMIITED_IDs =
	// "10A58000354D820D,B47D1812,D55EA7C5,001F77C0 , 04424992A13280 ,
	// 973CAF3E";
	private final static LL_IPreprocess cardIdHandler = new LL_IPreprocess() {
		@Override
		public Object composeData(byte rawData[]) {
			return null;
		}

		@Override
		public boolean preprocess(byte[] rawData) {

			StringBuilder sb = new StringBuilder();
			String tempString = "";
			try {
				for (int i = 0; i < rawData[1] - 5; i++) {
					int cc = rawData[i + 3];
					cc = cc < 0 ? cc + 256 : cc;
					sb.append(String.format("%02X", cc));
				}
				tempString = sb.toString();
			} catch (Exception e) {
			}

			final String upperCase = tempString; // rcvSerialPortData.toUpperCase(Locale.CHINA);

			Log.i(LongLakeApplication.DANIEL_TAG, "cardIdHandler " + upperCase.toString());
			/*
			 * LLDBManager.getInstance().verifyIcCardsAvailable(upperCase, new
			 * LLCardValidEvent() {
			 * 
			 * @Override public void onValid(boolean validTag) { //TODO
			 * //CmdRetKits.openDoor(validTag); } });
			 */
			cardData.icCardEvent(upperCase);
			return true;
		}

		@Override
		public void onMessage(Object data) {
		}
	};

	final LLMemoryManager<CmdNode> mm = new LLMemoryManager<CmdNode>() {
		protected CmdNode allocate() {
			return new CmdNode("key", 0x6, CMD_KEYBOARD_FROM_CTRLBOARD, const_ret_ls);
		}

	};
	final CmdNode cnComparator = new CmdNode("key", 0x6, CMD_KEYBOARD_FROM_CTRLBOARD, const_ret_ls);

	final Comparator<CmdNode> comparator = new Comparator<CmdNode>() {

		@Override
		public int compare(CmdNode arg0, CmdNode arg1) {
			return (arg0.getCmd()) - (arg1.getCmd());
		}
	};

	public static class CmdNode {

		public CmdNode(String prefix, int length, int cmd, LL_IPreprocess r) {
			super();

			this.length = length;
			this.cmd = cmd;
			this.r = r;
			this.prefix = prefix;
		}

		int length;

		public int getLength() {
			return length & 0xFF;
		}

		public boolean fixed_length() {
			return (length & FLEXIBLE_LENGTH_BIT) == 0;
		}

		public boolean isCmdReq() {
			return (cmd & REQ_BIT) != 0;
		}

		public int getCmd() {
			return cmd & 0xffff;
		}

		public void setCmd(int cmd) {
			this.cmd = cmd;
		}

		int cmd;
		String prefix;

		public String getPrefix() {
			return prefix;
		}

		public void SetPrefix(String prefix) {
			this.prefix = prefix;
		}

		LL_IPreprocess r;

		public synchronized LL_IPreprocess getR() {
			return r;
		}

		public synchronized void setR(LL_IPreprocess r) {
			this.r = r;
		}

	}

	private final static String PREFIX_RET = "ret";
	// static final byte[] temp_card_buffer = new byte[4];

	private final static int REQ_BIT = 0x10000;
	private final static int FLEXIBLE_LENGTH_BIT = 0x100;

	/**
	 * 
	 */
	public CmdNode searchByCmd(int cmdCode) {

		/*
		 * final CmdNode cnComparator = mm.popBuffer() ;
		 * cnComparator.setCmd(cmdCode); int iIndex =
		 * Arrays.binarySearch(VALID_CMD_PAIRS, cnComparator, comparator);
		 * mm.pushBuffer(cnComparator);
		 * 
		 * if (iIndex < 0) { Log.i(LongLakeApplication.DANIEL_TAG,
		 * "invalid code " + cmdCode); return null; } else { return
		 * VALID_CMD_PAIRS[iIndex]; }
		 */
		return cmdNodeMap.get(cmdCode);
	}

	private final static Map<Integer, CmdNode> cmdNodeMap = new HashMap<Integer, CmdNode>();
	static {
		cmdNodeMap.put(CMD_KEYBOARD_FROM_CTRLBOARD,
				new CmdNode("key", 0x6, CMD_KEYBOARD_FROM_CTRLBOARD | REQ_BIT, new LL_IPreprocess() {

					@Override
					public Object composeData(byte rawData[]) {
						return Byte.valueOf(rawData[DATA_INDEX]);
					}

					@Override
					public boolean preprocess(byte[] data) {
						return false;
					}

					@Override
					public void onMessage(Object data) {
						byte key = (Byte) data;
						if (0 <= Arrays.binarySearch(const_key_model, key)) {
							keyData.processKeyData(key, false);
						}
					}
				}));
		cmdNodeMap.put(CMD_DOOR_CMD_FROM_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_DOOR_CMD_FROM_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_RFID_CMD_TO_CTRL_BOARD,
				new CmdNode("ic", MAX_CMD_LENGTH, CMD_RFID_CMD_TO_CTRL_BOARD | REQ_BIT, cardIdHandler));
		cmdNodeMap.put(CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_DOOR_STATUS_CMD_TO_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_DOOR_STATUS_CMD_TO_CTRL_BOARD | REQ_BIT, new LL_IPreprocess() {

					@Override
					public Object composeData(byte[] data) {
						return null;
					}

					@Override
					public boolean preprocess(byte[] data) {
						final byte bit = data[DATA_INDEX];
						UIThreadDispatcher.dispatch(new Runnable() {
							public void run() {
								doorData.processDooEvent(bit != 0x1);
								// LLDoorObservable.getInstance().setDoorStatus(bit
								// != 0x1);
							}
						});
						return true;
					}

					@Override
					public void onMessage(Object data) {
					}

				}));
		cmdNodeMap.put(CMD_PAD_SWITCH_CMD_TO_CTRL_BOARD,
				new CmdNode("pad_status", 0x5, CMD_PAD_SWITCH_CMD_TO_CTRL_BOARD | REQ_BIT, new LL_IPreprocess() {
					@Override
					public Object composeData(byte rawData[]) {
						return null;
					}

					@Override
					public boolean preprocess(byte[] data) {

						return true;
					}

					@Override
					public void onMessage(Object data) {

					}

				}));
		cmdNodeMap.put(CMD_LED_SWITCH_CMD_FROM_CTRL_BOARD,
				new CmdNode("led", 0x6, CMD_LED_SWITCH_CMD_FROM_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_LOG_CMD,
				new CmdNode(PREFIX_RET, FLEXIBLE_LENGTH_BIT, CMD_LOG_CMD | REQ_BIT, new LL_IPreprocess() {

					@Override
					public Object composeData(byte[] data) {
						return null;
					}

					@Override
					public boolean preprocess(byte[] data) {
						// StringBuilder sb = new StringBuilder();
						// sb.append("FW=>");
						// try {
						// for (int index = DATA_INDEX; index <
						// data[LENGTH_KEY_OFFSET] - 2; index++) {
						// sb.append(String.format("%c", data[index]));
						// }
						// Log.d(LongLakeApplication.DANIEL_TAG, sb.toString());
						// } catch(Exception e) {
						// }
						return true;
					}

					@Override
					public void onMessage(Object data) {

					}

				}));
		cmdNodeMap.put(CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_START_RESET_SWITCH_CMD_FROM_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_START_RESET_SWITCH_CMD_FROM_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_DEV_READY_STATUS_CMD, new CmdNode(PREFIX_RET, 0x6, CMD_DEV_READY_STATUS_CMD, const_ret_ls));
		cmdNodeMap.put(CMD_LIGHT_SWITCH_CMD_TO_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_LIGHT_SWITCH_CMD_TO_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_DEV_RTC_CONFIGURATION_CMD,
				new CmdNode(PREFIX_RET, 0x6, CMD_DEV_RTC_CONFIGURATION_CMD, const_ret_ls));
		cmdNodeMap.put(CMD_SOUND_SWITCH_CMD_FROM_CTRL_BOARD,
				new CmdNode(PREFIX_RET, 0x6, CMD_SOUND_SWITCH_CMD_FROM_CTRL_BOARD, const_ret_ls));
		cmdNodeMap.put(CMD_ELEVATOR_ALLOW_FLOOR, new CmdNode(PREFIX_RET, 0x6, CMD_ELEVATOR_ALLOW_FLOOR, const_ret_ls));
		cmdNodeMap.put(CMD_ELEVATOR_DIRECT_FLOOR,
				new CmdNode(PREFIX_RET, 0x6, CMD_ELEVATOR_DIRECT_FLOOR, const_ret_ls));
		cmdNodeMap.put(CMD_ELEVATOR_BOARD_NUM, new CmdNode(PREFIX_RET, 0x6, CMD_ELEVATOR_BOARD_NUM, const_ret_ls));
		cmdNodeMap.put(CMD_GET_ELEVATOR_ELEVATOR_STATUS,
				new CmdNode(PREFIX_RET, 0x7, CMD_GET_ELEVATOR_ELEVATOR_STATUS, elevatorStatus));
		cmdNodeMap.put(CMD_SET_ELEVATOR_FLOOR_COUNT,
				new CmdNode(PREFIX_RET, 0x6, CMD_SET_ELEVATOR_FLOOR_COUNT, const_ret_ls));
		cmdNodeMap.put(CMD_GET_ELEVATOR_DIRECTION,
				new CmdNode(PREFIX_RET, 0x6, CMD_GET_ELEVATOR_DIRECTION | REQ_BIT, elevatorDirectionHandler));
		cmdNodeMap.put(CMD_GET_ELEVATOR_FLOOR,
				new CmdNode(PREFIX_RET, 0x6, CMD_GET_ELEVATOR_FLOOR | REQ_BIT, elevatorFloorHandler));
		cmdNodeMap.put(CMD_NFC_CARD_MSG, new CmdNode("NFC reader", 0, CMD_NFC_CARD_MSG, cardIdHandler));

	}
	private final static byte const_key_model[] = { '#', '*', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private final CmdNode VALID_CMD_PAIRS[] = { /*
												 * new CmdNode("key", 0x6,
												 * CMD_KEYBOARD_FROM_CTRLBOARD |
												 * REQ_BIT, new LL_IPreprocess()
												 * {
												 * 
												 * @Override public Object
												 * composeData(byte rawData[]) {
												 * return Byte.valueOf(rawData[
												 * DATA_INDEX]); }
												 * 
												 * @Override public boolean
												 * preprocess(byte[] data) {
												 * return false; }
												 * 
												 * @Override public void
												 * onMessage(Object data) { byte
												 * key = (Byte) data; if (0 <=
												 * Arrays.binarySearch(
												 * const_key_model, key)) {
												 * keyData.processKeyData(key,
												 * false); } } }),
												 * 
												 * new CmdNode(PREFIX_RET, 0x6,
												 * CMD_DOOR_CMD_FROM_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode("ic", MAX_CMD_LENGTH,
												 * CMD_RFID_CMD_TO_CTRL_BOARD |
												 * REQ_BIT, cardIdHandler),
												 * 
												 * new CmdNode(PREFIX_RET, 0x6,
												 * CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_DOOR_STATUS_CMD_TO_CTRL_BOARD
												 * | REQ_BIT, new
												 * LL_IPreprocess() {
												 * 
												 * @Override public Object
												 * composeData(byte[] data) {
												 * return null; }
												 * 
												 * @Override public boolean
												 * preprocess(byte[] data) {
												 * final byte bit =
												 * data[DATA_INDEX];
												 * UIThreadDispatcher.dispatch(
												 * new Runnable() { public void
												 * run() {
												 * doorData.processDooEvent(bit
												 * != 0x1); //
												 * LLDoorObservable.getInstance(
												 * ).setDoorStatus(bit // !=
												 * 0x1); } }); return true; }
												 * 
												 * @Override public void
												 * onMessage(Object data) { }
												 * 
												 * }), new CmdNode("pad_status",
												 * 0x5,
												 * CMD_PAD_SWITCH_CMD_TO_CTRL_BOARD
												 * | REQ_BIT, new
												 * LL_IPreprocess() {
												 * 
												 * @Override public Object
												 * composeData(byte rawData[]) {
												 * return null; }
												 * 
												 * @Override public boolean
												 * preprocess(byte[] data) {
												 * 
												 * return true; }
												 * 
												 * @Override public void
												 * onMessage(Object data) {
												 * 
												 * }
												 * 
												 * }), new CmdNode("led", 0x6,
												 * CMD_LED_SWITCH_CMD_FROM_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET,
												 * FLEXIBLE_LENGTH_BIT,
												 * CMD_LOG_CMD | REQ_BIT, new
												 * LL_IPreprocess() {
												 * 
												 * @Override public Object
												 * composeData(byte[] data) {
												 * return null; }
												 * 
												 * @Override public boolean
												 * preprocess(byte[] data) { //
												 * StringBuilder sb = new
												 * StringBuilder(); //
												 * sb.append("FW=>"); // try {
												 * // for (int index =
												 * DATA_INDEX; index < //
												 * data[LENGTH_KEY_OFFSET] - 2;
												 * index++) { //
												 * sb.append(String.format("%c",
												 * data[index])); // } //
												 * Log.d(LongLakeApplication.
												 * DANIEL_TAG, sb.toString());
												 * // } catch(Exception e) { //
												 * } return true; }
												 * 
												 * @Override public void
												 * onMessage(Object data) {
												 * 
												 * }
												 * 
												 * }), new CmdNode(PREFIX_RET,
												 * 0x6,
												 * CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_START_RESET_SWITCH_CMD_FROM_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_DEV_READY_STATUS_CMD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_LIGHT_SWITCH_CMD_TO_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_DEV_RTC_CONFIGURATION_CMD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_SOUND_SWITCH_CMD_FROM_CTRL_BOARD,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_ELEVATOR_ALLOW_FLOOR,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_ELEVATOR_DIRECT_FLOOR,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_ELEVATOR_BOARD_NUM,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x7,
												 * CMD_GET_ELEVATOR_ELEVATOR_STATUS,
												 * elevatorStatus), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_SET_ELEVATOR_FLOOR_COUNT,
												 * const_ret_ls), new
												 * CmdNode(PREFIX_RET, 0x6,
												 * CMD_GET_ELEVATOR_DIRECTION |
												 * REQ_BIT,
												 * elevatorDirectionHandler),
												 * new CmdNode(PREFIX_RET, 0x6,
												 * CMD_GET_ELEVATOR_FLOOR |
												 * REQ_BIT,
												 * elevatorFloorHandler), new
												 * CmdNode("NFC reader", 0,
												 * CMD_NFC_CARD_MSG,
												 * cardIdHandler), // new
												 * CmdNode("NFC reader", 14,
												 * CMD_NFC_CARD_MSG,
												 * cardIdHandler),
												 */ };

	public CmdNode[] getVALID_CMD_PAIRS() {
		return VALID_CMD_PAIRS;
	}

}
