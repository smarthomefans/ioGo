package de.nisnagel.iogo.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;

@Entity(tableName = "state", indices = {@Index(value = {"id"},unique = true)})
public class State {

    @PrimaryKey
    @NonNull
    private final String id;

    private String val;
    private boolean ack;
    private long ts;
    private long lc;
    private String from;
    private int q;

    private boolean sync;

    private Float min;
    private Float max;
    private String name;
    private String type;
    private String role;
    private String unit;
    private boolean read;
    private boolean write;
    @TypeConverters(ListConverters.class)
    private Map<String,String> states;

    private String favorite;

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

    public void update(String newVal) {
        this.val = newVal;
        this.ts = new Date().getTime();
        this.from = "app";
    }

    public void update(IoObject ioObject) {
        this.min = ioObject.getCommon().getMin();
        this.max = ioObject.getCommon().getMax();
        this.name = ioObject.getCommon().getName();
        this.type = ioObject.getCommon().getType();
        this.role = ioObject.getCommon().getRole();
        this.unit = ioObject.getCommon().getUnit();
        this.read = ioObject.getCommon().isRead();
        this.write = ioObject.getCommon().isWrite();
        this.states = ioObject.getCommon().getStates();
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

    public boolean getRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean getWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public Map<String, String> getStates() {
        return states;
    }

    public void setStates(Map<String, String> states) {
        this.states = states;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }
}