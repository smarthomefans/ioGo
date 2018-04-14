package com.example.nagel.io1.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.nagel.io1.App;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.repository.AppDatabase;
import com.example.nagel.io1.service.repository.ObjectDao;
import com.example.nagel.io1.service.repository.ObjectRepository;
import com.example.nagel.io1.service.repository.StateDao;
import com.example.nagel.io1.service.repository.StateRepository;
import com.example.nagel.io1.viewmodel.ListViewModel;
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

    @Singleton
    @Provides
    SocketService provideSocketService() {
        return new SocketService();
    }

    @Singleton
    @Provides
    StateRepository provideStateRepository(ObjectRepository objectRepository, StateDao stateDao) {
        return new StateRepository(objectRepository, stateDao);
    }

    @Singleton
    @Provides
    ObjectRepository provideObjectRepository(ObjectDao objectDao) {
        return new ObjectRepository(objectDao);
    }

    @Provides
    ViewModel provideListViewModel(ListViewModel viewModel) {
        return viewModel;
    }

    @Singleton
    @Provides
    AppDatabase provideDb(App app) {
        return Room.databaseBuilder(app, AppDatabase.class,"appDatabase.db").build();
    }

    @Singleton
    @Provides
    StateDao provideStateDao(AppDatabase db) {
        return db.getStateDao();
    }


    @Singleton
    @Provides
    ObjectDao provideObjectDao(AppDatabase db) {
        return db.getObjectDao();
    }

    @Provides
    ViewModelProvider.Factory provideListViewModelFactory(
            ViewModelFactory factory
    ) {
        return factory;
    }

}