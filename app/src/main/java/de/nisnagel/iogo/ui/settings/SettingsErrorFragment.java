package de.nisnagel.iogo.ui.settings;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import de.nisnagel.iogo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsErrorFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.settings_error, rootKey);
    }

    @Override
    public Preference findPreference(CharSequence key) {
        return super.findPreference(key);
    }
}
