package de.nisio.iobroker;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

import com.google.firebase.crash.FirebaseCrash;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import de.nisio.iobroker.di.component.DaggerAppComponent;

public class App extends Application implements HasActivityInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseCrash.setCrashCollectionEnabled(false);

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this);

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