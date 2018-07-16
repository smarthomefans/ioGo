package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.SerializedName;

public class IoObject {
    @SerializedName("_id")
    String id;
    String type;
    IoCommon common;

    public String getId() {
        return this.id;
    }

    public String getCommonName() {
        return this.common.getName();
    }

    public String getCommonType() {
        return this.common.getType();
    }

    public String getCommonRole() {
        return this.common.getRole();
    }

    public String getCommonUnit() {
        return this.common.getUnit();
    }

    public boolean isCommonRead() {
        return this.common.getRead();
    }

    public boolean isCommonWrite() {
        return this.common.getWrite();
    }

    public IoCommon getCommon() {
        return common;
    }

}
