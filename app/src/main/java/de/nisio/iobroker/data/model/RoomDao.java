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
public interface RoomDao {

    @Query("SELECT * FROM room")
    LiveData<List<Room>> getAllRooms();

    @Query("SELECT * FROM room WHERE id = :id")
    LiveData<Room> getRoomById(String id);

    @Query("SELECT count(*) FROM room")
    LiveData<Integer> countRooms();

    @TypeConverters(ListConverters.class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Room... rooms);

    @TypeConverters(ListConverters.class)
    @Update
    void update(Room... rooms);

    @Delete
    void delete(Room... rooms);

    @Query("DELETE FROM room")
    void deleteAll();
}