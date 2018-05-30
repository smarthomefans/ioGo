package com.example.nagel.io1.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IoCommon {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("members")
    @Expose
    private List<String> members = null;
    @SerializedName("smartName")
    @Expose
    private boolean smartName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public boolean isSmartName() {
        return smartName;
    }

    public void setSmartName(boolean smartName) {
        this.smartName = smartName;
    }

}