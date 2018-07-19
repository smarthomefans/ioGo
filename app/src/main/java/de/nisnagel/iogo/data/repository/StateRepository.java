package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import timber.log.Timber;

@Singleton
public class StateRepository {
    private Map<String, LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;
    private LiveData<List<State>> mListFavoriteStates;

    private final StateDao stateDao;
    private Gson gson;

    @Inject
    public StateRepository(StateDao stateDao) {
        this.stateDao = stateDao;

        stateEnumCache = new HashMap<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        connected = new MutableLiveData<>();

        Timber.v("instance created");
    }

    public List<String> getAllStateIds() {
        Timber.v("getAllStateIds called");
        return stateDao.getAllStateIds();
    }

    public LiveData<List<State>> getStatesByEnum(String enumId) {
        Timber.v("getStatesByEnum called");
        if (!stateEnumCache.containsKey(enumId)) {
            stateEnumCache.put(enumId, stateDao.getStatesByEnum(enumId));
            Timber.d("getStatesByEnum: load states from database enumId:" + enumId);
        }
        return stateEnumCache.get(enumId);
    }

    public LiveData<List<State>> getFavoriteStates() {
        Timber.v("getFavoriteStates called");
        if (mListFavoriteStates == null) {
            mListFavoriteStates = stateDao.getFavoriteStates();
            Timber.d("getFavoriteStates: load favorite states from database");
        }
        return mListFavoriteStates;
    }

    public LiveData<Integer> countStates() {
        Timber.v("countStates called");
        return stateDao.countStates();
    }

    public LiveData<String> getSocketState() {
        Timber.v("getSocketState called");
        return connected;
    }

    public void deleteAll() {
        Timber.v("deleteAll called");
        stateDao.deleteAll();
    }

    public void saveStateChanges(String data) {
        Timber.v("saveStateChanges called");
        try {
            TypeToken<Map<String, IoState>> token = new TypeToken<Map<String, IoState>>() {
            };
            Map<String, IoState> states = gson.fromJson(data, token.getType());
            for (Map.Entry<String, IoState> entry : states.entrySet()) {
                changeState(entry.getKey(), entry.getValue());
            }
        } catch (Throwable e) {
            Timber.e(e);
        }
        Timber.v("saveStates finished");
    }

    public void saveStateChange(String id, String data) {
        Timber.v("saveStateChange called");
        try {
            IoState ioState = gson.fromJson(data, IoState.class);
            changeState(id, ioState);
        } catch (Throwable e) {
            Timber.e(e);
        }
    }

    private void changeState(String id, IoState ioState) {
        Timber.v("changeState called");
        State state = stateDao.getStateById(id);
        if (state == null) {
            state = new State(id);
            state.update(ioState);
            stateDao.insert(state);
            Timber.d("changeState: state inserted stateId:" + state.getId());
        } else {
            state.update(ioState);
            stateDao.update(state);
            Timber.d("changeState: state updated stateId:" + state.getId());
        }
    }

    public void saveObjects(String data) {
        Timber.v("saveObjects called");
        try {
            JSONObject obj = new JSONObject(data);
            List<State> states = stateDao.getAllStates();
            for (State state : states) {
                JSONObject json = obj.optJSONObject(state.getId());
                if (json != null) {
                    try {
                        IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                        state.update(ioObject);
                        stateDao.update(state);
                        Timber.d("saveObjects: state updated from object stateId:" + state.getId());
                    } catch (Throwable e) {
                        Timber.e(e);
                    }
                } else {
                    stateDao.delete(state);
                    Timber.w("saveObjects: object not found for stateId:" + state.getId());
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        Timber.v("saveObjects finished");
    }

    public void saveSocketState(String state) {
        Timber.v("saveSocketState called");
        connected.postValue(state);
    }

    public void saveState(State state) {
        Timber.v("saveState called");
        stateDao.update(state);
    }
}
