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
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.repository.StateRepository;
import de.nisnagel.iogo.ui.main.MainActivity;
import timber.log.Timber;

public class FCMService extends FirebaseMessagingService {

    private static int count = 0;

    @Inject
    public SharedPreferences sharedPref;

    @Inject
    public StateRepository stateRepository;

    @Override
    public void onCreate() {
        Timber.v(" onCreate called");
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.getBus().unregister(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("Message received: " + remoteMessage.getNotification().getTitle());

        boolean isEnabled = sharedPref.getBoolean("fcm_enabled", false);
        if (isEnabled) {
            sendNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String fcm_user = sharedPref.getString("fcm_user", null);
        stateRepository.sendState(new Events.SetState("iogo.0." + fcm_user + ".token", s, "string"));
        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Timber.d("new token:" + s);
    }

    //This method is only generating push notification
    private void sendNotification(String messageTitle, String messageBody, Map<String, String> row) {
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