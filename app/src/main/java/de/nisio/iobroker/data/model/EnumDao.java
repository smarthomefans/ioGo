package de.nisio.iobroker.data.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EnumDao {

    @Query("SELECT * FROM enum")
    LiveData<List<Enum>> getAllEnums();

    @Query("SELECT * FROM enum WHERE id = :id")
    LiveData<Enum> getEnumById(String id);

    @Query("SELECT * FROM enum WHERE type = 'function'")
    LiveData<List<Enum>> getFunctionEnums();

    @Query("SELECT * FROM enum WHERE type = 'room'")
    LiveData<List<Enum>> getRoomEnums();

    @Query("SELECT * FROM enum WHERE favorite = 'true'")
    LiveData<List<Enum>> getFavoriteEnums();

    @Query("SELECT count(*) FROM enum WHERE type = 'function'")
    LiveData<Integer> countFunctionEnums();

    @Query("SELECT count(*) FROM enum WHERE type = 'room'")
    LiveData<Integer> countRoomEnums();

    @TypeConverters(ListConverters.class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Enum... enums);

    @TypeConverters(ListConverters.class)
    @Update
    void update(Enum... enums);

    @Delete
    void delete(Enum... enums);

    @Query("DELETE FROM enum")
    void deleteAll();
}