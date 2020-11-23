package com.xixi.sdk.model;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.device.LLElevatorController;
import com.xixi.sdk.logger.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LLElevatorData {
	
	private static final int BOARD_NUM = 1;
	private static final boolean ELEVATOR_MANAGED = false;
	private static final int COUNT_FLOOR = 8 ;
	private static final int FIRST_FLOOR = 1 ;
	 
	private String schedulerDeviceLocation;
	
	private int boardNum = BOARD_NUM;
	private boolean elevatormanaged = ELEVATOR_MANAGED;
	
	private int maxFloor = COUNT_FLOOR;
	private int firstFloor = FIRST_FLOOR;
	private String allowFloors = "1";
    private String parentId ;

    public String getHexByfloor(int floor) {
		int setFloor = toLogicalFloor(floor) ;
		int remainder = 1 << ((setFloor + 3) & 3);
		int quotient = (setFloor - 1) >> 2;
		return quotient == 0 ? String.valueOf(remainder)
				: (String.valueOf(remainder) + StringUtils.join(Collections.nCopies(quotient, "0"), ""));
	}

	public int toLogicalFloor(int realFloor) {
	 	return realFloor - firstFloor + (LLSDKUtils.oppositeSigns(realFloor, firstFloor) ? 0 : 1);
	}

    public int toRealFloor(int logicalFloor) {
        if(logicalFloor == 0){
        	logicalFloor = 1;
		}

		int distance = firstFloor < 0 ? 1 - firstFloor : firstFloor - 1;
		int showFloor = 0;
		if (logicalFloor - distance < 0) {
			showFloor = logicalFloor - distance;
		} else {
			showFloor = distance != 0 ? logicalFloor - distance + 1 : logicalFloor;
		}
		return showFloor;
	}

	public LLElevatorData() {
		this(BOARD_NUM , ELEVATOR_MANAGED);
	}
	public LLElevatorData(int boardNum , boolean elevatormanaged) {
		this.boardNum = boardNum;
		this.elevatormanaged = elevatormanaged;
	}
	
	public String getSchedulerDeviceLocation() {
		return schedulerDeviceLocation;
	}
	public void setSchedulerDeviceLocation(String schedulerDeviceLocation) {
		this.schedulerDeviceLocation = schedulerDeviceLocation;
	}
	
	private final Set<String> manipulatedElevatorList = new HashSet<String>() ; 
	
	public final Collection<String> getElevatorInfo() {
		return manipulatedElevatorList;
	}
	
	public void setElevatorInfo(String elevatorInfo) {
		try { 
			String []elevators = elevatorInfo.trim().split(",");
			manipulatedElevatorList.clear(); 
			manipulatedElevatorList.addAll(Arrays.asList(elevators)) ; 
		} catch(Exception e ){ 
			
		}
		this.elevatorInfo = elevatorInfo;
	}
	
	private String elevatorInfo ;
	public int getMaxFloor() {
		return maxFloor;
	}
	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}
	public int getFirstFloor() {
		return firstFloor;
	}
	public void setFirstFloor(int firstFloor) {
		this.firstFloor = firstFloor;
	}
	public String getAllowFloors() {
		return allowFloors;
	}
	public void setAllowFloors(String Floors) {
		this.allowFloors = Floors;
	}
	public int getBoardNum() {
		return boardNum;
	}
	public boolean getElevatormanaged() {
		return elevatormanaged;
	}
	public void setElevatormanaged(boolean elevatormanaged) {
		this.elevatormanaged = elevatormanaged;
	}
	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	} 
}
