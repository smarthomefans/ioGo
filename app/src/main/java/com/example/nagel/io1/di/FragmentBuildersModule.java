package com.example.nagel.io1.di;

import com.example.nagel.io1.view.ui.FragmentActivityFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract FragmentActivityFragment contributeFragmentActivityFragment();

}