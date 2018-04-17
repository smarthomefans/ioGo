package com.example.nagel.io1.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.nagel.io1.App;
import com.example.nagel.io1.service.model.AppDatabase;
import com.example.nagel.io1.service.model.FunctionDao;
import com.example.nagel.io1.service.model.RoomDao;
import com.example.nagel.io1.service.repository.RoomRepository;
import com.example.nagel.io1.service.model.StateDao;
import com.example.nagel.io1.service.repository.StateRepository;
import com.example.nagel.io1.viewmodel.RoomViewModel;
import com.example.nagel.io1.viewmodel.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is where you will inject application-wide dependencies.
 */
@Module
public class AppModule {

    @Provides
    Context provideContext(App application) {
        return application.getApplicationContext();
    }

    @Provides
    ViewModelProvider.Factory provideViewModelFactory(
            ViewModelFactory factory
    ) {
        return factory;
    }

    @Provides
    ViewModel provideRoomViewModel(RoomViewModel viewModel) {
        return viewModel;
    }

    @Singleton
    @Provides
    AppDatabase provideDb(App app) {
        return Room.databaseBuilder(app, AppDatabase.class,"appDatabase.db").build();
    }

    @Singleton
    @Provides
    StateRepository provideStateRepository(StateDao stateDao) {
        return new StateRepository(stateDao);
    }

    @Singleton
    @Provides
    RoomRepository provideRoomRepository(RoomDao roomDao, StateDao stateDao) {
        return new RoomRepository(roomDao, stateDao);
    }

    @Singleton
    @Provides
    StateDao provideStateDao(AppDatabase db) {
        return db.getStateDao();
    }

    @Singleton
    @Provides
    RoomDao provideRoomDao(AppDatabase db) {
        return db.getRoomDao();
    }

    @Singleton
    @Provides
    FunctionDao provideFunctionDao(AppDatabase db) {
        return db.getFunctionDao();
    }


}