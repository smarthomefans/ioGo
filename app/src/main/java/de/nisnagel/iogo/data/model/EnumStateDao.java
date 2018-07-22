package de.nisnagel.iogo.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EnumStateDao {

    @Query("SELECT state_id FROM enum_state")
    List<String> getAllObjectIds();

    @Query("SELECT enum_id FROM enum_state WHERE state_id = :id")
    String getEnumId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EnumState... enumStates);

    @Update
    void update(EnumState... enumStates);

    @Delete
    void delete(EnumState... enumStates);

    @Query("DELETE FROM enum_state")
    void deleteAll();
}