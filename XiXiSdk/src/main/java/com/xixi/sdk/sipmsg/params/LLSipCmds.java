package com.xixi.sdk.sipmsg.params;

public interface LLSipCmds {

	public static final String CMD_TURN_OFF_LIGHT = "CMD_TURN_OFF_LIGHT";
	public static final  String CMD_TURN_ON_LIGHT = "CMD_TURN_ON_LIGHT";
	public static final String CMD_GET_LIGHT_STATE = "CMD_GET_LIGHT_STATE" ;
	
	public static final String CMD_GET_AIR_AND_TEMP_STATE = "CMD_GET_AIR_AND_TEMP_STATE";
	
	public static final String CMD_DEL_DOOR_SAFE = "CMD_DEL_DOOR_SAFE";
	public static final String CMD_ADD_DOOR_SAFE = "CMD_ADD_DOOR_SAFE";
	public static final String CMD_CLOSE_DOOR = "CMD_CLOSE_DOOR";
	public static final String CMD_OPEN_DOOR = "CMD_OPEN_DOOR";
	public static final String CMD_GET_DOOR_STATE = "CMD_GET_DOOR_STATE" ;
	
	public static final String MESSAGE_DEVICE_ALERT = "MESSAGE_DEVICE_ALERT";
	
	public static final String CMD_TURN_OFF_CURTAIN = "CMD_TURN_OFF_CURTAIN";
	public static final String CMD_TURN_ON_CURTAIN = "CMD_TURN_ON_CURTAIN";
	
	public static final String CMD_CONTROL_CURTAIN = "CMD_CURTAIN_CONTROL";
	public static final String CMD_GET_CURTAIN_STATE = "CMD_CURTAIN_STATE";
	
//	public static String CMD_GET_CURTAIN_STATE = "CMD_GET_CURTAIN_STATE" ;
	public static final String CMD_CHANGE_CURTAIN = "CMD_CHANGE_CURTAIN";
	public static final String MESSAGE_LIFT_CMD_TYPE_RESPONSE = "MESSAGE_LIFT_CMD_TYPE_RESPONSE";
	public static final String CMD_TURN_OFF_AIRCONDITON = "CMD_TURN_OFF_AIRCONDITON";
	public static final String CMD_TURN_ON_AIRCONDITON = "CMD_TURN_ON_AIRCONDITON";
	public static final String CMD_TEMP_CTR_AIRCONDITION = "CMD_TEMP_CTR_AIRCONDITION"; 
	
	public static final String CMD_ADD_DEVICE = "CMD_ADD_DEVICE" ;
	public static final String CMD_ADD_ROOM = "CMD_ADD_ROOM" ;
	public static final String CMD_ADD_DEVICE_DOOR = "CMD_ADD_DEVICE_DOOR" ;
	
	public static final String CMD_AIRCONDITION_CONTROL = "CMD_AIRCONDITION_CONTROL" ;
	public static final String CMD_GET_AIRCONDITION_STATE = "CMD_GET_AIRCONDITION_STATE" ;
	
	public static final String CMD_CURTAIN_CONTROL = "CMD_CURTAIN_CONTROL" ;
	public static final String CMD_CURTAIN_STATE = "CMD_CURTAIN_STATE";
	public static final String CMD_DOOR_CONTROL = "CMD_DOOR_CONTROL";
	public static final String CMD_DOOR_SETPWD = "CMD_DOOR_SETPWD";
	public static final String CMD_SAFE_ALARM = "CMD_SAFE_ALARM";
	public static final String CMD_DOOR_OPEN = "CMD_DOOR_OPEN";
	public static final String CMD_OPTIONAL_FLOOR = "CMD_OPTIONAL_FLOOR";
	public static final String CMD_CURRENT_FLOOR = "CMD_CURRENT_FLOOR";
	public static final String CMD_THROUGH_FLOOR = "CMD_THROUGH_FLOOR";
	public static final String CMD_BEAT_WARNING = "CMD_BEAT_WARNING";
	public static final String CMD_LOG_IN = "CMD_LOG_IN";
	public static final String CMD_FLOOR_STATE = "CMD_FLOOR_STATE";
	public static final String CMD_ELEVATOR_DISTANCE = "CMD_ELEVATOR_DISTANCE";
	public static final String CMD_SCHEDULER_THROUGH_FLOOR = "CMD_SCHEDULER_THROUGH_FLOOR";
	
	public static final String CMD_ELEVATOR_DISTANCE_RESPONSE = "CMD_ELEVATOR_DISTANCE_RESPONSE";
	public static final String CMD_SCHEDULER_THROUGH_FLOOR_RESPONSE = "CMD_SCHEDULER_THROUGH_FLOOR_RESPONSE";
	
	public static final String MESSAGE_LIFT_MANAGE_CMD_TYPE = "MESSAGE_LIFT_MANAGE_CMD_TYPE";
	public static final String MESSAGE_DEVICE_CMD_TYPE_RESPONSE = "MESSAGE_DEVICE_CMD_TYPE_RESPONSE";
	

	public static int INVALID_TAG = -1;
	public static int BUSY_OP_TAG = -2 ; 
	public static int SUCCESS_TAG = 0 ;
	
}
	 
