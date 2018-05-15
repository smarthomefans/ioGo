package com.example.nagel.io1.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RoomStateDao {

    @Query("SELECT * FROM room_state")
    LiveData<List<RoomState>> getAllRoomStates();

    @Query("SELECT * FROM room_state WHERE room_id=:room_id and state_id = :state_id")
    RoomState getById(String room_id, String state_id);

    @Query("SELECT state_id FROM room_state")
    List<String> getAllStateIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RoomState... roomStates);

    @Update
    void update(RoomState... roomStates);

    @Delete
    void delete(RoomState... roomStates);
}