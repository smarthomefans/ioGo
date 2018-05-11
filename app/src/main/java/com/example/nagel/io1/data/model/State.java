package com.example.nagel.io1.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.nagel.io1.data.IoObject;
import com.example.nagel.io1.data.IoState;

@Entity
public class State {
    @PrimaryKey
    @NonNull
    private final String id;
    private String val;
    private boolean ack;
    private int ts;
    private int lc;
    private String from;
    private int q;

    private String roomId;
    private String functionId;
    private String name;
    private String type;
    private String role;


    public State(@NonNull String id, String val, boolean ack, int ts, int lc, String from, int q, String roomId, String functionId) {
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

    public void update(IoState ioState) {
        this.val = ioState.getVal();
        this.ack = ioState.isAck();
        this.ts = ioState.getTs();
        this.lc = ioState.getLc();
        this.from = ioState.getFrom();
        this.q = ioState.getQ();
    }

    public void update(IoObject ioObject) {
        this.name = ioObject.getCommonName();
        this.type = ioObject.getCommonType();
        this.role = ioObject.getCommonRole();
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

    public int getTs() {
        return ts;
    }

    public int getLc() {
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

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}