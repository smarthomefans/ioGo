package com.example.nagel.io1.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nagel.io1.service.model.IoState;
import com.example.nagel.io1.service.repository.StateRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class ListViewModel extends ViewModel {


    private StateRepository stateRepository;

    private MutableLiveData<String> mCurrentName;

    @Inject
    public ListViewModel(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public MutableLiveData<List<IoState>> getTempList() {
        return stateRepository.getTempList();
    }

    public MutableLiveData<String> getCurrentName() {
        if (mCurrentName == null) {
            mCurrentName = new MutableLiveData<String>();
        }
        return mCurrentName;
    }
}
