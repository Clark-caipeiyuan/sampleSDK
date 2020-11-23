package com.xixi.sdk.utils.mem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.xixi.sdk.app.LongLakeApplication;

public class LLBufferDescription {

    private static LLBufferDescription failureInstance;

    final static List<String> emptyList = new ArrayList<String>();

    public final static synchronized LLBufferDescription getFailureInstance() {

        if (failureInstance == null) {
            failureInstance = new LLBufferDescription();
            failureInstance.response_array = emptyList;
        }
        return failureInstance;
    }

    final static String CURTAIN_CLOSE_STATE = "0000----";
    final static String ISCURTAINSTATE = "Eblind";

    private final static String SUCCESS_RET = "ACK_OK";

    final static String S_LIGHT_RESPONSE_RET = "<FNSC";
    final static String S_ENABLE_WARNING_RET = "<ALAO";
    final static String S_DISABLE_WARNING_RET = "<ALAF";
    final static String S_WARNING_OCCURS = "<ALAR";
    final static String S_LIGHT_STATUS_RET = "<DVST";
    final static String S_AIRCONDITION_RET = "<RECI";
    final static String S_CURTAIN_RESPONSE_RET = "<FNSC";
    final static String S_WARNING_OCCURS_1 = "<AVST";
    final static String S_DEVICES_STATES = "<FNSA";
    final static String S_OPEN_DOOR = "<OPEN";
    final static String S_LIGHT_RET = "Dlight1";
    final static String S_AIRCONDITION_RET_NEW = "<CAIR";
    final static String S_GET_AIRCONDITION_RET = "<QAIR";

    public boolean isLightStatus() {
        return has(S_LIGHT_STATUS_RET) && has(S_LIGHT_RET);
    }

    public boolean isOpenDoorRet() {
        return has(S_OPEN_DOOR);
    }

    public boolean isAirConditionRet() {
        return has(S_AIRCONDITION_RET);
    }

    public boolean isDevicesStatesRet() {
        return has(S_DEVICES_STATES);
    }

    public boolean isNewAirConditionRet() {
        return has(S_AIRCONDITION_RET_NEW);
    }

    public boolean isGetAirConditionRet() {
        return has(S_GET_AIRCONDITION_RET);
    }

    public int getCurtainState() {
        for (int i = 0; i < response_array.size(); i++) {
            if (TextUtils.equals(response_array.get(i).trim(), ISCURTAINSTATE)) {
                if (CURTAIN_CLOSE_STATE.contains(response_array.get(++i))) {
                    return 0;
                } else {

                }
                return 1;
            }
        }
        return 3;
    }

    public boolean succeedsInControlAirCondition() {

        try {
            return response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean succeedsInControlNewAirCondition() {

        try {
            return response_array.get(4).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCurtainRet() {
        return has(S_CURTAIN_RESPONSE_RET);
    }

    public boolean succeedsInControlCurtain() {

        try {
            return response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean succeddsInControlDoor() {
        try {
            return response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPrimaryInfoRet() {
        return has("<FNAA");
    }

    public boolean isConnectNetRet() {
        return has(new String[]{"AM", "NET", "OK"});
    }

    public boolean isAlarmFired() {
        return has(new String[]{S_WARNING_OCCURS, S_WARNING_OCCURS_1});
    }

    public int getLightsStatus() {
        int s = 0;
        try {
            String lightStatus = response_array.get(4);
            for (int i = lightStatus.length() - 1; i >= 0; i--) {
                s += ((lightStatus.charAt(i) - '0') << (lightStatus.length() - 1 - i));
            }
        } catch (Exception e) {
        }
        return s;
    }

    private boolean _equal(String str) {
        try {
            return response_array.get(0).contains(str);
        } catch (Exception e) {
            return false;
        }

    }

    public boolean has(String[] str) {
        for (String strNode : str) {
            if (_equal(strNode))
                return true;
        }
        return false;
    }

    public boolean has(String str) {
        return has(new String[]{str});
    }

    public boolean isLoginRequest() {
        return has("<LOGN");
    }

    public boolean isEnablingAlarmResponse() {
        return has(new String[]{S_ENABLE_WARNING_RET, S_DISABLE_WARNING_RET});
    }

    public boolean isRetOfSettingAlarmPck() {
        try {
            return response_array.get(0).contains(S_ENABLE_WARNING_RET) && response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRetOfResettingAlarmPck() {
        try {
            return response_array.get(0).contains(S_DISABLE_WARNING_RET) && response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLightResponse() {
        return has(S_LIGHT_RESPONSE_RET);
    }


    public boolean succeedsInTurningOnLight() {

        try {
            return response_array.get(2).contains(SUCCESS_RET);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCmd() {
        return response_array.get(0);
    }

    private LLBufferDescription() {
    }

    public boolean isValid() {
        return response_array != null && response_array.size() != 0;
    }

    public List<String> getArray() {
        return response_array;
    }

    public static LLBufferDescription makeValidBufferDescription(String[] array) {
        return new LLBufferDescription(array);
    }

    private LLBufferDescription(String[] strArray) {
        super();
        if (strArray != null) {
            this.response_array = (Arrays.asList(strArray));
        }
    }

    private List<String> response_array;
}
