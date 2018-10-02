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

import java.util.List;
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
    private static final String PATH_ENUMS = "enums/";

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
                    refreshEnum(postSnapshot);
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
                refreshEnum(dataSnapshot);
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
                dbEnumsRef = database.getReference(PATH_ENUMS + user.getUid());
                dbEnumsRef.addListenerForSingleValueEvent(enumListener);
                dbEnumsRef.addChildEventListener(enumChildListener);

            }
        };

        mAuth.addAuthStateListener(authListener);
    }

    private void refreshEnum(DataSnapshot dataSnapshot){
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

    public List<Enum> getEnumsByType(String type) {
        Timber.v("getEnumsByType called");
        return enumDao.getEnumsByType(type);
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

    public void insertEnum(Enum item) {
        Timber.v("insertEnum called");
        executor.execute(() -> enumDao.insert(item));
    }

    public void saveEnum(Enum... anEnum) {
        Timber.v("saveEnum called");
        executor.execute(() -> enumDao.update(anEnum));
    }

    public void deleteEnum(Enum item) {
        Timber.v("deleteEnum called");
        enumDao.delete(item);
    }

    public void insertEnumState(EnumState item) {
        Timber.v("insertEnumState called");
        executor.execute(() -> enumStateDao.insert(item));
    }

    public void deleteStateEnum(Enum item) {
        Timber.v("deleteStateEnum called");
        executor.execute(() -> enumStateDao.deleteByEnum(item.getId()));
    }

}
