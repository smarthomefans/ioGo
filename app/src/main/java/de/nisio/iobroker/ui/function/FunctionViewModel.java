package de.nisio.iobroker.ui.function;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisio.iobroker.data.model.Function;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.repository.FunctionRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class FunctionViewModel extends ViewModel {

    private FunctionRepository functionRepository;
    private StateRepository stateRepository;

    @Inject
    public FunctionViewModel(FunctionRepository functionRepository, StateRepository stateRepository) {
        this.functionRepository = functionRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Function>> getFunctions() {
        return functionRepository.getAllFunctions();
    }

    public LiveData<Function> getFunction(String functionId) {
        return functionRepository.getFunction(functionId);
    }

    public LiveData<List<State>> getStates(String functionId) {
        return stateRepository.getStatesByFunction(functionId);
    }

}
