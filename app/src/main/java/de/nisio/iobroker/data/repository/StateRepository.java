package de.nisio.iobroker.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

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

import de.nisio.iobroker.data.io.IoObject;
import de.nisio.iobroker.data.io.IoState;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.model.StateDao;

@Singleton
public class StateRepository {
    private static final String TAG = "StateRepository";
    private Map<String,LiveData<State>> stateCache;
    private Map<String,LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;

    private final StateDao stateDao;
    private Gson gson;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateCache = new HashMap<>();
        stateEnumCache = new HashMap<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        connected = new MutableLiveData<>();

        Log.i(TAG,"Constructor created");
    }

    public List<String> getAllStateIds(){
        return stateDao.getAllStateIds();
    }

    public LiveData<List<State>> getStatesByEnum(String enumId){
        if(!stateEnumCache.containsKey(enumId)){
            LiveData<List<State>> stateList = stateDao.getStatesByEnum(enumId);
            stateEnumCache.put(enumId, stateList);
        }
        return stateEnumCache.get(enumId);
    }

    public LiveData<Integer> countStates(){
        return stateDao.countStates();
    }

    public LiveData<String> getSocketState(){
        return connected;
    }

    public void deleteAll(){
        stateDao.deleteAll();
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

    public void saveSocketState(String state){
        connected.postValue(state);
    }
}
