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


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.auth.UserProfilActivity;
import de.nisnagel.iogo.ui.base.BaseActivity;
import de.nisnagel.iogo.ui.billing.BillingActivity;

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

    public static final String intentAccount = "ACCOUNT";
    public static final String intentServer = "SERVER";
    public static final String intentLayout = "LAYOUT";
    public static final String intentDevice = "DEVICE";
    public static final String intentError = "ERROR";
    public static final String intentBilling = "BILLING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            loadFragment(getIntent().getStringExtra(Constants.ARG_CLASS));
        }
    }

    private void loadFragment(String intent) {
        Fragment fragment = null;
        if(intent != null) {
            switch (intent) {
                case intentServer:
                    fragment = new SettingsConnectFragment();
                    setTitle(R.string.settings_title_activity_settings_server);
                    break;

                case intentLayout:
                    fragment = new SettingsLayoutFragment();
                    setTitle(R.string.settings_title_activity_settings_design);
                    break;

                case intentError:
                    fragment = new SettingsErrorFragment();
                    setTitle(R.string.settings_title_activity_settings_error);
                    break;

                case intentDevice:
                    fragment = new SettingsDeviceFragment();
                    setTitle(R.string.settings_title_activity_settings_device);
                    break;

                case intentAccount:
                    startActivity(new Intent(SettingsMainActivity.this, UserProfilActivity.class));
                    finish();
                    break;

                case intentBilling:
                    startActivity(new Intent(SettingsMainActivity.this, BillingActivity.class));
                    finish();
                    break;

                default:
                    fragment = new SettingsFragment();
                    setTitle(R.string.settings_title_activity_settings);
            }
        }else{
            fragment = new SettingsFragment();
            setTitle(R.string.settings_title_activity_settings);
        }

        if(fragment != null) {
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
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
