package de.nisnagel.iogo.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.ui.detail.StateViewModel;
import de.nisnagel.iogo.ui.info.InfoViewModel;
import de.nisnagel.iogo.ui.main.EnumViewModel;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private EnumViewModel mEnumViewModel;
    private StateViewModel mStateViewModel;
    private InfoViewModel mInfoViewModel;

    @Inject
    public ViewModelFactory(EnumViewModel mEnumViewModel, StateViewModel mStateViewModel, InfoViewModel mInfoViewModel) {
        this.mEnumViewModel = mEnumViewModel;
        this.mStateViewModel = mStateViewModel;
        this.mInfoViewModel = mInfoViewModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EnumViewModel.class)) {
            return (T) mEnumViewModel;
        }
        if (modelClass.isAssignableFrom(StateViewModel.class)) {
            return (T) mStateViewModel;
        }
        if (modelClass.isAssignableFrom(InfoViewModel.class)) {
            return (T) mInfoViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}