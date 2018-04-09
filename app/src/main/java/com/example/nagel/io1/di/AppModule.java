package com.example.nagel.io1.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.nagel.io1.App;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.repository.RepoDao;
import com.example.nagel.io1.service.repository.RepoDatabase;
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
    StateRepository provideStateRepository(RepoDao repoDao) {
        return new StateRepository(repoDao);
    }

    @Provides
    ViewModel provideListViewModel(ListViewModel viewModel) {
        return viewModel;
    }

    @Singleton
    @Provides
    RepoDatabase provideDb(App app) {
        return Room.databaseBuilder(app, RepoDatabase.class,"repoDatabase.db").build();
    }

    @Singleton
    @Provides
    RepoDao provideRepoDao(RepoDatabase db) {
        return db.getRepoDao();
    }

    @Provides
    ViewModelProvider.Factory provideListViewModelFactory(
            ViewModelFactory factory
    ) {
        return factory;
    }

}