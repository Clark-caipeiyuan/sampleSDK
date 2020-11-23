package com.xixi.sdk.serialpos;

public interface ISerialDataEnum {

	public static final String SERIAL_PORT_ADDR = "/dev/ttyS1";
	public static final int SERIAL_PORT_BAUDRATE = 19200;

	// Data Index
	public static final byte FRAME_ALIGNMENT_HEAD_INDEX = 0;
	public final byte FRAME_LENGTH_INDEX = 1;
	public static final byte NET_ADDR_INDEX = 2;
	public static final byte DEST_LOGIC_ADDR_INDEX = 3;
	public static final byte DEST_CHANNEL_ADDR_INDEX = 4;
	public static final byte SRC_LOGIC_ADDR_INDEX = 5;
	public static final byte SRC_CHANNEL_ADDR_INDEX = 6;
	public static final byte REPSPONSE_TYPE_CODE_INDEX = 7;
	public static final byte DATA_TYPE_INDEX = 8;
	public static final byte DATA_INDEX = 3;
	public static final byte CHECK_CODE_INDEX = 10;

	public static final int RAW_DATA_BUFFER_LENGTH = 160;
	public static final int CMD_RET_OFFSET = 3;
	public static final int CMD_KEY_OFFSET = 2;
	public static final int LENGTH_KEY_OFFSET = 1;
	public static final int MAX_CMD_LENGTH = 9;
    public static final int ELEVATOR_FLOOR = 4;
	// Frame Alignment Head Value
	public static final byte FRAME_ALIGNMENT_HEAD_VALUE = 0x55;

	// Frame Length Default Value
	public static final byte FRAME_LENGTH_DEFAULT_VALUE = 0x05;

	// Data Type Values
	public static final byte DATA_TYPE_LOCK_ONOFF = 0x01;
	public static final byte DATA_TYPE_HEX = 0x08;
	public static final byte DATA_TYPE_ASCII = 0x0A;

	public final static int CMD_KEYBOARD_FROM_CTRLBOARD = 0x0;
	public final static int CMD_KEYBOARD_TO_CTRLBOARD = 0x0;

	public final static int CMD_DOOR_CMD_TO_CTRL_BOARD = 0x1;
	public final static int CMD_DOOR_CMD_FROM_CTRL_BOARD = 0x1;

	public final static int CMD_RFID_CMD_TO_CTRL_BOARD = 0x2;
	public final static int CMD_RFID_CMD_FROM_CTRL_BOARD = 0x2;

	public final static int CMD_BUTTON_SWITCH_CMD_TO_CTRL_BOARD = 0x3;
	public final static int CMD_BUTTON_SWITCH_CMD_FROM_CTRL_BOARD = 0x3;

	public final static int CMD_DOOR_STATUS_CMD_TO_CTRL_BOARD = 0x4;
	public final static int CMD_DOOR_STATUS_CMD_FROM_CTRL_BOARD = 0x4;

	public final static int CMD_PAD_SWITCH_CMD_TO_CTRL_BOARD = 0x5;
	public final static int CMD_PAD_SWITCH_CMD_FROM_CTRL_BOARD = 0x5;

	public final static int CMD_LED_SWITCH_CMD_TO_CTRL_BOARD = 0x6;
	public final static int CMD_LED_SWITCH_CMD_FROM_CTRL_BOARD = 0x6;

	public final static int CMD_LOG_CMD = 0x7;

	public final static int CMD_RESET_SWITCH_CMD_TO_CTRL_BOARD = 0x8;
	public final static int CMD_RESET_SWITCH_CMD_FROM_CTRL_BOARD = 0x8;

	public final static int CMD_START_RESET_SWITCH_CMD_TO_CTRL_BOARD = 0x9;
	public final static int CMD_START_RESET_SWITCH_CMD_FROM_CTRL_BOARD = 0x9;

	public final static int CMD_DEV_READY_STATUS_CMD = 0xA;
	
	public final static int CMD_LIGHT_SWITCH_CMD_TO_CTRL_BOARD = 0xB ;
	public final static int CMD_LIGHT_SWITCH_CMD_FROM_CTRL_BOARD = 0xB ;
	
	public final static int CMD_DEV_RTC_CONFIGURATION_CMD = 0xC;
	
	public final static int CMD_SOUND_SWITCH_CMD_TO_CTRL_BOARD = 0xF ;
	public final static int CMD_SOUND_SWITCH_CMD_FROM_CTRL_BOARD = 0xF ;

    public final static int CMD_ELEVATOR_ALLOW_FLOOR = 0x14 ;
	
	public final static int CMD_ELEVATOR_DIRECT_FLOOR = 0x15 ; 
	public final static int CMD_ELEVATOR_BOARD_NUM = 0x16 ; 
	
	public final static int CMD_GET_ELEVATOR_ELEVATOR_STATUS = 0X18;
	public final static int CMD_SET_ELEVATOR_FLOOR_COUNT = 0X19;
	public final static int CMD_GET_ELEVATOR_DIRECTION = 0X1A;
	public final static int CMD_GET_ELEVATOR_FLOOR = 0X1B;
	
	public final static int CMD_NFC_CARD_MSG = 0x7F;
	// public final static int CMD_NFC_CARD_MSG = 0x ;
	

	public final static int CMD_CRC_ERROR = 0 ; 
	public final static int CMD_NO_ERROR = 1 ; 
	public final static int CMD_EXECUTE_ERROR = 2 ;  
	public final static int CMD_EXECUTE_TIMEOUT = 3 ; 

}
