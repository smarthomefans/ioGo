package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.IoObject;
import com.example.nagel.io1.data.IoState;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.model.StateDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StateRepository {
    public static final String TAG = "StateRepository";
    private Map<String,LiveData<State>> stateCache;
    private Map<String,LiveData<List<State>>> stateRoomCache;
    private Map<String,LiveData<List<State>>> stateFunctionCache;

    private final StateDao stateDao;
    private Gson gson;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateCache = new HashMap<>();
        stateRoomCache = new HashMap<>();
        stateFunctionCache = new HashMap<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        Log.i(TAG,"Constructor created");
    }

    public void saveStates(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                saveState(key, obj.get(key).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"onStates finished");
    }

    public void saveState(String id, String data) {
        State state = stateDao.getStateById(id).getValue();
        if(state != null) {
            IoState ioState = gson.fromJson(data, IoState.class);
            state.update(ioState);
            stateDao.update(state);
            Log.i(TAG,"saveState stateId:" + state.getId());
        }
    }

    public List<State> getAllStates(){
        return stateDao.getAllStates();
    }

    public List<String> getAllStateIds(){
        return stateDao.getAllStateIds();
    }

    public LiveData<List<State>> getStatesByRoom(String roomId){
        if(!stateRoomCache.containsKey(roomId)){
            LiveData<List<State>> stateList = stateDao.getStatesByRoom(roomId);
            stateRoomCache.put(roomId, stateList);
        }
        return stateRoomCache.get(roomId);
    }

    public LiveData<List<State>> getStatesByFunction(String functionId){
        if(!stateFunctionCache.containsKey(functionId)){
            LiveData<List<State>> stateList = stateDao.getStatesByFunction(functionId);
            stateFunctionCache.put(functionId, stateList);
        }
        return stateFunctionCache.get(functionId);
    }

    public LiveData<State> getState(String id){
        if(!stateCache.containsKey(id)){
            LiveData<State> state = stateDao.getStateById(id);
            stateCache.put(id, state);
        }
        return stateCache.get(id);
    }

    public void saveObjects(String data){
        try {
            JSONObject obj = new JSONObject(data);
            List<State> states = stateDao.getAllStates();
            for(State state : states){
                IoObject ioObject = gson.fromJson(obj.getJSONObject(state.getId()).toString(), IoObject.class);
                state.update(ioObject);
                stateDao.update(state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"saveObjects finished");
    }
}
