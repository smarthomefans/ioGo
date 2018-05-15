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
public interface StateDao {

    @Query("SELECT * FROM state")
    List<State> getAllStates();

    @Query("SELECT id FROM state")
    List<String> getAllStateIds();

    @Query("SELECT * FROM state WHERE id = :id")
    LiveData<State> getLStateById(String id);

    @Query("SELECT * FROM state WHERE id = :id")
    State getStateById(String id);

    @Query("SELECT state.* FROM state INNER JOIN room_state ON state.id = room_state.state_id WHERE room_state.room_id = :roomId")
    LiveData<List<State>> getStatesByRoom(String roomId);

    @Query("SELECT * FROM state WHERE id IN (SELECT state_id FROM function_state WHERE function_id = :functionId)")
    LiveData<List<State>> getStatesByFunction(String functionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(State... states);

    @Update
    void update(State... states);

    @Delete
    void delete(State... states);
}