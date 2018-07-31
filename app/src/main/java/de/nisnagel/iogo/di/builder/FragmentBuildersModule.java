package de.nisnagel.iogo.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.ui.detail.StateFragment;
import de.nisnagel.iogo.ui.info.InfoFragment;
import de.nisnagel.iogo.ui.main.EnumFragment;
import de.nisnagel.iogo.ui.main.FunctionFragment;
import de.nisnagel.iogo.ui.main.HomeFragment;
import de.nisnagel.iogo.ui.main.RoomFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract EnumFragment contributeEnumFragment();

    @ContributesAndroidInjector
    abstract StateFragment contributeStateFragment();

    @ContributesAndroidInjector
    abstract InfoFragment contributeInfoFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract RoomFragment contributeRoomFragment();

    @ContributesAndroidInjector
    abstract FunctionFragment contributeFunctionFragment();
}