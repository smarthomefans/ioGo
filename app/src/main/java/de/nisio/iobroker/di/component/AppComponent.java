package de.nisio.iobroker.di.component;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import de.nisio.iobroker.App;
import de.nisio.iobroker.data.model.AppDatabase;
import de.nisio.iobroker.di.builder.ActivityBuildersModule;
import de.nisio.iobroker.di.builder.ServiceBuilderModule;
import de.nisio.iobroker.di.module.AppModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBuildersModule.class,
        ServiceBuilderModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);
        AppComponent build();
    }
    void inject(App app);

}