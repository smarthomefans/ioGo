package com.example.nagel.io1.data.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FunctionDao {

    @Query("SELECT * FROM function")
    List<Function> getAllFunctions();

    @TypeConverters(ListConverters.class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Function... functions);

    @TypeConverters(ListConverters.class)
    @Update
    void update(Function... functions);

    @Delete
    void delete(Function... functions);
}