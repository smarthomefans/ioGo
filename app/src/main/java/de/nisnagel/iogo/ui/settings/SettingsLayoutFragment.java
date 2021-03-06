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
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import de.nisnagel.iogo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsLayoutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListPreference stateSubtitle = (ListPreference)findPreference(getString(R.string.pref_layout_state_subtitle));
        if(stateSubtitle.getEntry() == null){
            stateSubtitle.setSummary(getString(R.string.settings_layout_state_subtitle_summary, "Nothing"));
        }else {
            stateSubtitle.setSummary(getString(R.string.settings_layout_state_subtitle_summary, "'" + stateSubtitle.getEntry() + "'"));
        }
        stateSubtitle.setOnPreferenceChangeListener((preference, newValue) -> {
            stateSubtitle.setSummary(getString(R.string.settings_layout_state_subtitle_summary, "'" + newValue + "'"));
            return true;
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey);
    }
}
