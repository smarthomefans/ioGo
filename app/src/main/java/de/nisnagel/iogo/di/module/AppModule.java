package de.nisnagel.iogo.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.nisnagel.iogo.App;
import de.nisnagel.iogo.data.model.AppDatabase;
import de.nisnagel.iogo.data.model.EnumDao;
import de.nisnagel.iogo.data.model.EnumStateDao;
import de.nisnagel.iogo.data.model.StateDao;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.data.repository.StateRepository;
import de.nisnagel.iogo.ui.ViewModelFactory;
import de.nisnagel.iogo.ui.info.InfoViewModel;
import de.nisnagel.iogo.ui.main.EnumViewModel;

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
    ViewModel provideEnumViewModel(EnumViewModel viewModel) {
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
    EnumRepository provideEnumRepository(EnumDao enumDao, EnumStateDao enumStateDao) {
        return new EnumRepository(enumDao, enumStateDao);
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

}