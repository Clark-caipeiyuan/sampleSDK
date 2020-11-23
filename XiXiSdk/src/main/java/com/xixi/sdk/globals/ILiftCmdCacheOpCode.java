package com.xixi.sdk.globals;

public interface ILiftCmdCacheOpCode {

	public final static int opInvalidCode = 0;
	public static final int opAllowCode = 0x1;
	public final static int opDisallowCode = 0x2;
	public static final int opDirectCode = 0x3;
	public static final int opDiscardDirectCode = 0x4;
	public static final int opAllAllowCode = 0x5;
	public static final int opAllDisableCode = 0x6;
	public static final String LiftOpCodeName[] = 
		{ "opInvalidCode", "opAllowCode", "opDisallowCode", "opDirectCode",
			"opDiscardDirectCode","opAllAllowCode","opAllDisableCode", };

	public static final int ELEVATOR_BUSY = 0x10 ; 
	public static final int ENABLE_ALL = 0x1 ; 
	public static final int DISABLE_ALL = 0x2 ; 
	public static final int INIT_STATE = 0x4 ; 
	
	public static final int INVALID_STATUS = 3 ; 
	public static final int NON_STATUS = 2  ;
	public static final int UP_STATUS = 1 ;
	public static final int DOWN_STATUS = 0 ;

	public static final String[] arrow_str = {"DOWN" , "UP" , "STATIC" , "INVALID_STATUS"}; 
	public final static int INIT_LOGICAL_FLOOR = 0 ;
}
