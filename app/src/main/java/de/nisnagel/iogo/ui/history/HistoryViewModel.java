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

package de.nisnagel.iogo.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.data.repository.StateHistoryRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class HistoryViewModel extends ViewModel {

    private StateRepository stateRepository;
    private StateHistoryRepository stateHistoryRepository;
    private String value;

    @Inject
    public HistoryViewModel(StateRepository stateRepository, StateHistoryRepository stateHistoryRepository) {
        this.stateRepository = stateRepository;
        this.stateHistoryRepository = stateHistoryRepository;
    }

    public LiveData<State> getState(String stateId) {
        return stateRepository.getState(stateId);
    }

    public LiveData<StateHistory> getHistory(String stateId) {
        return stateHistoryRepository.getHistory(stateId);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
