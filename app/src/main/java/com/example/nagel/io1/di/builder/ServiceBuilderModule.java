package com.example.nagel.io1.di.builder;

import com.example.nagel.io1.service.SocketService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract SocketService contributeSocketService();

}