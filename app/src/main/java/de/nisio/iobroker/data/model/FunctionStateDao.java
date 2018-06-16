package de.nisio.iobroker.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FunctionStateDao {

    @Query("SELECT * FROM function_state")
    LiveData<List<FunctionState>> getAllFunctionStates();

    @Query("SELECT * FROM function_state WHERE function_id=:function_id and state_id = :state_id")
    FunctionState getById(String function_id, String state_id);

    @Query("SELECT state_id FROM function_state")
    List<String> getAllStateIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FunctionState... functionStates);

    @Update
    void update(FunctionState... functionStates);

    @Delete
    void delete(FunctionState... functionStates);

    @Query("DELETE FROM function_state")
    void deleteAll();
}