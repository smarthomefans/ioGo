package de.nisio.iobroker.di.builder;



import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisio.iobroker.service.SocketService;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract SocketService contributeSocketService();

}