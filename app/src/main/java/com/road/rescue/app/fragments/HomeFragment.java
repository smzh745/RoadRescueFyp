package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;

import com.road.rescue.app.R;
import com.road.rescue.app.utils.CustomMainButtons;

import java.util.Objects;


public class HomeFragment extends Basefragment implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Objects.requireNonNull(getActivity()).finishAffinity();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

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
                if (baseActivity.isLocationEnabled(Objects.requireNonNull(getActivity()))) {
                    navigateFragment(R.id.settingsFragment);
                } else {
                    baseActivity.showAlertDialog("You need to enable your location to use this app. Otherwise location will not send properly.");
                }
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
