package com.example.nagel.io1.service.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Repo {
    @PrimaryKey
    public final int id;
    public final String name;
    public final String url;

    public Repo(@NonNull int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }
}