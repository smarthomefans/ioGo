package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
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

        DataBus.getBus().register(this);

        DataBus.getBus().post(new Events.getObjects());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Room> ls;
                ls = roomDao.getAllRooms();
                mListRooms.postValue(ls);

                if(ls != null){
                    for (Room room : ls) {
                        MutableLiveData<Room> mRoom = new MutableLiveData<>();
                        mRoom.postValue(room);
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

    @Subscribe
    public void saveObjects(final Events.Objects event){
        try {
            JSONObject obj = new JSONObject(event.getData());
            JSONArray arr = obj.getJSONArray("rows");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                if (item.getString("id").contains("enum.rooms.")) {
                    Room room = new Room(item.getString("id"), item.getJSONObject("value").getJSONObject("common").getString("name"), null);
                    List<String> members = new ArrayList<>();
                    JSONObject mm = item.getJSONObject("value").getJSONObject("members");
                    //members.add(mm.)
                    //room.setMembers();
                    roomDao.insert(room);
                }
            }
            Log.i("onConnect","receiving objects");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
