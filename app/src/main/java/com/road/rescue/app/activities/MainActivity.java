package com.road.rescue.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.BuildConfig;
import com.road.rescue.app.R;
import com.road.rescue.app.services.GPSTracker;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.road.rescue.app.utils.Constants.REQUEST_CHECK_SETTINGS_GPS;
import static com.road.rescue.app.utils.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS;
import static com.road.rescue.app.utils.Constants.TAGI;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private AppBarConfiguration mAppBarConfiguration;
    private GPSTracker gpsTracker;
    private String lat, longi = null;
    private Location mylocation;
    private GoogleApiClient googleApiClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* if (!SharedPrefUtils.getBooleanData(this, "isLogin")) {
            startNewActivity(new LoginActivity());
        } else if (!SharedPrefUtils.getBooleanData(this, "isContact")) {
            startNewActivity(new ShowAllContactsActivity());
        } else {*/
            setUpGClient();
            gpsTracker = new GPSTracker(this);
            sendSmsToContacts();
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View view = navigationView.getHeaderView(0);
            MaterialTextView name = view.findViewById(R.id.name);
            MaterialTextView email = view.findViewById(R.id.email);
            MaterialTextView version = findViewById(R.id.version);
            name.setText(userName);
            email.setText(userEmail);
            version.setText(" Version " + BuildConfig.VERSION_NAME);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_complaint, R.id.nav_pass)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_logout);
            menuItem.setOnMenuItemClickListener(item -> {
                SharedPrefUtils.saveData(MainActivity.this, "isLogin", false);
                finish();
                startNewActivity(new LoginActivity());
                return true;
            });


//        }
    }

    private void sendSmsToContacts() {
        try {

            if (getIntent().getBooleanExtra("isHelpActive", false)) {
                if (isLocationEnabled(MainActivity.this)) {
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
    public void onLocationChanged(Location location) {
        mylocation = location;

        if (mylocation != null) {
            double latitude = mylocation.getLatitude();
            double longitude = mylocation.getLongitude();
            Log.d(TAGI, "onCreate: " + latitude);
            Log.d(TAGI, "onCreate: " + longitude);
            lat = String.valueOf(latitude);
            longi = String.valueOf(longitude);


            //Or Do whatever you want with your location
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(result1 -> {
                        final Status status = result1.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                int permissionLocation1 = ContextCompat
                                        .checkSelfPermission(MainActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION);
                                if (permissionLocation1 == PackageManager.PERMISSION_GRANTED) {
                                    mylocation = LocationServices.FusedLocationApi
                                            .getLastLocation(googleApiClient);
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied.
                                // But could be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(MainActivity.this,
                                            REQUEST_CHECK_SETTINGS_GPS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied.
                                // However, we have no way
                                // to fix the
                                // settings so we won't show the dialog.
                                // finish();
                                break;
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    finish();
                    break;
            }
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

}
