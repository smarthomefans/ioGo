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
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

import de.nisnagel.iogo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsConnectFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitchPreference chkWeb = (SwitchPreference) findPreference(getString(R.string.pref_connect_web));
        SwitchPreference chkCloud = (SwitchPreference) findPreference(getString(R.string.pref_connect_cloud));
        SwitchPreference chkIogo = (SwitchPreference) findPreference(getString(R.string.pref_connect_iogo));

        chkWeb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if (newValueBool) {
                chkCloud.setChecked(false);
                chkIogo.setChecked(false);
            }
            return true;
        });

        chkCloud.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if (newValueBool) {
                chkWeb.setChecked(false);
                chkIogo.setChecked(false);
            }
            return true;
        });

        chkIogo.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if (newValueBool) {
                chkWeb.setChecked(false);
                chkCloud.setChecked(false);
            }
            return true;
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Preference prefIogo = findPreference(getString(R.string.pref_connect_iogo));
            prefIogo.setEnabled(false);
        }

        EditTextPreference prefCloudUser = (EditTextPreference) findPreference(getString(R.string.pref_connect_cloud_user));
        prefCloudUser.setSummary("Username is: " + prefCloudUser.getText());

        EditTextPreference prefWebUrl = (EditTextPreference) findPreference(getString(R.string.pref_connect_web_url));
        prefWebUrl.setSummary("URL is: " + prefWebUrl.getText());

        EditTextPreference prefWebUser = (EditTextPreference) findPreference(getString(R.string.pref_connect_web_user));
        prefWebUser.setSummary("Username is: " + prefWebUser.getText());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_connect, rootKey);
    }

}
