package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IoRow {

    private String id;
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