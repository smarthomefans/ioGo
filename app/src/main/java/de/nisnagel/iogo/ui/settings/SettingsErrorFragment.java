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
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.logging.LoggingUtils;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsErrorFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference loggingEnabled = findPreference(getString(R.string.pref_error_logging));
        loggingEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
            if(isStoragePermissionGranted()) {
                LoggingUtils.setupLogging(getContext());
            }else{
                return false;
            }
            return true;
        });

        ListPreference loggingLevel = (ListPreference)findPreference(getString(R.string.pref_error_logging_level));
        loggingLevel.setSummary(getString(R.string.settings_error_logging_level_summary, loggingLevel.getEntry()));
        loggingLevel.setOnPreferenceChangeListener((preference, newValue) -> {
            loggingLevel.setSummary(getString(R.string.settings_error_logging_level_summary, newValue));
            return true;
        });
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Timber.v("Permission is granted");
                return true;
            } else {

                Timber.v("Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Timber.v("Permission is granted");
            return true;
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_error, rootKey);
    }

}
