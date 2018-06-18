package de.nisio.iobroker.ui.info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import javax.inject.Inject;

import de.nisio.iobroker.data.repository.EnumRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class InfoViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public InfoViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<Integer> countStates() {
        return stateRepository.countStates();
    }

    public LiveData<Integer> countRooms() {
        return enumRepository.countRooms();
    }

    public LiveData<Integer> countFunctions() {
        return enumRepository.countFunctions();
    }

    public LiveData<String> getSocketState() {
        return stateRepository.getSocketState();
    }

    public void clearDatabase() {
        stateRepository.deleteAll();
        enumRepository.deleteAll();
    }
}
