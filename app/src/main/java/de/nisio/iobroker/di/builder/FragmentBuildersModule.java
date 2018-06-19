package de.nisio.iobroker.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisio.iobroker.ui.info.InfoFragment;
import de.nisio.iobroker.ui.main.EnumDetailFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract EnumDetailFragment contributeEnumDetailFragment();

    @ContributesAndroidInjector
    abstract InfoFragment contributeInfoFragment();
}