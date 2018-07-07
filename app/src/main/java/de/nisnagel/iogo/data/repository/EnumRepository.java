package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.io.IoCommon;
import de.nisnagel.iogo.data.io.IoEnum;
import de.nisnagel.iogo.data.io.IoRow;
import de.nisnagel.iogo.data.io.IoValue;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.EnumDao;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import timber.log.Timber;

@Singleton
public class EnumRepository {

    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_ROOM = "room";

    private Map<String, LiveData<Enum>> enumCache;
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
            Timber.d("getEnum enumCache.put enumId:" + enumId);
        }
        return enumCache.get(enumId);
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

                Timber.d("saveObjects getId:" + ioValue.getId());
            }
        }catch(Throwable e){
            Timber.e(e);
        }

        Timber.i("saveObjects finished");
    }

    public void saveEnum(Enum anEnum) {
        enumDao.update(anEnum);
    }
}
