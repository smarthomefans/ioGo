/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.data.model.StateHistoryDao;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import timber.log.Timber;

@Singleton
public class StateRepository {

    private Map<String, LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;
    private LiveData<List<State>> mListFavoriteStates;

    private final StateDao stateDao;
    private final StateHistoryDao stateHistoryDao;
    private final EnumStateDao enumStateDao;
    private Executor executor;
    private Context context;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference dbObjectsRef;
    DatabaseReference dbStatesRef;

    @Inject
    public StateRepository(StateDao stateDao, StateHistoryDao stateHistoryDao, EnumStateDao enumStateDao, Executor executor, Context context) {
        this.stateDao = stateDao;
        this.stateHistoryDao = stateHistoryDao;
        this.enumStateDao = enumStateDao;
        this.executor = executor;
        this.context = context;

        stateEnumCache = new HashMap<>();
        connected = new MutableLiveData<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        init();

        Timber.v("instance created");
    }

    private void init() {

        ValueEventListener objectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
                Gson gson = gsonBuilder.create();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        IoObject ioObject = gson.fromJson(postSnapshot.getValue().toString(), IoObject.class);
                        syncObject(ioObject.getId(), ioObject);
                    } catch (Throwable t) {
                        Timber.e(postSnapshot.getKey(), t);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        ChildEventListener objectChildListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
                    Gson gson = gsonBuilder.create();
                    try {
                        IoObject ioObject = gson.fromJson(dataSnapshot.getValue().toString(), IoObject.class);
                        syncObject(ioObject.getId(), ioObject);
                    } catch (Throwable t) {
                        Timber.e(dataSnapshot.getKey(), t);
                    }
                }
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ValueEventListener stateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        IoState ioState = postSnapshot.getValue(IoState.class);
                        syncState(ioState.getId(), ioState);
                    } catch (Throwable t) {
                        Timber.e(postSnapshot.getKey(), t);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        ChildEventListener statesChildListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Timber.w("onChildChanged: state deleted stateId:" + dataSnapshot.getKey());
                if (dataSnapshot.getValue() != null) {
                    try {
                        IoState ioState = dataSnapshot.getValue(IoState.class);
                        syncState(ioState.getId(), ioState);
                    } catch (Throwable t) {
                        Timber.e(dataSnapshot.getKey(), t);
                    }
                }
                Timber.w("onChildChanged");
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                this.saveSocketState("connected");
                dbObjectsRef = database.getReference("objects/" + user.getUid());
                dbObjectsRef.addListenerForSingleValueEvent(objectListener);
                dbObjectsRef.addChildEventListener(objectChildListener);
                dbStatesRef = database.getReference("states/" + user.getUid());
                dbStatesRef.addListenerForSingleValueEvent(stateListener);
                dbStatesRef.addChildEventListener(statesChildListener);
            }
        };
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFirebaseEnabled = sharedPref.getBoolean("firebase_enabled", false);
        if(isFirebaseEnabled) {
            mAuth.addAuthStateListener(authListener);
        }
    }

    public void sendState(final Events.SetState event) {
        Timber.v("sendState called");

        if(dbStatesRef != null) {
            String node = event.getId();
            node = node.replace(".", "_");

            IoState ioState = new IoState();
            ioState.setId(event.getId());
            ioState.setVal(event.getVal());
            ioState.setFrom("app");
            node = node + "x";
            dbStatesRef.child(node).setValue(ioState);
        }
    }

    public LiveData<State> getState(String stateId) {
        Timber.v("getState called");
        return stateDao.getStateById2(stateId);
    }

    public LiveData<StateHistory> getHistory(String stateId) {
        DataBus.getBus().post(new Events.LoadHistory(stateId));
        return stateHistoryDao.getStateById2(stateId);
    }

    public List<String> getAllStateIds() {
        Timber.v("getAllStateIds called");
        return stateDao.getAllObjectIds();
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

    public void syncObject(String id, IoObject ioObject) {
        Timber.v("syncObject called");
        executor.execute(() -> {
                    State state = stateDao.getStateById(id);
                    if (state == null) {
                        state = new State(id);
                        state.setSync(true);
                        state.update(ioObject);
                        stateDao.insert(state);
                        Timber.d("syncObject: state inserted stateId:" + state.getId());
                    } else {
                        state.update(ioObject);
                        state.setSync(true);
                        stateDao.update(state);
                        Timber.d("syncObject: state updated stateId:" + state.getId());
                    }
                }
        );
    }

    public void syncState(String id, IoState ioState) {
        Timber.v("syncState called");
        executor.execute(() -> {
                    State state = stateDao.getStateById(id);
                    if (state == null) {
                        state = new State(id);
                        state.setSync(true);
                        state.update(ioState);
                        stateDao.insert(state);
                        Timber.d("syncState: state inserted stateId:" + state.getId());
                    } else {
                        state.update(ioState);
                        state.setSync(true);
                        stateDao.update(state);
                        Timber.d("syncState: state updated stateId:" + state.getId());
                    }
                }
        );
    }

    public void syncHistoryDay(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setDay(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryWeek(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setWeek(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryMonth(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setMonth(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void syncHistoryYear(String id, String data) {
        Timber.v("syncHistoryDay called");
        StateHistory stateHistory = stateHistoryDao.getStateById(id);
        if (stateHistory == null) {
            stateHistory = new StateHistory(id);
        }
        stateHistory.setYear(data);
        stateHistoryDao.insert(stateHistory);
    }

    public void saveSocketState(String state) {
        Timber.v("saveSocketState called");
        connected.postValue(state);
    }

    public void changeState(String id, String newVal) {
        Timber.v("saveState called");

        executor.execute(() -> {
                    State state = stateDao.getStateById(id);
                    state.update(newVal);
                    state.setSync(false);
                    stateDao.update(state);
                    sendState(new Events.SetState(id, newVal, state.getType()));
                    DataBus.getBus().post(new Events.SetState(id, newVal, state.getType()));
                }
        );
    }

    public void saveState(State state) {
        Timber.v("saveState called");
        executor.execute(() -> stateDao.update(state));
    }

    public void deleteState(State state) {
        Timber.v("deleteState called");
        stateDao.delete(state);
    }

    public void linkToEnum(String parent, String id) {
        Timber.v("linkToEnum called");
        String enumId = enumStateDao.getEnumId(parent);
        EnumState enumState = new EnumState(enumId, id);
        enumStateDao.insert(enumState);
    }

    public void setSyncAll(boolean sync) {
        stateDao.setSyncAll(sync);
    }

}
