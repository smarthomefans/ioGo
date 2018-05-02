package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nagel.io1.data.model.Room;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.model.StateDao;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
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
    private Map<String,LiveData<List<State>>> stateFunctionCache;

    private final StateDao stateDao;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateCache = new HashMap<>();
        stateRoomCache = new HashMap<>();
        stateFunctionCache = new HashMap<>();

        DataBus.getBus().register(this);
        DataBus.getBus().post(new Events.getStateObjects());

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
                }else{
                    state = new State(key,null,false,null,null,null,0,null,null);
                    state.setData(data.get(key).toString());
                    stateDao.insert(state);
                }
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

    public LiveData<List<State>> getStatesByFunction(String functionId){
        if(!stateFunctionCache.containsKey(functionId)){
            LiveData<List<State>> stateList = stateDao.getStatesByFunction(functionId);
            stateFunctionCache.put(functionId, stateList);
            if(stateList.getValue() == null){
                Log.d(TAG, "getStatesByFunction stateFunctionCache.put functionId:" + functionId + " stateList.size:0" );
            }else {
                Log.d(TAG, "getStatesByFunction stateFunctionCache.put functionId:" + functionId + " stateList.size:" + stateList.getValue().size());
            }
        }
        if(stateFunctionCache.get(functionId).getValue() == null){
            Log.i(TAG, "getStatesByFunction functionId:" + functionId + " stateList.size:0" );
        }else {
            Log.i(TAG, "getStatesByFunction functionId:" + functionId + " stateList.size:" + stateFunctionCache.get(functionId).getValue().size());
        }
        return stateFunctionCache.get(functionId);
    }

    @Subscribe
    public void saveObjects(final Events.Objects event){
        try {
            JSONObject obj = new JSONObject(event.getData());
            JSONArray arr = obj.getJSONArray("rows");
            Log.d(TAG,"saveObjects arr.length:" + arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                if (item.getString("id").startsWith("javascript.0.")) {
                    State state = stateDao.getStateById(item.getString("id"));
                    if(state != null) {
                        state.setName(item.getJSONObject("value").getJSONObject("common").getString("name").toString());
                        state.setRole(item.getJSONObject("value").getJSONObject("common").getString("role").toString());
                        state.setType(item.getJSONObject("value").getJSONObject("common").getString("type").toString());
                        stateDao.update(state);
                        Log.d(TAG,"saveObjects stateDao.update stateId:" + state.getId());
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataBus.getBus().post(new Events.getStates());
        Log.i(TAG,"saveObjects finished");
    }
}
