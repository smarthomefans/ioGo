package com.example.nagel.io1.service.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class IoEnum {
    private String name;
    private List<String> members;

    public IoEnum(String name, String data) {
        this.name = name;
        this.members = new ArrayList<>();
        JSONArray json;
        if (data != null) {
            try {
                json = new JSONArray(data);
                for (int i = 0; i < json.length(); i++) {
                    members.add(json.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
}
