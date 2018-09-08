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

package de.nisnagel.iogo.service.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.nisnagel.iogo.data.io.IoCommon;
import de.nisnagel.iogo.data.io.IoEnum;
import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoRow;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.io.IoValue;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;
import timber.log.Timber;

public class SyncUtils {

    public static void saveEnums(EnumRepository repo, String data, String type) {
        Timber.v("saveEnums called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        Gson gson = gsonBuilder.create();

        try {
            IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
            for (IoRow ioRow : ioEnum.getRows()) {
                IoValue ioValue = ioRow.getValue();
                IoCommon ioCommon = ioValue.getCommon();
                Enum anEnum = new Enum(ioValue.getId(), ioCommon.getName(), type, false, ioCommon.getColor(), ioCommon.getIcon());
                repo.syncEnum(anEnum);
                Timber.d("saveEnums: enum inserted enumId:" + anEnum.getId());
                if (ioCommon.getMembers() != null) {
                    for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                        EnumState enumState = new EnumState(anEnum.getId(), ioCommon.getMembers().get(j));
                        repo.syncEnumState(enumState);
                        Timber.d("saveEnums: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                    }
                } else {
                    Timber.i("saveEnums: no members found for enumId:" + anEnum.getId());
                }
                Timber.d("saveEnums: enum saved enumId:" + anEnum.getId());
            }
        } catch (Throwable e) {
            Timber.e(e);
        }

        Timber.v("saveEnums finished");
    }

    public static void saveObjects(StateRepository repo, String data, boolean syncChildren) {
        Timber.v("saveObjects called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        Gson gson = gsonBuilder.create();
        try {
            JSONObject obj = new JSONObject(data);
            List<String> ids = repo.getAllEnumStateIds();
            for (String id : ids) {
                JSONObject json = obj.optJSONObject(id);
                if (json != null) {
                    try {
                        IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                        repo.syncObject(id,ioObject);
                        Timber.d("saveObjects: state updated from object stateId:" + id);
                    } catch (Throwable e) {
                        Timber.e(e);
                    }
                }else if(syncChildren){
                    Iterator iter = obj.keys();
                    while (iter.hasNext()) {
                        String key = (String)iter.next();
                        if(key.contains(id)){
                            json = obj.optJSONObject(key);
                            try {
                                IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                                repo.syncObject(key,ioObject);
                                repo.linkToEnum(id, key);
                                Timber.d("saveObjects: state updated from object stateId:" + key);
                            } catch (Throwable e) {
                                Timber.e(e);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        Timber.v("saveObjects finished");
    }

    public static void saveStates(StateRepository repo, String data) {
        Timber.v("saveStates called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try {
            TypeToken<Map<String, IoState>> token = new TypeToken<Map<String, IoState>>() {
            };
            Map<String, IoState> states = gson.fromJson(data, token.getType());
            for (Map.Entry<String, IoState> entry : states.entrySet()) {
                repo.syncState(entry.getKey(), entry.getValue());
            }
        } catch (Throwable e) {
            Timber.e(e);
        }
        Timber.v("saveStates finished");
    }

    public static void saveState(StateRepository repo, String id, String data) {
        Timber.v("saveState called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try {
            IoState ioState = gson.fromJson(data, IoState.class);
            repo.syncState(id, ioState);
        } catch (Throwable e) {
            Timber.e(e);
        }
        Timber.v("saveState finished");
    }
}
