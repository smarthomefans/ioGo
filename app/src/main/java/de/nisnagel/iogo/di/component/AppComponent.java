package de.nisnagel.iogo.di.component;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import de.nisnagel.iogo.App;
import de.nisnagel.iogo.data.model.AppDatabase;
import de.nisnagel.iogo.di.builder.ActivityBuildersModule;
import de.nisnagel.iogo.di.builder.ServiceBuilderModule;
import de.nisnagel.iogo.di.module.AppModule;

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