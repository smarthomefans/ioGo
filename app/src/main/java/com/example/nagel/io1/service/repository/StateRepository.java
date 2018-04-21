package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

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
    public static final String TAG = "StateRepository";
    private Map<String,MutableLiveData<State>> stateCache;
    private Map<String,LiveData<List<State>>> stateRoomCache;

    private final StateDao stateDao;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateCache = new HashMap<>();
        stateRoomCache = new HashMap<>();

        DataBus.getBus().register(this);


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<State> ls;
                ls = stateDao.getAllStates();
                Log.d(TAG, "Constructor stateDao.getAllStates size:" + ls.size());
                if(ls != null){
                    for (State state : ls) {
                        MutableLiveData<State> mState = new MutableLiveData<>();
                        mState.postValue(state);
                        stateCache.put(state.getId(), mState);
                        Log.d(TAG, "Constructor stateCache.put stateId:" + state.getId());
                    }
                }
                DataBus.getBus().post(new Events.getStates());
            }
        });
        Log.i(TAG,"Constructor created");
    }

    @Subscribe
    public void onStates(final Events.States event) {
        try {
            JSONObject data = new JSONObject(event.getData());
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                State state = stateDao.getStateById(key);
                if(state != null) {
                    state.setData(data.get(key).toString());
                    stateDao.update(state);
                }
                //Log.d(TAG, "onStates stateDao.insert stateId:" + state.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"onStates finished");
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        State state = stateDao.getStateById(event.getId());
        if(state != null) {
            state.setData(event.getData());
            stateDao.update(state);
            Log.i(TAG,"onStateChange stateId:" + state.getId());
        }

    }

    public LiveData<List<State>> getStatesByRoom(String roomId){
        if(!stateRoomCache.containsKey(roomId)){
            LiveData<List<State>> stateList = stateDao.getStatesByRoom(roomId);
            stateRoomCache.put(roomId, stateList);
            if(stateList.getValue() == null){
                Log.d(TAG, "getStatesByRoom stateRoomCache.put roomId:" + roomId + " stateList.size:0" );
            }else {
                Log.d(TAG, "getStatesByRoom stateRoomCache.put roomId:" + roomId + " stateList.size:" + stateList.getValue().size());
            }
        }
        if(stateRoomCache.get(roomId).getValue() == null){
            Log.i(TAG, "getStatesByRoom roomId:" + roomId + " stateList.size:0" );
        }else {
            Log.i(TAG, "getStatesByRoom roomId:" + roomId + " stateList.size:" + stateRoomCache.get(roomId).getValue().size());
        }
        return stateRoomCache.get(roomId);
    }
}
