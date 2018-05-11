package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.IoEnum;
import com.example.nagel.io1.data.IoObject;
import com.example.nagel.io1.data.model.Function;
import com.example.nagel.io1.data.model.FunctionDao;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.model.StateDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FunctionRepository {
    private static final String TAG = "FunctionRepository";
    private Map<String, LiveData<Function>> functionCache;
    private LiveData<List<Function>> mListFunctions;
    private final FunctionDao functionDao;
    private final StateDao stateDao;

    @Inject
    public FunctionRepository(FunctionDao functionDao, StateDao stateDao) {
        this.functionDao = functionDao;
        this.stateDao = stateDao;
        functionCache = new HashMap<>();
    }

    public LiveData<Function> getFunction(String functionId) {
        if (!functionCache.containsKey(functionId)) {
            functionCache.put(functionId, functionDao.getFunctionById(functionId));
            Log.d(TAG, "getFunction functionCache.put functionId:" + functionId);
        }
        return functionCache.get(functionId);
    }

    public LiveData<List<Function>> getAllFunctions() {
        if(mListFunctions == null){
            mListFunctions = functionDao.getAllFunctions();
        }
        return mListFunctions;
    }

    public void saveObjects(String data) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        JSONObject obj = null;
        JSONArray arr = null;
        try {
            obj = new JSONObject(data);
            arr = obj.optJSONArray("rows");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                IoEnum ioEnum = gson.fromJson(item.getJSONObject("value").toString(), IoEnum.class);
                Function function = new Function(ioEnum.getId(), ioEnum.getName(), ioEnum.getMembers());
                functionDao.insert(function);
                for (int j = 0; j < ioEnum.getMembers().size(); j++) {
                    State state = stateDao.getStateById(ioEnum.getMembers().get(j)).getValue();
                    if (state != null) {
                        state.setFunctionId(function.getId());
                        stateDao.update(state);
                        Log.d(TAG, "saveObjects stateDao.update stateId:" + state.getId());
                    }else{
                        state = new State(ioEnum.getMembers().get(j), null, false, 0, 0, null,0, null, ioEnum.getId());
                        stateDao.insert(state);
                    }
                }

                Log.d(TAG, "saveObjects getId:" + ioEnum.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "saveObjects finished");
    }
}
