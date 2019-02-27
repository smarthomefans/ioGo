/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EnumDao {

    @Query("SELECT * FROM enum")
    LiveData<List<Enum>> getAllEnums();

    @Query("SELECT * FROM enum WHERE id = :id")
    Enum getEnumById(String id);

    @Query("SELECT * FROM enum WHERE id = :id")
    LiveData<Enum> getEnumById2(String id);

    @Query("SELECT * FROM enum WHERE type = :type")
    List<Enum> getEnumsByType(String type);

    @Query("SELECT * FROM enum WHERE type = 'function' ORDER BY rank, name")
    LiveData<List<Enum>> getFunctionEnums();

    @Query("SELECT * FROM enum WHERE type = 'room' ORDER BY rank, name")
    LiveData<List<Enum>> getRoomEnums();

    @Query("SELECT * FROM enum WHERE favorite = 1 ORDER BY rank, name")
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