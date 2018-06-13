package de.nisio.iobroker.data.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "function_state", indices = {@Index(value = {"function_id", "state_id"},unique = true)})
public class FunctionState {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @NonNull
    @ColumnInfo(name = "function_id")
    private String functionId;
    @NonNull
    @ColumnInfo(name = "state_id")
    private String stateId;

    public FunctionState(@NonNull String functionId, @NonNull String stateId) {
        this.functionId = functionId;
        this.stateId = stateId;
    }

    @NonNull
    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(@NonNull String functionId) {
        this.functionId = functionId;
    }

    @NonNull
    public String getStateId() {
        return stateId;
    }

    public void setStateId(@NonNull String stateId) {
        this.stateId = stateId;
    }
}
