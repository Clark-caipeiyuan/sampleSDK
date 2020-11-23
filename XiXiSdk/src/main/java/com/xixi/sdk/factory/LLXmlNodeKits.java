package com.xixi.sdk.factory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLLayoutXmlNode;
import com.xixi.sdk.model.LLRoomMenu;
import com.xixi.sdk.model.LLSmartDevice;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.model.LLXmlNode.LLXmlNodeMap;

import android.text.TextUtils;

import com.xixi.sdk.model.SortedNode;

public class LLXmlNodeKits {

	private static void addJsonObjects(JsonObject jo, String[] keys, String[] values) {
		for (int i = 0; i < keys.length; i++) {
			String s = values[i] ;
			if (s != null) {
				jo.add(keys[i], new JsonPrimitive(s));
			}
		}
	}

	public static class LLSortedNodeSerializer implements JsonSerializer<SortedNode> {
		@Override
		public JsonElement serialize(SortedNode arg0, Type arg1, JsonSerializationContext arg2) {
			JsonObject result = new JsonObject();
			addJsonObjects(result , new String[]{ "buddyName" , "bd_id" , "timestamp" , } , new String[]{arg0.getBuddyName() , arg0.getBd_id() , String.valueOf(arg0.getTimestamp())});
			return result ;
		}
	}

	public static class LLSortedNodeDeserializer implements JsonDeserializer<SortedNode> {

		@Override
		public SortedNode deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			JsonObject jo = json.getAsJsonObject();
			SortedNode sn = new SortedNode();
			sn.setTimestamp(jo.get("timestamp").getAsLong());
			String bd_id = "" ;
			String buddyName = "" ;
			try {
				bd_id = jo.get("bd_id").getAsString();
				buddyName = jo.get("buddyName").getAsString();
			} catch(NullPointerException e) {

			}

			if ( TextUtils.isEmpty(bd_id)) {
				JsonElement je = jo.get("bd").getAsJsonObject();
				LLXmlNodeDeserializer xnd = new LLXmlNodeDeserializer() ;
				LLBuddy lbd = (LLBuddy) xnd.deserialize(je, arg1, arg2);
				bd_id = lbd.getId() ;
				buddyName= lbd.getName() ;
			}
			sn.setBd_id(bd_id);
			sn.setBuddyName(buddyName);
			return sn;
		}

	}
	public static class LLXmlNodeSerializer implements JsonSerializer<LLXmlNode> {



		@Override
		public JsonElement serialize(LLXmlNode node, Type arg1, JsonSerializationContext arg2) {
			JsonObject result = new JsonObject();
			result.add("xmlNodeName", new JsonPrimitive(node.getXmlNodeName()));
			LLXmlNodeMap nodeMap = node.getChildren();
			if (nodeMap.size() != 0) {

				JsonObject _l_ = new JsonObject() ;
				for (String key : nodeMap.keySet()) {
					JsonArray ja = new JsonArray();
					for (LLXmlNode _node : nodeMap.get(key)) {
						ja.add(serialize(_node, arg1, arg2));
					}
					_l_.add(key, ja);
				}
				result.add("children", _l_);

			}

			if (node instanceof LLLayoutXmlNode) {
				LLLayoutXmlNode layoutNode = (LLLayoutXmlNode) node;
				addJsonObjects(result, new String[] { "id", "name" }, new String[] { layoutNode.getId(), layoutNode.getName() });

				if (node instanceof LLBuddy) {
					LLBuddy bd = (LLBuddy) node;
					addJsonObjects(result, new String[] { "PinYin", "firstPinYin", "type", "imagePath", "time","elevator" },
							new String[] { bd.getPinYin(), bd.getFirstPinYinAsString(), bd.getType(), bd.getImagePath(),
									bd.getTime(),bd.getElevator() });
					result.add("cInitialLetter", new JsonPrimitive(bd.getFirstPinYin()));
				} else if (node instanceof LLRoomMenu) {

					LLRoomMenu menu = (LLRoomMenu) node;
					addJsonObjects(result, new String[] { "op", "url" }, new String[] { menu.getOp(), menu.getUrl() });
				} else if (node instanceof LLSmartDevice) {
					LLSmartDevice smartDevice = (LLSmartDevice) node;
					addJsonObjects(result, new String[] { "status", "type" },
							new String[] { smartDevice.getStatus(), smartDevice.getType() });
				}
			}
			return result;
		}
	}

	public static class LLXmlNodeDeserializer implements JsonDeserializer<LLXmlNode> {

		private String parseStr(String key, JsonObject jo) {
			try {
				return jo.get(key).getAsString();
			} catch (Exception e) {
			}
			;

			return "";
		}

		@Override
		public LLXmlNode deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {

			JsonObject jo = json.getAsJsonObject();

			String strNodeName = jo.get("xmlNodeName").getAsString();

			LLLayoutXmlNode node = null;
			LLBuddy bd = null;
			LLRoomMenu rm = null;
			LLSmartDevice sd = null;
			JsonObject _jo;
			if (LLBuddy.CLASS_TAG.equals(strNodeName)) {
				bd = new LLBuddy();
				bd.setXmlNodeName(LLBuddy.CLASS_TAG);
				bd.setId(parseStr("id", jo));
				bd.setName(parseStr("name", jo));
				bd.setTime(parseStr("time", jo));
				bd.setType(parseStr("type", jo));
				bd.setImagePath(parseStr("imagePath", jo));
				bd.setElevator(parseStr("elevator",jo));
				node = bd;

			} else if (LLRoomMenu.CLASS_TAG.equals(strNodeName)) {
				rm = new LLRoomMenu();
				rm.setXmlNodeName(LLRoomMenu.CLASS_TAG);
				rm.setId(parseStr("id", jo));
				rm.setName(parseStr("name", jo));

				rm.setOp(parseStr("op", jo));
				rm.setUrl(parseStr("url", jo));

				node = rm;

			} else if (LLSmartDevice.CLASS_TAG_COLLECTION.contains(strNodeName)) {
				sd = new LLSmartDevice();
				sd.setXmlNodeName(LLSmartDevice.CLASS_TAG);
				sd.setType(parseStr("type", jo));
				sd.setStatus("0");
				sd.setId(parseStr("id", jo));
				sd.setName(parseStr("name", jo));
				node = sd;
			}

			if (node != null) {

				_jo = (JsonObject) jo.get("children");
				if (_jo != null) {

					LLXmlNodeMap _map = node.getChildren();
					for (Entry<String, JsonElement> entry : _jo.entrySet()) {
						JsonElement j1 = entry.getValue();
						LLSDKUtils.danielAssert(j1.isJsonArray());
						JsonArray ja = j1.getAsJsonArray();
						List<LLXmlNode> _l = new ArrayList<LLXmlNode>();
						for (JsonElement je : ja) {
							LLXmlNode ln = deserialize(je, arg1, arg2);
							_l.add(ln);
						}
						_map.put(entry.getKey(), _l);
					}
					// bd.setChildren(_jo_ == null ? null );
				} else {
					node.setChildren(null);
				}

				return node;
			}
			return null;
		}

		public final static String CLASS_TAG_AIR_CON = "airCon";
		public final static String CLASS_TAG_CURTAIN = "curtain";
		public final static String CLASS_TAG_PROPERTY = "property";
		//
		// private final static String nodeNames[] ={
		// Buddy.CLASS_TAG ,
		// LLRoomMenu.CLASS_TAG ,
		// LLSmartDevice.CLASS_TAG_LIGHT ,
		// LLSmartDevice.CLASS_TAG_AIR_CON ,
		// LLSmartDevice.CLASS_TAG_CURTAIN ,
		// LLSmartDevice.CLASS_TAG_PROPERTY
		// };
		//
		//
		// private final static Class<?> instanceClass[] ={
		// Buddy.class ,
		// LLRoomMenu.class ,
		// LLSmartDevice.class ,
		// LLSmartDevice.class ,
		// LLSmartDevice.class ,
		// LLSmartDevice.class
		// };

		public LLLayoutXmlNode createInstance(String xmlNodeName)
				throws InstantiationException, IllegalAccessException {

			// for (int i = 0 ; i < instanceClass.length ; i++ ) {
			// if ( )
			// if ( nodeNames[i].equals(xmlNodeName)) {
			// return (LLXmlNode) instanceClass[i].newInstance();
			// }
			// }

			return null;
		}

	}

}
