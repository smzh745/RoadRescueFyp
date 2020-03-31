package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.road.rescue.app.R;
import com.road.rescue.app.utils.CustomMainButtons;


public class HomeFragment extends Basefragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        CustomMainButtons settings = view.findViewById(R.id.settings);
        CustomMainButtons nationalEmergencyService = view.findViewById(R.id.nationalEmergencyService);
        CustomMainButtons addEmergencyContact = view.findViewById(R.id.addEmergencyContact);
        CustomMainButtons complaintSystem = view.findViewById(R.id.complaintSystem);

        settings.setOnClickListener(this);
        nationalEmergencyService.setOnClickListener(this);
        addEmergencyContact.setOnClickListener(this);
        complaintSystem.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complaintSystem:
                navigateFragment(R.id.complaintSystemFragment);
                break;
            case R.id.settings:
                navigateFragment(R.id.settingsFragment);
                break;
            case R.id.addEmergencyContact:
                navigateFragment(R.id.emergencyContactFragment);
                break;
            case R.id.nationalEmergencyService:
                navigateFragment(R.id.nationalEmergencyFragment);

                break;
            default:
                break;
        }
    }
}
