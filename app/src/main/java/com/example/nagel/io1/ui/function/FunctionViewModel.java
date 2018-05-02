package com.example.nagel.io1.ui.function;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nagel.io1.data.model.Function;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.data.repository.FunctionRepository;
import com.example.nagel.io1.data.repository.StateRepository;

import java.util.List;

import javax.inject.Inject;

public class FunctionViewModel extends ViewModel {

    private FunctionRepository functionRepository;
    private StateRepository stateRepository;

    private MutableLiveData<String> mFunctionName;

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

    public LiveData<String> getFunctionName(String functionId){
        mFunctionName.setValue(functionRepository.getFunction(functionId).getValue().getName());
        return mFunctionName;
    }

    public LiveData<List<State>> getStates(String functionId) {
        return stateRepository.getStatesByFunction(functionId);
    }

}
