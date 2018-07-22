package de.nisnagel.iogo.service.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class TimberReleaseTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        if (priority == Log.ERROR)
            Crashlytics.log(priority, tag, message);
    }
}
