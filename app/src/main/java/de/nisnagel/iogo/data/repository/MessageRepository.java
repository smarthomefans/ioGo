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

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.data.io.FMessage;
import de.nisnagel.iogo.data.model.Message;
import de.nisnagel.iogo.data.model.MessageDao;
import timber.log.Timber;

@Singleton
public class MessageRepository extends BaseRepository {

    private static final String MESSAGES = "messages/";

    private final MessageDao messageDao;
    private DatabaseReference dbMessagesRef;
    private ChildEventListener messageChildListener;

    @Inject
    public MessageRepository(MessageDao messageDao, Executor executor, Context context, SharedPreferences sharedPref) {
        super(executor, context, sharedPref, null);
        this.messageDao = messageDao;
        Timber.v("instance created");
    }

    void initFirebase() {

        messageChildListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                saveMessages(dataSnapshot);
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
            } else {
                removeListener();
            }
        };

        if (mAuth.getCurrentUser() != null) {
            addListener(mAuth.getCurrentUser());
        }
        mAuth.addAuthStateListener(authListener);
    }

    public String getFirebaseUid() {
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    void removeListener() {
        try {
            if (dbMessagesRef != null) {
                dbMessagesRef.removeEventListener(messageChildListener);
            }
        } catch (Throwable t) {
            Timber.e(t);
        }
    }

    private void addListener(FirebaseUser user) {
        if (dbMessagesRef == null) {
            dbMessagesRef = database.getReference(MESSAGES + user.getUid());
        }
        dbMessagesRef.removeEventListener(messageChildListener);
        dbMessagesRef.addChildEventListener(messageChildListener);
    }

    @Override
    void initWeb() {

    }

    @Override
    void initCloud() {

    }

    public boolean isPro() {
        return sharedPref.getBoolean("pro", false);
    }

    private void saveMessages(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            try {
                FMessage fMessagge = dataSnapshot.getValue(FMessage.class);
                Message message = new Message(dataSnapshot.getKey(), fMessagge.getTitle(), fMessagge.getText(), fMessagge.getImg(), fMessagge.getTs());
                insertMessage(message);
            } catch (Throwable t) {
                Timber.e(dataSnapshot.getKey(), t);
            }
            dbMessagesRef.child(dataSnapshot.getKey()).removeValue();
        }
    }

    public LiveData<List<Message>> getMessages() {
        Timber.v("getRoomEnums called");
        return messageDao.getAllMessages();
    }

    private void insertMessage(Message msg) {
        Timber.v("insertMessage called");
        executor.execute(() -> messageDao.insert(msg));
    }

    public void delete(Message msg) {
        Timber.v("delete called");
        executor.execute(() -> messageDao.delete(msg));
    }
}
