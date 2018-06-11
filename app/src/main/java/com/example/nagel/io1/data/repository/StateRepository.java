package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.io.IoObject;
import com.example.nagel.io1.data.io.IoState;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.model.StateDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StateRepository {
    private static final String TAG = "StateRepository";
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

    public LiveData<Integer> countStates(){
        return stateDao.countStates();
    }

    public void saveStateChanges(String data) {
        TypeToken<Map<String,IoState>> token = new TypeToken<Map<String,IoState>>() {};
        Map<String,IoState> states = gson.fromJson(data, token.getType());
        for (Map.Entry<String, IoState> entry : states.entrySet())
        {
            saveStateChange(entry.getKey(), entry.getValue());
        }
        Log.i(TAG,"saveStates finished");
    }

    public void saveStateChange(String id, String data) {
        IoState ioState = gson.fromJson(data, IoState.class);
        saveStateChange(id, ioState);
    }

    private void saveStateChange(String id, IoState ioState){
        State state = stateDao.getStateById(id);
        if(state == null) {
            state = new State(id);
            state.update(ioState);
            stateDao.insert(state);
        }else {
            state.update(ioState);
            stateDao.update(state);
        }
    }

    public void saveObjects(String data){
        try {
            JSONObject obj = new JSONObject(data);
            List<State> states = stateDao.getAllStates();
            for(State state : states){
                JSONObject json = obj.optJSONObject(state.getId());
                if(json != null) {
                    IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                    state.update(ioObject);
                    stateDao.update(state);
                }else{
                    stateDao.delete(state);
                    Log.w(TAG,"saveObjects: Object not found: "+state.getId());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"saveObjects finished");
    }
}
