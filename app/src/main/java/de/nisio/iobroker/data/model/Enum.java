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
    private String favorite;

    public Enum(@NonNull String id, String name, String type, String favorite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.favorite = favorite;
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

    public String getFavorite() {
        return favorite;
    }

    public Boolean isFavorite() { return "true".equals(favorite); }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
