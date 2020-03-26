package com.road.rescue.app.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.road.rescue.app.R;
import com.road.rescue.app.activities.ShowAllContactsActivity;
import com.road.rescue.app.adapter.EmergencyContactAdapter;
import com.road.rescue.app.model.EmergencyContact;
import com.road.rescue.app.utils.Constants;
import com.road.rescue.app.utils.InternetConnection;
import com.road.rescue.app.utils.RequestHandler;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.ADD_EMERGENCY_CONTACT;
import static com.road.rescue.app.utils.Constants.TAGI;


public class EmergencyContactFragment extends Basefragment {
    private RecyclerView recyclerView;
    private LinearLayout empty_view;
    private List<EmergencyContact> myComplaintList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);
        FloatingActionButton addEmergencyContact = view.findViewById(R.id.addEmergencyContact);
        recyclerView = view.findViewById(R.id.recycler_home);
        empty_view = view.findViewById(R.id.empty_view);
        myComplaintList = new ArrayList<>();

        addEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    /*    if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {
            init();
        } else {
            baseActivity.showToast("No Internet Connection!");
            init();
        }*/

        return view;
    }

    private void init() {
        try {
            myComplaintList.clear();
            JSONArray jsonArray = new JSONArray(SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "eData"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                myComplaintList.add(new EmergencyContact(jsonObject.getInt("id"),
                        jsonObject.getInt("uid"), jsonObject.getString("ename"),
                        jsonObject.getString("econtact")));
            }
            setDataOnRecycler();
          /*  baseActivity.setProgressDialog("Fetching All Contacts...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + FETCH_EMERGENCY_CONTACT,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        Log.d(TAGI, "e1: " + obj.getString("message"));

                                    } else {
                                        baseActivity.showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1: " + obj.getString("message"));

                                        CheckEmptyState();
                                    }

                                    CheckEmptyState();
                                    baseActivity.cancelProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    baseActivity.cancelProgressDialog();
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
                                baseActivity.cancelProgressDialog();
                                baseActivity.showToast(error.getMessage());
                                CheckEmptyState();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("uid", SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "uid"));


                    return params;
                }
            };


            RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckEmptyState() {
        if (myComplaintList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
    }

    private void addEmergencyContactDialog() {
        final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") final View myview = inflater.inflate(R.layout.add_emergency_layout, null);

        final AlertDialog mydialog = dialog.create();
        mydialog.setView(myview);
        mydialog.setCancelable(false);
        final TextInputEditText cName = myview.findViewById(R.id.cName);
        final TextInputEditText cContact = myview.findViewById(R.id.cContact);

        MaterialButton savebtn = myview.findViewById(R.id.btn_save);
        MaterialButton cancelbtn = myview.findViewById(R.id.btn_cancel);


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get values
                String cName1 = Objects.requireNonNull(cName.getText()).toString().trim();
                String cContact1 = Objects.requireNonNull(cContact.getText()).toString().trim();

                if (TextUtils.isEmpty(cContact1) || TextUtils.isEmpty(cName1)) {
                    baseActivity.showToast("Please fill the field");
                    return;
                }
                if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {
                    addContact(cName1, cContact1);
                } else {
                    baseActivity.showToast("No internet connection");
                }


                mydialog.dismiss();
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        });
        mydialog.show();
    }

    private void addContact(final String cName1, final String cContact1) {
        try {
            myComplaintList.clear();
            baseActivity.setProgressDialog("Adding Emergency Contact...");
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
                                        baseActivity.showToast(obj.getString("message"));
                                        SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "eData", obj.getString("data"));
                                        JSONArray jsonArray = new JSONArray(obj.getString("data"));

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            myComplaintList.add(new EmergencyContact(jsonObject.getInt("id"),
                                                    jsonObject.getInt("uid"), jsonObject.getString("ename"),
                                                    jsonObject.getString("econtact")));
                                        }
                                    } else {
                                        baseActivity.showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1" + obj.getString("message"));
                                        CheckEmptyState();
                                    }
                                    setDataOnRecycler();
                                    CheckEmptyState();
                                    baseActivity.cancelProgressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    baseActivity.cancelProgressDialog();
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
                                baseActivity.cancelProgressDialog();
                                baseActivity.showToast(error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("uid", SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "uid"));
                    params.put("ename", cName1);
                    params.put("econtact", cContact1);

                    return params;
                }
            };


            RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataOnRecycler() {
        EmergencyContactAdapter myComplaintAdapter = new EmergencyContactAdapter(getActivity(), myComplaintList);
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setAdapter(myComplaintAdapter);
    }

    //choose dialog
    private void showDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        pictureDialog.setTitle("Select Action");

        String[] pictureDialogItems = {
                "Add Contact from Phone",
                "Add Contact Manually"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(getActivity(), ShowAllContactsActivity.class);
                                intent.putExtra("isFromContact", true);
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            case 1:
                                addEmergencyContactDialog();
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
