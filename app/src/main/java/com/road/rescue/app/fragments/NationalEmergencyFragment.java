package com.road.rescue.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.road.rescue.app.R;
import com.road.rescue.app.adapter.EmergencyContactAdapter;
import com.road.rescue.app.model.EmergencyContact;

import java.util.ArrayList;
import java.util.List;


public class NationalEmergencyFragment extends Basefragment {
    private RecyclerView recyclerView;
    private LinearLayout empty_view;
    private List<EmergencyContact> myComplaintList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);
        view.findViewById(R.id.addEmergencyContact).setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recycler_home);
        empty_view = view.findViewById(R.id.empty_view);
        myComplaintList = new ArrayList<>();
        setDataOnRecycler();
        return view;
    }

    private void setDataOnRecycler() {
        EmergencyContactAdapter myComplaintAdapter = new EmergencyContactAdapter(getActivity(), getMyComplaintList());
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setAdapter(myComplaintAdapter);
    }

    private List<EmergencyContact> getMyComplaintList() {
        myComplaintList.add(new EmergencyContact("Rescue","1122"));
        myComplaintList.add(new EmergencyContact("Police","15"));
        myComplaintList.add(new EmergencyContact("Traffic Police","1915"));
        myComplaintList.add(new EmergencyContact("Fire Brigade Center","16"));
        return myComplaintList;
    }

}
