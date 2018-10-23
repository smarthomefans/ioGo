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
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.data.model.StateHistoryDao;
import de.nisnagel.iogo.service.util.HistoryUtils;
import timber.log.Timber;

@Singleton
public class StateHistoryRepository extends BaseRepository implements OnHistoryReceived {

    private final StateHistoryDao stateHistoryDao;

    @Inject
    public StateHistoryRepository(StateHistoryDao stateHistoryDao, Executor executor, Context context, SharedPreferences sharedPref, WebService webService) {
        super(executor,context,sharedPref,webService);
        this.stateHistoryDao = stateHistoryDao;
        this.webService = webService;

        Timber.v("instance created");
    }

    public LiveData<StateHistory> getHistory(String stateId) {
        loadHistory(stateId);
        return stateHistoryDao.getStateById2(stateId);
    }

    private void loadHistory(String id) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("start", HistoryUtils.startOfDay * 1000);
            json.put("end", HistoryUtils.endOfDay * 1000);
            json.put("step", "60000");
            json.put("aggregate", "average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webService.getHistory(id, "day", json, this);

        json = new JSONObject();
        try {
            json.put("id", id);
            json.put("start", HistoryUtils.startOfWeek * 1000);
            json.put("end", HistoryUtils.endOfDay * 1000);
            json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfWeek) * 1000 / 200);
            json.put("aggregate", "average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webService.getHistory(id, "week", json, this);

        json = new JSONObject();
        try {
            json.put("id", id);
            json.put("start", HistoryUtils.startOfMonth * 1000);
            json.put("end", HistoryUtils.endOfDay * 1000);
            json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfMonth) * 1000 / 200);
            json.put("aggregate", "average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webService.getHistory(id, "month", json, this);

        json = new JSONObject();
        try {
            json.put("id", id);
            json.put("start", HistoryUtils.startOfYear * 1000);
            json.put("end", HistoryUtils.endOfDay * 1000);
            json.put("step", (HistoryUtils.endOfDay - HistoryUtils.startOfYear) * 1000 / 200);
            json.put("aggregate", "average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webService.getHistory(id, "year", json, this);
    }

    private void syncHistoryDay(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setDay(data);
        stateHistoryDao.insert(stateHistory);
    }

    private void syncHistoryWeek(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setWeek(data);
        stateHistoryDao.insert(stateHistory);
    }

    private void syncHistoryMonth(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setMonth(data);
        stateHistoryDao.insert(stateHistory);
    }

    private void syncHistoryYear(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setYear(data);
        stateHistoryDao.insert(stateHistory);
    }

    @Override
    void initFirebase() {

    }

    @Override
    void removeListener() {

    }

    @Override
    void initWeb() {

    }

    @Override
    void initCloud() {

    }

    @Override
    public void onHistoryReceived(String id, String data, String type) {
        switch (type) {
            case "day":
                syncHistoryDay(id,data);
                break;
            case "week":
                syncHistoryWeek(id,data);
                break;
            case "month":
                syncHistoryMonth(id,data);
                break;
            case "year":
                syncHistoryYear(id,data);
                break;
        }
    }
}
