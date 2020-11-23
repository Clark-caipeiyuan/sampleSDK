package com.xixi.sdk.utils.network;

import com.xixi.sdk.utils.network.LLRet;

import java.util.List;

/**
 * Created by Administrator on 2018/11/22 0022.
 */

public class LLIPGroupRet extends LLRet {

    private String success;
    private String msg;
    private Data data;

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private int communityId;
        private String communityName;
        private long timestamp;
        private ReqDeviceInfo reqDeviceInfo;
        private List<IntranetMap> intranetMap;

        public int getCommunityId() {
            return communityId;
        }

        public String getCommunityName() {
            return communityName;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public ReqDeviceInfo getReqDeviceInfo() {
            return reqDeviceInfo;
        }

        public List<IntranetMap> getIntranetMap() {
            return intranetMap;
        }

        public void setCommunityId(int communityId) {
            this.communityId = communityId;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void setReqDeviceInfo(ReqDeviceInfo reqDeviceInfo) {
            this.reqDeviceInfo = reqDeviceInfo;
        }

        public void setIntranetMap(List<IntranetMap> intranetMap) {
            this.intranetMap = intranetMap;
        }
    }

    public static class ReqDeviceInfo {

        private String deviceType;
        private String buildingName;
        private String internalAddress;
        private String unitName;
        private String propertyName;
        private int unitId;
        private String deviceName;
        private int propertyId;
        private String productSubtype;
        private int buildingId;
        private String propertyNumber;

        public String getDeviceType() {
            return deviceType;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getInternalAddress() {
            return internalAddress;
        }

        public String getUnitName() {
            return unitName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public int getUnitId() {
            return unitId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public int getPropertyId() {
            return propertyId;
        }

        public String getProductSubtype() {
            return productSubtype;
        }

        public int getBuildingId() {
            return buildingId;
        }

        public String getPropertyNumber() {
            return propertyNumber;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
        }

        public void setInternalAddress(String internalAddress) {
            this.internalAddress = internalAddress;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setUnitId(int unitId) {
            this.unitId = unitId;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public void setPropertyId(int propertyId) {
            this.propertyId = propertyId;
        }

        public void setProductSubtype(String productSubtype) {
            this.productSubtype = productSubtype;
        }

        public void setBuildingId(int buildingId) {
            this.buildingId = buildingId;
        }

        public void setPropertyNumber(String propertyNumber) {
            this.propertyNumber = propertyNumber;
        }
    }

    public static class IntranetMap {

        private String deviceType;
        private String buildingName;
        private String internalAddress;
        private String unitName;
        private String propertyName;
        private int unitId;
        private String deviceName;
        private int propertyId;
        private String productSubtype;
        private int buildingId;
        private String propertyNumber;

        public String getDeviceType() {
            return deviceType;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getInternalAddress() {
            return internalAddress;
        }

        public String getUnitName() {
            return unitName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public int getUnitId() {
            return unitId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public int getPropertyId() {
            return propertyId;
        }

        public String getProductSubtype() {
            return productSubtype;
        }

        public int getBuildingId() {
            return buildingId;
        }

        public String getPropertyNumber() {
            return propertyNumber;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
        }

        public void setInternalAddress(String internalAddress) {
            this.internalAddress = internalAddress;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setUnitId(int unitId) {
            this.unitId = unitId;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public void setPropertyId(int propertyId) {
            this.propertyId = propertyId;
        }

        public void setProductSubtype(String productSubtype) {
            this.productSubtype = productSubtype;
        }

        public void setBuildingId(int buildingId) {
            this.buildingId = buildingId;
        }

        public void setPropertyNumber(String propertyNumber) {
            this.propertyNumber = propertyNumber;
        }
    }
}
