package com.example.nagel.io1.di;


import com.example.nagel.io1.view.ui.FragmentActivity;
import com.example.nagel.io1.view.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FragmentActivity bindFragmentActivity();
}