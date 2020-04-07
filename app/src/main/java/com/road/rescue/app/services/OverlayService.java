package com.road.rescue.app.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.road.rescue.app.R;
import com.road.rescue.app.activities.HaltScreenActivity;

import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.road.rescue.app.utils.Constants.TAGI;

public class OverlayService extends Service {
    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "My Channel";// The user-visible name of the channel.


    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {

        @SuppressLint("InvalidWakeLockTag")
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAGI, "[onReceive]" + action);
            if (Objects.equals(action, Intent.ACTION_SCREEN_ON) || Objects.equals(action, Intent.ACTION_SCREEN_OFF)) {
                // ACTON_SCREEN_ON はコードからのみ登録可
                showOverlayActivity(context);


            } else {
                assert action != null;
                if (action.equals(ACTION_DEBUG)) {
                    showOverlayActivity(context);
                }
            }
        }


    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServiceOreoCondition() {
        try {
            if (Build.VERSION.SDK_INT >= 26) {


                String CHANNEL_ID = "ch_01";
                String CHANNEL_NAME = "intent_service";

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
                ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setCategory(Notification.CATEGORY_SERVICE).setSmallIcon(R.mipmap.ic_launcher_round).setPriority(PRIORITY_MIN).build();

                startForeground(101, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerOverlayReceiver();
        startServiceOreoCondition();
        // Logic to turn on the screen
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        if (!Objects.requireNonNull(powerManager).isInteractive()) { // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MH24_SCREENLOCK");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK");
            wl_cpu.acquire(10000);
        }
        sendNotificationMsg("Call any emergency service for help!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterOverlayReceiver();
        super.onDestroy();
    }

    private void registerOverlayReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_DEBUG);
        registerReceiver(overlayReceiver, filter);
    }

    private void unregisterOverlayReceiver() {
        unregisterReceiver(overlayReceiver);
    }

    private void showOverlayActivity(Context context) {
        Intent intent = new Intent(context, HaltScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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