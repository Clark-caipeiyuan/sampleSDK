package com.xixi.sdk.serialpos;

public  class CmdErrorCode implements ISerialDataEnum { 
	int errorCode ; 
	
	public static CmdErrorCode valueOf(boolean crc , int errorCode ) { 
		return new CmdErrorCode(!crc ? CMD_CRC_ERROR : errorCode) ; 
	}

	public static CmdErrorCode valueOf( int errorCode ) { 
		return valueOf(true , errorCode ) ; 
	}
	
	public CmdErrorCode(int errorCode ) { 
		this.errorCode = errorCode ; 
	}  
	public boolean noError() { 
		return errorCode == CMD_NO_ERROR ; 
	}
	
	public boolean needToResend()  { 
		return CMD_NO_ERROR != errorCode && errorCode != CMD_EXECUTE_ERROR ; 
	}
} ; 