package com.example.nagel.io1.service.repository;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.nagel.io1.service.ListConverters;

import java.util.List;

@Entity
public class Room {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    @TypeConverters(ListConverters.class)
    private List<String> members;

    public Room(@NonNull String id, String name, List<String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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
