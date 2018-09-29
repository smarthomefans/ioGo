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
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.di.Injectable;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsDeviceFragment extends PreferenceFragmentCompat {

    private EditTextPreference prefName;
    private Preference prefNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefName = (EditTextPreference)findPreference(getString(R.string.pref_device_name));
        prefName.setSummary("Current device is: " + prefName.getText());
        prefName.setOnPreferenceChangeListener((preference, newValue) -> {
            String newValueString = (String) newValue;
            preference.setSummary("Current device is: " + newValueString);

            //enable all device features
            prefNotification.setEnabled(true);
            return true;
        });

        prefNotification = findPreference(getString(R.string.pref_device_notification));

        //disable all device features
        if(prefName.getText() == null){
            prefNotification.setEnabled(false);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_device, rootKey);

    }
}
