package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.model.IoState;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StateRepository {
    private Map<String,IoState> stateCache;
    private SocketService socketService;
    private MutableLiveData<List<IoState>> mTempListMutableLiveData;

    static StateRepository stateRepository;

    public StateRepository() {
        //this.socketService = service;
        DataBus.getBus().register(this);
        //this.socketService.getStates();
        mTempListMutableLiveData = new MutableLiveData<>();
    }

    public static StateRepository getInstance(){
        if(stateRepository == null){
            stateRepository = new StateRepository();
        }
        return stateRepository;
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
            JSONArray arr = new JSONArray(event.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<List<IoState>> getTempList(){
        List<IoState> list = new ArrayList<>();
        IoState t = new IoState("ID1", "derName", "dieRolle", "{\"val\":6.12,\"ack\":true,\"ts\":1522867531113,\"q\":0,\"from\":\"system.adapter.javascript.0\",\"lc\":1520884333496}");
        list.add(t);
        mTempListMutableLiveData.setValue(list);
        return mTempListMutableLiveData;
    }

}
