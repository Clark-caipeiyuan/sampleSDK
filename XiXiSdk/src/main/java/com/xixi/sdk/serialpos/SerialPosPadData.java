package com.xixi.sdk.serialpos;

import com.xixi.sdk.serialpos.ISerialDataEnum;

public class SerialPosPadData implements ISerialDataEnum {

	// // Data Index
	// private static final byte FRAME_ALIGNMENT_HEAD_INDEX = 0;
	// private static final byte FRAME_LENGTH_INDEX = 1;
	// private static final byte NET_ADDR_INDEX = 2;
	// private static final byte DEST_LOGIC_ADDR_INDEX = 3;
	// private static final byte DEST_CHANNEL_ADDR_INDEX = 4;
	// private static final byte SRC_LOGIC_ADDR_INDEX = 5;
	// private static final byte SRC_CHANNEL_ADDR_INDEX = 6;
	// private static final byte REPSPONSE_TYPE_CODE_INDEX = 7;
	// private static final byte DATA_TYPE_INDEX = 8;
	// private static final byte DATA_INDEX = 3;
	// private static final byte CHECK_CODE_INDEX = 10;
	//
	// // Frame Alignment Head Value
	// private static final byte FRAME_ALIGNMENT_HEAD_VALUE = 0x55;
	//
	// // Frame Length Default Value
	// private static final byte FRAME_LENGTH_DEFAULT_VALUE = 0x05;
	//
	// // Data Type Values
	// private static final byte DATA_TYPE_LOCK_ONOFF = 0x01;
	// private static final byte DATA_TYPE_HEX = 0x08;
	// private static final byte DATA_TYPE_ASCII = 0x0A;

	private byte frameAlighmentHead;
	private byte frameLength;
	private byte netAddr;
	private byte dstLogicAddr;
	private byte dstChannelAddr;
	private byte srcLogicAddr;
	private byte srcChannelAddr;
	private byte respTypeCode;
	private byte dataType;
	private byte data;
	private byte crc;

	public byte getFrameAlighmentHead() {
		return frameAlighmentHead;
	}

	public void setFrameAlighmentHead(byte frameAlighmentHead) {
		this.frameAlighmentHead = frameAlighmentHead;
	}

	public byte getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(byte frameLength) {
		this.frameLength = frameLength;
	}

	public byte getNetAddr() {
		return netAddr;
	}

	public void setNetAddr(byte netAddr) {
		this.netAddr = netAddr;
	}

	public byte getDstLogicAddr() {
		return dstLogicAddr;
	}

	public void setDstLogicAddr(byte dstLogicAddr) {
		this.dstLogicAddr = dstLogicAddr;
	}

	public byte getDstChannelAddr() {
		return dstChannelAddr;
	}

	public void setDstChannelAddr(byte dstChannelAddr) {
		this.dstChannelAddr = dstChannelAddr;
	}

	public byte getSrcLogicAddr() {
		return srcLogicAddr;
	}

	public void setSrcLogicAddr(byte srcLogicAddr) {
		this.srcLogicAddr = srcLogicAddr;
	}

	public byte getSrcChannelAddr() {
		return srcChannelAddr;
	}

	public void setSrcChannelAddr(byte srcChannelAddr) {
		this.srcChannelAddr = srcChannelAddr;
	}

	public byte getRespTypeCode() {
		return respTypeCode;
	}

	public void setRespTypeCode(byte respTypeCode) {
		this.respTypeCode = respTypeCode;
	}

	public byte getDataType() {
		return dataType;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte rawData) {
		this.data = rawData;
	}

	public byte getCrc() {
		return crc;
	}

	public void setCrc(byte crc) {
		this.crc = crc;
	}

	public SerialPosPadData() {

	}

	public SerialPosPadData(byte[] rawData) {
		this.frameAlighmentHead = rawData[FRAME_ALIGNMENT_HEAD_INDEX];
		this.frameLength = rawData[FRAME_LENGTH_INDEX];
		this.netAddr = rawData[NET_ADDR_INDEX];
		this.dstLogicAddr = rawData[DEST_LOGIC_ADDR_INDEX];
		this.dstChannelAddr = rawData[DEST_CHANNEL_ADDR_INDEX];
		this.srcLogicAddr = rawData[SRC_LOGIC_ADDR_INDEX];
		this.srcChannelAddr = rawData[SRC_CHANNEL_ADDR_INDEX];
		this.respTypeCode = rawData[REPSPONSE_TYPE_CODE_INDEX];
		this.dataType = rawData[DATA_TYPE_INDEX];
		this.data = rawData[DATA_INDEX];
		this.crc = rawData[CHECK_CODE_INDEX];
		validate();
	}

	public byte[] genSerialDataInBytes() {
		byte[] rawData = new byte[FRAME_LENGTH_DEFAULT_VALUE + 1];
		rawData[FRAME_ALIGNMENT_HEAD_INDEX] = this.frameAlighmentHead;
		rawData[FRAME_LENGTH_INDEX] = this.frameLength;
		rawData[NET_ADDR_INDEX] = this.netAddr;
		rawData[DEST_LOGIC_ADDR_INDEX] = this.dstLogicAddr;
		rawData[DEST_CHANNEL_ADDR_INDEX] = this.dstChannelAddr;
		rawData[SRC_LOGIC_ADDR_INDEX] = this.srcLogicAddr;
		rawData[SRC_CHANNEL_ADDR_INDEX] = this.srcChannelAddr;
		rawData[REPSPONSE_TYPE_CODE_INDEX] = this.respTypeCode;
		rawData[DATA_TYPE_INDEX] = this.dataType;
		rawData[DATA_INDEX] = this.data;
		rawData[CHECK_CODE_INDEX] = this.crc;
		return rawData;
	}

	private boolean validate() {
		return true;
	}

}
