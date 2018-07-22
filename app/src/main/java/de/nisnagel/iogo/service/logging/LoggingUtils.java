package de.nisnagel.iogo.service.logging;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import de.nisnagel.iogo.BuildConfig;
import timber.log.Timber;

public class LoggingUtils {

    public static void setupLogging(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("logging_enabled", false)) {
            String priority = sharedPref.getString("logging_level", null);
            if (priority != null) {
                Timber.plant(new TimberFileTree(Integer.parseInt(priority)));
            } else {
                Timber.plant(new TimberFileTree(Log.ERROR));
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.plant(new TimberDebugTree());
            } else {
                Timber.plant(new TimberReleaseTree());
            }
        }
    }

}
