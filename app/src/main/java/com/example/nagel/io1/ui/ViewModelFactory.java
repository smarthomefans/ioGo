package com.example.nagel.io1.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.nagel.io1.ui.function.FunctionViewModel;
import com.example.nagel.io1.ui.info.InfoViewModel;
import com.example.nagel.io1.ui.room.RoomViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private RoomViewModel mRoomViewModel;
    private FunctionViewModel mFunctionViewModel;
    private InfoViewModel mInfoViewModel;

    @Inject
    public ViewModelFactory(RoomViewModel mRoomViewModel, FunctionViewModel mFunctionViewModel, InfoViewModel mInfoViewModel) {
        this.mRoomViewModel = mRoomViewModel;
        this.mFunctionViewModel = mFunctionViewModel;
        this.mInfoViewModel = mInfoViewModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RoomViewModel.class)) {
            return (T) mRoomViewModel;
        }
        if (modelClass.isAssignableFrom(FunctionViewModel.class)) {
            return (T) mFunctionViewModel;
        }
        if (modelClass.isAssignableFrom(InfoViewModel.class)) {
            return (T) mInfoViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}