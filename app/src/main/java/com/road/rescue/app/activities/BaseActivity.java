package com.road.rescue.app.activities;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.location.LocationManagerCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    TextInputEditText pass, email, name, phone, cnic;
    TextInputLayout passwordinput, emailinput;
    String encrypted;
    String userName, userEmail;

    int notifyID = 2;
    String CHANNEL_ID = "my_channel_02";// The id of the channel.
    CharSequence name1 = "My Channel";// The user-visible name of the channel.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(BaseActivity.this);

        try {
            JSONObject jsonObject = new JSONObject(SharedPrefUtils.getStringData(this, "userData"));
            userEmail = jsonObject.getString("email");
            userName = jsonObject.getString("name");
            SharedPrefUtils.saveData(this, "uid", jsonObject.getString("id"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO: show progress dialog
    public void setProgressDialog(String message) {
        progressDialog.setTitle("Please wait");
        progressDialog.setIcon(R.drawable.unnamed);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //TODO: cancel progress dialog
    public void cancelProgressDialog() {

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    //toDO: show toast message
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //TODO: start new activity
    public void startNewActivity(Activity activity) {
        startActivity(new Intent(BaseActivity.this, activity.getClass()));
    }

    /*
     * TODO: method for email input field validation
     * */
    public void validateEmailfield(Editable s) {
        if (TextUtils.isEmpty(s)) {
            emailinput.setError(getString(R.string.error_field_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            emailinput.setErrorEnabled(true);
            emailinput.setError(getString(R.string.error_invalid_email));

        } else if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            emailinput.setError(null);
            emailinput.setErrorEnabled(false);
        }
    }

    /*
     * Todo: method for password input field validation
     * */
    public void validatePasswordField(Editable s) {
        if (TextUtils.isEmpty(s)) {

            passwordinput.setError(getString(R.string.error_field_required));
        } else if (s.toString().length() <= 6) {
            passwordinput.setErrorEnabled(true);
            passwordinput.setError(getString(R.string.error_invalid_password));


        } else if (s.toString().length() >= 6) {
            passwordinput.setError(null);
            passwordinput.setErrorEnabled(false);
        }
    }

    //TODO: current time
    public String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    //TODO: current date
    public String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public void sendNotificationMsg(String body) {
        try {
                /*Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        CHANNEL_ID, name1, NotificationManager.IMPORTANCE_HIGH);
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

    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(Objects.requireNonNull(locationManager));
    }

    public void showAlertDialog(String message) {

        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                dialog.dismiss();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);

        builder.setMessage(message).setPositiveButton("Ok", dialogClickListener).show();
    }
}
