package de.nisio.iobroker.ui.settings;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import de.nisio.iobroker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
