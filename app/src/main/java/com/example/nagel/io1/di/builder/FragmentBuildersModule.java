package com.example.nagel.io1.di.builder;

import com.example.nagel.io1.ui.room.RoomDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RoomDetailFragment contributeRoomDetailFragment();

}