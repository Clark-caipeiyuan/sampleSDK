
package com.xixi.sdk.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;

import com.xixi.sdk.factory.LLInstanceCreatorMap;
import com.xixi.sdk.factory.XmlNodeFactory;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.utils.pingyin.PinyinUtils;

import android.text.TextUtils;

public class LLBuddy extends LLLayoutXmlNode implements Serializable {

	/**
	 * 
	 */
	final static LLInstanceCreatorMap map_layout_instance_callback;

	public final static String BUDDY_NAME = "buddy";

	public final static List<LLXmlNode> getBuddiesFromLayout(LLXmlNode xmlNodes) {
		try {
			return xmlNodes.getChildren().get(ROOT_ELEMENT_NAME).get(0).getChildren().get(BUDDY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<LLXmlNode>();
	}

	public final static List<LLXmlNode> getBuddiesFromInfo(LLXmlNode xmlNodes) {

		try {
			return xmlNodes.getChildren().get(BUDDY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<LLXmlNode>();
	}

	private String ver;

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
    private String property_id ;
	private String PinYin;
	private char cInitialLetter;
	private String firstPinYin;
    private String schedulerLocation;
    private String elevatorInfo; 
    
	public String getSchedulerLocation() {
		return schedulerLocation;
	}

	public void setSchedulerLocation(String schedulerLocation) {
		this.schedulerLocation = schedulerLocation;
	}

	public String getElevatorInfo() {
		return elevatorInfo;
	}

	public void setElevatorInfo(String elevatorInfo) {
		this.elevatorInfo = elevatorInfo;
	}

	public String getProperty_id() {
		return property_id;
	}

	public void setProperty_id(String property_id) {
		this.property_id = property_id;
	}

	private String type;

	private String imagePath;
	private String time;
	
	private String floor;

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	private String expireDate;

	private String authorizedUserId;
	
	private String appAccessExpDate;
	
	private String elevator ;
	
	private String parentId ;
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getElevator() {
		return elevator;
	}

	public void setElevator(String elevator) {
		this.elevator = elevator;
	}

	public String getAppAccessExpDate() {
		return appAccessExpDate;
	}

	public void setAppAccessExpDate(String appAccessExpDate) {
		this.appAccessExpDate = appAccessExpDate;
	}

	public String getAuthorizedUserId() {
		return authorizedUserId;
	}

	public void setAuthorizedUserId(String authorizedUserId) {
		this.authorizedUserId = authorizedUserId;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getPinYin() {
		return PinYin;
	}

	public void setPinYin(String pinYin) {
		PinYin = pinYin;
	}

	private final static char DEFAULT_SYMBOL_AS_CHAR = '#';
	private final static String DEFAULT_SYMBOL = String.valueOf(DEFAULT_SYMBOL_AS_CHAR);

	public char getFirstPinYin() {
		return cInitialLetter;
	}

	public String getFirstPinYinAsString() {
		return firstPinYin;
	}

	public void setFirstPinYin(String firstPinYin) {
		try {
			cInitialLetter = firstPinYin.toUpperCase(Locale.ENGLISH).charAt(0);
			this.firstPinYin = firstPinYin; // String.valueOf((char)cInitialLetter);String.valueOf((char)cInitialLetter);
		} catch (Exception e) {
			cInitialLetter = DEFAULT_SYMBOL_AS_CHAR;
		}
	}
	// public void setFirstPinYin(char firstPinYin) {
	// cInitialLetter = firstPinYin ;
	// }

	final static String Layout_Keys[][] = new String[][] { { ROOT_ELEMENT_NAME }, { BUDDY_NAME }, { "menu" },
			{ "light","airCon","curtain" } };

	final static String Buddy_Keys[][] = new String[][] { { BUDDY_NAME }, { "menu" }, { "light","airCon","curtain" } };

	public static String[][] getBuddyKeys() {
		return Buddy_Keys;
	}

	public static String[][] getLayoutKeys() {
		return Layout_Keys;
	}

	public static LLInstanceCreatorMap getLayoutMapInstanceCallback() {
		return map_layout_instance_callback;
	}

	public void setName(String name) {
		super.setName(name);
		setPinYinInfo();
	}

	private void setPinYinInfo() {
		try {
			String pinyin = PinyinUtils.getPingYin(getName());
			String Fpinyin = pinyin.substring(0, 1).toUpperCase(Locale.ENGLISH);
			setPinYin(pinyin);
			// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
			if (Fpinyin.matches("[A-Z]")) {
				setFirstPinYin(Fpinyin);
			} else {
				setFirstPinYin(DEFAULT_SYMBOL);
			}
		} catch (Exception e) {
			setPinYin(DEFAULT_SYMBOL);
			setFirstPinYin(DEFAULT_SYMBOL);
		}
	}

	static {
		map_layout_instance_callback = new LLInstanceCreatorMap();
		map_layout_instance_callback.put(LLLayoutXmlNode.ROOT_ELEMENT_NAME, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {
				return new LLLayoutXmlNode();
			}
		});
		map_layout_instance_callback.put(LLBuddy.CLASS_TAG, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {
				LLBuddy buddy = new LLBuddy();
				
				buddy.setVer(parser.getAttributeValue("", "ver"));
				buddy.setId(parser.getAttributeValue("", "id"));
				buddy.setName(parser.getAttributeValue("", "name"));
				buddy.setType(parser.getAttributeValue("", "type"));
				buddy.setProperty_id(parser.getAttributeValue("", "property_id"));
				buddy.setImagePath(parser.getAttributeValue("", "image"));
                buddy.setExpireDate(parser.getAttributeValue("", "expireDate"));
                buddy.setAuthorizedUserId(parser.getAttributeValue("", "authorizedUserId"));
                buddy.setAppAccessExpDate(parser.getAttributeValue("","appAccessExpDate"));
                buddy.setFloor(parser.getAttributeValue("", "floor"));
                buddy.setElevator(parser.getAttributeValue("", "elevator"));
                buddy.setParentId(parser.getAttributeValue("", "parentId"));
                buddy.setElevatorInfo(parser.getAttributeValue("", "elevatorInfo"));
                buddy.setSchedulerLocation(parser.getAttributeValue("", "schedulerLocation"));
				buddy.setPinYinInfo();
				return buddy;
			}
		});

		map_layout_instance_callback.put(LLRoomMenu.CLASS_TAG, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {
				LLRoomMenu menu = new LLRoomMenu();
				menu.setOp(parser.getAttributeValue("", "op"));
				menu.setId(parser.getAttributeValue("", "id"));
				menu.setName(parser.getAttributeValue("", "name"));
				menu.setUrl(parser.getAttributeValue("", "url"));
				return menu;
			}
		});

		map_layout_instance_callback.put(LLSmartDevice.CLASS_TAG_LIGHT, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {

				LLSmartDevice device = new LLSmartDevice();
				device.setId(parser.getAttributeValue("", "id"));
				device.setName(parser.getAttributeValue("", "name"));
				device.setType(LLSmartDevice.CLASS_TAG_LIGHT);
				return device;
			}
		});
		map_layout_instance_callback.put(LLSmartDevice.CLASS_TAG_CURTAIN, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {

				LLSmartDevice device = new LLSmartDevice();
				device.setId(parser.getAttributeValue("", "id"));
				device.setName(parser.getAttributeValue("", "name"));
				device.setType(LLSmartDevice.CLASS_TAG_CURTAIN);
				return device;
			}
		});
		map_layout_instance_callback.put(LLSmartDevice.CLASS_TAG_AIR_CON, new XmlNodeFactory() {
			public LLXmlNode makeNewInstanceWithAttribute(XmlPullParser parser) {

				LLSmartDevice device = new LLSmartDevice();
				device.setId(parser.getAttributeValue("", "id"));
				device.setName(parser.getAttributeValue("", "name"));
				device.setType(LLSmartDevice.CLASS_TAG_AIR_CON);
				return device;
			}
		});
	}

	public static LLBuddy fromJson(String json) {
		return (LLBuddy) LLGsonUtils.fromJson(json, LLBuddy.class);
	}

	private static final long serialVersionUID = 9196136842045220794L;
	public final static String CLASS_TAG = "buddy";

	public List<LLXmlNode> getMenus() {
		return this.getChildren().get(LLRoomMenu.CLASS_TAG);
	}

	public LLBuddy(String id, String name) {
		super(id, name);
	}

	public LLBuddy() {
	}

	public LLBuddy(String id, String name, String type, String imagePath) {
		super(id, name);
		this.type = type;
		this.imagePath = imagePath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	// public int getDb_id() {
	// return db_id;
	// }
	//
	// public void setDb_id(int db_id) {
	// this.db_id = db_id;
	// }

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == (LLBuddy) obj)
			return true;
		if (!(obj instanceof LLBuddy)) {
			return false;
		}
		LLBuddy target = (LLBuddy) obj;

		String strId = (obj == null ? "" : target.getId());

		return TextUtils.equals(getId(), strId);
	}

	@Override
	public int hashCode() {

		return getId().hashCode();
	}

	@Override
	public String toString() {
		return getId();
	}

}
