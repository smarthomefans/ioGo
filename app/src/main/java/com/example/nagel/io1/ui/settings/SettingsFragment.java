package com.example.nagel.io1.ui.settings;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nagel.io1.R;
import com.example.nagel.io1.ui.function.FunctionListActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
