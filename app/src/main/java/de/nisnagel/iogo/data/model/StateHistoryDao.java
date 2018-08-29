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
public interface StateHistoryDao {

    @Query("SELECT id FROM state_history")
    List<String> getAllObjectIds();

    @Query("SELECT * FROM state_history WHERE id = :id")
    StateHistory getStateById(String id);

    @Query("SELECT * FROM state_history WHERE id = :id")
    LiveData<StateHistory> getStateById2(String id);

    @Query("SELECT count(*) FROM state_history")
    LiveData<Integer> countStates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StateHistory... states);

    @Update
    void update(StateHistory... states);

    @Delete
    void delete(StateHistory... states);

    @Query("DELETE FROM state_history")
    void deleteAll();

}