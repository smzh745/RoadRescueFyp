package com.road.rescue.app.activities;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.services.GPSTracker;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.road.rescue.app.utils.Constants.TAGI;

public class MainActivity extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private GPSTracker gpsTracker;
    private String lat, longi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!SharedPrefUtils.getBooleanData(this, "isLogin")) {
            startNewActivity(new LoginActivity());
        } else if (!SharedPrefUtils.getBooleanData(this, "isContact")) {
            startNewActivity(new ShowAllContactsActivity());
        } else {
            gpsTracker = new GPSTracker(this);
            sendSmsToContacts();
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View view = navigationView.getHeaderView(0);
            MaterialTextView name = view.findViewById(R.id.name);
            MaterialTextView email = view.findViewById(R.id.email);
            name.setText(userName);
            email.setText(userEmail);
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
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    SharedPrefUtils.saveData(MainActivity.this, "isLogin", false);
                    finish();
                    startNewActivity(new LoginActivity());
                    return true;
                }
            });


        }
    }

    private void sendSmsToContacts() {
        try {

            if (getIntent().getBooleanExtra("isHelpActive", false)) {
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
}
