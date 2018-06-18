package de.nisio.iobroker.data.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(tableName = "favorite", indices = {@Index(value = {"id"},unique = true)})
public class Favorite {
    @PrimaryKey
    @NonNull
    private String id;
    private String flag;

    public Favorite(@NonNull String id, String flag) {
        this.id = id;
        this.flag = flag;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
