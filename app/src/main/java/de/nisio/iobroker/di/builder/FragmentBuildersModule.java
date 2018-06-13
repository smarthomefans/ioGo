package de.nisio.iobroker.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisio.iobroker.ui.function.FunctionDetailFragment;
import de.nisio.iobroker.ui.info.InfoFragment;
import de.nisio.iobroker.ui.room.RoomDetailFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RoomDetailFragment contributeRoomDetailFragment();

    @ContributesAndroidInjector
    abstract FunctionDetailFragment contributeFunctionDetailFragment();

    @ContributesAndroidInjector
    abstract InfoFragment contributeInfoFragment();
}