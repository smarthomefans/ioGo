package com.example.nagel.io1.service.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Object {
    @PrimaryKey
    @NonNull
    public final String id;
    public final String type;
    public final String common;
    public final String nativ;

    public Object(@NonNull String id, String type, String common, String nativ) {
        this.id = id;
        this.type = type;
        this.common = common;
        this.nativ = nativ;
    }

    public String getName(){
        JSONObject object;
        String name = null;

        try {
            object = new JSONObject(this.common);
            name = object.optString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return name;

    }

    public String getRole(){
        JSONObject object;
        String role = null;

        try {
            object = new JSONObject(this.common);
            role = object.optString("role");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return role;

    }

}