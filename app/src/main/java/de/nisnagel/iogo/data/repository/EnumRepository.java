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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.io.FEnum;
import de.nisnagel.iogo.data.io.IoCommon;
import de.nisnagel.iogo.data.io.IoEnum;
import de.nisnagel.iogo.data.io.IoName;
import de.nisnagel.iogo.data.io.IoRow;
import de.nisnagel.iogo.data.io.IoValue;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.EnumDao;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import timber.log.Timber;

@Singleton
public class EnumRepository implements OnEnumReceived{

    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_ROOM = "room";
    private static final String PATH_ENUMS = "enums/";

    private final EnumDao enumDao;
    private final EnumStateDao enumStateDao;
    private Executor executor;
    private Context context;
    private SharedPreferences sharedPref;
    private WebService webService;

    private boolean bFirebase;
    private boolean bSocket;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbEnumsRef;
    private ValueEventListener enumListener;
    private ChildEventListener enumChildListener;

    @Inject
    public EnumRepository(EnumDao enumDao, EnumStateDao enumStateDao, Executor executor, Context context, SharedPreferences sharedPref, WebService webService) {
        this.enumDao = enumDao;
        this.enumStateDao = enumStateDao;
        this.executor = executor;
        this.context = context;
        this.sharedPref = sharedPref;
        this.webService = webService;

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
        enumListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    saveEnums(postSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        enumChildListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                saveEnums(dataSnapshot);
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

    private void addListener(FirebaseUser user) {
        dbEnumsRef = database.getReference(PATH_ENUMS + user.getUid());
        boolean bSync = sharedPref.getBoolean(context.getString(R.string.pref_layout_object_sync), false);
        if(bSync) {
            dbEnumsRef.addListenerForSingleValueEvent(enumListener);
        }
        dbEnumsRef.addChildEventListener(enumChildListener);
    }

    private void initSocket() {
        if (webService.isConnected()) {
            executor.execute(this::initialLoad);
        } else {
            executor.execute(() -> {
                webService.init();
                webService.on(Socket.EVENT_CONNECT, onConnect);
                webService.on(Socket.EVENT_DISCONNECT, onDisconnect);
                webService.start();
            });
        }
    }

    private Emitter.Listener onConnect = args -> executor.execute(this::initialLoad);

    private Emitter.Listener onDisconnect = args -> {
        Timber.i("disconnected");
    };

    private void initialLoad() {
        webService.getEnumObjects("enum.rooms", EnumRepository.TYPE_ROOM, this);
        webService.getEnumObjects("enum.functions", EnumRepository.TYPE_FUNCTION, this);
    }

    private void saveEnums(String data, String type) {
        Timber.v("saveEnums called");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IoName.class, IoName.getDeserializer());
        Gson gson = gsonBuilder.create();
        Set<String> enumSet = new HashSet();

        try {
            IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
            for (IoRow ioRow : ioEnum.getRows()) {
                IoValue ioValue = ioRow.getValue();
                IoCommon ioCommon = ioValue.getCommon();
                Enum anEnum = new Enum(ioValue.getId(), ioCommon.getName(), type, false, ioCommon.getColor(), ioCommon.getIcon());
                insertEnum(anEnum);
                deleteStateEnum(anEnum);
                Timber.d("saveEnums: enum inserted enumId:" + anEnum.getId());
                if (ioCommon.getMembers() != null) {
                    for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                        EnumState enumState = new EnumState(anEnum.getId(), ioCommon.getMembers().get(j));
                        insertEnumState(enumState);
                        Timber.d("saveEnums: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                    }
                } else {
                    Timber.i("saveEnums: no members found for enumId:" + anEnum.getId());
                }
                enumSet.add(anEnum.getId());

                Timber.d("saveEnums: enum saved enumId:" + anEnum.getId());
            }

            List<Enum> enumList = enumDao.getEnumsByType(type);
            for (Enum anEnum : enumList) {
                if (!enumSet.contains(anEnum.getId())) {
                    deleteStateEnum(anEnum);
                    deleteEnum(anEnum);
                    Timber.d("saveEnums: enum deleted enumId:" + anEnum.getId());
                }
            }

        } catch (Throwable e) {
            Timber.e(e);
        }

        Timber.v("saveEnums finished");
    }

    private void saveEnums(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            try {
                FEnum fEnum = dataSnapshot.getValue(FEnum.class);
                String type;
                if (dataSnapshot.getKey().contains(TYPE_ROOM)) {
                    type = TYPE_ROOM;
                } else {
                    type = TYPE_FUNCTION;
                }
                Enum anEnum = new Enum(fEnum.getId(), fEnum.getName(), type, false, fEnum.getColor(), fEnum.getIcon());
                insertEnum(anEnum);
                deleteStateEnum(anEnum);
                for (String member : fEnum.getMembers()) {
                    EnumState enumState = new EnumState(anEnum.getId(), member);
                    insertEnumState(enumState);
                    Timber.d("saveEnums: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                }
            } catch (Throwable t) {
                Timber.e(dataSnapshot.getKey(), t);
            }
        }
    }

    public LiveData<Enum> getEnum(String enumId) {
        Timber.v("getEnum called");
        return enumDao.getEnumById(enumId);
    }

    public LiveData<List<Enum>> getFunctionEnums() {
        Timber.v("getFunctionEnums called");
        return enumDao.getFunctionEnums();
    }

    public LiveData<List<Enum>> getRoomEnums() {
        Timber.v("getRoomEnums called");
        return enumDao.getRoomEnums();
    }

    public LiveData<List<Enum>> getFavoriteEnums() {
        Timber.v("getFavoriteEnums called");
        return enumDao.getFavoriteEnums();
    }

    public LiveData<Integer> countFunctions() {
        Timber.v("countFunctions called");
        return enumDao.countFunctionEnums();
    }

    public LiveData<Integer> countRooms() {
        Timber.v("countRooms called");
        return enumDao.countRoomEnums();
    }

    private void insertEnum(Enum item) {
        Timber.v("insertEnum called");
        executor.execute(() -> enumDao.insert(item));
    }

    public void saveEnum(Enum... anEnum) {
        Timber.v("saveEnum called");
        executor.execute(() -> enumDao.update(anEnum));
    }

    private void deleteEnum(Enum item) {
        Timber.v("deleteEnum called");
        enumDao.delete(item);
    }

    private void insertEnumState(EnumState item) {
        Timber.v("insertEnumState called");
        executor.execute(() -> enumStateDao.insert(item));
    }

    private void deleteStateEnum(Enum item) {
        Timber.v("deleteStateEnum called");
        executor.execute(() -> enumStateDao.deleteByEnum(item.getId()));
    }

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
                        if (dbEnumsRef != null) {
                            dbEnumsRef.removeEventListener(enumListener);
                            dbEnumsRef.removeEventListener(enumChildListener);
                        }
                    } catch (Throwable t) {
                        Timber.e(t);
                    }
                }
            }
        }
    };

    @Override
    public void onEnumReceived(String data, String type) {
        saveEnums(data, type);
    }
}
