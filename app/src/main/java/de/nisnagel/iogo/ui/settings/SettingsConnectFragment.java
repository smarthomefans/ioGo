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
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.di.Injectable;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsConnectFragment extends PreferenceFragmentCompat implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    SharedPreferences sharedPref;

    private SettingsViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SettingsViewModel.class);

        CheckBoxPreference chkWeb = (CheckBoxPreference) findPreference(getString(R.string.pref_connect_web));
        CheckBoxPreference chkCloud = (CheckBoxPreference) findPreference(getString(R.string.pref_connect_cloud));
        CheckBoxPreference chkIogo = (CheckBoxPreference) findPreference(getString(R.string.pref_connect_iogo));

        chkWeb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if(newValueBool) {
                chkCloud.setChecked(false);
                chkIogo.setChecked(false);
                mViewModel.activateWeb();
            }
            return true;
        });

        chkCloud.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if(newValueBool) {
                chkWeb.setChecked(false);
                chkIogo.setChecked(false);
                mViewModel.activateCloud();
            }
            return true;
        });

        chkIogo.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newValueBool = (Boolean) newValue;
            if(newValueBool) {
                chkWeb.setChecked(false);
                chkCloud.setChecked(false);
                mViewModel.activateIogo();
            }
            return true;
        });

        Preference prefCloudUser = findPreference(getString(R.string.pref_connect_cloud_user));
        prefCloudUser.setSummary("Username is: " + sharedPref.getString(getString(R.string.pref_connect_cloud_user),"not specified"));

        Preference prefWebUrl = findPreference(getString(R.string.pref_connect_web_url));
        prefWebUrl.setSummary("Username is: " + sharedPref.getString(getString(R.string.pref_connect_web_url),"not specified"));

        Preference prefWebUser = findPreference(getString(R.string.pref_connect_web_user));
        prefWebUser.setSummary("Username is: " + sharedPref.getString(getString(R.string.pref_connect_web_user),"not specified"));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_connect, rootKey);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
