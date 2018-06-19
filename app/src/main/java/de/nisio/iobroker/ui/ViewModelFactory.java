package de.nisio.iobroker.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisio.iobroker.ui.info.InfoViewModel;
import de.nisio.iobroker.ui.main.EnumViewModel;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private EnumViewModel mEnumViewModel;
    private InfoViewModel mInfoViewModel;

    @Inject
    public ViewModelFactory(EnumViewModel mEnumViewModel, InfoViewModel mInfoViewModel) {
        this.mEnumViewModel = mEnumViewModel;
        this.mInfoViewModel = mInfoViewModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EnumViewModel.class)) {
            return (T) mEnumViewModel;
        }
        if (modelClass.isAssignableFrom(InfoViewModel.class)) {
            return (T) mInfoViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}