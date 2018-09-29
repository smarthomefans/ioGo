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

package de.nisnagel.iogo.ui.info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import javax.inject.Inject;

import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class InfoViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public InfoViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<Integer> countStates() {
        return stateRepository.countStates();
    }

    public LiveData<Integer> countRooms() {
        return enumRepository.countRooms();
    }

    public LiveData<Integer> countFunctions() {
        return enumRepository.countFunctions();
    }

    public LiveData<String> getSocketState() {
        return stateRepository.getSocketState();
    }

    public void syncObjects(){
        stateRepository.syncObjects();
    }
}
