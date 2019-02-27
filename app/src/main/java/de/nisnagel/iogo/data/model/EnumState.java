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


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "enum_state", indices = {@Index(value = {"enum_id", "state_id"},unique = true)})
public class EnumState {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @NonNull
    @ColumnInfo(name = "enum_id")
    private String enumId;
    @NonNull
    @ColumnInfo(name = "state_id")
    private String stateId;

    public EnumState(@NonNull String enumId, @NonNull String stateId) {
        this.enumId = enumId;
        this.stateId = stateId;
    }

    @NonNull
    public String getEnumId() {
        return enumId;
    }

    public void setEnumId(@NonNull String enumId) {
        this.enumId = enumId;
    }

    @NonNull
    public String getStateId() {
        return stateId;
    }

    public void setStateId(@NonNull String stateId) {
        this.stateId = stateId;
    }
}
