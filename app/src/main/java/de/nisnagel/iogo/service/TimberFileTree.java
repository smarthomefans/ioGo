package de.nisnagel.iogo.service;

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

public class TimberFileTree extends Timber.DebugTree {

    private static final String LOG_TAG = TimberFileTree.class.getSimpleName();
    private int priority;

    public TimberFileTree(int priority) {
        this.priority = priority;
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
    private static File generateFile(@NonNull String path, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    BuildConfig.APPLICATION_ID + File.separator + path);

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