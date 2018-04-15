package com.example.nagel.io1.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nagel.io1.service.repository.Room;
import com.example.nagel.io1.service.repository.RoomRepository;
import com.example.nagel.io1.service.repository.State;
import com.example.nagel.io1.service.repository.StateRepository;

import java.util.List;

import javax.inject.Inject;

public class RoomViewModel extends ViewModel {

    private RoomRepository roomRepository;
    private StateRepository stateRepository;

    private MutableLiveData<String> mRoomName;

    @Inject
    public RoomViewModel(RoomRepository roomRepository, StateRepository stateRepository) {
        this.roomRepository = roomRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Room>> getRooms() {
        return roomRepository.getAllRooms();
    }

    public LiveData<String> getRoomName(String roomId){
        mRoomName.setValue(roomRepository.getRoom(roomId).getValue().getName());
        return mRoomName;
    }

    public LiveData<List<State>> getStates(String roomId) {
        return stateRepository.getStatesByRoom(roomId);
    }

}
