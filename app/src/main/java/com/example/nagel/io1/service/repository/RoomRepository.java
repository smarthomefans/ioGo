package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoomRepository {
    private Map<String,MutableLiveData<Room>> roomCache;

    private final RoomDao roomDao;

    private MutableLiveData<List<Room>> mListRooms;

    @Inject
    public RoomRepository(RoomDao roomDao) {
        this.roomDao = roomDao;
        mListRooms = new MutableLiveData<>();
        roomCache = new HashMap<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Room> ls;
                ls = roomDao.getAllRooms();
                mListRooms.setValue(ls);

                if(ls != null){
                    for (Room room : ls) {
                        MutableLiveData<Room> mRoom = new MutableLiveData<>();
                        mRoom.setValue(room);
                        roomCache.put(room.getId(), mRoom);
                    }
                }
            }
        });
    }

    public LiveData<Room> getRoom(String roomId){
        if(!roomCache.containsKey(roomId)){
            roomCache.put(roomId, new MutableLiveData<>());
        }
        return roomCache.get(roomId);
    }

    public LiveData<List<Room>> getAllRooms(){
        return mListRooms;
    }

}
