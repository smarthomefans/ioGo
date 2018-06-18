package de.nisio.iobroker.ui.room;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.repository.EnumRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class RoomViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public RoomViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Enum>> getRooms() {
        return enumRepository.getRoomEnums();
    }

    public LiveData<List<Enum>> getFavoriteEnums() {
        return enumRepository.getFavoriteEnums();
    }

    public LiveData<Enum> getRoom(String roomId) {
        return enumRepository.getEnum(roomId);
    }

    public LiveData<List<State>> getStates(String roomId) {
        return stateRepository.getStatesByRoom(roomId);
    }

    public void flagFavorite(String roomId, boolean checked) {
        enumRepository.flagFavorite(roomId,checked);
    }

    public boolean isFavorite(String roomId) {
        return true;
    }
}
