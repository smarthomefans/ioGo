package com.example.nagel.io1.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.model.Room;
import com.example.nagel.io1.service.model.RoomDao;
import com.example.nagel.io1.service.model.State;
import com.example.nagel.io1.service.model.StateDao;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoomRepository {
    public static final String TAG = "RoomRepository";
    private Map<String,LiveData<Room>> roomCache;

    private final RoomDao roomDao;
    private final StateDao stateDao;

    private LiveData<List<Room>> mListRooms;

    @Inject
    public RoomRepository(RoomDao roomDao, StateDao stateDao) {
        this.roomDao = roomDao;
        this.stateDao = stateDao;
        mListRooms = new MutableLiveData<>();
        roomCache = new HashMap<>();

        DataBus.getBus().register(this);

        DataBus.getBus().post(new Events.getEnumObjects());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mListRooms = roomDao.getAllRooms();

                if(mListRooms.getValue() != null){
                    Log.d(TAG, "Constructor roomDao.getAllRooms size:" + mListRooms.getValue().size());
                    for (Room room : mListRooms.getValue()) {
                        MutableLiveData<Room> mRoom = new MutableLiveData<>();
                        mRoom.postValue(room);
                        roomCache.put(room.getId(), mRoom);
                        Log.d(TAG, "Constructor roomCache.put roomId:" + room.getId());
                    }
                }
            }
        });
        Log.i(TAG, "Constructor created");
    }

    public LiveData<Room> getRoom(String roomId){
        if(!roomCache.containsKey(roomId)){
            roomCache.put(roomId, roomDao.getRoomById(roomId));
            Log.d(TAG, "getRoom roomCache.put roomId:" + roomId);
        }
        Log.i(TAG, "getRoom roomId:" + roomId);
        return roomCache.get(roomId);
    }

    public LiveData<List<Room>> getAllRooms(){
        if(mListRooms.getValue() == null) {
            Log.i(TAG, "getAllRooms size:0");
        }else{
            Log.i(TAG, "getAllRooms size:" + mListRooms.getValue().size());
        }
        return mListRooms;
    }

    @Subscribe
    public void saveObjects(final Events.Objects event){
        try {
            JSONObject obj = new JSONObject(event.getData());
            JSONArray arr = obj.getJSONArray("rows");
            Log.d(TAG,"saveObjects arr.length:" + arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                if (item.getString("id").contains("enum.rooms.")) {
                    Room room = new Room(item.getString("id"), item.getJSONObject("value").getJSONObject("common").getString("name"), null);
                    Log.d(TAG,"saveObjects roomId:" + room.getId());
                    List<String> members = new ArrayList<>();
                    JSONArray arrMembers = item.getJSONObject("value").getJSONObject("common").getJSONArray("members");
                    if(arrMembers != null) {
                        Log.d(TAG,"saveObjects arrMembers.length:" + arrMembers.length());
                        for (int j=0; j<arrMembers.length(); j++) {
                            members.add(arrMembers.getString(j));

                            State state = stateDao.getStateById(arrMembers.getString(j));
                            if(state != null) {
                                state.setRoomId(room.getId());
                                stateDao.update(state);
                                Log.d(TAG,"saveObjects stateDao.update stateId:" + state.getId());
                            }
                        }
                        room.setMembers(members);
                    }
                    roomDao.insert(room);
                    Log.d(TAG,"saveObjects roomDao.insert roomId:" + room.getId());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"saveObjects finished");
    }
}
