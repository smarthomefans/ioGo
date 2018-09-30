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
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.io.FEnum;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.EnumDao;
import de.nisnagel.iogo.data.model.EnumState;
import de.nisnagel.iogo.data.model.EnumStateDao;
import timber.log.Timber;

@Singleton
public class EnumRepository {

    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_ROOM = "room";
    public static final String ENUMS = "enums/";

    private Map<String, LiveData<Enum>> enumCache;
    private LiveData<List<Enum>> mListFunctionEnums;
    private LiveData<List<Enum>> mListRoomEnums;
    private LiveData<List<Enum>> mListFavoriteEnums;

    private final EnumDao enumDao;
    private final EnumStateDao enumStateDao;
    private Executor executor;
    private Context context;
    private SharedPreferences sharedPref;

    private boolean bFirebase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbEnumsRef;

    @Inject
    public EnumRepository(EnumDao enumDao, EnumStateDao enumStateDao, Executor executor, Context context, SharedPreferences sharedPref) {
        this.enumDao = enumDao;
        this.enumStateDao = enumStateDao;
        this.executor = executor;
        this.context = context;
        this.sharedPref = sharedPref;

        enumCache = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        bFirebase = sharedPref.getBoolean(context.getString(R.string.pref_connect_iogo), false);

        if (bFirebase) {
            initFirebase();
        }

        Timber.v("instance created");
    }

    private void initFirebase() {
        ValueEventListener enumListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        FEnum fEnum = postSnapshot.getValue(FEnum.class);
                        String type;
                        if (postSnapshot.getKey().contains(TYPE_ROOM)) {
                            type = TYPE_ROOM;
                        } else {
                            type = TYPE_FUNCTION;
                        }
                        Enum anEnum = new Enum(fEnum.getId(), fEnum.getName(), type, false, fEnum.getColor(), fEnum.getIcon());
                        syncEnum(anEnum);
                        deleteStateEnum(anEnum);
                        for (String member : fEnum.getMembers()) {
                            EnumState enumState = new EnumState(anEnum.getId(), member);
                            syncEnumState(enumState);
                            Timber.d("saveEnums: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                        }
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

        ChildEventListener enumChildListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
                        syncEnum(anEnum);
                        deleteStateEnum(anEnum);
                        for (String member : fEnum.getMembers()) {
                            EnumState enumState = new EnumState(anEnum.getId(), member);
                            syncEnumState(enumState);
                            Timber.d("saveEnums: enum linked to state enumId:" + enumState.getEnumId() + " stateId:" + enumState.getStateId());
                        }
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
                dbEnumsRef = database.getReference(ENUMS + user.getUid());
                dbEnumsRef.addListenerForSingleValueEvent(enumListener);
                dbEnumsRef.addChildEventListener(enumChildListener);

            }
        };

        mAuth.addAuthStateListener(authListener);
    }

    public LiveData<Enum> getEnum(String enumId) {
        Timber.v("getEnum called");
        if (!enumCache.containsKey(enumId)) {
            enumCache.put(enumId, enumDao.getEnumById(enumId));
            Timber.d("getEnum: load enum from database enumId:" + enumId);
        }
        return enumCache.get(enumId);
    }

    public List<Enum> getEnumsByType(String type) {
        Timber.v("getEnumsByType called");
        return enumDao.getEnumsByType(type);
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

    public void syncEnum(Enum item) {
        Timber.v("syncEnum called");
        executor.execute(() -> enumDao.insert(item));
    }

    public void deleteEnum(Enum item) {
        Timber.v("deleteEnum called");
        enumDao.delete(item);
    }

    public void deleteStateEnum(Enum item) {
        Timber.v("deleteStateEnum called");
        executor.execute(() -> enumStateDao.deleteByEnum(item.getId()));
    }

    public void syncEnumState(EnumState item) {
        Timber.v("syncEnumState called");
        executor.execute(() -> enumStateDao.insert(item));
    }

    public void saveEnum(Enum... anEnum) {
        Timber.v("saveEnum called");
        executor.execute(() -> enumDao.update(anEnum));
    }

}
