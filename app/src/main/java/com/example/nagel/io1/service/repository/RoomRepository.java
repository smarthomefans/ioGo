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
    private Map<String,MutableLiveData<Room>> roomCache;

    private final RoomDao roomDao;
    private final StateDao stateDao;

    private MutableLiveData<List<Room>> mListRooms;

    @Inject
    public RoomRepository(RoomDao roomDao, StateDao stateDao) {
        this.roomDao = roomDao;
        this.stateDao = stateDao;
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
                    JSONArray arrMembers = item.getJSONObject("value").getJSONObject("common").getJSONArray("members");
                    if(arrMembers != null) {
                        for (int j=0; j<arrMembers.length(); j++) {
                            members.add(arrMembers.getString(j));

                            State state = stateDao.getStateById(arrMembers.getString(j));
                            if(state != null) {
                                state.setRoomId(room.getId());
                                stateDao.update(state);
                            }
                        }
                        room.setMembers(members);



                    }
                    roomDao.insert(room);
                }
            }
            Log.i("onConnect","receiving objects");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
