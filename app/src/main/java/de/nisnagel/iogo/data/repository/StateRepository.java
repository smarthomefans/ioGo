package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
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

import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import timber.log.Timber;

@Singleton
public class StateRepository {
    private Map<String,LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;

    private final StateDao stateDao;
    private Gson gson;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateEnumCache = new HashMap<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        connected = new MutableLiveData<>();

        Timber.i("Constructor created");
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
        try{
            TypeToken<Map<String,IoState>> token = new TypeToken<Map<String,IoState>>() {};
            Map<String,IoState> states = gson.fromJson(data, token.getType());
            for (Map.Entry<String, IoState> entry : states.entrySet())
            {
                changeState(entry.getKey(), entry.getValue());
            }
        }catch(Throwable e){
            Timber.e(e);
        }
        Timber.i("saveStates finished");
    }

    public void saveStateChange(String id, String data) {
        try{
            IoState ioState = gson.fromJson(data, IoState.class);
            changeState(id, ioState);
        }catch(Throwable e){
            Timber.e(e);
        }
    }

    private void changeState(String id, IoState ioState){
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
                    try{
                        IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                        state.update(ioObject);
                        stateDao.update(state);
                    }catch(Throwable e){
                        Timber.e(e);
                    }
                }else{
                    stateDao.delete(state);
                    Timber.w("saveObjects: Object not found: "+state.getId());
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        Timber.i("saveObjects finished");
    }

    public void saveSocketState(String state){
        connected.postValue(state);
    }
}
