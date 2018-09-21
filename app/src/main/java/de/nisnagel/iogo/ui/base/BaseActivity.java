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

package de.nisnagel.iogo.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.SocketService;
import de.nisnagel.iogo.ui.info.InfoActivity;
import de.nisnagel.iogo.ui.settings.SettingsMainActivity;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirebaseEnabled = sharedPref.getBoolean("firebase_enabled", false);
        if(!isFirebaseEnabled) {
            startService(new Intent(this, SocketService.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_settings) {
            Toast.makeText(this, R.string.menu_action_settings, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, SettingsMainActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_action_info) {
            Toast.makeText(this, R.string.menu_action_info, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
