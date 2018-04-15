package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
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
    private Map<String,MutableLiveData<List<State>>> stateRoomCache;

    private final StateDao stateDao;

    private MutableLiveData<List<State>> mTempListMutableLiveData;
    private MutableLiveData<List<State>> mListStates;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;
        DataBus.getBus().register(this);
        mTempListMutableLiveData = new MutableLiveData<>();
        stateCache = new HashMap<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<State> ls;
                ls = stateDao.getAllStates();
                if(ls != null){
                    for (State state : ls) {
                        MutableLiveData<State> mState = new MutableLiveData<>();
                        mState.setValue(state);
                        stateCache.put(state.getId(), mState);
                    }
                    updateTempListFromCache();
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
                State state;
                if(stateCache.containsKey(key)){
                    state = stateCache.get(key).getValue();
                    state.setData(data.get(key).toString());
                }else {
                    state = new State(key, data.get(key).toString());
                }
                MutableLiveData<State> mState = new MutableLiveData<>();
                mState.setValue(state);
                stateCache.put(state.getId(), mState);
                stateDao.insert(state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateTempListFromCache();
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        State state;
        if(stateCache.containsKey(event.getId())){
            state = stateCache.get(event.getId()).getValue();
            state.setData(event.getData());
        }else {
            state = new State(event.getId(), event.getData());
        }
        MutableLiveData<State> mState = new MutableLiveData<>();
        mState.setValue(state);
        stateCache.put(state.getId(), mState);
        updateTempListFromCache();
    }

    private void updateTempListFromCache(){
        List<State> list = new ArrayList<>();
        mTempListMutableLiveData.postValue(list);
    }

    public MutableLiveData<List<State>> getTempList(){
        updateTempListFromCache();
        return mTempListMutableLiveData;
    }

    public MutableLiveData<List<State>> getStatesByRoom(String roomId){
        if(!stateRoomCache.containsKey(roomId)){
            MutableLiveData<List<State>> list = new MutableLiveData<>();
            list.setValue(stateDao.getStatesByRoom(roomId).getValue());
            stateRoomCache.put(roomId, list);
        }
        return stateRoomCache.get(roomId);
    }
}
