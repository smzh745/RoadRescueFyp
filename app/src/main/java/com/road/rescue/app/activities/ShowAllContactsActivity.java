package com.road.rescue.app.activities;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.road.rescue.app.R;
import com.road.rescue.app.adapter.ContactAdapter;
import com.road.rescue.app.model.EmergencyContact;
import com.road.rescue.app.utils.ClickListener;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RecyclerTouchListener;
import com.road.rescue.app.utils.RequestHandler;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.road.rescue.app.utils.Constants.ADD_EMERGENCY_CONTACT;
import static com.road.rescue.app.utils.Constants.TAGI;

public class ShowAllContactsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<EmergencyContact> myComplaintList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_contacts);
        recyclerView = findViewById(R.id.recycler_home);
        myComplaintList = new ArrayList<>();
        getAllContacts(getContentResolver());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EmergencyContact emergencyContact = myComplaintList.get(position);
                Log.d(TAGI, "onClick: " + emergencyContact.geteName());
                addContact(emergencyContact.geteName(), emergencyContact.geteContact());
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(TAGI, "onLongClick: ");
            }
        }));

    }

    private void addContact(final String geteName, final String geteContact) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (InternetConnection.checkConnection(ShowAllContactsActivity.this)) {
                            addContactE(geteName, geteContact);
                        } else {
                            showToast("No internet connection");
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowAllContactsActivity.this);
        builder.setMessage("Are you sure you want to add this number as emergncy contact?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void addContactE(final String geteName, final String geteContact) {
        try {
            setProgressDialog("Adding Emergency Contact...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + ADD_EMERGENCY_CONTACT,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {


                                        Log.d(TAGI, "e" + obj.getString("message"));
                                        showToast(obj.getString("message"));
                                        SharedPrefUtils.saveData(ShowAllContactsActivity.this, "eData", obj.getString("data"));
                                        SharedPrefUtils.saveData(ShowAllContactsActivity.this, "isContact", true);
                                        startNewActivity(new MainActivity());
                                        finish();
                                    } else {
                                        showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1" + obj.getString("message"));

                                    }
                                    cancelProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    cancelProgressDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                cancelProgressDialog();
                                showToast(error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("uid", SharedPrefUtils.getStringData(ShowAllContactsActivity.this, "uid"));
                    params.put("ename", geteName);
                    params.put("econtact", geteContact);

                    return params;
                }
            };


            RequestHandler.getInstance(ShowAllContactsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllContacts(ContentResolver contentResolver) {
        try {
            myComplaintList.clear();
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "display_name asc");
            if (phones != null) {
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d(TAGI, "getAllContacts: " + phoneNumber + "\n");
                    Log.d(TAGI, "getAllContacts: " + name);
                    myComplaintList.add(new EmergencyContact(name, phoneNumber));
                }
            }

            if (phones != null) {
                phones.close();
            }
            setDataOnRecycler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void setDataOnRecycler() {
        ContactAdapter myComplaintAdapter = new ContactAdapter(myComplaintList);
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myComplaintAdapter);
    }
}
