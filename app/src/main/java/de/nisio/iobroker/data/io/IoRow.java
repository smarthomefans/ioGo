package de.nisio.iobroker.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IoRow {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("value")
    @Expose
    private IoValue value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IoValue getValue() {
        return value;
    }

    public void setValue(IoValue value) {
        this.value = value;
    }

}