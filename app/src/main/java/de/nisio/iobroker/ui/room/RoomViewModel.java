package de.nisio.iobroker.ui.room;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisio.iobroker.data.model.Room;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.repository.RoomRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class RoomViewModel extends ViewModel {

    private RoomRepository roomRepository;
    private StateRepository stateRepository;

    @Inject
    public RoomViewModel(RoomRepository roomRepository, StateRepository stateRepository) {
        this.roomRepository = roomRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Room>> getRooms() {
        return roomRepository.getAllRooms();
    }

    public LiveData<Room> getRoom(String roomId) {
        return roomRepository.getRoom(roomId);
    }

    public LiveData<List<State>> getStates(String roomId) {
        return stateRepository.getStatesByRoom(roomId);
    }

}
