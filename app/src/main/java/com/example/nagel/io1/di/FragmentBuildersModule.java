package com.example.nagel.io1.di;

import com.example.nagel.io1.view.ui.RoomDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RoomDetailFragment contributeRoomDetailFragment();

}