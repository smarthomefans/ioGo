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

package de.nisnagel.iogo.ui.main;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class EnumViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;
    private String enumId;

    @Inject
    public EnumViewModel(EnumRepository enumRepository, StateRepository stateRepository, SharedPreferences sharedPref) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Enum>> getEnums(String type) {
        if (EnumRepository.TYPE_FUNCTION.equals(type)) {
            return enumRepository.getFunctionEnums();
        } else if (EnumRepository.TYPE_ROOM.equals(type)) {
            return enumRepository.getRoomEnums();
        }
        return null;
    }

    public LiveData<List<Enum>> getFunctions() {
        return enumRepository.getFunctionEnums();
    }

    public LiveData<List<Enum>> getFavoriteEnums() {
        return enumRepository.getFavoriteEnums();
    }

    public LiveData<List<State>> getFavoriteStates() {
        return stateRepository.getFavoriteStates();
    }

    public LiveData<Enum> getEnum(String enumId) {
        this.enumId = enumId;
        return enumRepository.getEnum(enumId);
    }

    public LiveData<List<State>> getStates(String enumId) {
        return stateRepository.getStatesByEnum(enumId);
    }

    public void saveEnum(Enum... anEnum) {
        enumRepository.saveEnum(anEnum);
    }

    public void saveState(State state) {
        stateRepository.saveState(state);
    }

    public void changeState(String id, String newVal) {
        stateRepository.changeState(id, newVal);
    }

    public String getEnumId() {
        return enumId;
    }

    public boolean hasConnection(){
        return stateRepository.hasConnection();
    }
}
