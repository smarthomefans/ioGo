package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.SerializedName;

public class IoObject {
    @SerializedName("_id")
    private
    String id;
    private String type;
    private IoCommon common;

    public String getId() {
        return this.id;
    }

    public String getType() {
        return type;
    }

    public IoCommon getCommon() {
        return common;
    }

}
