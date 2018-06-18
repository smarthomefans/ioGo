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
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<Favorite>> getAllFavorites();

    @Query("SELECT * FROM favorite WHERE id = :id")
    LiveData<Favorite> getFavoriteById(String id);

    @Query("SELECT count(*) FROM favorite")
    LiveData<Integer> countFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite... favorits);

    @TypeConverters(ListConverters.class)
    @Update
    void update(Favorite... favorits);

    @Delete
    void delete(Favorite... favorits);

    @Query("DELETE FROM favorite")
    void deleteAll();
}