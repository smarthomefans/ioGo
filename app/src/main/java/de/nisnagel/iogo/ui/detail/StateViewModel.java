package de.nisnagel.iogo.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class StateViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;
    private String stateId;
    private String value;

    @Inject
    public StateViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<State> getState(String stateId) {
        this.stateId = stateId;
        return stateRepository.getState(stateId);
    }

    public LiveData<Enum> getEnum(String enumId) {
        return enumRepository.getEnum(enumId);
    }

    public LiveData<StateHistory> getHistory(String stateId) {
        return stateRepository.getHistory(stateId);
    }

    public void saveState(State state) {
        stateRepository.saveState(state);
    }

    public void changeValue(String newVal) {
        this.value = newVal;
        stateRepository.changeState(this.stateId, newVal);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
