package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public List<String> getAllStateIds() {
        Timber.v("getAllStateIds called");
        return enumStateDao.getAllStateIds();
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
        enumDao.deleteAll();
        enumStateDao.deleteAll();
    }

    public void saveFunctionObjects(String data) {
        Timber.v("saveFunctionObjects called");
        saveObjects(data, TYPE_FUNCTION);
    }

    public void saveRoomObjects(String data) {
        Timber.v("saveRoomObjects called");
        saveObjects(data, TYPE_ROOM);
    }

    private void saveObjects(String data, String type) {
        Timber.v("saveObjects called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<IoCommon> deserializer = new JsonDeserializer<IoCommon>() {
            @Override
            public IoCommon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();

                IoCommon ioCommon = new IoCommon();
                try {
                    if (jsonObject.get("name").isJsonPrimitive()) {
                        ioCommon.setName(jsonObject.get("name").getAsString());
                    } else if (jsonObject.get("name").isJsonObject()) {
                        ioCommon.setName(jsonObject.get("name").getAsJsonObject().get("en").getAsString());
                    }
                } catch (Throwable t) {
                    Timber.e(t);
                }
                if (jsonObject.has("type")) {
                    ioCommon.setType(jsonObject.get("type").getAsString());
                }
                if (jsonObject.has("role")) {
                    ioCommon.setRole(jsonObject.get("role").getAsString());
                }
                if (jsonObject.has("read")) {
                    ioCommon.setRead(jsonObject.get("read").getAsBoolean());
                }
                if (jsonObject.has("write")) {
                    ioCommon.setWrite(jsonObject.get("write").getAsBoolean());
                }
                if (jsonObject.has("unit")) {
                    ioCommon.setUnit(jsonObject.get("unit").getAsString());
                }
                if (jsonObject.has("def")) {
                    ioCommon.setDef(jsonObject.get("def").getAsString());
                }
                if (jsonObject.has("desc")) {
                    ioCommon.setDesc(jsonObject.get("desc").getAsString());
                }
                if (jsonObject.has("members")) {
                    Gson gson = new Gson();
                    Type founderListType = new TypeToken<ArrayList<String>>(){}.getType();
                    ioCommon.setMembers(gson.fromJson(jsonObject.get("members"), founderListType));
                }
                if (jsonObject.has("states")) {
                    Gson gson = new Gson();
                    Type founderListType = new TypeToken<Map<String,String>>(){}.getType();
                    ioCommon.setStates(gson.fromJson(jsonObject.get("states"), founderListType));
                }
                return ioCommon;
            }
        };

        gsonBuilder.registerTypeAdapter(IoCommon.class, deserializer);
        Gson gson = gsonBuilder.create();

        try {
            IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
            for (IoRow ioRow : ioEnum.getRows()) {
                IoValue ioValue = ioRow.getValue();
                IoCommon ioCommon = ioValue.getCommon();
                Enum anEnum = new Enum(ioValue.getId(), ioCommon.getName(), type, "false");
                enumDao.insert(anEnum);
                Timber.d("saveObjects: enum inserted enumId:" + anEnum.getId());
                if (ioCommon.getMembers() != null) {
                    for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                        EnumState enumState = new EnumState(anEnum.getId(), ioCommon.getMembers().get(j));
                        enumStateDao.insert(enumState);
                        Timber.d("saveObjects: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                    }
                } else {
                    Timber.i("saveObjects: no members found for enumId:" + anEnum.getId());
                }
                Timber.d("saveObjects: enum saved enumId:" + anEnum.getId());
            }
        } catch (Throwable e) {
            Timber.e(e);
        }

        Timber.v("saveObjects finished");
    }

    public void saveEnum(Enum anEnum) {
        Timber.v("saveEnum called");
        enumDao.update(anEnum);
    }
}
