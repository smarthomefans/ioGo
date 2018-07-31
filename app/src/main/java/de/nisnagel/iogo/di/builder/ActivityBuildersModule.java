package de.nisnagel.iogo.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.ui.detail.StateActivity;
import de.nisnagel.iogo.ui.info.InfoActivity;
import de.nisnagel.iogo.ui.main.EnumActivity;
import de.nisnagel.iogo.ui.main.MainActivity;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract EnumActivity bindEnumActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract StateActivity bindStateActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract InfoActivity bindInfoActivity();

}