package de.nisnagel.iogo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.util.Log;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import de.nisnagel.iogo.di.component.DaggerAppComponent;
import de.nisnagel.iogo.service.TimberDebugTree;
import de.nisnagel.iogo.service.TimberFileTree;
import de.nisnagel.iogo.service.TimberReleaseTree;
import timber.log.Timber;

public class App extends Application implements HasActivityInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this);


        if (BuildConfig.DEBUG) {
            Timber.plant(new TimberDebugTree());
        } else {
            Timber.plant(new TimberReleaseTree());
        }
    }


    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

}