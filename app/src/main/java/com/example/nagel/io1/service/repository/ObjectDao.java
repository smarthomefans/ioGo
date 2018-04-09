package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ObjectDao {

    @Query("SELECT * FROM object")
    LiveData<List<Object>> getAllObjects();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Object... objects);

    @Update
    void update(Object... objects);

    @Delete
    void delete(Object... objects);
}