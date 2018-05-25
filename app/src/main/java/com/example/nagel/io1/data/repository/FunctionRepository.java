package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.io.Common;
import com.example.nagel.io1.data.io.IoEnum;
import com.example.nagel.io1.data.io.Row;
import com.example.nagel.io1.data.io.Value;
import com.example.nagel.io1.data.model.Function;
import com.example.nagel.io1.data.model.FunctionDao;
import com.example.nagel.io1.data.model.FunctionState;
import com.example.nagel.io1.data.model.FunctionStateDao;
import com.example.nagel.io1.data.model.Room;
import com.example.nagel.io1.data.model.RoomState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private final FunctionStateDao functionStateDao;

    @Inject
    public FunctionRepository(FunctionDao functionDao, FunctionStateDao functionStateDao) {
        this.functionDao = functionDao;
        this.functionStateDao = functionStateDao;
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

    public List<String> getAllStateIds(){
        return functionStateDao.getAllStateIds();
    }

    public void saveObjects(String data) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
        for(Row row : ioEnum.getRows()){
            Value value = row.getValue();
            Common common = value.getCommon();
            Function function = new Function(value.getId(), common.getName(), common.getMembers());
            functionDao.insert(function);
            for (int j = 0; j < common.getMembers().size(); j++) {
                FunctionState functionState = new FunctionState(function.getId(), common.getMembers().get(j));
                functionStateDao.insert(functionState);
            }

            Log.d(TAG, "saveObjects getId:" + value.getId());
        }

        Log.i(TAG, "saveObjects finished");
    }
}
