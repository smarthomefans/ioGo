package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.nagel.io1.App;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.model.IoState;
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
    private Map<String,IoState> stateCache;


    private SocketService socketService;

    private MutableLiveData<List<IoState>> mTempListMutableLiveData;

    @Inject
    public StateRepository() {
        //this.socketService = service;
        DataBus.getBus().register(this);
        //this.socketService.getStates();
        mTempListMutableLiveData = new MutableLiveData<>();
        stateCache = new HashMap<>();
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
                IoState state;
                if(stateCache.containsKey(key)){
                    state = stateCache.get(key);
                    state.setData(data.get(key).toString());
                }else {
                    state = new IoState(key, null, null, data.get(key).toString());
                }
                stateCache.put(state.getId(),state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateTempList();
    }

    private void updateTempList(){
        List<IoState> list = new ArrayList<>();
        for (Map.Entry<String, IoState> entry : stateCache.entrySet()) {
            if(entry.getKey().contains("temperature")){
                list.add(entry.getValue());
            }
        }
        mTempListMutableLiveData.postValue(list);
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        IoState state;
        if(stateCache.containsKey(event.getId())){
            state = stateCache.get(event.getId());
            state.setData(event.getData());
        }else {
            state = new IoState(event.getId(), null, null, event.getData());
        }

        stateCache.put(state.getId(), state);
        updateTempList();
    }

    public MutableLiveData<List<IoState>> getTempList(){
        updateTempList();
        return mTempListMutableLiveData;
    }

}
