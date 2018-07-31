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
public interface StateDao {

    @Query("SELECT id FROM state")
    List<String> getAllObjectIds();

    @Query("SELECT * FROM state WHERE id = :id")
    State getStateById(String id);

    @Query("SELECT * FROM state WHERE id = :id")
    LiveData<State> getStateById2(String id);

    @Query("SELECT * FROM state WHERE name IS NOT NULL AND id IN (SELECT state_id FROM enum_state WHERE enum_id = :enumId) ORDER BY name")
    LiveData<List<State>> getStatesByEnum(String enumId);

    @Query("SELECT * FROM state WHERE favorite = 'true' ORDER by name")
    LiveData<List<State>> getFavoriteStates();

    @Query("SELECT count(*) FROM state")
    LiveData<Integer> countStates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(State... states);

    @Update
    void update(State... states);

    @Delete
    void delete(State... states);

    @Query("DELETE FROM state")
    void deleteAll();

    @Query("UPDATE state SET sync = :sync")
    void setSyncAll(boolean sync);
}