package com.example.nagel.io1.service.repository;

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
    private ObjectRepository objectRepository;
    private Map<String,State> stateCache;

    private final StateDao stateDao;

    private MutableLiveData<List<State>> mTempListMutableLiveData;

    @Inject
    public StateRepository(ObjectRepository objectRepository, StateDao stateDao) {
        this.objectRepository = objectRepository;
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
                        //state.setName(objectRepository.getObject(state.id).getName());
                        stateCache.put(state.id, state);
                    }
                    updateTempListFromCache();
                }
            }
        });
    }

    //IoState state = stateCache.get(args[0].toString());
     //       state.setData(args[1].toString());
      //      stateCache.put(state.getId(), state);


   /* public LiveData<IoState> getState(String stateId) {
        LiveData<IoState> cached = stateCache.get(stateId);
        if (cached != null) {
            return cached;
        }

        socketService.getStates();
        return null;
    }*/

    @Subscribe
    public void onStates(final Events.States event) {
        try {
            JSONObject data = new JSONObject(event.getData());
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                State state;
                if(stateCache.containsKey(key)){
                    state = stateCache.get(key);
                    state.setData(data.get(key).toString());
                }else {
                    state = new State(key, data.get(key).toString());
                }
                stateCache.put(state.id,state);
                stateDao.insert(state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateTempListFromCache();
    }

    private void updateTempListFromCache(){
        List<State> list = new ArrayList<>();
        List<Object> objects = objectRepository.getObjectsByRole("value.temperature");

        for(Object object : objects){
            State state = stateCache.get(object.id);
            if(state != null) {
                //state.setName(object.getName());
                //state.setRole(object.getRole());
                list.add(state);
            }
        }

        mTempListMutableLiveData.postValue(list);
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        State state;
        if(stateCache.containsKey(event.getId())){
            state = stateCache.get(event.getId());
            state.setData(event.getData());
        }else {
            state = new State(event.getId(), event.getData());
        }

        stateCache.put(state.id, state);
        updateTempListFromCache();
    }

    public MutableLiveData<List<State>> getTempList(){
        updateTempListFromCache();
        return mTempListMutableLiveData;
    }

}
