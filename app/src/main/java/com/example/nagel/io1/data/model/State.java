package com.example.nagel.io1.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.nagel.io1.data.IoObject;
import com.example.nagel.io1.data.IoState;

@Entity(tableName = "state", indices = {@Index(value = {"id"},unique = true)})
public class State {

    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_STRING = "string";

    @PrimaryKey
    @NonNull
    private final String id;
    private String val;
    private boolean ack;
    private long ts;
    private long lc;
    private String from;
    private int q;

    private String name;
    private String type;
    private String role;
    private String unit;
    private Boolean read;
    private Boolean write;

    @Ignore
    public State(@NonNull String id) {
        this.id = id;
    }

    public State(@NonNull String id, String val, boolean ack, long ts, long lc, String from, int q) {
        this.id = id;
        this.val = val;
        this.ack = ack;
        this.ts = ts;
        this.lc = lc;
        this.from = from;
        this.q = q;
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
        this.unit = ioObject.getCommonUnit();
        this.read = ioObject.isCommonRead();
        this.write = ioObject.isCommonWrite();
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

    public long getTs() {
        return ts;
    }

    public long getLc() {
        return lc;
    }

    public String getFrom() {
        return from;
    }

    public int getQ() {
        return q;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }

    public void setWrite(Boolean write) {
        this.write = write;
    }
}