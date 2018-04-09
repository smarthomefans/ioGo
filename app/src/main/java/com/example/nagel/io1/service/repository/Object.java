package com.example.nagel.io1.service.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Object {
    @PrimaryKey
    @NonNull
    public final String id;
    public final String data;

    public Object(@NonNull String id, String data) {
        this.id = id;
        this.data = data;
    }
}