package com.road.rescue.app.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.adapter.MyComplaintAdapter;
import com.road.rescue.app.model.MyComplaint;
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

import static com.road.rescue.app.utils.Constants.TAGI;
import static com.road.rescue.app.utils.Constants.USER_MY_COMPLAINTS;


public class MyComplaintsFragment extends Basefragment {

    private RecyclerView recyclerView;
    private MaterialTextView empty_view;
    private List<MyComplaint> myComplaintList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        empty_view = view.findViewById(R.id.noComplaint);
        myComplaintList = new ArrayList<>();
        if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {
            init();
        }else{
            baseActivity.showToast("No Internet Connection!");
            CheckEmptyState();
        }
        return view;
    }

    private void init() {
        try {
            baseActivity.setProgressDialog("Fetching All Complaints...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL + USER_MY_COMPLAINTS,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response) {
                            try {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                        Log.d(TAGI, "e1: " + obj.getString("message"));
                                        JSONArray jsonArray = new JSONArray(obj.getString("message"));
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            Log.d(TAGI, jsonObject.getString("complainttype"));
                                            myComplaintList.add(new MyComplaint(jsonObject.getInt("id"),
                                                    jsonObject.getInt("uid"), jsonObject.getString("complainttype"),
                                                    jsonObject.getString("city"), jsonObject.getString("complaintdetails"),
                                                    jsonObject.getString("complaintimage"), jsonObject.getString("cdate"), jsonObject.getString("ctime")));
                                        }
                                    } else {
                                        baseActivity.showToast(obj.getString("message"));
                                        Log.d(TAGI, "e1: " + obj.getString("message"));

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


            RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
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

    private void setDataOnRecycler() {
        MyComplaintAdapter myComplaintAdapter = new MyComplaintAdapter(getActivity(), myComplaintList);
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setAdapter(myComplaintAdapter);
    }
}
