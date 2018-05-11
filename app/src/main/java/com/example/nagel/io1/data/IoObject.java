package com.example.nagel.io1.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IoObject {
    @SerializedName("_id")
    String id;
    String type;
    IoCommon common;

    public String getId(){return this.id;}

    public String getCommonName(){return this.common.name;}
    public String getCommonType(){return this.common.type;}
    public String getCommonRole(){return this.common.role;}

    public class IoCommon{
        String name;
        String type;
        String role;
        Boolean read;
        Boolean write;
        int min;
        int max;
        String unit;
        String def;
        String desc;
        List<String> members;
    }

}
