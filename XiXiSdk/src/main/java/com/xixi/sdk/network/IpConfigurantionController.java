//package com.xixi.sdk.network;
//
//import android.content.Context;
//
//import com.xixi.sdk.cmds.LLCmdController;
//import com.xixi.sdk.utils.network.LLCallbackAsJsonString;
//import com.xixi.sdk.utils.network.LLIPGroupRet;
//import com.xixi.sdk.utils.network.LLRetrofitUtils;
//import com.xixi.sdk.utils.network.LLSdkRetrofitUtils;
//
//import java.io.IOException;
//
//import okhttp3.ResponseBody;
//
//import retrofit2.Call;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
//
///**
// * Created by Administrator on 2018/11/16 0016.
// */
//
//public class IpConfigurantionController {
//
//    private static IpConfigurantionController instance;
//
//    public void setDeviceInfo(LLSdkRetrofitUtils.DeviceInfo _deviceInfo) {
//        this.deviceInfo.setDeviceName(_deviceInfo.getDeviceName());
//        this.deviceInfo.setDeviceType(_deviceInfo.getDeviceType());
//        this.deviceInfo.setTimestamp(_deviceInfo.getTimestamp());
//    }
//
//    LLSdkRetrofitUtils.DeviceInfo deviceInfo ;
//
//    public synchronized static IpConfigurantionController getInstance() {
//        if (instance == null) {
//            instance = new IpConfigurantionController();
//        }
//        return instance;
//    }
//
//    LLCmdController llcmd = LLCmdController.getInstance();
//    Boolean flag = null;
//
//    public void configureIntranet(Retrofit retrofit , final Context context) {
//        LLRetrofitUtils.getIps( retrofit,deviceInfo).enqueue(
//                new LLCallbackAsJsonString<LLIPGroupRet>() {
//                    @Override
//                    public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
//                        Log.e("Daniel", "未连接到服务器");
//                    }
//
//                    @Override
//                    public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, LLIPGroupRet o) {
//                        if ("true".equals(o.getSuccess())) {
//                            if (userData.getTimestamp() != o.getData().getTimestamp()) {
//                                // 将地址池中的ip保存
//                                LLUserInfoMng.getInstance().setTimestamp(o.getData().getTimestamp());
//                                String ethIp = LLNetworkReachable.getAddrsInfo().get(0);
//                                if (!ethIp.equals(o.getData().getReqDeviceInfo().getInternalAddress())) {
//                                    // 将dgcp关闭，静态ip开启
//                                    try {
//                                        LLFileUtils.getInstance(context).copy("settings.db", "/data/data/ta/com.android.providers.settings/databases/settings.db");
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                // 修改ip地址
//                                String ethCom = "ifconfig eth0 " + o.getData().getReqDeviceInfo().getInternalAddress() + " netmask  255.255.255.0 up";
//                                String[] cmd = new String[]{ethCom};
//                                llcmd.exeCmd(cmd);
//                                // 判断修改后的网络能否连网
//                                boolean newConnection = IsNetworkConnected();
//                                if (!newConnection) {
//                                    String dhcpCom = "netcfg eth0 dhcp";
//                                    String rebootCom = "reboot";
//                                    cmd = new String[]{dhcpCom, rebootCom};
//                                    llcmd.exeCmd(cmd);
//                                }
//                            }
//                        } else {
//                            Log.e("Daniel", o.getMsg());
//                        }
//                    }
//
//                    @Override
//                    public Class<LLIPGroupRet> _getClass() {
//                        return null;
//                    }
//                }
//
//        );
//    }
//
//    private boolean IsNetworkConnected() {
//        flag = llcmd.exeCmdNetwork();
//        // 判断网络是否连接
//        if (!flag) {
//            final LLCursor cursor = new LLCursor();
//            final Runnable runner = new Runnable() {
//                @Override
//                public void run() {
//                    // 一分钟之内循环发送6次
//                    if (cursor.getCurrentPosition() == 3) {
//                    } else {
//                        flag = llcmd.exeCmdNetwork();
//                        if (flag) {
//                            UIThreadDispatcher.removeCallbacks(this);
//                        } else {
//                            cursor.moveToNext();
//                            UIThreadDispatcher.dispatch(this, 10000);
//                        }
//                    }
//                }
//            };
//            UIThreadDispatcher.dispatch(runner, 0);
//        }
//        return flag;
//    }
//}
