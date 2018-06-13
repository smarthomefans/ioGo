package de.nisio.iobroker.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.nisio.iobroker.App;
import de.nisio.iobroker.data.model.AppDatabase;
import de.nisio.iobroker.data.model.FunctionDao;
import de.nisio.iobroker.data.model.FunctionStateDao;
import de.nisio.iobroker.data.model.RoomDao;
import de.nisio.iobroker.data.model.RoomStateDao;
import de.nisio.iobroker.data.model.StateDao;
import de.nisio.iobroker.data.repository.FunctionRepository;
import de.nisio.iobroker.data.repository.RoomRepository;
import de.nisio.iobroker.data.repository.StateRepository;
import de.nisio.iobroker.ui.ViewModelFactory;
import de.nisio.iobroker.ui.function.FunctionViewModel;
import de.nisio.iobroker.ui.info.InfoViewModel;
import de.nisio.iobroker.ui.room.RoomViewModel;

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