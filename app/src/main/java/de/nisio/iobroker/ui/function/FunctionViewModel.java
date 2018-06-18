package de.nisio.iobroker.ui.function;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.repository.EnumRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class FunctionViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public FunctionViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Enum>> getFunctions() {
        return enumRepository.getFunctionEnums();
    }

    public LiveData<Enum> getEnum(String enumId) {
        return enumRepository.getEnum(enumId);
    }

    public LiveData<List<State>> getStates(String functionId) {
        return stateRepository.getStatesByFunction(functionId);
    }

}
