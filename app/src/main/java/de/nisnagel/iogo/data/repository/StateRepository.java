package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import timber.log.Timber;

@Singleton
public class StateRepository {
    private Map<String, LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;
    private LiveData<List<State>> mListFavoriteStates;

    private final StateDao stateDao;
    private final EnumStateDao enumStateDao;
    private Gson gson;

    @Inject
    public StateRepository(StateDao stateDao, EnumStateDao enumStateDao) {
        this.stateDao = stateDao;
        this.enumStateDao = enumStateDao;

        stateEnumCache = new HashMap<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        gson = gsonBuilder.create();
        connected = new MutableLiveData<>();

        Timber.v("instance created");
    }

    public List<String> getAllStateIds() {
        Timber.v("getAllObjectIds called");
        return stateDao.getAllObjectIds();
    }

    public State getStateById(String id) {
        Timber.v("getStateById called");
        return stateDao.getStateById(id);
    }

    public List<String> getAllEnumStateIds() {
        Timber.v("getAllEnumStateIds called");
        return enumStateDao.getAllObjectIds();
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

    public void changeState(String id, IoState ioState) {
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

    public void saveSocketState(String state) {
        Timber.v("saveSocketState called");
        connected.postValue(state);
    }

    public void saveState(State state) {
        Timber.v("saveState called");
        stateDao.insert(state);
    }

    public void linkToEnum(String parent, String id) {
        Timber.v("linkToEnum called");
        String enumId = enumStateDao.getEnumId(parent);
        EnumState enumState = new EnumState(enumId, id);
        enumStateDao.insert(enumState);
    }
}
