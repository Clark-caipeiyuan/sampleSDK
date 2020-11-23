package com.xixi.sdk.model;

import com.xixi.sdk.parser.LLGsonUtils;

import android.text.TextUtils;

public class SortedNode {

    public static int test(){
        String ss = "[{\"bd\":{\"PinYin\":\"Aloudong1danyuan10cun\",\"cInitialLetter\":\"A\",\"firstPinYin\":\"A\",\"imagePath\":\"\",\"type\":\"VI\",\"ver\":\"1512197240849\",\"id\":\"aca2135811c9\",\"name\":\"121\",\"children\":{\"menu\":[{\"xmlNodeName\":\"menu\",\"id\":\"1000\",\"name\":\"232\",\"op\":\"call\"},{\"xmlNodeName\":\"menu\",\"id\":\"1001\",\"name\":\"afds\"}]},\"xmlNodeName\":\"buddy\"},\"timestamp\":1515808555349}]" ;
        SortedNode sn[] = (SortedNode[])LLGsonUtils.fromJson(ss, SortedNode[].class);
        return sn.length ;
    }

    private String buddyName ;
    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public String getBuddyName() {
        return buddyName;
    }

    public SortedNode(){}
    public SortedNode(LLBuddy bd, long timestamp) {
        super();
        this.bd = bd;
        bd_id = bd.getId() ;
        buddyName = bd.getName() ;
        this.timestamp = timestamp;
    }

    private String bd_id ;
    public String getBd_id() {
        return bd_id;
    }
    public void setBd_id(String bd_id) {
        this.bd_id = bd_id;
    }

    private LLBuddy bd;

    public void setBd( LLBuddy bd){
        this.bd = bd ;
    }
    public LLBuddy getBd() {
        return bd;
    }

    long timestamp = -1;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {

        return this.getBd_id().hashCode();
    }

    @Override
    public String toString() {
        return getBd_id();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SortedNode)) {
            return false;
        }

        if (this == (SortedNode) obj)
            return true;

        SortedNode target = (SortedNode) obj;

        String strId = (obj == null ? "" : target.getBd_id());

        return TextUtils.equals(getBd_id(), strId);
    }
}
