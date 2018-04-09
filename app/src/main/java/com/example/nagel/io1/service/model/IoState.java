package com.example.nagel.io1.service.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class IoState {
    private String id;
    private String val;
    private boolean ack;
    private Timestamp ts;
    private Timestamp lc;
    private String from;
    private int q;

    //Additionally
    private String name;
    private String role;

    public IoState(String id, String name, String role, String data) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.setData(data);
    }

    public void setData(String data) {
        JSONObject json;
        if (data != null) {
            try {
                json = new JSONObject(data);
                this.val = json.getString("val");
                this.ack = json.getBoolean("ack");
                this.ts = new Timestamp(json.getInt("ts"));
                this.lc = new Timestamp(json.getInt("lc"));
                this.from = json.getString("from");
                this.q = json.getInt("q");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public Timestamp getLc() {
        return lc;
    }

    public void setLc(Timestamp lc) {
        this.lc = lc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
