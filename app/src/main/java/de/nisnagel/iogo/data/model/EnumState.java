package de.nisnagel.iogo.data.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "enum_state", indices = {@Index(value = {"enum_id", "state_id"},unique = true)})
public class EnumState {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @NonNull
    @ColumnInfo(name = "enum_id")
    private String enumId;
    @NonNull
    @ColumnInfo(name = "state_id")
    private String stateId;

    public EnumState(@NonNull String enumId, @NonNull String stateId) {
        this.enumId = enumId;
        this.stateId = stateId;
    }

    @NonNull
    public String getEnumId() {
        return enumId;
    }

    public void setEnumId(@NonNull String enumId) {
        this.enumId = enumId;
    }

    @NonNull
    public String getStateId() {
        return stateId;
    }

    public void setStateId(@NonNull String stateId) {
        this.stateId = stateId;
    }
}
