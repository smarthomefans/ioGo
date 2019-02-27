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
import androidx.room.Update;

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