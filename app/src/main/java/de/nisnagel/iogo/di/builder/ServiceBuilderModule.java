package de.nisnagel.iogo.di.builder;



import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.service.SocketService;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract SocketService contributeSocketService();

}