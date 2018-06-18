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
import de.nisio.iobroker.data.model.EnumDao;
import de.nisio.iobroker.data.model.EnumStateDao;
import de.nisio.iobroker.data.model.FavoriteDao;
import de.nisio.iobroker.data.model.StateDao;
import de.nisio.iobroker.data.repository.EnumRepository;
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
        return Room.databaseBuilder(app, AppDatabase.class,"appDatabase.db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
    }

    @Singleton
    @Provides
    StateRepository provideStateRepository(StateDao stateDao) {
        return new StateRepository(stateDao);
    }

    @Singleton
    @Provides
    EnumRepository provideEnumRepository(EnumDao enumDao, EnumStateDao enumStateDao, FavoriteDao favoriteDao) {
        return new EnumRepository(enumDao, enumStateDao, favoriteDao);
    }

    @Singleton
    @Provides
    StateDao provideStateDao(AppDatabase db) {
        return db.getStateDao();
    }

    @Singleton
    @Provides
    EnumDao provideEnumDao(AppDatabase db) {
        return db.getEnumDao();
    }

    @Singleton
    @Provides
    EnumStateDao provideEnumStateDao(AppDatabase db) {
        return db.getEnumStateDao();
    }

    @Singleton
    @Provides
    FavoriteDao provideFavoriteeDao(AppDatabase db) {
        return db.getFavoriteDao();
    }
}