package com.example.nagel.io1.service.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.nagel.io1.service.TimeConverters;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

@Entity
public class State {
    @PrimaryKey
    @NonNull
    private final String id;
    private String val;
    private boolean ack;
    @TypeConverters(TimeConverters.class)
    private Timestamp ts;
    @TypeConverters(TimeConverters.class)
    private Timestamp lc;
    private String from;
    private int q;

    private String roomId;
    private String functionId;


    public State(@NonNull String id, String val, boolean ack, Timestamp ts, Timestamp lc, String from, int q, String roomId, String functionId) {
        this.id = id;
        this.val = val;
        this.ack = ack;
        this.ts = ts;
        this.lc = lc;
        this.from = from;
        this.q = q;
        this.roomId = roomId;
        this.functionId = functionId;
    }

    public State(@NonNull String id, String data) {
        this.id = id;
        this.setData(data);
    }

    public void setData(String data) {
        JSONObject json;
        if (data != null) {
            try {
                json = new JSONObject(data);
                this.val = json.getString("val");
                try {
                    float value = Float.parseFloat(this.val);
                    this.val = String.valueOf(Math.round(value * 10) / 10);
                }catch (Exception e){

                }
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

    @NonNull
    public String getId() {
        return id;
    }

    public String getVal() {
        return val;
    }

    public boolean isAck() {
        return ack;
    }

    public Timestamp getTs() {
        return ts;
    }

    public Timestamp getLc() {
        return lc;
    }

    public String getFrom() {
        return from;
    }

    public int getQ() {
        return q;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getFunctionId() {
        return functionId;
    }
}