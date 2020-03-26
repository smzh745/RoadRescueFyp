package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.adapter.MyComplaintAdapter;
import com.road.rescue.app.model.MyComplaint;
import com.road.rescue.app.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;


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
      /*  if (InternetConnection.checkConnection(Objects.requireNonNull(getActivity()))) {

        } else {
            baseActivity.showToast("No Internet Connection!");

        }*/
        init();
        CheckEmptyState();
        return view;
    }

    private void init() {
        try {
            myComplaintList.clear();
            JSONArray jsonArray = new JSONArray(SharedPrefUtils.getStringData(Objects.requireNonNull(getActivity()), "myComplaints"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d(TAGI, jsonObject.getString("complainttype"));
                myComplaintList.add(new MyComplaint(jsonObject.getInt("id"),
                        jsonObject.getInt("uid"), jsonObject.getString("complainttype"),
                        jsonObject.getString("city"), jsonObject.getString("complaintdetails"),
                        jsonObject.getString("complaintimage"), jsonObject.getString("cdate"), jsonObject.getString("ctime")));
            }
            setDataOnRecycler();
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
