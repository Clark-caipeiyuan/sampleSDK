package com.xixi.sdk.device;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.handler.LLHandlerOverSmartDevice;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.sipmsg.params.LLSipMsgDevicesCmds;
import com.xixi.sdk.sipmsg.params.LLSmartAir;
import com.xixi.sdk.sipmsg.params.LLSmartCurtain;
import com.xixi.sdk.sipmsg.params.LLSmartDev;
import com.xixi.sdk.sipmsg.params.LLSmartDoor;
import com.xixi.sdk.sipmsg.params.LLSmartEnvir;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLFileUtils;
import com.xixi.sdk.utils.housekeeper.AirAlgorithm;
import com.xixi.sdk.utils.housekeeper.ByteUtils;
import com.xixi.sdk.utils.mem.LLBufferDescription;
import com.xixi.sdk.utils.network.IISocketUtils;
import com.xixi.sdk.utils.network.IoCompletionListener2;
import com.xixi.sdk.utils.network.LLAirconditionCodeNetController;
import com.xixi.sdk.utils.network.LLCallback;
import com.xixi.sdk.utils.network.LLDoorLockNetController;
import com.xixi.sdk.utils.network.LLDoorSafeNetController;
import com.xixi.sdk.utils.network.LLLongConnectionSocket;
import com.xixi.sdk.utils.network.LLSmartBoxImmediateConnectionRequest;
import com.xixi.sdk.utils.network.LLSmartBoxRequestDataEx;
import com.xixi.sdk.utils.network.LLSmartConnectionRequest;
import com.xixi.sdk.utils.network.LLSocketClient;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLViliamSmartBoxCenter extends LLSmartBoxCenter implements Runnable, LLSipCmds {

    private final IoCompletionListener1<LLSmartConnectionRequest> default_tcpIoComp = new IoCompletionListener1<LLSmartConnectionRequest>() {
        @Override
        public void onFinish(LLSmartConnectionRequest data, Object context) {
        }
    };

    final protected LLightSwitcher _switcher = new LLightSwitcher();

    private final IoCompletionListener1<LLSmartEnvir> default_enviromentListener = new IoCompletionListener1<LLSmartEnvir>() {

        @Override
        public void onFinish(LLSmartEnvir data, Object context) {

        }
    };

    private WeakReference<IoCompletionListener1<LLSmartEnvir>> _envirmentParamsListener = new WeakReference<IoCompletionListener1<LLSmartEnvir>>(
            default_enviromentListener);

    IoCompletionListener1<String> alarmSender = null;

    public IoCompletionListener1<String> getWarningDelegate() {
        return alarmSender;
    }

    public void setWarningDelegate(IoCompletionListener1<String> warningDelegate) {
        this.alarmSender = warningDelegate;
    }

    boolean watchDogStatus = false;

    protected boolean getDogStatus() {
        return watchDogStatus;
    }

    public void setLeftHome(boolean bLeaveHome, final IoCompletionListener1<IDeviceData> io) {

        super.setLeftHome(bLeaveHome, io);
        enableWatchDog(bLeaveHome, LLDevPortMap.DOOR_WATCH_DOG, io);
    }

    final Runnable getTempAndAirData = new Runnable() {
        public void run() {
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.LIGHT_SWITCHER_1);
            client.sendData(new LLSmartBoxRequestDataEx(PRIMARY_INFO, true, 0xff, new IoCompletionListener2() {
                @Override
                public void onNetworkIoCompletion(LLSmartConnectionRequest request, LLBufferDescription bd) {

                }

                @Override
                public void onFinishEx(final LLSmartConnectionRequest data, final LLBufferDescription bd) {
                    data.reset();
                    try {
                        if (!bd.isValid()) {
                            UIThreadDispatcher.dispatch(getTempAndAirData, 5000);
                        } else {
                            environment_params.clear();
                            for (String node : bd.getArray()) {
                                int value = (int) LLSDKUtils.convertToInt(node.trim());
                                environment_params.add(value);
                            }
                            _envirmentParamsListener.get().onFinish(getEnvironmentData(), null);
                            UIThreadDispatcher.dispatch(getTempAndAirData, 180000);
                        }
                    } catch (Exception e) {
                        Log.d(LongLakeApplication.DANIEL_TAG, "LGL:" + android.util.Log.getStackTraceString(e));
                    }
                }
            }) {
                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    LLBufferDescription bd = (LLBufferDescription) context;

                    return bd.isPrimaryInfoRet();
                }
            }, null);
        }
    };

    final Runnable keepConnectNet = new Runnable() {
        public void run() {
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.LIGHT_SWITCHER_1);
            client.sendData(new LLSmartBoxRequestDataEx(String.format("<ADVHT,%s,%s\r\n", PHONENUM, houseKeeperId), true, 0xff, new IoCompletionListener2() {
                @Override
                public void onNetworkIoCompletion(LLSmartConnectionRequest request, LLBufferDescription bd) {

                }

                @Override
                public void onFinishEx(final LLSmartConnectionRequest data, final LLBufferDescription bd) {
                    try {
                        UIThreadDispatcher.dispatch(keepConnectNet, 30000);
                    } catch (Exception e) {
                        Log.d(LongLakeApplication.DANIEL_TAG, "LGL:" + android.util.Log.getStackTraceString(e));
                    }
                }
            }) {
                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    LLBufferDescription bd = (LLBufferDescription) context;
                    return bd.isConnectNetRet();
                }
            }, null);
        }
    };

    private final static String HouseKeeperId = "HouseKeeperId";

    private void initHouseKeeperId() {
        final String deviceName = LLSdkGlobals.getUserName();
        LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_HOUSEKEEPERID,
                LLSdkGlobals.getMessagePushUrl(), deviceName, new LLCallback<ResponseBody>() {

            @Override
            public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
                Log.d(LongLakeApplication.DANIEL_TAG, "get houseKeeperId success  :" + payload);
                try {
                    if (payload.trim() != null) {
                        onGetHouseKeepInfoReady(payload);
                    }
                } catch (Exception e) {
                    initHouseKeeperId();
                    Log.d(LongLakeApplication.DANIEL_TAG, "get houseKeeperId exception  :" + payload);
                }
            }

            @Override
            public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
                Log.d(LongLakeApplication.DANIEL_TAG,
                        "get houseKeeperId  fail : " + android.util.Log.getStackTraceString(arg1));
                LLFileUtils.getInstance().readFromFile(HouseKeeperId, new IoCompletionListener1<String>() {
                    @Override
                    public void onFinish(String data, Object context) {
                        if (!data.isEmpty()) {
                            onGetHouseKeepInfoReady(data);
                        } else {
                            initHouseKeeperId();
                        }
                    }
                });
            }
        }));
    }

    public void onGetHouseKeepInfoReady(String infomation){
        String[]  houseKeeperInfo = infomation.split(",");
        setHouseKeeperIdPhoneAndPwd(houseKeeperInfo[0].trim(), houseKeeperInfo[1].trim(), houseKeeperInfo[2].trim());
        LOGIN_TEXT = String.format(">LOGN,\"%s\",\"%s\"\r\n", PHONENUM, PWD);
        PRIMARY_INFO = String.format(">FNAA,\"%s\"\r\n", PHONENUM);
        FETCH_LAYOUT_INFO = String.format(">FROM,\"%s\"\n", PHONENUM);
        FETCH_LAYOUT_DETAILS = String.format(">FNSA,\"%s\"\n", PHONENUM);
        startupSmartBox(null);
        startPollingOnUdpPort();
        UIThreadDispatcher.dispatch(getTempAndAirData, 5000);
        UIThreadDispatcher.dispatch(keepConnectNet, 5000);
        LLFileUtils.getInstance().writeToFile(HouseKeeperId, infomation);
        LLDoorLockNetController.getInstance().setMenu_ID(houseKeeperId);
        LLDoorSafeNetController.getInstance().setHouseKeeperId(houseKeeperId);
        LLDoorSafeNetController.getInstance().getAllUsers();
        LLDoorLockNetController.getInstance().initGetLockPwd();
    }

    protected LLViliamSmartBoxCenter() {
//        LLDevPortMap.getInstance();
////		LLFileUtils.getInstance().writeToFile(HouseKeeperId, houseKeeperId);
////		LLDoorLockNetController.getInstance().setMenu_ID(houseKeeperId);
////		LLDoorSafeNetController.getInstance().setHouseKeeperId(houseKeeperId);
////		LLDoorSafeNetController.getInstance().getAllUsers();
////		LLDoorLockNetController.getInstance().initGetLockPwd();
//        initHouseKeeperId();
//        LLFileUtils.getInstance().readFromFile(AIRCONDITON_DATA, new IoCompletionListener1<String>() {
//
//            @Override
//            public void onFinish(String airconditionData, Object context) {
//                if (!airconditionData.isEmpty()) {
//                    llSmartAir = (LLSmartAir) LLGsonUtils.fromJson(airconditionData, LLSmartAir.class);
//                } else {
//                    llSmartAir.setSignAndTemp(25, 0, 2, 0);
//                }
//            }
//        });
//
//        _generalRoutineIo = new LLSmartBoxRequestDataEx(null, true, 0xff,
//                new IoCompletionListener1<LLSmartConnectionRequest>() {
//                    @Override
//                    public void onFinish(LLSmartConnectionRequest data, final Object context) {
//                        final LLBufferDescription bd = (LLBufferDescription) context;
//                        if (bd.isAlarmFired()) {
//                            LLSDKUtils.runInMainThread(new Runnable() {
//                                public void run() {
//                                    alarmSender.onFinish(null, bd.getArray());
//                                }
//                            });
//                        } else if (bd.isLightStatus()) {
//                            final int status = bd.getLightsStatus();
//                            LLSDKUtils.runInMainThread(new Runnable() {
//                                public void run() {
//                                    _switcher.setSwitcherBits(status);
//                                    _switcher.synchSwitcherBits();
//                                    _notify[LIGHT_CODE].notifyOb(new Object[]{});
//                                    // devStatusNotifier.notifyOb(new
//                                    // Object[]{});
//                                }
//                            });
//                        }
//                        // else if (bd.isPrimaryInfoRet()) {
//                        // for (String node : bd.getArray()) {
//                        // int value = (int) LLSDKUtils.convertToInt(node.trim());
//                        // environment_params.add(value);
//                        // _envirmentParamsListener.get().onFinish(getEnvironmentData(), null);
//                        // }
//                        // }
//                        _generalRoutineIo.reset();
//
//                    }
//                }) {
//
//            public boolean isInterestedPck(final LLSmartConnectionRequest data, final Object context) {
//                LLBufferDescription bd = (LLBufferDescription) context;
//                return bd.isAlarmFired() || bd.isLightStatus();
//            }
//        };
//
//        _generalRoutineIo.setEnableTimeout(0);
//        LLLongConnectionSocket.getInstance().registerAsObserver(_generalRoutineIo, true);
    }

    static LLViliamSmartBoxCenter instance;

    public static synchronized LLViliamSmartBoxCenter getInstance() {
        if (instance == null) {
            instance = new LLViliamSmartBoxCenter();
        }
        return instance;
    }

    String houseKeeperId = "7495035350177134";

    public String getHouseKeeperId() {
        return houseKeeperId;
    }

    public void setHouseKeeperIdPhoneAndPwd(String houseKeeperId, String PHONENUM, String PWD) {
        this.houseKeeperId = houseKeeperId;
        this.PHONENUM = PHONENUM;
        this.PWD = PWD;
    }

    @Override
    public void switchOn(boolean on, String devName, final IoCompletionListener1<IDeviceData> io) {
        final int sign = (on ? 1 : 0);
        final int position = Integer.parseInt(DevName_posion.get(devName));
        if (_switcher.isBusy(position)) {
            Log.i(LongLakeApplication.DANIEL_TAG, "busy op");
            io.onFinish(null, Integer.valueOf(BUSY_OP_TAG));
        } else if (sign != _switcher.getSwitch_bit(position)) {
            _switcher.setBusyBit(position, true);
            String request = String.format("%s,\"D%s\",\"%s\"\r\n", String.format("%s,\"%s\",\"%s\"", ">FNSC", PHONENUM, "room1"),
                    LLDevPortMap.LIGHT_SWITCHER_1, _switcher.generateReqStr(position, sign));
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.LIGHT_SWITCHER_1);

            final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                    new IoCompletionListener1<LLSmartBoxRequestDataEx>() {
                        @Override
                        public void onFinish(final LLSmartBoxRequestDataEx data, Object context) {
                            LLBufferDescription bd = (LLBufferDescription) context;
                            client.unregister(data);
                            _switcher.setBusyBit(position, false);
                            if (!bd.isValid()) {
                                _switcher.synchSwitcherBits();
                                io.onFinish(null, Integer.valueOf(INVALID_TAG));
                            } else {
                                boolean succeed = bd.succeedsInTurningOnLight();
                                if (succeed) {
                                    _switcher.setSwitch_one_bit(position, sign);
                                    _switcher.synchSwitcherBits();
                                    _notify[LIGHT_CODE].notifyOb(new Object[]{});
                                } else {
                                    _switcher.synchSwitcherBits();
                                }
                                io.onFinish(null, Integer.valueOf(succeed ? SUCCESS_TAG : INVALID_TAG));
                            }

                        }
                    }) {

                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    LLBufferDescription bd = (LLBufferDescription) context;
                    return bd.isLightResponse();
                }

            };
            client.sendData(rd, null);
        } else {
            Log.i(LongLakeApplication.DANIEL_TAG, "light status identical");
            io.onFinish(null, Integer.valueOf(0));
        }
    }

    boolean isAirConditionBusy = false;
    private final static String AIRCONDITON_DATA = "AIRCONDITON_DATA";

    public void airConditionNewOrder(final int airconditionNum, final int actualTemp, final int setTemp, final int windLevl, final int model, final int sign,
                                     final IoCompletionListener1<IDeviceData> io) {
        if (isAirConditionBusy) {
            io.onFinish(llSmartAir, Integer.valueOf(BUSY_OP_TAG));
        } else {
            isAirConditionBusy = true;
            String request = String.format(">CAIR,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    PHONENUM, airconditionNum, sign, actualTemp, setTemp, windLevl, model);
            android.util.Log.i(LongLakeApplication.DANIEL_TAG, "request_cmd" + request);
            android.util.Log.i(LongLakeApplication.DANIEL_TAG, "sending data");
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.AIR_CONDITION);
            final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                    new IoCompletionListener1<LLSmartBoxRequestDataEx>() {
                        @Override
                        public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                            LLBufferDescription bd = (LLBufferDescription) context;
                            client.unregister(data);
                            isAirConditionBusy = false;
                            if (!bd.isValid()) {
                                io.onFinish(llSmartAir, Integer.valueOf(INVALID_TAG));
                            } else {
                                if (bd.isNewAirConditionRet()) {
                                    if (bd.succeedsInControlNewAirCondition()) {
                                        LLFileUtils.getInstance().writeToFile(AIRCONDITON_DATA,
                                                LLGsonUtils.getInstance().toJson(llSmartAir));
                                        llSmartAir.setSignAndTemp(setTemp, windLevl, model, sign);
                                        _notify[AIR_CODE].notifyOb(new Object[]{llSmartAir});
                                    }
                                }
                                io.onFinish(llSmartAir, Integer
                                        .valueOf(bd.succeedsInControlNewAirCondition() ? SUCCESS_TAG : INVALID_TAG));
                            }
                        }
                    }) {

                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    return ((LLBufferDescription) context).isNewAirConditionRet();
                }

            };
            client.sendData(rd, null);
        }
    }


    public void getAirConditon(String airconditionNum, final IoCompletionListener1<IDeviceData> io) {
        String request = String.format("%s,\"%s\",\"%s\"\n",
                ">QAIR", PHONENUM, airconditionNum);
        android.util.Log.d(LongLakeApplication.DANIEL_TAG, "LGL_cmd" + request);
        final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.AIR_CONDITION);
        final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                new IoCompletionListener1<LLSmartBoxRequestDataEx>() {
                    @Override
                    public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                        LLBufferDescription bd = (LLBufferDescription) context;
                        client.unregister(data);
                        LLSmartAir getAir = new LLSmartAir();
                        if (!bd.isValid()) {
                            io.onFinish(getAir, Integer.valueOf(INVALID_TAG));
                            android.util.Log.d(LongLakeApplication.DANIEL_TAG, "LGL_air_1:" + getAir.getSign() + getAir.getTemp());
                        } else {
                            if (bd.isGetAirConditionRet()) {
                                getAir.setSign(Integer.parseInt(bd.getArray().get(3).trim()));
                                getAir.setTemp(Integer.parseInt(bd.getArray().get(4).trim()));
                                android.util.Log.d(LongLakeApplication.DANIEL_TAG, "LGL_air_2:" + getAir.getSign() + getAir.getTemp());
                            }
                            android.util.Log.d(LongLakeApplication.DANIEL_TAG, "LGL_air_3:" + getAir.getSign() + getAir.getTemp());
                            io.onFinish(getAir, Integer
                                    .valueOf(bd.isGetAirConditionRet() ? SUCCESS_TAG : INVALID_TAG));
                        }
                    }
                }) {

            @Override
            public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                return ((LLBufferDescription) context).isGetAirConditionRet();
            }

        };
        client.sendData(rd, null);
    }


    @Override
    public void airConditionOn(final int temp, final int windLevl, final int model, final int sign,
                               final IoCompletionListener1<IDeviceData> io) {
        if (isAirConditionBusy) {
            io.onFinish(llSmartAir, Integer.valueOf(BUSY_OP_TAG));
        } else {
            isAirConditionBusy = true;
            int[] buf = AirAlgorithm.SearchKeyData(LLAirconditionCodeNetController.getInstance().getAirconditionCode(),
                    temp, windLevl, sign, model);
            String request = String.format(">RECI,\"%s\",\"%s\"\n", PHONENUM, ByteUtils.getStringHWM(buf));
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.AIR_CONDITION);
            final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                    new IoCompletionListener1<LLSmartBoxRequestDataEx>() {
                        @Override
                        public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                            LLBufferDescription bd = (LLBufferDescription) context;
                            client.unregister(data);
                            isAirConditionBusy = false;
                            if (!bd.isValid()) {
                                io.onFinish(llSmartAir, Integer.valueOf(INVALID_TAG));
                            } else {
                                if (bd.isAirConditionRet()) {
                                    if (bd.succeedsInControlAirCondition()) {
                                        LLFileUtils.getInstance().writeToFile(AIRCONDITON_DATA,
                                                LLGsonUtils.getInstance().toJson(llSmartAir));
                                        llSmartAir.setSignAndTemp(temp, windLevl, model, sign);
                                        _notify[AIR_CODE].notifyOb(new Object[]{llSmartAir});
                                    }
                                }
                                io.onFinish(llSmartAir, Integer
                                        .valueOf(bd.succeedsInControlAirCondition() ? SUCCESS_TAG : INVALID_TAG));
                            }
                        }
                    }) {

                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    return ((LLBufferDescription) context).isAirConditionRet();
                }

            };
            client.sendData(rd, null);
        }
    }

    boolean isCurtainBusy = false;

    public void curtainOnWithName(final int sign, final String roomName, final String curtainNameWithType, final IoCompletionListener1<IDeviceData> io) {

        if (isCurtainBusy) {
            io.onFinish(llSmartCurtain, Integer.valueOf(BUSY_OP_TAG));
        } else {

            String request = String.format(">FNSC,\"%s\",\"%s\",\"%s\",\"0%d02\"\r\n", PHONENUM, roomName, curtainNameWithType, sign);
            if (sign != llSmartCurtain.getSign()) {
                isCurtainBusy = true;
                final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.CURTAINER);
                final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                        new IoCompletionListener1<LLSmartBoxRequestDataEx>() {

                            @Override
                            public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                                LLBufferDescription bd = (LLBufferDescription) context;
                                client.unregister(data);
                                isCurtainBusy = false;
                                if (!bd.isValid()) {
                                    io.onFinish(llSmartCurtain, Integer.valueOf(INVALID_TAG));

                                } else {
                                    if (bd.isCurtainRet()) {
                                        if (bd.succeedsInControlCurtain()) {
                                            llSmartCurtain.setSign(sign);
                                            _notify[CURTAIN_CODE].notifyOb(new Object[]{llSmartCurtain});
                                        }
                                    }
                                    io.onFinish(llSmartCurtain,
                                            Integer.valueOf(bd.succeedsInControlCurtain() ? SUCCESS_TAG : INVALID_TAG));
                                }

                            }
                        }) {

                    @Override
                    public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                        return ((LLBufferDescription) context).isCurtainRet();
                    }

                };
                client.sendData(rd, null);
            } else {
                io.onFinish(llSmartCurtain, Integer.valueOf(SUCCESS_TAG));
            }
        }
    }


    @Override
    public void curtainOn(final int sign, final IoCompletionListener1<IDeviceData> io) {
        if (isCurtainBusy) {
            io.onFinish(llSmartCurtain, Integer.valueOf(BUSY_OP_TAG));
        } else {

            String request = String.format(">FNSC,\"%s\",\"room1\",\"Eblind\",\"0%d02\"\r\n", PHONENUM, sign);
            if (sign != llSmartCurtain.getSign()) {
                isCurtainBusy = true;
                final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.CURTAINER);
                final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                        new IoCompletionListener1<LLSmartBoxRequestDataEx>() {

                            @Override
                            public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                                LLBufferDescription bd = (LLBufferDescription) context;
                                client.unregister(data);
                                isCurtainBusy = false;
                                if (!bd.isValid()) {
                                    io.onFinish(llSmartCurtain, Integer.valueOf(INVALID_TAG));

                                } else {
                                    if (bd.isCurtainRet()) {
                                        if (bd.succeedsInControlCurtain()) {
                                            llSmartCurtain.setSign(sign);
                                            _notify[CURTAIN_CODE].notifyOb(new Object[]{llSmartCurtain});
                                        }
                                    }
                                    io.onFinish(llSmartCurtain,
                                            Integer.valueOf(bd.succeedsInControlCurtain() ? SUCCESS_TAG : INVALID_TAG));
                                }

                            }
                        }) {

                    @Override
                    public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                        return ((LLBufferDescription) context).isCurtainRet();
                    }

                };
                client.sendData(rd, null);
            } else {
                io.onFinish(llSmartCurtain, Integer.valueOf(SUCCESS_TAG));
            }
        }
    }

    @Override
    public void stepOn(float flStatus, String devName) {

    }

    @Override
    public void onArrivalOfData(byte[] data) {

    }

    static String PHONENUM = "18691493660";
    String PWD = "123456";
    private final static HashMap<String, String> DevName_posion;

    static {

        DevName_posion = new HashMap<String, String>();

        final String[] _lightIds = new String[]{"1587657524438178_1", "1587657524438178_2", "1587657524438178_3",
                "1587657524438178_4"};

        int i = 0;
        for (String node : new String[]{"0", "1", "2", "3"}) {
            DevName_posion.put(_lightIds[i], node);
            i++;
        }
    }

    private String LOGIN_TEXT = String.format(">LOGN,\"%s\",\"%s\"\r\n", PHONENUM, PWD);

    private String PRIMARY_INFO = String.format(">FNAA,\"%s\"\r\n", PHONENUM);

    private String FETCH_LAYOUT_INFO = String.format(">FROM,\"%s\"\n", PHONENUM);

    private String FETCH_LAYOUT_DETAILS = String.format(">FNSA,\"%s\"\n", PHONENUM);

    private final String OPEN_DOOR = String.format(">OPEN,\"%S\"\n", PHONENUM);

    final LLSmartConnectionRequest const_initialization_request_pck_with_long_connection[] = new LLSmartBoxRequestDataEx[]{
            new LLSmartBoxRequestDataEx(PRIMARY_INFO, true, 0xff, new IoCompletionListener2() {
                @Override
                public void onNetworkIoCompletion(LLSmartConnectionRequest request, LLBufferDescription bd) {

                }

                @Override
                public void onFinishEx(LLSmartConnectionRequest data, LLBufferDescription context) {
                    if (!context.isValid()) {
                        data.reset();
                        LLLongConnectionSocket.getInstance().sendData(data, null);
                    } else {
                        LLLongConnectionSocket.getInstance().sendData(const_initialization_request_pck_with_long_connection[1], null);
                    }
                }
            }) {

                @Override
                public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                    LLBufferDescription bd = (LLBufferDescription) context;

                    return bd.isPrimaryInfoRet();
                }
            }, new LLSmartBoxRequestDataEx(FETCH_LAYOUT_DETAILS, true, 0xff, new IoCompletionListener2() {
        @Override
        public void onNetworkIoCompletion(LLSmartConnectionRequest request, LLBufferDescription bd) {
            if (bd.isDevicesStatesRet() && bd.getCurtainState() != 3) {
                llSmartCurtain.setSign(bd.getCurtainState());
            }
        }

        @Override
        public void onFinishEx(LLSmartConnectionRequest data, LLBufferDescription context) {
            if (!context.isValid()) {
                data.reset();
                LLLongConnectionSocket.getInstance().sendData(data, null);
            } else {
                LLSmartBoxCentersFactory.createSmartBox().enableWatchDog(true, LLDevPortMap.DOOR_WATCH_DOG,
                        new IoCompletionListener1<IDeviceData>() {
                            @Override
                            public void onFinish(IDeviceData data, Object context) {
                                Integer i = (Integer) context;
                                if (i != INVALID_TAG) {
                                    Log.i(LongLakeApplication.DANIEL_TAG, "enable wd");
                                } else {
                                    Log.i(LongLakeApplication.DANIEL_TAG, "failed to enable wd");
                                }
                            }
                        });
            }
        }
    }) {

        @Override
        public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
            LLBufferDescription bd = (LLBufferDescription) context;
            return bd.has("<FNSA");
        }
    }};

    final LLSmartConnectionRequest const_initialization_request_pck[] = new LLSmartBoxImmediateConnectionRequest[]{
            new LLSmartBoxImmediateConnectionRequest(LOGIN_TEXT, false, 0, default_tcpIoComp)
            // new LLSmartBoxImmediateConnectionRequest(houseKeeperId
            // +PRIMARY_INFO, false, 0, default_tcpIoComp)
            // new LLSmartBoxImmediateConnectionRequest(PRIMARY_INFO, true,
            // 0xff,
            // new IoCompletionListener1<LLSmartConnectionRequest>() {
            // @Override
            // public void onFinish(LLSmartConnectionRequest request, Object
            // context) {
            //
            // String str[] = request.getResults();
            // if (str.length >= 6) {
            // for (String node : str) {
            // int value = (int) LLSDKUtils.convertToInt(node.trim());
            // Log.i(LongLakeApplication.DANIEL_TAG, "rv " +
            // String.valueOf(value));
            // environment_params.add(value);
            // }
            // LLSDKUtils.runInMainThread(new Runnable() {
            // public void run() {
            // _envirmentParamsListener.get().onFinish(getEnvironmentData(),
            // null);
            //
            // }
            // });
            // }
            // }
            // }),
            //
            // (LLSmartBoxImmediateConnectionRequest) new
            // LLSmartBoxImmediateConnectionRequest(FETCH_LAYOUT_INFO, true,
            // 0xff, new IoCompletionListener1<LLSmartConnectionRequest>() {
            //
            // @Override
            // public void onFinish(LLSmartConnectionRequest request, Object
            // context) {
            // parsePrimaryInfo(request.getResponse_data());
            //
            // }
            // }).enableEndingTagCheck(0xa),
            //
            // (LLSmartBoxImmediateConnectionRequest) new
            // LLSmartBoxImmediateConnectionRequest(FETCH_LAYOUT_DETAILS, true,
            // 0xff, new IoCompletionListener1<LLSmartConnectionRequest>() {
            // @Override
            // public void onFinish(LLSmartConnectionRequest request, Object
            // context) {
            // parsePrimaryInfo(request.getResponse_data());
            // }
            // }).enableEndingTagCheck(0xa)

    };

    String boxIp = "123.57.2.251";

    public String getBoxIp() {
        return boxIp;
    }

    private void initializationLongConnection() {
        LLLongConnectionSocket.getInstance().sendData(const_initialization_request_pck_with_long_connection[0], null);
    }

    private final IoCompletionListener1<String> _internal_onCompletionOfScanningDev = new IoCompletionListener1<String>() {

        @Override
        public void onFinish(String ip, Object context) {
            try {
                dispatchBeacon();
                Log.i(LongLakeApplication.DANIEL_TAG, "box " + ip);
                if (TextUtils.equals(ip, boxIp)) {
                    return;
                }
                // boxIp = ip;
                if (boxIp != null) {

                    TcpIoCompletionListener1 cl = new TcpIoCompletionListener1() {
                        @Override
                        public void onTcpFinish(LLSmartConnectionRequest data, Object context) {
                        }
                    };
                    cl.executeLinks(0);
                }
            } catch (Exception e) {
                LLSocketClient.rvInstance();
            }
        }
    };

    private abstract class TcpIoCompletionListener1 implements IoCompletionListener1<LLSmartConnectionRequest> {

        int _index = 0;

        public abstract void onTcpFinish(final LLSmartConnectionRequest data, final Object context);

        public void executeLinks(int index) {
            final LLSocketClient client = LLSocketClient.getInstance();
            if (index < const_initialization_request_pck.length) {
                client.sendData(const_initialization_request_pck[index], this);
            } else {

                LLSDKUtils.runInMainThread(new Runnable() {
                    public void run() {
                        Log.d(LongLakeApplication.DANIEL_TAG, "get information form xcap tag:" + PHONENUM);
                        LLDoorSafeNetController.getInstance().getAllUsers();
                        LLDoorLockNetController.getInstance().initGetLockPwd();
                        // LLAirconditionCodeNetController.getInstance().getAirconditionCodeFromXcap(null);
                    }
                });

                initializationLongConnection();
            }
        }

        public void onFinish(LLSmartConnectionRequest data, Object context) {
            //
            // final LLSocketClient client = LLSocketClient.getInstance();
            // LLSDKUtils.danielAssert(Looper.myLooper().equals(mReceiveHandler.getLooper()));
            Integer size = (Integer) context;
            if (size == INVALID_TAG) {
                // boxIp = null;
                Log.d(LongLakeApplication.DANIEL_TAG, "failed to connect box , reset myself and retry");
                _index = 0;
            } else {
                data.getIoCompletion().onFinish(data, context);
                _index++;
                executeLinks(_index);
            }
        }
    }

    @Override
    public boolean startupSmartBox(IoCompletionListener1<EnvironmentParams> io) {
        android.util.Log.d(LongLakeApplication.DANIEL_TAG, "get info from xcap to login" + PHONENUM);
        if (pollingOnReadyOfSmartBox == null) {
            mReceiveThread = new HandlerThread("scanning smartBox");
            mReceiveThread.start();
            mReceiveHandler = new Handler(mReceiveThread.getLooper());
            // dispatchBeacon();
            // pollingOnReadyOfSmartBox = new Thread(this);
            // pollingOnReadyOfSmartBox.start();
            Log.d(LongLakeApplication.DANIEL_TAG, "get information form xcap tag");
            final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.LIGHT_SWITCHER_1);
            client.sendData(new LLSmartBoxImmediateConnectionRequest(LOGIN_TEXT, false, 0, default_tcpIoComp), null);
        }
        return false;
    }

    Thread pollingOnReadyOfSmartBox;
    Handler mReceiveHandler;
    HandlerThread mReceiveThread;

    DatagramSocket mScanningSocket;

    @Override
    public void shutDownSmartBox() {

    }

    public final static int TCP_PORT = 9292;

    public final static int getServicePort() {
        return TCP_PORT;
    }

    private final static int UDP_PORT = 1025;

    private final static String QUERY_STRING = "Are You AirM2M IOT Smart Device?";
    private final static String BC_ADDR = "255.255.255.255";

    // final Semaphore mSynch = new Semaphore(1);
    final byte[] mReceiveBuffer = new byte[64];

    private final static String PREFIX_KEY = "I am ";

    private final boolean verifyIsFromBox(String targetStr) {
        return targetStr.startsWith(PREFIX_KEY) && !targetStr.contains("0.0.0.0");
    }

    final Runnable reset_beacon_runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(LongLakeApplication.DANIEL_TAG, "reset ip");
            _internal_onCompletionOfScanningDev.onFinish(null, null);
        }
    };

    void dispatchBeacon() {
        // mReceiveHandler.removeCallbacks(reset_beacon_runnable);
        // mReceiveHandler.postDelayed(reset_beacon_runnable, BEACON_INTERVAL *
        // 2);
    }

    private final static int BEACON_INTERVAL = 5000;

    private void trickyTestingAlarmPck(String str) {
        if (str.contains("<AVST")) {
            LLSDKUtils.runInMainThread(new Runnable() {
                public void run() {
                    alarmSender.onFinish(null, null);
                }
            });

        }
    }

    void startPollingOnUdpPort() {
        mReceiveHandler.post(new Runnable() {
            public void run() {
                if (mScanningSocket == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
                    }
                } else {
                    try {
                        DatagramPacket p1 = new DatagramPacket(mReceiveBuffer, mReceiveBuffer.length);
                        mScanningSocket.receive(p1);
                        String recvStr = new String(p1.getData(), 0, p1.getLength(), "utf-8");
                        if (recvStr != null) {
                            if (verifyIsFromBox(recvStr)) {
                                _internal_onCompletionOfScanningDev.onFinish(p1.getAddress().getHostAddress(), null);
                            } else {
                                trickyTestingAlarmPck(recvStr);
                            }
                        }

                    } catch (IOException e) {
                        Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
                    } catch (Exception e) {
                        Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
                    }
                }
                mReceiveHandler.post(this);
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (mScanningSocket != null) {
                    try {
                        mScanningSocket.close();
                    } catch (Exception e) {
                    }
                    mScanningSocket = null;
                }
                mScanningSocket = new DatagramSocket(UDP_PORT);
                InetAddress local = InetAddress.getByName(BC_ADDR);
                int msg_length = QUERY_STRING.length();
                byte[] message = QUERY_STRING.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length, local, UDP_PORT);

                while (true) {

                    mScanningSocket.send(p);// properly able to send data. i
                    // receive
                    // data
                    // to server

                    Thread.sleep(BEACON_INTERVAL);
                }
            } catch (SocketException e) {
                Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
            } catch (UnknownHostException e) {
                Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
            } catch (IOException e) {
                Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
            } catch (InterruptedException e) {
                Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
            }

            try {
                Thread.sleep(BEACON_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public String[] parsePrimaryInfo(String info) {
        Log.i(LongLakeApplication.DANIEL_TAG, "response data" + info);
        if (info != null) {
            return info.split(",");
        }
        return new String[]{};
    }

    @Override
    protected void initAllConnectedDevices() {

    }

    @Override
    public boolean onHandleMsg(final LLSipMsgDevicesCmds deviceMsg, final IoCompletionListener1<IDeviceData> io,
                               final Object context) {
        if (deviceMsg != null && deviceMsg.isTypeOf(LLSipMsgDevicesCmds.MESSAGE_DEVICE_CMD_TYPE)) {
            LLSDKUtils.runInMainThread(new Runnable() {
                public void run() {
                    LLHandlerOverSmartDevice.onHandleSmartDeviceCmd(deviceMsg, io, context);
                }
            });
            return true;
        } else {
            return false;
        }

    }

    @Override
    public LLSmartDev getSmartDevData(String devName) {
        int position = Integer.parseInt(DevName_posion.get(devName));
        LLSmartDev llSmartDev = new LLSmartDev(devName, _switcher.getSwitch_bit(position) + "", "");
        return llSmartDev;
    }

    @Override
    public LLSmartEnvir getEnvironmentData() {
        LLSmartEnvir llSmartEnvir = new LLSmartEnvir(environment_params.getPM() + "", environment_params.getTemp() + "",
                environment_params.getMoisture() + "", environment_params.getVoc() + "");
        return llSmartEnvir;
    }

    LLSmartBoxRequestDataEx _generalRoutineIo = null;

    @Override
    public void enableWatchDog(final boolean on, String devName, final IoCompletionListener1<IDeviceData> io) {

        if (Boolean.valueOf(on).equals(watchDogStatus)) {
            io.onFinish(null, SUCCESS_TAG);
            return;
        }

        final String request = on ? String.format(">ALAO,\"%s\"\n", PHONENUM) : String.format(">ALAF,\"%s\"\n", PHONENUM);
        final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(devName);

        LLSmartConnectionRequest watchDogReqPck = new LLSmartBoxRequestDataEx(request, true, 0xff,
                new IoCompletionListener2() {
                    @Override
                    public void onNetworkIoCompletion(LLSmartConnectionRequest request, LLBufferDescription bd) {

                        if ((on && bd.isRetOfSettingAlarmPck()) || (!on && bd.isRetOfResettingAlarmPck())) {
                            Log.i(LongLakeApplication.DANIEL_TAG,
                                    on ? "succeeds in swtiching on alarm sys" : "succeeds in swtiching off alarm sys");
                            setDogStatus(on);
                        }
                    }

                    @Override
                    public void onFinishEx(LLSmartConnectionRequest data, LLBufferDescription bd) {
                        Integer ret = Integer.valueOf(
                                (bd.isValid() && (bd.isRetOfSettingAlarmPck() || bd.isRetOfResettingAlarmPck()))
                                        ? SUCCESS_TAG : INVALID_TAG);
                        io.onFinish(null, ret);

                    }
                }) {

            @Override
            public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                LLBufferDescription bd = (LLBufferDescription) context;
                return bd.isEnablingAlarmResponse();// .isRetOfSettingAlarmPck()
                // ||
                // bd.isRetOfResettingAlarmPck();
            }

        };

        Log.i(LongLakeApplication.DANIEL_TAG, "start to switch on alarm sys");
        client.sendData(watchDogReqPck, null);
    }

    @Override
    public void registerEnvironmentParams(IoCompletionListener1<LLSmartEnvir> io) {
        _envirmentParamsListener = io != null ? new WeakReference<IoCompletionListener1<LLSmartEnvir>>(io)
                : new WeakReference<IoCompletionListener1<LLSmartEnvir>>(default_enviromentListener);

    }

    @Override
    protected void setDogStatus(final boolean dogStatus) {
        watchDogStatus = dogStatus;
    }

    LLSmartAir llSmartAir = new LLSmartAir();

    @Override
    public LLSmartAir getAirConditionData() {
        return llSmartAir;
    }

    @Override
    public LLSmartCurtain getCurtainData() {
        return llSmartCurtain;
    }

    LLSmartDoor llSmartDoor = new LLSmartDoor();

    public LLSmartDoor getLlSmartDoor() {
        return llSmartDoor;
    }

    public void setLlSmartDoor(LLSmartDoor llSmartDoor) {
        this.llSmartDoor = llSmartDoor;
    }

    private LLSmartDevStatusNotifier[] _notify = new LLSmartDevStatusNotifier[]{new LLSmartDevStatusNotifier(),
            new LLSmartDevStatusNotifier(), new LLSmartDevStatusNotifier()};

    boolean isOpenDoorBusy = false;

    @Override
    public void openDoor(final String pwd, final IoCompletionListener1<IDeviceData> io) {
        if (isOpenDoorBusy) {
            io.onFinish(llSmartDoor, Integer.valueOf(BUSY_OP_TAG));
        } else {
            String request = String.format(">OPEN,\"%s\"\n", PHONENUM);
            if (TextUtils.equals(pwd, LLDoorLockNetController.getInstance().getPassword())) {
                isOpenDoorBusy = true;
                final IISocketUtils client = LLDevPortMap.getInstance().getDevPort(LLDevPortMap.OPEN_DOOR);
                final LLSmartConnectionRequest rd = new LLSmartBoxRequestDataEx(request, true, 0xff,
                        new IoCompletionListener1<LLSmartBoxRequestDataEx>() {

                            @Override
                            public void onFinish(LLSmartBoxRequestDataEx data, Object context) {
                                LLBufferDescription bd = (LLBufferDescription) context;
                                client.unregister(data);
                                isOpenDoorBusy = false;
                                if (!bd.isValid()) {
                                    llSmartDoor.setLLsmartDoor("", "2");
                                    io.onFinish(llSmartDoor, Integer.valueOf(INVALID_TAG));
                                } else {
                                    if (bd.isOpenDoorRet() && bd.succeddsInControlDoor()) {
                                        llSmartDoor.setLLsmartDoor("", "1");
                                    }
                                    io.onFinish(llSmartDoor,
                                            Integer.valueOf(bd.succeddsInControlDoor() ? SUCCESS_TAG : INVALID_TAG));
                                }

                            }
                        }) {

                    @Override
                    public boolean isInterestedPck(LLSmartConnectionRequest data, Object context) {
                        return ((LLBufferDescription) context).isOpenDoorRet();
                    }

                };
                client.sendData(rd, null);
            } else {
                llSmartDoor.setLLsmartDoor("", "0");
                io.onFinish(llSmartDoor, INVALID_TAG);
            }
        }
    }

    @Override
    public LLSmartDoor getDoorData() {
        return llSmartDoor;
    }

    @Override
    public void setSmartDevStatusCallback(int code, IoCompletionListener1<IDeviceData> io) {
        _notify[code].registerAsObserver(io, true);

    }

    @Override
    public void unsetSmartDevStatusCallback(int code, IoCompletionListener1<IDeviceData> io) {
        _notify[code].registerAsObserver(io, false);
    }
}
