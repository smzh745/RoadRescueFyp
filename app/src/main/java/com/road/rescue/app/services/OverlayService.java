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

    private static final String ACTION_DEBUG = "daichan4649.lockoverlay.action.DEBUG";
    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @SuppressLint("InvalidWakeLockTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = Objects.requireNonNull(pm).isScreenOn();
            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(2000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

                wl_cpu.acquire(2000);
            }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerOverlayReceiver();
        startServiceOreoCondition();
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
}