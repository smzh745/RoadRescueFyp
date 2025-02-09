package com.road.rescue.app.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import com.road.rescue.app.utils.RecyclerItemTouchHelper;
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
import static com.road.rescue.app.utils.Constants.DELETE_EMERGENCY_CONTACT;
import static com.road.rescue.app.utils.Constants.PREFS_NAME;
import static com.road.rescue.app.utils.Constants.TAGI;


public class EmergencyContactFragment extends Basefragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private RecyclerView recyclerView;
    private LinearLayout empty_view;
    private List<EmergencyContact> myComplaintList;
    private EmergencyContactAdapter myComplaintAdapter;
    private ShowcaseView builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);
        FloatingActionButton addEmergencyContact = view.findViewById(R.id.addEmergencyContact);

        recyclerView = view.findViewById(R.id.recycler_home);
        empty_view = view.findViewById(R.id.empty_view);
        myComplaintList = new ArrayList<>();
        loadShowCase();
        addEmergencyContact.setOnClickListener(
                v -> showDialog());
    /*    if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {
            init();
        } else {
            baseActivity.showToast("No Internet Connection!");
            init();
        }*/
        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        return view;
    }

    private void init() {
        try {
            myComplaintList.clear();
            JSONArray jsonArray = new JSONArray(SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "eData"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                myComplaintList.add(new EmergencyContact(jsonObject.getInt("uid"),
                        jsonObject.getInt("id"), jsonObject.getString("ename"),
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


        savebtn.setOnClickListener(view -> {
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
        });
        cancelbtn.setOnClickListener(view -> mydialog.dismiss());
        mydialog.show();
    }

    private void addContact(final String cName1, final String cContact1) {
        try {
            myComplaintList.clear();
            baseActivity.setProgressDialog("Adding Emergency Contact...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + ADD_EMERGENCY_CONTACT,
                    response -> {
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
                                        myComplaintList.add(new EmergencyContact(jsonObject.getInt("uid"),
                                                jsonObject.getInt("id"), jsonObject.getString("ename"),
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
                    },
                    error -> {
                        try {
                            baseActivity.cancelProgressDialog();
                            baseActivity.showToast(error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
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
        myComplaintAdapter = new EmergencyContactAdapter(getActivity(), myComplaintList);
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

                (dialog, which) -> {
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
                });
        pictureDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof EmergencyContactAdapter.MyHolder) {
            // get the removed item name to display it in snack bar
            final String name = myComplaintList.get(viewHolder.getAdapterPosition()).geteName();
            final int id = myComplaintList.get(viewHolder.getAdapterPosition()).getId();

            // backup of removed item for undo purpose
            final EmergencyContact deletedItem = myComplaintList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            myComplaintAdapter.removeItem(viewHolder.getAdapterPosition());
            // showing snack bar with Undo option

            final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {
                            deleteContact(name, id);
                        } else {
                            baseActivity.showToast("No Internet Connection!");
                            myComplaintAdapter.restoreItem(deletedItem, deletedIndex);

                        }
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        // undo is selected, restore the deleted item
                        myComplaintAdapter.restoreItem(deletedItem, deletedIndex);
                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Are you sure you want to delete this number?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }
    }

    private void deleteContact(final String name, final int id) {
        baseActivity.setProgressDialog("Deleting Emergency Contact...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.ROOT_URL + DELETE_EMERGENCY_CONTACT,
                response -> {
                    try {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {


                                Log.d(TAGI, "e" + obj.getString("message"));
                                baseActivity.showToast(name + " removed from list!");
                                myComplaintList.clear();
                                Log.d(TAGI, "onResponse: " + obj.getString("data"));
                                SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "eData", obj.getString("data"));
                                JSONArray jsonArray = new JSONArray(obj.getString("data"));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    myComplaintList.add(new EmergencyContact(jsonObject.getInt("uid"),
                                            jsonObject.getInt("id"), jsonObject.getString("ename"),
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
                },
                error -> {
                    try {
                        baseActivity.cancelProgressDialog();
                        baseActivity.showToast(error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("uid", SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "uid"));
                params.put("id", String.valueOf(id));

                return params;
            }
        };


        RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void loadShowCase() {
        SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_NAME, 0);
        boolean dialogShown = settings.getBoolean("missingDialog5", false);
        if (!dialogShown) {

            builder = new ShowcaseView.Builder(Objects.requireNonNull(getActivity())).setTarget(new ViewTarget(recyclerView))
                    .setContentTitle("Delete Item")
                    .setContentText("" +
                            "1. Delete any contact from list. \n" +
                            "2. Just swipe right to delete. \n" +
                            "3. Click Yes when done.")
                    .blockAllTouches()

                    .setStyle(R.style.CustomShowcaseTheme3)

                    .setOnClickListener(v -> {
                        if (builder.isShowing()) {
                            builder.hide();
                        }

                    })
                    .build();
            builder.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("missingDialog5", true);
            editor.apply();

        }
    }
}
