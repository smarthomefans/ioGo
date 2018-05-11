package com.example.nagel.io1.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IoEnum {
    @SerializedName("_id")
    private String id;
    private String type;
    private IoObject.IoCommon common;

    public String getId() {
        return id;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return common.name;
    }

    public List<String> getMembers(){
        return common.members;
    }

}
