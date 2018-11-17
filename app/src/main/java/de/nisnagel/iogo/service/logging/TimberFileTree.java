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
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.nisnagel.iogo.BuildConfig;
import timber.log.Timber;

class TimberFileTree extends Timber.DebugTree {

    private static final String LOG_TAG = TimberFileTree.class.getSimpleName();
    private int priority;
    private Context context;

    public TimberFileTree(int priority, Context context) {
        this.priority = priority;
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.ERROR)
            Crashlytics.log(priority, tag, message);

        if (this.priority <= priority) {
            try {
                String path = "logs";
                String fileNameTimeStamp = new SimpleDateFormat("yyyyMMdd",
                        Locale.getDefault()).format(new Date());
                String fileName = fileNameTimeStamp + ".log";
                String logTimeStamp = new SimpleDateFormat("hh:mm:ss:SSS",
                        Locale.getDefault()).format(new Date());
                String logPriority = getLogPriority(priority);

                // Create file
                File file = generateFile(path, fileName);

                // If file created or exists save logs
                if (file != null) {
                    FileWriter writer = new FileWriter(file, true);
                    writer.append(logTimeStamp)
                              .append(" ")
                              .append(logPriority)
                              .append("/")
                              .append(tag)
                              .append(": ")
                              .append(message)
                              .append("\n");
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while logging into file : " + e);
            }
        }
    }

    private String getLogPriority(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return "V";
            case Log.DEBUG:
                return "D";
            case Log.INFO:
                return "I";
            case Log.WARN:
                return "W";
            case Log.ERROR:
                return "E";
            default:
                return "?";
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    /*  Helper method to create file*/
    @Nullable
    private File generateFile(@NonNull String path, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = context.getExternalFilesDir(null);

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();
            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }
        return file;
    }

    /* Helper method to determine if external storage is available*/
    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}