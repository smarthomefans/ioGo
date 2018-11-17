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
import de.nisnagel.iogo.data.model.Message;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.MessageRepository;
import de.nisnagel.iogo.data.repository.StateRepository;

public class MessageViewModel extends ViewModel {

    private MessageRepository messageRepository;

    @Inject
    public MessageViewModel(MessageRepository messageRepository, SharedPreferences sharedPref) {
        this.messageRepository = messageRepository;
    }

    public LiveData<List<Message>> getMessages() {
        return messageRepository.getMessages();
    }

    public void deleteMessage(Message anMessage) {
        messageRepository.delete(anMessage);
    }

}
