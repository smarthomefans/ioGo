/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.nisnagel.iogo.ui.detail.StateViewModel;
import de.nisnagel.iogo.ui.history.HistoryViewModel;
import de.nisnagel.iogo.ui.info.InfoViewModel;
import de.nisnagel.iogo.ui.main.EnumViewModel;
import de.nisnagel.iogo.ui.settings.SettingsViewModel;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
    private EnumViewModel mEnumViewModel;
    private StateViewModel mStateViewModel;
    private InfoViewModel mInfoViewModel;
    private SettingsViewModel mSettingsViewModel;
    private HistoryViewModel mHistoryViewModel;

    @Inject
    public ViewModelFactory(EnumViewModel mEnumViewModel, StateViewModel mStateViewModel, InfoViewModel mInfoViewModel, SettingsViewModel mSettingsViewModel, HistoryViewModel mHistoryViewModel) {
        this.mEnumViewModel = mEnumViewModel;
        this.mStateViewModel = mStateViewModel;
        this.mInfoViewModel = mInfoViewModel;
        this.mSettingsViewModel = mSettingsViewModel;
        this.mHistoryViewModel = mHistoryViewModel;
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
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) mSettingsViewModel;
        }
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) mHistoryViewModel;
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}