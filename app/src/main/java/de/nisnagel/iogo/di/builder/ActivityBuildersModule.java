package de.nisnagel.iogo.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.di.module.AppModule;
import de.nisnagel.iogo.ui.info.InfoActivity;
import de.nisnagel.iogo.ui.main.EnumDetailActivity;
import de.nisnagel.iogo.ui.main.EnumListActivity;
import de.nisnagel.iogo.ui.main.EnumSettingsActivity;
import de.nisnagel.iogo.ui.main.MainActivity;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract EnumListActivity bindEnumListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract EnumDetailActivity bindEnumDetailActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract EnumSettingsActivity bindRoomSettingsActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract InfoActivity bindInfoActivity();
}