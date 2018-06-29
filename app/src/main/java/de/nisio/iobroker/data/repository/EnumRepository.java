package de.nisio.iobroker.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisio.iobroker.data.io.IoCommon;
import de.nisio.iobroker.data.io.IoEnum;
import de.nisio.iobroker.data.io.IoRow;
import de.nisio.iobroker.data.io.IoValue;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.model.EnumDao;
import de.nisio.iobroker.data.model.EnumState;
import de.nisio.iobroker.data.model.EnumStateDao;

@Singleton
public class EnumRepository {

    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_ROOM = "room";

    private static final String TAG = "EnumRepository";
    private Map<String, LiveData<Enum>> enumCache;
    private LiveData<List<Enum>> mListEnums;
    private LiveData<List<Enum>> mListFunctionEnums;
    private LiveData<List<Enum>> mListRoomEnums;
    private LiveData<List<Enum>> mListFavoriteEnums;
    private final EnumDao enumDao;
    private final EnumStateDao enumStateDao;

    @Inject
    public EnumRepository(EnumDao enumDao, EnumStateDao enumStateDao) {
        this.enumDao = enumDao;
        this.enumStateDao = enumStateDao;
        enumCache = new HashMap<>();
    }

    public LiveData<Enum> getEnum(String enumId) {
        if (!enumCache.containsKey(enumId)) {
            enumCache.put(enumId, enumDao.getEnumById(enumId));
            Log.d(TAG, "getEnum enumCache.put enumId:" + enumId);
        }
        return enumCache.get(enumId);
    }

    public LiveData<List<Enum>> getAllEnums() {
        if(mListEnums == null){
            mListEnums = enumDao.getAllEnums();
        }
        return mListEnums;
    }

    public LiveData<List<Enum>> getFunctionEnums() {
        if(mListFunctionEnums == null){
            mListFunctionEnums = enumDao.getFunctionEnums();
        }
        return mListFunctionEnums;
    }

    public LiveData<List<Enum>> getRoomEnums() {
        if(mListRoomEnums == null){
            mListRoomEnums = enumDao.getRoomEnums();
        }
        return mListRoomEnums;
    }

    public LiveData<List<Enum>> getFavoriteEnums() {
        if(mListFavoriteEnums == null){
            mListFavoriteEnums = enumDao.getFavoriteEnums();
        }
        return mListFavoriteEnums;
    }

    public List<String> getAllStateIds(){
        return enumStateDao.getAllStateIds();
    }

    public LiveData<Integer> countFunctions(){
        return enumDao.countFunctionEnums();
    }

    public LiveData<Integer> countRooms(){
        return enumDao.countRoomEnums();
    }

    public void deleteAll(){
        enumDao.deleteAll();
        enumStateDao.deleteAll();
    }

    public void saveFunctionObjects(String data){
        saveObjects(data, TYPE_FUNCTION);
    }

    public void saveRoomObjects(String data){
        saveObjects(data, TYPE_ROOM);
    }

    private void saveObjects(String data, String type) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try {
            IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
            for (IoRow ioRow : ioEnum.getRows()) {
                IoValue ioValue = ioRow.getValue();
                IoCommon ioCommon = ioValue.getCommon();
                Enum anEnum = new Enum(ioValue.getId(), ioCommon.getName(), type, "false");
                enumDao.insert(anEnum);
                for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                    EnumState enumState = new EnumState(anEnum.getId(), ioCommon.getMembers().get(j));
                    enumStateDao.insert(enumState);
                }

                Log.d(TAG, "saveObjects getId:" + ioValue.getId());
            }
        }catch(Throwable e){
            e.printStackTrace();
        }

        Log.i(TAG, "saveObjects finished");
    }

    public void saveEnum(Enum anEnum) {
        enumDao.update(anEnum);
    }
}
