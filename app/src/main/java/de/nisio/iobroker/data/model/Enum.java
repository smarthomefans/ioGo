package de.nisio.iobroker.data.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "enum", indices = {@Index(value = {"id"},unique = true)})
public class Enum {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String type;

    public Enum(@NonNull String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
