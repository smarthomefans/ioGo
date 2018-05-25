package com.example.nagel.io1.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.nagel.io1.data.io.Common;
import com.example.nagel.io1.data.io.IoEnum;
import com.example.nagel.io1.data.io.Row;
import com.example.nagel.io1.data.io.Value;
import com.example.nagel.io1.data.model.Room;
import com.example.nagel.io1.data.model.RoomDao;
import com.example.nagel.io1.data.model.RoomState;
import com.example.nagel.io1.data.model.RoomStateDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private final RoomStateDao roomStateDao;

    private LiveData<List<Room>> mListRooms;

    @Inject
    public RoomRepository(RoomDao roomDao, RoomStateDao roomStateDao) {
        this.roomDao = roomDao;
        this.roomStateDao = roomStateDao;
        roomCache = new HashMap<>();
    }

    public LiveData<Room> getRoom(String roomId){
        if(!roomCache.containsKey(roomId)){
            roomCache.put(roomId, roomDao.getRoomById(roomId));
            Log.d(TAG, "getRoom roomCache.put roomId:" + roomId);
        }
        return roomCache.get(roomId);
    }

    public LiveData<List<Room>> getAllRooms(){
        if(mListRooms == null){
            mListRooms = roomDao.getAllRooms();
        }
        return mListRooms;
    }

    public List<String> getAllStateIds(){
        return roomStateDao.getAllStateIds();
    }

    public void saveObjects(String data){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
        for(Row row : ioEnum.getRows()){
            Value value = row.getValue();
            Common common = value.getCommon();
            Room room = new Room(value.getId(), common.getName(), common.getMembers());
            roomDao.insert(room);
            for (int j = 0; j < common.getMembers().size(); j++) {
                RoomState roomState = new RoomState(room.getId(), common.getMembers().get(j));
                roomStateDao.insert(roomState);
            }

            Log.d(TAG, "saveObjects getId:" + value.getId());
        }

        Log.i(TAG, "saveObjects finished");
    }
}
