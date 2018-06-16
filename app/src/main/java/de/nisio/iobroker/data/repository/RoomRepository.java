package de.nisio.iobroker.data.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisio.iobroker.data.io.IoCommon;
import de.nisio.iobroker.data.io.IoEnum;
import de.nisio.iobroker.data.io.IoRow;
import de.nisio.iobroker.data.io.IoValue;
import de.nisio.iobroker.data.model.Room;
import de.nisio.iobroker.data.model.RoomDao;
import de.nisio.iobroker.data.model.RoomState;
import de.nisio.iobroker.data.model.RoomStateDao;

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

    public LiveData<Integer> countRooms(){
        return roomDao.countRooms();
    }

    public void deleteAll(){
        roomDao.deleteAll();
        roomStateDao.deleteAll();
    }

    public void saveObjects(String data){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        IoEnum ioEnum = gson.fromJson(data, IoEnum.class);
        for(IoRow ioRow : ioEnum.getRows()){
            IoValue ioValue = ioRow.getValue();
            IoCommon ioCommon = ioValue.getCommon();
            Room room = new Room(ioValue.getId(), ioCommon.getName(), ioCommon.getMembers());
            roomDao.insert(room);
            for (int j = 0; j < ioCommon.getMembers().size(); j++) {
                RoomState roomState = new RoomState(room.getId(), ioCommon.getMembers().get(j));
                roomStateDao.insert(roomState);
            }

            Log.d(TAG, "saveObjects getId:" + ioValue.getId());
        }

        Log.i(TAG, "saveObjects finished");
    }
}
