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

    @Query("SELECT * FROM enum_state")
    LiveData<List<EnumState>> getAllEnumStates();

    @Query("SELECT * FROM enum_state WHERE enum_id=:enum_id and state_id = :state_id")
    EnumState getById(String enum_id, String state_id);

    @Query("SELECT state_id FROM enum_state")
    List<String> getAllStateIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EnumState... enumStates);

    @Update
    void update(EnumState... enumStates);

    @Delete
    void delete(EnumState... enumStates);

    @Query("DELETE FROM enum_state")
    void deleteAll();
}