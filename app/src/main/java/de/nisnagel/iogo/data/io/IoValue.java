package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IoValue {

    @SerializedName("_id")
    @Expose
    private String id;
    private IoCommon common;
    private String type;
    private IoAcl acl;
    private String from;
    private long ts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IoCommon getCommon() {
        return common;
    }

    public void setCommon(IoCommon common) {
        this.common = common;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IoAcl getAcl() {
        return acl;
    }

    public void setAcl(IoAcl ioAcl) {
        this.acl = acl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

}