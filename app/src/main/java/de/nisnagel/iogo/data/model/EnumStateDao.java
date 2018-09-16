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

    @Query("DELETE FROM enum_state WHERE enum_id = :enum_id")
    void deleteByEnum(String enum_id);
}