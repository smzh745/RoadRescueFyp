package com.road.rescue.app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;

import com.road.rescue.app.R;
import com.road.rescue.app.services.GPSTracker;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.road.rescue.app.utils.Constants.TAGI;

public class CounterActivity extends BaseActivity {
    private GPSTracker gpsTracker;
    private String lat, longi = null;
    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sendSmsToContacts();
            }
        };
        cTimer.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_counter);
        gpsTracker = new GPSTracker(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note!")
                .setMessage("Cancel the dialog in 10 second otherwise message will sent!")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTimer();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void sendSmsToContacts() {
        try {

            if (getIntent().getBooleanExtra("isCounter", false)) {
                if (isLocationEnabled(CounterActivity.this)) {
                    lat = String.valueOf(gpsTracker.getLatitude());
                    longi = String.valueOf(gpsTracker.getLongitude());
                    JSONArray jsonArray = new JSONArray(SharedPrefUtils.getStringData(getApplicationContext(), "eData"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d(TAGI, "sendSmsToContacts: " + jsonObject.getString("econtact"));
                        Log.d(TAGI, "sendSmsToContacts: long: " + longi);
                        Log.d(TAGI, "sendSmsToContacts: lat: " + lat);
                        sendSMS(jsonObject.getString("econtact"), "Please help me its an emergency. I am in trouble and in lots of pain. This is my location:\n\n" + "http://maps.google.com/?q=" + lat + "," + longi + "\n\n" +
                                "Come hurry up!");

                    }
                } else {
                    sendNotificationMsg("Unable to send SMS because location is off");
                }

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

    @Override
    public void onBackPressed() {
        cancelTimer();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
}