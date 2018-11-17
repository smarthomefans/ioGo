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

package de.nisnagel.iogo.service.logging;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import de.nisnagel.iogo.BuildConfig;
import de.nisnagel.iogo.R;
import timber.log.Timber;

public class LoggingUtils {

    public static void setupLogging(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(context.getString(R.string.pref_error_logging), false)) {
            String priority = sharedPref.getString(context.getString(R.string.pref_error_logging_level), null);
            if (priority != null) {
                Timber.plant(new TimberFileTree(Integer.parseInt(priority), context));
            } else {
                Timber.plant(new TimberFileTree(Log.ERROR, context));
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.plant(new TimberDebugTree());
            } else {
                Timber.plant(new TimberReleaseTree());
            }
        }
    }

}
