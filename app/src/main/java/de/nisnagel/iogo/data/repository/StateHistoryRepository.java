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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.io.FCommon;
import de.nisnagel.iogo.data.io.FObject;
import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.data.model.StateHistoryDao;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import timber.log.Timber;

@Singleton
public class StateHistoryRepository {

    private final StateHistoryDao stateHistoryDao;
    private Executor executor;
    private Context context;


    @Inject
    public StateHistoryRepository(StateHistoryDao stateHistoryDao, Executor executor, Context context) {
        this.stateHistoryDao = stateHistoryDao;
        this.executor = executor;
        this.context = context;

        Timber.v("instance created");
    }

    public LiveData<StateHistory> getHistory(String stateId) {
        DataBus.getBus().post(new Events.LoadHistory(stateId));
        return stateHistoryDao.getStateById2(stateId);
    }

    public void syncHistoryDay(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setDay(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryWeek(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setWeek(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryMonth(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setMonth(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryYear(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setYear(data);
        stateHistoryDao.insert(stateHistory);
    }

}
