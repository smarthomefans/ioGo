package com.example.nagel.io1.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private RoomViewModel mRoomViewModel;

    @Inject
    public ViewModelFactory(RoomViewModel mRoomViewModel) {
        this.mRoomViewModel = mRoomViewModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RoomViewModel.class)) {
            return (T) mRoomViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}