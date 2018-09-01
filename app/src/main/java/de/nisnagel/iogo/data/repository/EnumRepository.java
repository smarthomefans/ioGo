package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    private Executor executor;

    @Inject
    public EnumRepository(EnumDao enumDao, EnumStateDao enumStateDao, Executor executor) {
        this.enumDao = enumDao;
        this.enumStateDao = enumStateDao;
        this.executor = executor;

        enumCache = new HashMap<>();
        Timber.v("instance created");
    }

    public LiveData<Enum> getEnum(String enumId) {
        Timber.v("getEnum called");
        if (!enumCache.containsKey(enumId)) {
            enumCache.put(enumId, enumDao.getEnumById(enumId));
            Timber.d("getEnum: load enum from database enumId:" + enumId);
        }
        return enumCache.get(enumId);
    }

    public LiveData<List<Enum>> getFunctionEnums() {
        Timber.v("getFunctionEnums called");
        if (mListFunctionEnums == null) {
            mListFunctionEnums = enumDao.getFunctionEnums();
            Timber.d("getFunctionEnums: load function enums from database");
        }
        return mListFunctionEnums;
    }

    public LiveData<List<Enum>> getRoomEnums() {
        Timber.v("getRoomEnums called");
        if (mListRoomEnums == null) {
            mListRoomEnums = enumDao.getRoomEnums();
            Timber.d("getFunctionEnums: load room enums from database");
        }
        Timber.v("getRoomEnums");
        return mListRoomEnums;
    }

    public LiveData<List<Enum>> getFavoriteEnums() {
        Timber.v("getFavoriteEnums called");
        if (mListFavoriteEnums == null) {
            mListFavoriteEnums = enumDao.getFavoriteEnums();
            Timber.d("getFunctionEnums: load favorite enums from database");
        }
        return mListFavoriteEnums;
    }

    public LiveData<Integer> countFunctions() {
        Timber.v("countFunctions called");
        return enumDao.countFunctionEnums();
    }

    public LiveData<Integer> countRooms() {
        Timber.v("countRooms called");
        return enumDao.countRoomEnums();
    }

    public void deleteAll() {
        Timber.v("deleteAll called");
        executor.execute(enumDao::deleteAll);
        executor.execute(enumStateDao::deleteAll);
    }

    public void syncEnum(Enum item){
        Timber.v("syncEnum called");
        enumDao.insert(item);
    }

    public void syncEnumState(EnumState item){
        Timber.v("syncEnumState called");
        enumStateDao.insert(item);
    }

    public void saveEnum(Enum... anEnum) {
        Timber.v("saveEnum called");
        executor.execute(() -> enumDao.update(anEnum));
    }

}
