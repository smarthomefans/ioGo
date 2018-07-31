package de.nisnagel.iogo.ui.detail;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class StateViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public StateViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<State> getState(String stateId) {
        return stateRepository.getState(stateId);
    }

    public void saveState(State state) {
        stateRepository.saveState(state);
    }
    public void changeState(String id, String newVal) {
        stateRepository.changeState(id, newVal);
    }

}
