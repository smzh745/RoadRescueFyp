package com.road.rescue.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.road.rescue.app.R;
import com.road.rescue.app.activities.MainActivity;
import com.road.rescue.app.utils.SharedPrefUtils;
import com.squareup.seismic.ShakeDetector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.road.rescue.app.utils.Constants.TAGI;

public class ShakeService extends Service implements ShakeDetector.Listener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ShakeDetector sd;
    private GPSTracker gpsTracker;
    private String lat, longi = null;

    @Override
    public void onCreate() {
        super.onCreate();
        gpsTracker = new GPSTracker(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceOreoCondition();
        Log.d(TAGI, "onStartCommand: ");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new ShakeDetector(this);
        sd.start(sensorManager);

        return START_STICKY;
    }

    @Override
    public void hearShake() {
        Log.d(TAGI, "hearShake: ");
        if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isLogin")) {
            if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isShake")) {
                if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isHelp")) {
                    if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isCall")) {
//                        sendSmsToContacts();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("isHelpActive", true);
                        startActivity(intent);
                    }
                }
                if (SharedPrefUtils.getBooleanData(getApplicationContext(), "isCall")) {
                    Intent intent = new Intent(getApplicationContext(), OverlayService.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
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

    private void sendSmsToContacts() {
        try {

            if (gpsTracker.canGetLocation()) {
                lat = String.valueOf(gpsTracker.getLatitude());
                longi = String.valueOf(gpsTracker.getLongitude());
            }
            JSONArray jsonArray = new JSONArray(SharedPrefUtils.getStringData(getApplicationContext(), "eData"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d(TAGI, "sendSmsToContacts: " + jsonObject.getString("econtact"));
                Log.d(TAGI, "sendSmsToContacts: long: " + longi);
                Log.d(TAGI, "sendSmsToContacts: lat: " + lat);
                sendSMS(jsonObject.getString("econtact"), "Please help me its an emergency. I am in trouble and in lots of pain. This is my location:\n\n" + "http://maps.google.com/?q=" + lat + "," + longi + "\n\n" +
                        "Come hurry up!");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
