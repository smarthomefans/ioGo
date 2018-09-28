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

import com.google.gson.JsonDeserializer;

import java.util.Locale;

import timber.log.Timber;

public class IoName {

    private String language;
    private String name;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static JsonDeserializer<IoName> getDeserializer(){

        return (json, typeOfT, context) -> {

            IoName ioName = new IoName();
            try {
                if (json.isJsonPrimitive()) {
                    ioName.setName(json.getAsString());
                } else if (json.isJsonObject()) {
                    String language1 = Locale.getDefault().getLanguage();
                    if(json.getAsJsonObject().has(language1)){
                        ioName.setName(json.getAsJsonObject().get(language1).getAsString());
                    }else if(json.getAsJsonObject().has("en")){
                        ioName.setName(json.getAsJsonObject().get("en").getAsString());
                    }else if(json.getAsJsonObject().has("de")){
                        ioName.setName(json.getAsJsonObject().get("de").getAsString());
                    }
                }
            } catch (Throwable t) {
                Timber.e(t);
            }

            return ioName;
        };
    }
}
