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

package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.io.IoName;

public abstract class BaseRepository {

    MutableLiveData<String> connected;
    boolean bFirebase;
    boolean bSocket;
    boolean bWeb;
    boolean bCloud;
    Executor executor;
    Context context;
    SharedPreferences sharedPref;
    WebService webService;
    Gson gson;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    public BaseRepository(Executor executor, Context context, SharedPreferences sharedPref, WebService webService) {
        this.executor = executor;
        this.context = context;
        this.sharedPref = sharedPref;
        this.webService = webService;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        gson = gsonBuilder.create();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        connected = new MutableLiveData<>();

        checkSettings(context, sharedPref);
    }

    void checkSettings(Context context, SharedPreferences sharedPref) {
        bFirebase = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);
        bWeb = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false);
        bCloud = sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);
        bSocket = bWeb || bCloud;

        if (bFirebase) {
            initFirebase();
        } else if (bWeb) {
            initWeb();
        }else if(bCloud) {
            initCloud();
        }
        if(bSocket){
            removeListener();
        }
    }

    public boolean hasConnection() {
        boolean bIogo = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);
        boolean bWeb = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false);
        boolean bCloud = sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);

        return (bIogo || bWeb || bCloud);
    }

    abstract void initFirebase();
    abstract void removeListener();

    abstract void initWeb();
    abstract void initCloud();
}
