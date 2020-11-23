package com.xixi.sdk.handler;

import java.util.HashMap;
import java.util.Map;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.device.IDeviceData;
import com.xixi.sdk.device.LLSmartBoxCentersFactory;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.serialpos.SerialDataParser.CmdRetKits;
import com.xixi.sdk.serialpos.SerialPosClient;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.sipmsg.params.LLSipMsgDevicesCmds;
import com.xixi.sdk.sipmsg.params.LLSmartAir;
import com.xixi.sdk.sipmsg.params.LLSmartAirRetResponse;
import com.xixi.sdk.sipmsg.params.LLSmartCurtain;
import com.xixi.sdk.sipmsg.params.LLSmartCurtainRetResponse;
import com.xixi.sdk.sipmsg.params.LLSmartDev;
import com.xixi.sdk.sipmsg.params.LLSmartDoor;
import com.xixi.sdk.sipmsg.params.LLSmartDoorRetResponse;
import com.xixi.sdk.sipmsg.params.LLSmartEnvir;
import com.xixi.sdk.sipmsg.params.LLSmartEnvirRetResponse;
import com.xixi.sdk.sipmsg.params.LLSmartLightRetResponse;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.network.LLDoorLockNetController;
import com.xixi.sdk.utils.network.LLDoorLockPasController;
import com.xixi.sdk.utils.network.LLDoorSafeNetController;

import android.text.TextUtils;

public class LLHandlerOverSmartDevice implements LLSipCmds {
	private interface SmartDeviceHandler {
		public void onHandler(LLSipMsgDevicesCmds cmd, IoCompletionListener1<IDeviceData> io, Object context);
	}

	final private static Map<String, SmartDeviceHandler> handler_map = new HashMap<String, SmartDeviceHandler>();
	final private static SmartDeviceHandler empty_Handler = new SmartDeviceHandler() {

		@Override
		public void onHandler(LLSipMsgDevicesCmds cmd, IoCompletionListener1<IDeviceData> io, Object context) {
			io.onFinish(null, null);
		}

	};
	static {
		handler_map.put(LLSipCmds.CMD_GET_LIGHT_STATE, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartDev data = LLSmartBoxCentersFactory.createSmartBox().getSmartDevData(cmd.getId());
				LLSDKUtils.runInMainThread(new Runnable() {
					public void run() {
						LLSmartDev llSmartDev = LLSmartBoxCentersFactory.createSmartBox()
						.getSmartDevData(cmd.getId());
						LLSmartLightRetResponse ret = new LLSmartLightRetResponse();
						ret.setLLSmartLightRetResponse(SUCCESS_TAG, CMD_GET_LIGHT_STATE, llSmartDev.getDeviceId(),
								TextUtils.equals(llSmartDev.getState(), "1")?"open":"close", llSmartDev.getData());
						io.onFinish((IDeviceData)ret, null);
					}
				});
			}
		});

		handler_map.put(LLSipCmds.CMD_GET_AIR_AND_TEMP_STATE, new SmartDeviceHandler() {

			@Override

			public void onHandler(LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartEnvir data = LLSmartBoxCentersFactory.createSmartBox().getEnvironmentData();
				LLSDKUtils.runInMainThread(new Runnable() {
					public void run() {
						LLSmartEnvirRetResponse ret = new LLSmartEnvirRetResponse();
						ret.setLLSmartEnvirRetResponse(CMD_GET_AIR_AND_TEMP_STATE, SUCCESS_TAG, data.getPm(), data.getTemp(), data.getMoisture()
								, data.getVoc());
						io.onFinish((IDeviceData) ret, null);
					}
				});
			}
		});

		handler_map.put(LLSipCmds.CMD_TURN_ON_LIGHT, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				LLSmartBoxCentersFactory.createSmartBox().switchOn(true, cmd.getId(),
						new IoCompletionListener1<IDeviceData>() {
							@Override
							public void onFinish(IDeviceData data, Object context) {
								int errorCode = (Integer)context ;
								LLSmartDev llSmartDev = LLSmartBoxCentersFactory.createSmartBox()
								.getSmartDevData(cmd.getId());
								LLSmartLightRetResponse ret = new LLSmartLightRetResponse();
								ret.setLLSmartLightRetResponse(errorCode, CMD_TURN_ON_LIGHT, llSmartDev.getDeviceId(),
										TextUtils.equals(llSmartDev.getState(), "1")?"open":"close", llSmartDev.getData());
								io.onFinish((IDeviceData)ret, null);
							}
						});
			}
		});

		handler_map.put(LLSipCmds.CMD_TURN_OFF_LIGHT, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				LLSmartBoxCentersFactory.createSmartBox().switchOn(false, cmd.getId(),
						new IoCompletionListener1<IDeviceData>() {
							@Override
							public void onFinish(IDeviceData data, Object context) {
								int errorCode = (Integer)context ;
								LLSmartDev llSmartDev = LLSmartBoxCentersFactory.createSmartBox()
								.getSmartDevData(cmd.getId());
								LLSmartLightRetResponse ret = new LLSmartLightRetResponse();
								ret.setLLSmartLightRetResponse(errorCode, CMD_TURN_OFF_LIGHT, llSmartDev.getDeviceId(),
										TextUtils.equals(llSmartDev.getState(), "1")?"open":"close", llSmartDev.getData());
								io.onFinish((IDeviceData)ret, null);
//								LLSmartDev ret_data = LLSmartBoxCentersFactory.createSmartBox()
//										.getSmartDevData(cmd.getId());
//								io.onFinish((IDeviceData) ret_data, null);
							}
						});
			}
		});
		
		handler_map.put(LLSipCmds.CMD_ADD_DOOR_SAFE, new SmartDeviceHandler() {

			@Override
			public void onHandler(LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io, Object context) {
				String userId = (String) context;
				LLDoorSafeNetController.getInstance().operateUser(userId,true, new IoCompletionListener1<String>() {
							@Override
							public void onFinish(String data, Object context) {
								int errorCode = (Integer)context ;
								LLSmartDoorRetResponse ret = new LLSmartDoorRetResponse();
								ret.setLLSmartDoorRetResponse(errorCode, CMD_ADD_DOOR_SAFE, "", "");
								io.onFinish((IDeviceData)ret, null);
							}
						});
			}
		});
		
		handler_map.put(LLSipCmds.CMD_DEL_DOOR_SAFE, new SmartDeviceHandler() {

			@Override
			public void onHandler(LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io, Object context) {
				
				String userId = (String) context;
				LLDoorSafeNetController.getInstance().operateUser(userId,false, new IoCompletionListener1<String>() {
					@Override
					public void onFinish(String data, Object context) {
						int errorCode = (Integer)context ;
						LLSmartDoorRetResponse ret = new LLSmartDoorRetResponse();
						ret.setLLSmartDoorRetResponse(errorCode, CMD_DEL_DOOR_SAFE, "", "");
						io.onFinish((IDeviceData)ret, null);
					}
				});
				
			}

		});
		
		handler_map.put(LLSipCmds.CMD_AIRCONDITION_CONTROL, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				LLSmartAir llSmartAir = (LLSmartAir) LLGsonUtils.getInstance().fromJson(cmd.getMessage(), LLSmartAir.class);
				LLSmartBoxCentersFactory.createSmartBox().airConditionOn(llSmartAir.getTemp(), llSmartAir.getWindLevl(),
						llSmartAir.getModel(), llSmartAir.getSign(), new IoCompletionListener1<IDeviceData>() {
							@Override
							public void onFinish(IDeviceData data, Object context) {
								LLSmartAir llSmartAirData = (LLSmartAir) data ;
								int errorCode = (Integer)context ;
								LLSmartAirRetResponse ret = new LLSmartAirRetResponse() ;
								ret.setLLSmartAirRetResponse(CMD_AIRCONDITION_CONTROL, errorCode,llSmartAirData.getTemp() , 
										llSmartAirData.getWindLevl(), llSmartAirData.getModel(), llSmartAirData.getSign());
								io.onFinish((IDeviceData)ret, null); 
							}
						});
			}
		});
		
		handler_map.put(LLSipCmds.CMD_GET_AIRCONDITION_STATE, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartAir llSmartAir = LLSmartBoxCentersFactory.createSmartBox().getAirConditionData();
				LLSDKUtils.runInMainThread(new Runnable() {
					public void run() {
					LLSmartAirRetResponse ret = new LLSmartAirRetResponse() ;
					ret.setLLSmartAirRetResponse(CMD_GET_AIRCONDITION_STATE, SUCCESS_TAG, llSmartAir.getTemp(),
							llSmartAir.getWindLevl(), llSmartAir.getModel(), llSmartAir.getSign());
					io.onFinish((IDeviceData)ret, null); 
					}
				});
			}
		});
		handler_map.put(LLSipCmds.CMD_CURTAIN_CONTROL, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartCurtain llSmartCurtain = (LLSmartCurtain) LLGsonUtils.getInstance().fromJson(cmd.getMessage(), LLSmartCurtain.class);
				LLSmartBoxCentersFactory.createSmartBox().curtainOn(llSmartCurtain.getSign(),
						new IoCompletionListener1<IDeviceData>() {
							@Override
							public void onFinish(IDeviceData data, Object context) {
								int  errorCode  = (Integer) context;
								LLSmartCurtainRetResponse ret = new LLSmartCurtainRetResponse() ;
								ret.setLLSmartCurtainReturnResponse(errorCode, CMD_CURTAIN_CONTROL, llSmartCurtain.getSign());
								io.onFinish((IDeviceData) ret, null);
							}
						});
			}
		});

		handler_map.put(LLSipCmds.CMD_CURTAIN_STATE, new SmartDeviceHandler() {

			@Override
			public void onHandler(LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartCurtain data = LLSmartBoxCentersFactory.createSmartBox().getCurtainData();
				LLSDKUtils.runInMainThread(new Runnable() {
					public void run() {
						LLSmartCurtainRetResponse ret = new LLSmartCurtainRetResponse() ;
						ret.setLLSmartCurtainReturnResponse(SUCCESS_TAG, CMD_CURTAIN_STATE, data.getSign());
						io.onFinish((IDeviceData) ret, null);
					}
				});
			}
		});
		
		handler_map.put(LLSipCmds.CMD_DOOR_CONTROL, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				LLSmartDoor llSmartDoor = (LLSmartDoor) LLGsonUtils.getInstance().fromJson(cmd.getMessage(), LLSmartDoor.class);
				LLSmartBoxCentersFactory.createSmartBox().openDoor(llSmartDoor.getPwd(),
						new IoCompletionListener1<IDeviceData>() {

							@Override
							public void onFinish(IDeviceData data, Object context) {
								int  errorCode  = (Integer) context;
								LLSmartDoorRetResponse  ret = new  LLSmartDoorRetResponse() ;
								ret.setLLSmartDoorRetResponse(errorCode, CMD_DOOR_CONTROL, "", ((LLSmartDoor)data).getData());
								io.onFinish((IDeviceData) ret, null);
							}
						});
			}
		});
		
		handler_map.put(LLSipCmds.CMD_DOOR_OPEN, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				LLSmartDoor llSmartDoor = (LLSmartDoor) LLGsonUtils.getInstance().fromJson(cmd.getMessage(), LLSmartDoor.class);
				LLSmartDoorRetResponse  ret = new  LLSmartDoorRetResponse() ;
				if (TextUtils.equals(llSmartDoor.getPwd(), LLDoorLockPasController.getInstance().getDoorLockPas())) {
					SerialPosClient.instance().sendDataViaCom(CmdRetKits.makeOpenDoorCmd(true));
					ret.setLLSmartDoorRetResponse(SUCCESS_TAG, CMD_DOOR_OPEN, "", "");
					io.onFinish((IDeviceData) ret, null);
				}else{
					ret.setLLSmartDoorRetResponse(INVALID_TAG, CMD_DOOR_OPEN, "", "");
					io.onFinish((IDeviceData) ret, null);
				}
			}
		});
		
		handler_map.put(LLSipCmds.CMD_DOOR_SETPWD, new SmartDeviceHandler() {

			@Override
			public void onHandler(final LLSipMsgDevicesCmds cmd, final IoCompletionListener1<IDeviceData> io,
					Object context) {
				final LLSmartDoor llSmartDoor = (LLSmartDoor) LLGsonUtils.getInstance().fromJson(cmd.getMessage(), LLSmartDoor.class);
				if (!TextUtils.equals(llSmartDoor.getPwd(), LLDoorLockNetController.getInstance().getPassword())) {
					LLDoorLockNetController.getInstance().setPassword(llSmartDoor.getPwd(),
							new IoCompletionListener1<String>() {

								@Override
								public void onFinish(String data, Object context) {
									int  errorCode  = (Integer) context;
									LLSmartDoorRetResponse  ret = new  LLSmartDoorRetResponse() ;
									ret.setLLSmartDoorRetResponse(errorCode, CMD_DOOR_SETPWD, "", "");
									io.onFinish((IDeviceData) ret, null);
								}
							});
				}

			}
		});
	}

	public static void onHandleSmartDeviceCmd(LLSipMsgDevicesCmds cmd, IoCompletionListener1<IDeviceData> io,
			Object context) {
		SmartDeviceHandler handler = handler_map.get(cmd.getOp());
		if (handler != null) {
			handler.onHandler(cmd, io, context);
		}
	}
}
