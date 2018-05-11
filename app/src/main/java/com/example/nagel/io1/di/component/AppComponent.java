package com.example.nagel.io1.di.component;

import com.example.nagel.io1.App;
import com.example.nagel.io1.di.builder.ActivityBuildersModule;
import com.example.nagel.io1.di.builder.ServiceBuilderModule;
import com.example.nagel.io1.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

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