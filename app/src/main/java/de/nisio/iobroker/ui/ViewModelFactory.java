package de.nisio.iobroker.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisio.iobroker.ui.function.FunctionViewModel;
import de.nisio.iobroker.ui.info.InfoViewModel;
import de.nisio.iobroker.ui.room.RoomViewModel;

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