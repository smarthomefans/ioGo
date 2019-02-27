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