package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.model.State;
import com.example.nagel.io1.service.model.StateDao;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StateRepository {
    private Map<String,MutableLiveData<State>> stateCache;
    private Map<String,LiveData<List<State>>> stateRoomCache;

    private final StateDao stateDao;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;
        DataBus.getBus().register(this);
        stateCache = new HashMap<>();
        stateRoomCache = new HashMap<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<State> ls;
                ls = stateDao.getAllStates();
                if(ls != null){
                    for (State state : ls) {
                        MutableLiveData<State> mState = new MutableLiveData<>();
                        mState.postValue(state);
                        stateCache.put(state.getId(), mState);
                    }
                }
            }
        });
    }

    @Subscribe
    public void onStates(final Events.States event) {
        try {
            JSONObject data = new JSONObject(event.getData());
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                State state = new State(key, data.get(key).toString());
                stateDao.insert(state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        State state = new State(event.getId(), event.getData());
        stateDao.insert(state);
    }

    public LiveData<List<State>> getStatesByRoom(String roomId){
        if(!stateRoomCache.containsKey(roomId)){
            LiveData<List<State>> list = stateDao.getStatesByRoom(roomId);
            stateRoomCache.put(roomId, list);
        }
        return stateRoomCache.get(roomId);
    }
}
