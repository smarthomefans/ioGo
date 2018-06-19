package de.nisio.iobroker.ui.main;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.data.repository.EnumRepository;
import de.nisio.iobroker.data.repository.StateRepository;

public class EnumViewModel extends ViewModel {

    private EnumRepository enumRepository;
    private StateRepository stateRepository;

    @Inject
    public EnumViewModel(EnumRepository enumRepository, StateRepository stateRepository) {
        this.enumRepository = enumRepository;
        this.stateRepository = stateRepository;
    }

    public LiveData<List<Enum>> getEnums(String type) {
        if(EnumRepository.TYPE_FUNCTION.equals(type)) {
            return enumRepository.getFunctionEnums();
        }else if(EnumRepository.TYPE_ROOM.equals(type)) {
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

    public LiveData<Enum> getEnum(String enumId) {
        return enumRepository.getEnum(enumId);
    }

    public LiveData<List<State>> getStates(String enumId) {
        return stateRepository.getStatesByEnum(enumId);
    }

    public void saveEnum(Enum anEnum) {
        enumRepository.saveEnum(anEnum);
    }

}
