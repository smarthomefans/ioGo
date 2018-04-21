package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.model.Function;
import com.example.nagel.io1.service.model.FunctionDao;
import com.example.nagel.io1.service.model.State;
import com.example.nagel.io1.service.model.StateDao;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FunctionRepository {
    public static final String TAG = "FunctionRepository";
    private Map<String,MutableLiveData<Function>> functionCache;

    private final FunctionDao functionDao;
    private final StateDao stateDao;

    private MutableLiveData<List<Function>> mListFunctions;

    @Inject
    public FunctionRepository(FunctionDao functionDao, StateDao stateDao) {
        this.functionDao = functionDao;
        this.stateDao = stateDao;
        mListFunctions = new MutableLiveData<>();
        functionCache = new HashMap<>();

        DataBus.getBus().register(this);

        DataBus.getBus().post(new Events.getObjects());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Function> ls;
                ls = functionDao.getAllFunctions();
                Log.d(TAG, "Constructor functionDao.getAllFunctions size:" + ls.size());
                mListFunctions.postValue(ls);

                if(ls != null){
                    for (Function function : ls) {
                        MutableLiveData<Function> mFunction = new MutableLiveData<>();
                        mFunction.postValue(function);
                        functionCache.put(function.getId(), mFunction);
                        Log.d(TAG, "Constructor functionCache.put functionId:" + function.getId());
                    }
                }
            }
        });
        Log.i(TAG, "Constructor created");
    }

    public LiveData<Function> getFunction(String functionId){
        if(!functionCache.containsKey(functionId)){
            functionCache.put(functionId, new MutableLiveData<>());
            Log.d(TAG, "getFunction functionCache.put functionId:" + functionId);
        }
        Log.i(TAG, "getFunction functionId:" + functionId);
        return functionCache.get(functionId);
    }

    public LiveData<List<Function>> getAllFunctions(){
        if(mListFunctions.getValue() == null) {
            Log.i(TAG, "getAllFunctions size:0");
        }else{
            Log.i(TAG, "getAllFunctions size:" + mListFunctions.getValue().size());
        }
        return mListFunctions;
    }

    @Subscribe
    public void saveObjects(final Events.Objects event){
        try {
            JSONObject obj = new JSONObject(event.getData());
            JSONArray arr = obj.getJSONArray("rows");
            Log.d(TAG,"saveObjects arr.length:" + arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                if (item.getString("id").contains("enum.functions.")) {
                    Function function = new Function(item.getString("id"), item.getJSONObject("value").getJSONObject("common").getString("name"), null);
                    Log.d(TAG,"saveObjects functionId:" + function.getId());
                    List<String> members = new ArrayList<>();
                    JSONArray arrMembers = item.getJSONObject("value").getJSONObject("common").getJSONArray("members");
                    if(arrMembers != null) {
                        Log.d(TAG,"saveObjects arrMembers.length:" + arrMembers.length());
                        for (int j=0; j<arrMembers.length(); j++) {
                            members.add(arrMembers.getString(j));

                            State state = stateDao.getStateById(arrMembers.getString(j));
                            if(state != null) {
                                state.setFunctionId(function.getId());
                                stateDao.update(state);
                                Log.d(TAG,"saveObjects stateDao.update stateId:" + state.getId());
                            }
                        }
                        function.setMembers(members);
                    }
                    functionDao.insert(function);
                    Log.d(TAG,"saveObjects functionDao.insert functionId:" + function.getId());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"saveObjects finished");
    }
}
