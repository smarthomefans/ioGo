package com.example.nagel.io1.service.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Timestamp;

@Entity
public class State {
    @PrimaryKey
    @NonNull
    public final String id;
    public final String val;
    public final boolean ack;
    public final int ts;
    public final int lc;
    public final String from;
    public final int q;

    public State(@NonNull String id, String val, boolean ack, int ts, int lc, String from, int q) {
        this.id = id;
        this.val = val;
        this.ack = ack;
        this.ts = ts;
        this.lc = lc;
        this.from = from;
        this.q = q;
    }
}