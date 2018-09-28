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


import android.app.Fragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsNotificationFragment extends PreferenceFragmentCompat implements Injectable {

    public static final String FCM_ENABLED = "fcm_enabled";
    public static final String FCM_DEVICE = "fcm_device";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    SharedPreferences sharedPref;

    private SettingsViewModel mViewModel;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(FCM_DEVICE)) {


                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( getActivity(), instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    Timber.d("newToken" + newToken);

                    String deviceName = sharedPreferences.getString(FCM_DEVICE, null);
                    if(deviceName != null) {
                        mViewModel.setDevice(sharedPreferences.getString(key, ""), newToken);
                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SettingsViewModel.class);
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        Preference prefEnabled = findPreference(FCM_ENABLED);
        prefEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
            String fcm_device = sharedPref.getString(FCM_DEVICE, "");
            boolean newValueBool = (Boolean) newValue;
            if (newValueBool && TextUtils.isEmpty(fcm_device)) {
                Toast.makeText(SettingsNotificationFragment.this.getContext(), "Devicename must be set first", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        });
        Preference prefDevice = findPreference(FCM_DEVICE);
        prefDevice.setSummary("Current device is: " + sharedPref.getString(FCM_DEVICE,"not specified"));
        prefDevice.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean fcm_enabled = sharedPref.getBoolean(FCM_ENABLED, false);
            String newValueString = (String) newValue;
            if (fcm_enabled && TextUtils.isEmpty(newValueString)) {
                Toast.makeText(SettingsNotificationFragment.this.getContext(), "Notification must be disabled first.", Toast.LENGTH_SHORT).show();
                return false;
            }
            preference.setSummary("Current device is: " + newValueString);
            return true;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_notification, rootKey);

    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

}
