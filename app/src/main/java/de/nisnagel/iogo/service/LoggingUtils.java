package de.nisnagel.iogo.service;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.nisnagel.iogo.BuildConfig;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
