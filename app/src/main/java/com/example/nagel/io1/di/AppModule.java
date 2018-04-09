package com.example.nagel.io1.di;

import android.content.Context;

import com.example.nagel.io1.App;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.repository.StateRepository;

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
    StateRepository provideStateRepository() {
        return new StateRepository();
    }
}