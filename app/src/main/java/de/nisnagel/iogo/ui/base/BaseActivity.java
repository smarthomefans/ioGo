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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.SocketService;
import de.nisnagel.iogo.ui.info.InfoActivity;
import de.nisnagel.iogo.ui.settings.SettingsMainActivity;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, SocketService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_settings) {
            Intent i = new Intent(this, SettingsMainActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_action_info) {
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
