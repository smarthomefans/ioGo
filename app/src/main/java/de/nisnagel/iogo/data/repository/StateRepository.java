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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.io.FCommon;
import de.nisnagel.iogo.data.io.FObject;
import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoObject;
import de.nisnagel.iogo.data.io.IoState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateDao;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import timber.log.Timber;

@Singleton
public class StateRepository implements OnObjectsReceived, OnStatesReceived {

    private static final String FROM = "app";
    private static final String STATE_QUEUES = "stateQueues/";
    private static final String STATES = "states/";
    private static final String OBJECT_QUEUES = "objectQueues/";
    private static final String OBJECTS = "objects/";
    private Map<String, LiveData<List<State>>> stateEnumCache;
    private MutableLiveData<String> connected;
    private LiveData<List<State>> mListFavoriteStates;

    private final StateDao stateDao;
    private final EnumStateDao enumStateDao;
    private Executor executor;
    private Context context;
    private SharedPreferences sharedPref;
    private WebService webService;
    private Gson gson;

    private boolean bFirebase;
    private boolean bSocket;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbObjectsRef;
    private DatabaseReference dbObjectQueuesRef;
    private DatabaseReference dbStatesRef;
    private DatabaseReference dbStateQueuesRef;
    private ValueEventListener objectListener;
    private ChildEventListener objectChildListener;
    private ValueEventListener stateListener;
    private ChildEventListener stateChildListener;

    @Inject
    public StateRepository(StateDao stateDao, EnumStateDao enumStateDao, Executor executor, Context context, SharedPreferences sharedPref, WebService webService) {
        this.stateDao = stateDao;
        this.enumStateDao = enumStateDao;
        this.executor = executor;
        this.context = context;
        this.sharedPref = sharedPref;
        this.webService = webService;

        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals(context.getString(R.string.pref_device_name))) {

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(executor, instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    Timber.d("newToken" + newToken);

                    String deviceName = sharedPreferences.getString(context.getString(R.string.pref_device_name), null);
                    if (deviceName != null) {
                        setDevice(sharedPreferences.getString(key, ""), newToken);
                    }
                });
            }
            if (key.equals(context.getString(R.string.pref_connect_web))
                    || key.equals(context.getString(R.string.pref_connect_cloud))
                    || key.equals(context.getString(R.string.pref_connect_iogo))) {
                bFirebase = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);
                bSocket = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false) || sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);

                if (bFirebase) {
                    initFirebase();
                } else if (bSocket) {
                    initSocket();
                    try {
                        if (dbObjectsRef != null) {
                            dbObjectsRef.removeEventListener(objectListener);
                            dbObjectsRef.removeEventListener(objectChildListener);
                        }
                        if (dbStatesRef != null) {
                            dbStatesRef.removeEventListener(stateListener);
                            dbStatesRef.removeEventListener(stateChildListener);
                        }
                    } catch (Throwable t) {
                        Timber.e(t);
                    }
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        gson = gsonBuilder.create();

        stateEnumCache = new HashMap<>();
        connected = new MutableLiveData<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        bFirebase = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);
        bSocket = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false) || sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);

        if (bFirebase) {
            initFirebase();
        } else if (bSocket) {
            initSocket();
        }

        Timber.v("instance created");
    }

    private void initFirebase() {

        objectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                Timber.e("Database read error: " + databaseError.getCode());
            }
        };

        objectChildListener = new ChildEventListener() {
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

        stateListener = new ValueEventListener() {
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
                Timber.e("Database read error: " + databaseError.getCode());
            }
        };

        stateChildListener = new ChildEventListener() {
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
                addListener(user);
            }
        };

        if (mAuth.getCurrentUser() != null) {
            addListener(mAuth.getCurrentUser());
        }
        mAuth.addAuthStateListener(authListener);
    }

    public boolean hasConnection() {
        boolean bIogo = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);
        boolean bWeb = sharedPref.getBoolean(context.getString(R.string.pref_connect_web), false);
        boolean bCloud = sharedPref.getBoolean(context.getString(R.string.pref_connect_cloud), false);

        return (bIogo || bWeb || bCloud);
    }

    private void addListener(FirebaseUser user) {
        this.saveSocketState(context.getString(R.string.pref_connect_iogo));
        dbObjectsRef = database.getReference(OBJECTS + user.getUid());
        boolean bSync = sharedPref.getBoolean(context.getString(R.string.pref_layout_object_sync), false);
        if(bSync) {
            dbObjectsRef.addListenerForSingleValueEvent(objectListener);
        }
        dbObjectsRef.addChildEventListener(objectChildListener);
        dbObjectQueuesRef = database.getReference(OBJECT_QUEUES + user.getUid());
        dbStatesRef = database.getReference(STATES + user.getUid());
        dbStatesRef.addListenerForSingleValueEvent(stateListener);
        dbStatesRef.addChildEventListener(stateChildListener);
        dbStateQueuesRef = database.getReference(STATE_QUEUES + user.getUid());
    }

    private void initSocket() {
        if (webService.isConnected()) {
            executor.execute(this::initialLoad);
        } else {
            executor.execute(() -> {
                webService.init();
                webService.on(Socket.EVENT_CONNECT, onConnect);
                webService.on(Socket.EVENT_DISCONNECT, onDisconnect);
                webService.on("stateChange", onStateChange);
                webService.start();
            });
        }
    }

    private Emitter.Listener onConnect = args -> executor.execute(this::initialLoad);

    private void initialLoad() {
        boolean bSync = sharedPref.getBoolean(context.getString(R.string.pref_layout_object_sync), false);
        if(bSync) {
            webService.getObjects(this);
        }else{
            requestStates();
        }
    }

    private Emitter.Listener onDisconnect = args -> {
        setSyncFalseAll();
        Timber.i("disconnected");
    };

    private void saveObjects(String data) {
        Timber.v("saveObjects called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        Gson gson = gsonBuilder.create();
        try {
            JSONObject obj = new JSONObject(data);
            List<String> ids = getAllEnumStateIds();

            for (String id : ids) {
                JSONObject json = obj.optJSONObject(id);
                if (json != null) {
                    try {
                        IoObject ioObject = gson.fromJson(json.toString(), IoObject.class);
                        syncObject(id, ioObject);
                        Timber.d("saveObjects: state updated from object stateId:" + id);
                    } catch (Throwable e) {
                        Timber.e(e);
                    }
                } else {
                    Timber.d("saveObjects: state deleted stateId:" + id);
                    State state = new State(id);
                    deleteState(state);
                }
            }

            List<String> stateIds = getAllStateIds();
            stateIds.removeAll(ids);
            for (String id : stateIds) {
                Timber.d("saveObjects: state deleted stateId:" + id);
                State state = new State(id);
                deleteState(state);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        Timber.v("saveObjects finished");
    }

    private void saveStates(String data) {
        Timber.v("saveStates called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try {
            TypeToken<Map<String, IoState>> token = new TypeToken<Map<String, IoState>>() {
            };
            Map<String, IoState> states = gson.fromJson(data, token.getType());
            for (Map.Entry<String, IoState> entry : states.entrySet()) {
                syncState(entry.getKey(), entry.getValue());
            }
        } catch (Throwable e) {
            Timber.e(e);
        }
        Timber.v("saveStates finished");
    }

    private Emitter.Listener onStateChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Timber.v("onStateChange called");
            if (args[0] != null && args[1] != null) {
                try {
                    IoState ioState = gson.fromJson(args[1].toString(), IoState.class);
                    syncState(args[0].toString(), ioState);
                } catch (Throwable e) {
                    Timber.e(e);
                }
            }
        }
    };

    public void sendState(String id, String val, String type) {
        Timber.v("sendState called");

        if (bFirebase) {
            if (dbStateQueuesRef != null) {
                IoState ioState = new IoState();
                ioState.setId(id);
                ioState.setVal(val);
                ioState.setFrom(FROM);
                dbStateQueuesRef.push().setValue(ioState);
            }
        } else if (bSocket) {
            webService.setState(id, val, type);
        }
    }

    public LiveData<State> getState(String stateId) {
        Timber.v("getState called");
        return stateDao.getStateById2(stateId);
    }

    private List<String> getAllStateIds() {
        Timber.v("getAllStateIds called");
        return stateDao.getAllObjectIds();
    }

    private List<String> getAllEnumStateIds() {
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

    private void syncObject(String id, IoObject ioObject) {
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

    private void syncState(String id, IoState ioState) {
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

    private void saveSocketState(String state) {
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
                    sendState(id, newVal, state.getType());
                }
        );
    }

    public void saveState(State state) {
        Timber.v("saveState called");
        executor.execute(() -> stateDao.update(state));
    }

    private void deleteState(State state) {
        Timber.v("deleteState called");
        stateDao.delete(state);
    }

    private void setSyncFalseAll() {
        stateDao.setSyncAll(false);
    }

    public void setDevice(String deviceName, String token) {
        FObject fObject = new FObject();
        FCommon fCommon = new FCommon();
        fCommon.setName(deviceName + ".token");
        fCommon.setRead(true);
        fCommon.setWrite(true);
        fCommon.setDesc("device token is used to address push notification to this device");
        fCommon.setType("string");
        fCommon.setRole("text");

        fObject.setId(deviceName + ".token");
        fObject.setType("state");
        fObject.setVal(token);
        fObject.setCommon(fCommon);

        if (dbObjectQueuesRef != null) {
            dbObjectQueuesRef.push().setValue(fObject);
        }
    }

    @Override
    public void onObjectsReceived(String data) {
        saveObjects(data);
        requestStates();
    }

    private void requestStates() {
        List<String> objectIds = getAllStateIds();
        JSONArray json = new JSONArray(objectIds);
        webService.subscribe(json);
        webService.getStates(json, this);
    }

    @Override
    public void onStatesReceived(String data) {
        saveStates(data);
    }
}
