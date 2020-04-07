package com.road.rescue.app.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.road.rescue.app.R;
import com.road.rescue.app.activities.MainActivity;
import com.road.rescue.app.utils.SharedPrefUtils;
import com.squareup.seismic.ShakeDetector;

import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.road.rescue.app.utils.Constants.TAGI;

public class ShakeService extends Service implements ShakeDetector.Listener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "My Channel";// The user-visible name of the channel.

    private ShakeDetector sd;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceOreoCondition();
        Log.d(TAGI, "onStartCommand: ");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new ShakeDetector(this);
        sd.start(sensorManager);

        // Logic to turn on the screen
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        if (!Objects.requireNonNull(powerManager).isInteractive()) { // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MH24_" +
                    "SCREENLOCK");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK");
            wl_cpu.acquire(10000);
        }
        sendNotificationMsg("Call any emergency service for help!");
        return START_STICKY;
    }

    @Override
    public void hearShake() {
        Log.d(TAGI, "hearShake: ");
        if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isLogin")) {
            if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isShake")) {
                if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isHelp")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isHelpActive", true);
                    startActivity(intent);

                }


            }
        }

    }

    private void startServiceOreoCondition() {
        try {
            if (Build.VERSION.SDK_INT >= 26) {


                String CHANNEL_ID = "ch_745";
                String CHANNEL_NAME = "Road Rescue Shake Service";

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
                ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSubText(getString(R.string.app_name))
                        .setContentText("This service use shake process in background and will help you when you shake your phone in emergency.")
                        .setCategory(Notification.CATEGORY_SERVICE).setSmallIcon(R.mipmap.ic_launcher).setPriority(PRIORITY_MIN).build();


                startForeground(745, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        sd.stop();
        super.onDestroy();
    }

    private void sendNotificationMsg(String body) {
        try {
                /*Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(null, null);
                Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(body)
                        /* .setDefaults(Notification.DEFAULT_SOUND)*/
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setLights(Color.WHITE, 2000, 3000)
                        .setAutoCancel(true);
//                            .setContentIntent(pendingIntent);

                notificationManager.notify(notifyID, mBuilder.build());

            } else {
//Get an instance of NotificationManager//

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(body)
                        .setLights(Color.WHITE, 2000, 3000)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        /*  .setDefaults(Notification.DEFAULT_SOUND)*/
                        .setAutoCancel(true);
//                            .setContentIntent(pendingIntent);


                NotificationManager mNotificationManager =

                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Objects.requireNonNull(mNotificationManager).notify(notifyID, mBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
