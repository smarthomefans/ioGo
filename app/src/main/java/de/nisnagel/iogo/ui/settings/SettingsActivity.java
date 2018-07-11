package de.nisnagel.iogo.ui.settings;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import de.nisnagel.iogo.BuildConfig;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.LoggingUtils;
import de.nisnagel.iogo.service.TimberDebugTree;
import de.nisnagel.iogo.service.TimberFileTree;
import de.nisnagel.iogo.service.TimberReleaseTree;
import timber.log.Timber;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("logging_enabled") || key.equals("logging_level")) {
                Timber.uprootAll();
                if(isStoragePermissionGranted()) {
                    LoggingUtils.setupLogging(getApplicationContext());
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("logging_enabled",false);
                    editor.apply();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        getSupportActionBar().setTitle(R.string.title_activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Timber.v("Permission is granted");
                return true;
            } else {

                Timber.v("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Timber.v("Permission is granted");
            return true;
        }
    }
}
