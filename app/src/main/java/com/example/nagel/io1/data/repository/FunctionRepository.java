package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.io.IoCommon;
import com.example.nagel.io1.data.io.IoEnum;
import com.example.nagel.io1.data.io.IoRow;
import com.example.nagel.io1.data.io.IoValue;
import com.example.nagel.io1.data.model.Function;
import com.example.nagel.io1.data.model.FunctionDao;
import com.example.nagel.io1.data.model.FunctionState;
import com.example.nagel.io1.data.model.FunctionStateDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public LiveData<Integer> countFunctions(){
        return functionDao.countFunctions();
    }

    public List<String> getAllStateIds(){
        return functionStateDao.getAllStateIds();
    }

    public void saveObjects(String data) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
        for(IoRow ioRow : ioEnum.getRows()){
            IoValue ioValue = ioRow.getValue();
            IoCommon ioCommon = ioValue.getCommon();
            Function function = new Function(ioValue.getId(), ioCommon.getName(), ioCommon.getMembers());
            functionDao.insert(function);
            for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                FunctionState functionState = new FunctionState(function.getId(), ioCommon.getMembers().get(j));
                functionStateDao.insert(functionState);
            }

            Log.d(TAG, "saveObjects getId:" + ioValue.getId());
        }

        Log.i(TAG, "saveObjects finished");
    }
}
