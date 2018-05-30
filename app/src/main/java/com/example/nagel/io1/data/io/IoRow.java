package com.example.nagel.io1.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IoRow {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("value")
    @Expose
    private IoValue ioValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IoValue getValue() {
        return ioValue;
    }

    public void setIoValue(IoValue ioValue) {
        this.ioValue = ioValue;
    }

}