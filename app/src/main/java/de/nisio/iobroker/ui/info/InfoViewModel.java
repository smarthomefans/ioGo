package de.nisio.iobroker.ui.info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import javax.inject.Inject;

import de.nisio.iobroker.data.repository.FunctionRepository;
import de.nisio.iobroker.data.repository.RoomRepository;
import de.nisio.iobroker.data.repository.StateRepository;

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

    public LiveData<String> getSocketState() {
        return stateRepository.getSocketState();
    }
}
