package de.nisio.iobroker.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisio.iobroker.di.module.AppModule;
import de.nisio.iobroker.ui.info.InfoActivity;
import de.nisio.iobroker.ui.main.EnumDetailActivity;
import de.nisio.iobroker.ui.main.EnumListActivity;
import de.nisio.iobroker.ui.main.EnumSettingsActivity;
import de.nisio.iobroker.ui.main.MainActivity;

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