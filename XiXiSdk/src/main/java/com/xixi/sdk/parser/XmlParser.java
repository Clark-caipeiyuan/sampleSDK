
package com.xixi.sdk.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.factory.LLInstanceCreatorMap;
import com.xixi.sdk.model.LLLayoutXmlNode;
import com.xixi.sdk.model.LLPushContent;
import com.xixi.sdk.model.LLSmartDevice;
import com.xixi.sdk.model.LLXmlNode;

import android.util.Log;

public class XmlParser {

	LLInstanceCreatorMap instanceFactory;
	private String[][] attributes_keys;
	private XmlPullParser parserUtils;

	public XmlParser(LLInstanceCreatorMap instanceFactory, String result, String[][] node_keys)
			throws XmlPullParserException {
		super();
		this.attributes_keys = node_keys;
		this.instanceFactory = instanceFactory;

		parserUtils = GlobalParser.getXmlParser();
		parserUtils.setInput(new StringReader(result));
		InputStream is = new ByteArrayInputStream(result.getBytes());
		parserUtils.setInput(is, "utf-8");
	}

	public LLXmlNode parseXml() {
		LLXmlNode node = _parseXml(0, parserUtils);
		LLXmlNode rootNode = new LLLayoutXmlNode();
		rootNode.setXmlNodeName(node.getXmlNodeName());
	
		rootNode.getChildren().put(attributes_keys[0][0],
				new ArrayList<LLXmlNode>(Arrays.asList(new LLXmlNode[] { node })));
		return rootNode;
	}

	public XmlParser() {

	}

	private boolean validationNode(String node_name, int depth) {
		String[] nodes = attributes_keys[depth];
		int i;
		for (i = 0; i < nodes.length; i++) {
			if (node_name.equals(nodes[i])) {
				break;
			}
		}
		return i != nodes.length;
	}

	private void searchNextEndTag(XmlPullParser parser) throws XmlPullParserException, IOException {

		int depth = -1;
		Loop: while (true) {
			int eventType = parser.getEventType();
			switch (eventType) {
			case XmlPullParser.END_TAG:
				if (depth == 0) {
					parser.next();
					break Loop;
				}
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			default:
			}
			parser.next();
			LLSDKUtils.danielAssert(depth >= 0);
		}
	}
 
	private LLXmlNode _parseXml(int depth, XmlPullParser parser) {
		int eventType;
		LLXmlNode xmlNodeInstance = null;
		LLXmlNode xmlChildNode = null;

		try {
			eventType = parser.getEventType();
			LLSDKUtils.danielAssert(XmlPullParser.START_DOCUMENT == eventType || XmlPullParser.START_TAG == eventType);
			if (eventType == XmlPullParser.START_TAG) {
				String strNodeName = parser.getName();

				if (validationNode(strNodeName, depth)) {
					xmlNodeInstance = instanceFactory.get(strNodeName).makeNewInstanceWithAttribute(parser);
					xmlNodeInstance.setXmlNodeName(strNodeName);
				} else {
					searchNextEndTag(parser);
					return null;
				}

				parser.next();
				Loop: while (true) {
					eventType = parser.getEventType();
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						xmlChildNode = _parseXml(depth + 1, parser);
						if (xmlChildNode != null) {
							String strNameNode = xmlChildNode.getXmlNodeName();
							List<LLXmlNode> ln = xmlNodeInstance.getChildren().get(strNameNode);
							if (ln == null) {
								ln = new ArrayList<LLXmlNode>();
								xmlNodeInstance.getChildren().put(strNameNode,ln);
							}
							ln.add(xmlChildNode);
						}
						break;
					}
					case XmlPullParser.END_TAG:
						parser.next();
					case XmlPullParser.END_DOCUMENT:
						break Loop;
					default:
						parser.next();
						break;
					}

				}
			} else {
				parser.next();
				xmlNodeInstance = _parseXml(depth, parser);
			}

		} catch (Exception e) {
			Log.i(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e));
			e.printStackTrace();
		}

		return xmlNodeInstance;
	}
  
	public static LLXmlNode parseXml(String result, LLInstanceCreatorMap instanceCreator,
			String[][] attributes) {
		try {
			XmlParser parser = new XmlParser(instanceCreator, result, attributes);
			return  parser.parseXml();
			 
		} catch (Exception e) {
		}
		return new LLXmlNode();
	}

	private static void verify(LLXmlNode nodes) {
		List<LLXmlNode> childrenList = nodes.getChildren().get("body");

		LLSDKUtils.danielAssert(childrenList != null && childrenList.size() == 1);
		; // childrenNode != null );
		childrenList = childrenList.get(0).getChildren().get("buddy");
		LLSDKUtils.danielAssert(childrenList != null && childrenList.size() == 2);
		; // childrenNode != null );
		List<LLXmlNode> childrenList1 = childrenList.get(1).getChildren().get("menu");
		LLSDKUtils.danielAssert(childrenList1 != null && childrenList1.size() == 2);
		; // childrenNode != null );
		// childrenList1 = childrenList.get(1).getChildren().get("menu") ;
		// LLSDKUtils.danielAssert(childrenList1!= null && childrenList1.size()
		// == 2 ) ;; //childrenNode != null );
		//
		// childrenList = childrenList.get(0).getChildren().get("menu") ;
		// int groups[] = new int[] { 1 , 1 , 1 , 1, 1 ,2 };
		// for (int i = 0 ; i < groups.length ; i++ ) {
		// childrenList.get(i)
		// }

	}

	public static boolean isStatusFirstTag(String messageText) {
		boolean a = false;

		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		try {
			p.setInput(new StringReader(messageText));
			InputStream is = new ByteArrayInputStream(messageText.getBytes());
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();
					if ("sts".equals(name)) {
						a = true;
						break;
					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return a;
	}

	public static Map<String, LLSmartDevice> parserLightStatus(String lightStatus) {

		// XmlResourceParser p=context.getResources().getXml(R.xml.layout);
		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		Map<String, LLSmartDevice> lights = new HashMap<String, LLSmartDevice>();

		try {
			p.setInput(new StringReader(lightStatus));
			InputStream is = new ByteArrayInputStream(lightStatus.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();

					if ("l".equals(name)) {

						LLSmartDevice light = new LLSmartDevice();
						light.setId(p.getAttributeValue(null, "id"));
						light.setStatus(p.getAttributeValue(null, "cur"));
						lights.put(light.getId(), light);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lights;
	}

	public String getUrlFromFile(String messageUrl, String keys) {
		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		String layout_url = null;
		try {
			p.setInput(new StringReader(messageUrl));
			InputStream is = new ByteArrayInputStream(messageUrl.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();

					if ("data".equals(name)) {
						if ((p.getAttributeValue("", "func")).equals(keys)) {
							layout_url = p.getAttributeValue("", "url");
						}
						return layout_url;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return layout_url;
	}

	public LLPushContent getLLPushContentFromFile(String messageeJson, String keys) {
		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		LLPushContent llPushContent = null;
		try {
			p.setInput(new StringReader(messageeJson));
			InputStream is = new ByteArrayInputStream(messageeJson.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();

					if ("data".equals(name)) {
						llPushContent = new LLPushContent();
						if ((p.getAttributeValue("", "func")).equals(keys)) {
							String title = p.getAttributeValue("", "service_title");
							String body = p.getAttributeValue("", "service_body_des");
							String url = p.getAttributeValue("", "service_url");
							String image = p.getAttributeValue("", "service_image");
							String time = p.getAttributeValue("", "service_time");
							llPushContent.setService_title(LLSDKUtils.parseFromHexString(title));
							llPushContent.setService_body_des(LLSDKUtils.parseFromHexString(body));
							llPushContent.setService_url(LLSDKUtils.parseFromHexString(url));
							llPushContent.setService_image(LLSDKUtils.parseFromHexString(image));
							llPushContent.setService_time(LLSDKUtils.parseFromHexString(time));

							return llPushContent;
						} else {
							return llPushContent;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return llPushContent;
	}

	public String getJsonStrFromFile(String messageeJson, String keys) {
		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		JSONObject json = null;
		try {
			p.setInput(new StringReader(messageeJson));
			InputStream is = new ByteArrayInputStream(messageeJson.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();

					if ("data".equals(name)) {
						if ((p.getAttributeValue("", "func")).equals(keys)) {
							String title = p.getAttributeValue("", "service_title");
							String body = p.getAttributeValue("", "service_body_des");
							String url = p.getAttributeValue("", "service_url");
							String image = p.getAttributeValue("", "service_image");
							String time = p.getAttributeValue("", "service_time");
							json = new JSONObject();
							json.put("service_title", LLSDKUtils.parseFromHexString(title));
							json.put("service_body_des", LLSDKUtils.parseFromHexString(body));
							json.put("service_url", LLSDKUtils.parseFromHexString(url));
							json.put("service_image", LLSDKUtils.parseFromHexString(image));
							json.put("service_time", LLSDKUtils.parseFromHexString(time));
							return json.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

	public String getVersionFromFile(String messageUrl) {
		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		String version = null;
		try {
			p.setInput(new StringReader(messageUrl));
			InputStream is = new ByteArrayInputStream(messageUrl.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();

					if ("data".equals(name)) {
						version = p.getAttributeValue(null, "ver");
						return version;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return version;
	}

	public boolean isFileFirstTag(String messageText) {
		boolean a = false;

		XmlPullParser p = GlobalParser.getXmlParser();
		int type;
		String name;
		String layout_url = null;
		try {
			p.setInput(new StringReader(messageText));
			InputStream is = new ByteArrayInputStream(messageText.getBytes());
			p.setInput(is, "utf-8");
			while ((type = p.next()) != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					name = p.getName();
					if ("file".equals(name)) {
						a = true;
					}
				}
			}
		} catch (Exception e) {
		}

		return a;
	}
}