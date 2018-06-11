package com.example.nagel.io1.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.nagel.io1.App;
import com.example.nagel.io1.data.model.AppDatabase;
import com.example.nagel.io1.data.model.FunctionDao;
import com.example.nagel.io1.data.model.FunctionStateDao;
import com.example.nagel.io1.data.model.RoomDao;
import com.example.nagel.io1.data.model.RoomStateDao;
import com.example.nagel.io1.data.repository.FunctionRepository;
import com.example.nagel.io1.data.repository.RoomRepository;
import com.example.nagel.io1.data.model.StateDao;
import com.example.nagel.io1.data.repository.StateRepository;
import com.example.nagel.io1.ui.function.FunctionViewModel;
import com.example.nagel.io1.ui.info.InfoViewModel;
import com.example.nagel.io1.ui.room.RoomViewModel;
import com.example.nagel.io1.ui.ViewModelFactory;

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

    @Provides
    ViewModel provideFunctionViewModel(FunctionViewModel viewModel) {
        return viewModel;
    }

    @Provides
    ViewModel provideInfoViewModel(InfoViewModel viewModel) {
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
    RoomRepository provideRoomRepository(RoomDao roomDao, RoomStateDao roomStateDao) {
        return new RoomRepository(roomDao, roomStateDao);
    }

    @Singleton
    @Provides
    FunctionRepository provideFunctionRepository(FunctionDao functionDao, FunctionStateDao functionStateDao) {
        return new FunctionRepository(functionDao, functionStateDao);
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

    @Singleton
    @Provides
    FunctionStateDao provideFunctionStateDao(AppDatabase db) {
        return db.getFunctionStateDao();
    }

    @Singleton
    @Provides
    RoomStateDao provideRoomStateDao(AppDatabase db) {
        return db.getRoomStateDao();
    }
}