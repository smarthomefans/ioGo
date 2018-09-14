/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.settings;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import de.nisnagel.iogo.service.logging.LoggingUtils;
import de.nisnagel.iogo.ui.base.BaseActivity;
import de.nisnagel.iogo.ui.main.MainActivity;
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
public class SettingsMainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FirebaseAuth mAuth;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, key) -> {
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
        if(key.equals("pro_cloud_enabled")||key.equals("sync_children")) {
            String value = (sharedPreferences.getBoolean(key, false)) ? "true" : "false";
            FirebaseAnalytics.getInstance(getApplicationContext()).setUserProperty(key, value);
        }
        if(key.equals("fcm_user")){
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SettingsMainActivity.this, instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Timber.d("newToken" + newToken);

                String uniqueId = user.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.child(uniqueId).child("token").setValue(newToken);

                String fcm_user = sharedPreferences.getString("fcm_user", null);
                if(fcm_user != null) {
                    DataBus.getBus().post(new Events.User(fcm_user, newToken));
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(SettingsMainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SettingsMainActivity.this, instanceIdResult -> {
                            String newToken = instanceIdResult.getToken();
                            Timber.d("newToken" + newToken);

                            String fcm_user = sharedPref.getString("fcm_user", null);
                            if (fcm_user != null) {
                                DataBus.getBus().post(new Events.User(fcm_user, newToken));
                            }

                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.w("signInAnonymously:failure", task.getException());
                    }
                });
        if (savedInstanceState == null) {
            loadFragment(getIntent().getStringExtra(Constants.ARG_CLASS));
        }
    }

    public void loadFragment(String intent) {
        Fragment fragment = null;
        if(intent != null) {
            switch (intent) {
                case "Connection":
                    fragment = new SettingsConnectionFragment();
                    setTitle(R.string.title_activity_settings_connection);
                    break;

                case "Design":
                    fragment = new SettingsDesignFragment();
                    setTitle(R.string.title_activity_settings_design);
                    break;

                case "Error":
                    fragment = new SettingsErrorFragment();
                    setTitle(R.string.title_activity_settings_error);
                    break;

                case "Notification":
                    fragment = new SettingsNotificationFragment();
                    setTitle(R.string.title_activity_settings_notification);
                    break;

                default:
                    fragment = new SettingsFragment();
                    setTitle(R.string.title_activity_settings);
            }
        }else{
            fragment = new SettingsFragment();
            setTitle(R.string.title_activity_settings);
        }

        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private boolean isStoragePermissionGranted() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
