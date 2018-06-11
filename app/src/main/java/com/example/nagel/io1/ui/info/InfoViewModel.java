package com.example.nagel.io1.ui.info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nagel.io1.data.model.Room;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.repository.FunctionRepository;
import com.example.nagel.io1.data.repository.RoomRepository;
import com.example.nagel.io1.data.repository.StateRepository;

import java.util.List;

import javax.inject.Inject;

public class InfoViewModel extends ViewModel {

    private RoomRepository roomRepository;
    private FunctionRepository functionRepository;
    private StateRepository stateRepository;

    @Inject
    public InfoViewModel(RoomRepository roomRepository, FunctionRepository functionRepository, StateRepository stateRepository) {
        this.roomRepository = roomRepository;
        this.functionRepository = functionRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<Integer> countStates() {
        return stateRepository.countStates();
    }

    public LiveData<Integer> countRooms() {
        return roomRepository.countRooms();
    }

    public LiveData<Integer> countFunctions() {
        return functionRepository.countFunctions();
    }

}
