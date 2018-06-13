package de.nisio.iobroker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "room_state", indices = {@Index(value = {"room_id", "state_id"},unique = true)})
public class RoomState {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @NonNull
    @ColumnInfo(name = "room_id")
    private String roomId;
    @NonNull
    @ColumnInfo(name = "state_id")
    private String stateId;

    public RoomState(@NonNull String roomId, @NonNull String stateId) {
        this.roomId = roomId;
        this.stateId = stateId;
    }

    @NonNull
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(@NonNull String roomId) {
        this.roomId = roomId;
    }

    @NonNull
    public String getStateId() {
        return stateId;
    }

    public void setStateId(@NonNull String stateId) {
        this.stateId = stateId;
    }
}
