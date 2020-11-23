package com.xixi.sdk.serialpos;

import com.xixi.sdk.serialpos.SerialPosEvent;

import android.os.Parcel;
import android.os.Parcelable;

public class SerialPosEvent implements Parcelable {
	public static final int EVENT_MSG = 1;
	public static final int EVENT_DATA = 2;

	public int type;
	public String msg;
	public int data_length;
	public byte[] data;

	public SerialPosEvent() {

	}

	public SerialPosEvent(Parcel source) {
		readFromParcel(source);
	}

	public void readFromParcel(Parcel source) {
		type = source.readInt();
		msg = source.readString();
		data_length = source.readInt();
		if (data_length > 0) {
			source.readByteArray(data);
		}
	}

	public static final Creator<SerialPosEvent> CREATOR = new Creator<SerialPosEvent>() {
		@Override
		public SerialPosEvent createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new SerialPosEvent(source);
		}

		@Override
		public SerialPosEvent[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SerialPosEvent[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(type);
		dest.writeString(msg);
		dest.writeInt(data_length);
		if (data != null) {
			dest.writeByteArray(data);
		}
	}

	public static Creator<SerialPosEvent> getCreator() {
		return CREATOR;
	}
}
