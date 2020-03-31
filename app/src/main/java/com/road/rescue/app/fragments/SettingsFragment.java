package com.road.rescue.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.road.rescue.app.R;
import com.road.rescue.app.utils.LoadServiceUtils;
import com.road.rescue.app.utils.SharedPrefUtils;

import java.util.Objects;


public class SettingsFragment extends Basefragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        SwitchMaterial enableShake = view.findViewById(R.id.enableShake);
        enableShake.setChecked(SharedPrefUtils.getBooleanData(Objects.requireNonNull(getActivity()), "isShake"));
        enableShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", true);
                    LoadServiceUtils.startService(getActivity());
                } else {
                    SharedPrefUtils.saveData(Objects.requireNonNull(getActivity()), "isShake", false);
                    LoadServiceUtils.stopService(getActivity());
                }
            }
        });
        return view;
    }
}
