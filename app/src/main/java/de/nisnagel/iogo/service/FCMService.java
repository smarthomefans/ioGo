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

package de.nisnagel.iogo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Message;
import de.nisnagel.iogo.data.repository.MessageRepository;
import de.nisnagel.iogo.data.repository.StateRepository;
import timber.log.Timber;

public class FCMService extends FirebaseMessagingService {

    private static int count = 0;
    FirebaseStorage storage;
    StorageReference messagesRef;
    File localFile = null;
    Bitmap bmp = null;

    @Inject
    public SharedPreferences sharedPref;

    @Inject
    public MessageRepository messageRepository;

    @Inject
    public StateRepository stateRepository;

    @Override
    public void onCreate() {
        Timber.v(" onCreate called");
        AndroidInjection.inject(this);
        super.onCreate();
        if (messageRepository.isPro()) {
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            messagesRef = storageRef.child("messages").child(messageRepository.getFirebaseUid());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("Message received");

        boolean isEnabled = sharedPref.getBoolean(getString(R.string.pref_device_notification), false);
        if (isEnabled) {
            Map<String, String> data = remoteMessage.getData();
            sendNotification(data.get("title"), data.get("body"), data.get("img"));
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String fcm_device = sharedPref.getString(getString(R.string.pref_device_name), null);
        sharedPref.edit().putString(getString(R.string.pref_device_token), s).apply();
        stateRepository.sendState("iogo.0." + fcm_device + ".token", s, "string");
        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Timber.d("new token:" + s);
    }

    //This method is only generating push notification
    private void sendNotification(String messageTitle, String messageBody, String filname) {
        PendingIntent contentIntent = null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("main_channel", "main", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }


        if (filname != null) {
            localFile = generateFile(filname);
            StorageReference imageRef = messagesRef.child(filname);
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "main_channel")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.test48))
                            .setSmallIcon(R.drawable.test48)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bmp))
                            .setContentIntent(contentIntent);
                    notificationManager.notify(count, notificationBuilder.build());
                    imageRef.delete();
                    count++;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Timber.w(exception);
                }
            });
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "main_channel")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.test48))
                    .setSmallIcon(R.drawable.test48)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(contentIntent);
            notificationManager.notify(count, notificationBuilder.build());
            count++;
        }
    }

    private File generateFile(@NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);

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

    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}