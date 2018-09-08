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

package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.SerializedName;

public class IoCustom {

    @SerializedName("sql.0")
    private IoCustomAdapter sql0;
    @SerializedName("history.0")
    private IoCustomAdapter history0;

    public IoCustomAdapter getSql0() {
        return sql0;
    }

    public void setSql0(IoCustomAdapter sql0) {
        this.sql0 = sql0;
    }

    public IoCustomAdapter getHistory0() {
        return history0;
    }

    public void setHistory0(IoCustomAdapter history0) {
        this.history0 = history0;
    }
}
