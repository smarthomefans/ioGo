package com.example.nagel.io1.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.nagel.io1.ui.function.FunctionViewModel;
import com.example.nagel.io1.ui.room.RoomViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private RoomViewModel mRoomViewModel;
    private FunctionViewModel mFunctionViewModel;

    @Inject
    public ViewModelFactory(RoomViewModel mRoomViewModel, FunctionViewModel mFunctionViewModel) {
        this.mRoomViewModel = mRoomViewModel;
        this.mFunctionViewModel = mFunctionViewModel;
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
        throw new IllegalArgumentException("Unknown class name");
    }
}