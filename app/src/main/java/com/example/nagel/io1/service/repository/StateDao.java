package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.example.nagel.io1.service.TimeConverters;

import java.util.List;

@Dao
public interface StateDao {

    @Query("SELECT * FROM state")
    List<State> getAllStates();

    @Query("SELECT * FROM state WHERE roomId = :roomId")
    LiveData<List<State>> getStatesByRoom(String roomId);

    @TypeConverters(TimeConverters.class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(State... states);

    @TypeConverters(TimeConverters.class)
    @Update
    void update(State... states);

    @Delete
    void delete(State... states);
}