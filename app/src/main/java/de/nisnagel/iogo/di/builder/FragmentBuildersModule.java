package de.nisnagel.iogo.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.ui.info.InfoFragment;
import de.nisnagel.iogo.ui.main.EnumDetailFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract EnumDetailFragment contributeEnumDetailFragment();

    @ContributesAndroidInjector
    abstract InfoFragment contributeInfoFragment();
}