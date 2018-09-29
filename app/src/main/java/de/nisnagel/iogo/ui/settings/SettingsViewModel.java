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

package de.nisnagel.iogo.ui.settings;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.nisnagel.iogo.data.repository.StateRepository;
import timber.log.Timber;

public class SettingsViewModel extends ViewModel {

    private StateRepository stateRepository;

    @Inject
    public SettingsViewModel(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public void setDevice(String deviceName, String token) {
        Timber.v("setDevice called");
        stateRepository.setDevice(deviceName, token);
    }

    public void activateWeb(){
        Timber.v("activateWeb called");
    }

    public void activateCloud(){
        Timber.v("activateCloud called");
    }

    public void activateIogo(){
        Timber.v("activateIogo called");
    }
}
