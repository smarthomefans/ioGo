package de.nisio.iobroker.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisio.iobroker.di.module.AppModule;
import de.nisio.iobroker.ui.function.FunctionDetailActivity;
import de.nisio.iobroker.ui.function.FunctionListActivity;
import de.nisio.iobroker.ui.info.InfoActivity;
import de.nisio.iobroker.ui.main.MainActivity;
import de.nisio.iobroker.ui.room.RoomDetailActivity;
import de.nisio.iobroker.ui.room.RoomListActivity;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract RoomListActivity bindRoomListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract RoomDetailActivity bindRoomDetailActivity();

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract FunctionListActivity bindFunctionListActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FunctionDetailActivity bindFunctionDetailActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract InfoActivity bindInfoActivity();
}