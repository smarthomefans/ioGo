package com.example.nagel.io1.di.builder;


import com.example.nagel.io1.di.module.AppModule;
import com.example.nagel.io1.ui.function.FunctionDetailActivity;
import com.example.nagel.io1.ui.function.FunctionListActivity;
import com.example.nagel.io1.ui.room.RoomDetailActivity;
import com.example.nagel.io1.ui.room.RoomListActivity;
import com.example.nagel.io1.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract RoomListActivity bindRoomListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract RoomDetailActivity bindRoomDetailActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract FunctionListActivity bindFunctionListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FunctionDetailActivity bindFunctionDetailActivity();
}