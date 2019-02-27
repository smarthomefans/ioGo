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

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("state")
public class ParseObjectState extends ParseObject {

    // Ensure that your subclass has a public default constructor

    public ParseObjectState() {
        super();
    }

    public String getId() {
        return getString("id");
    }

    public String getVal() {
        return getString("val");
    }

    public void setId(String id) {
        put("id", id);
    }

    public void setVal(String val) {
        put("val", val);
    }

}